// App State
const AppState = {
    currentChat: null, // { type: 'room' | 'private', id: number }
    rooms: [],
    privateChats: [],
    onlineUsers: [],
    typingUsers: {}
};

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    if (Auth.init()) {
        showChat();
    } else {
        showAuthScreen();
    }
});

// Initialize chat
function initChat() {
    WebSocketClient.connect(() => {
        loadRooms();
        loadPrivateChats();
    });

    // Setup typing indicator
    const messageInput = document.getElementById('message-input');
    let typingTimeout;

    messageInput.addEventListener('input', () => {
        if (AppState.currentChat && AppState.currentChat.type === 'room') {
            WebSocketClient.sendTyping(AppState.currentChat.id);

            clearTimeout(typingTimeout);
            typingTimeout = setTimeout(() => {
                WebSocketClient.sendStopTyping(AppState.currentChat.id);
            }, 1000);
        }
    });
}

// API Calls
async function apiCall(endpoint, options = {}) {
    const response = await fetch(endpoint, {
        ...options,
        headers: Auth.getHeaders()
    });

    if (!response.ok) {
        if (response.status === 401) {
            logout();
            throw new Error('Session expired');
        }
        throw new Error('API request failed');
    }

    return response.json();
}

// Load data
async function loadRooms() {
    try {
        AppState.rooms = await apiCall('/api/rooms');
        renderRooms();
    } catch (error) {
        console.error('Error loading rooms:', error);
    }
}

async function loadPrivateChats() {
    try {
        AppState.privateChats = await apiCall('/api/private');
        renderPrivateChats();
    } catch (error) {
        console.error('Error loading private chats:', error);
    }
}

async function loadMessages() {
    if (!AppState.currentChat) return;

    try {
        let messages;
        if (AppState.currentChat.type === 'room') {
            messages = await apiCall(`/api/rooms/${AppState.currentChat.id}/messages/recent`);
        } else {
            messages = await apiCall(`/api/private/${AppState.currentChat.id}/messages/recent`);
        }
        renderMessages(messages);
    } catch (error) {
        console.error('Error loading messages:', error);
    }
}

// Render functions
function renderRooms() {
    const roomsList = document.getElementById('rooms-list');
    roomsList.innerHTML = AppState.rooms.map(room => `
        <li onclick="selectRoom(${room.id})" class="${AppState.currentChat?.type === 'room' && AppState.currentChat?.id === room.id ? 'active' : ''}">
            <span>#</span>
            <span>${escapeHtml(room.name)}</span>
            <span class="badge">${room.members?.length || 0}</span>
        </li>
    `).join('');
}

function renderPrivateChats() {
    const chatsList = document.getElementById('private-chats-list');
    chatsList.innerHTML = AppState.privateChats.map(chat => {
        const otherUser = chat.user1.id === Auth.user.id ? chat.user2 : chat.user1;
        const isOnline = AppState.onlineUsers.some(u => u.id === otherUser.id);
        return `
            <li onclick="selectPrivateChat(${chat.id}, '${escapeHtml(otherUser.username)}')"
                class="${AppState.currentChat?.type === 'private' && AppState.currentChat?.id === chat.id ? 'active' : ''}">
                <span class="status-badge ${isOnline ? 'online' : 'offline'}"></span>
                <span>${escapeHtml(otherUser.username)}</span>
            </li>
        `;
    }).join('');
}

function updateOnlineUsers(users) {
    AppState.onlineUsers = users;
    document.getElementById('online-count').textContent = users.length;

    const usersList = document.getElementById('online-users-list');
    usersList.innerHTML = users
        .filter(u => u.id !== Auth.user.id)
        .map(user => `
            <li onclick="startPrivateChat(${user.id})">
                <span class="status-badge online"></span>
                <span>${escapeHtml(user.username)}</span>
            </li>
        `).join('');

    // Update private chats list for online status
    renderPrivateChats();
}

function renderMessages(messages) {
    const container = document.getElementById('messages-container');

    if (messages.length === 0) {
        container.innerHTML = '<div class="no-chat-selected"><p>No messages yet. Start the conversation!</p></div>';
        return;
    }

    container.innerHTML = messages.map(msg => {
        const isSent = msg.senderId === Auth.user.id;
        const time = new Date(msg.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

        return `
            <div class="message ${isSent ? 'sent' : 'received'}">
                ${!isSent ? `<div class="message-sender">${escapeHtml(msg.senderUsername)}</div>` : ''}
                <div class="message-content">${escapeHtml(msg.content)}</div>
                <div class="message-time">${time}</div>
            </div>
        `;
    }).join('');

    // Scroll to bottom
    container.scrollTop = container.scrollHeight;
}

function addMessage(msg) {
    const container = document.getElementById('messages-container');
    const noChat = container.querySelector('.no-chat-selected');
    if (noChat) {
        container.innerHTML = '';
    }

    const isSent = msg.senderId === Auth.user.id;
    const time = new Date(msg.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });

    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${isSent ? 'sent' : 'received'}`;
    messageDiv.innerHTML = `
        ${!isSent ? `<div class="message-sender">${escapeHtml(msg.senderUsername)}</div>` : ''}
        <div class="message-content">${escapeHtml(msg.content)}</div>
        <div class="message-time">${time}</div>
    `;

    container.appendChild(messageDiv);
    container.scrollTop = container.scrollHeight;
}

function addSystemMessage(text) {
    const container = document.getElementById('messages-container');
    const messageDiv = document.createElement('div');
    messageDiv.className = 'system-message';
    messageDiv.textContent = text;
    container.appendChild(messageDiv);
    container.scrollTop = container.scrollHeight;
}

// Chat selection
async function selectRoom(roomId) {
    // Unsubscribe from previous room
    if (AppState.currentChat?.type === 'room') {
        WebSocketClient.unsubscribeFromRoom(AppState.currentChat.id);
    }

    AppState.currentChat = { type: 'room', id: roomId };

    const room = AppState.rooms.find(r => r.id === roomId);
    document.getElementById('chat-title').textContent = `# ${room.name}`;
    document.getElementById('message-input-container').classList.remove('hidden');

    // Subscribe to room
    WebSocketClient.subscribeToRoom(roomId, (msg) => {
        if (msg.type === 'JOIN') {
            addSystemMessage(`${msg.user.username} joined the room`);
        } else if (msg.type === 'LEAVE') {
            addSystemMessage(`${msg.user.username} left the room`);
        } else if (msg.id) {
            addMessage(msg);
        }
    });

    // Join room via API if not a member
    try {
        await apiCall(`/api/rooms/${roomId}/join`, { method: 'POST' });
    } catch (e) {
        // Already a member
    }

    WebSocketClient.sendJoinRoom(roomId);
    loadMessages();
    renderRooms();
}

async function selectPrivateChat(chatId, username) {
    if (AppState.currentChat?.type === 'room') {
        WebSocketClient.unsubscribeFromRoom(AppState.currentChat.id);
    }

    AppState.currentChat = { type: 'private', id: chatId };

    document.getElementById('chat-title').textContent = username;
    document.getElementById('message-input-container').classList.remove('hidden');
    document.getElementById('typing-indicator').classList.add('hidden');

    loadMessages();
    renderPrivateChats();
}

async function startPrivateChat(userId) {
    try {
        const chat = await apiCall(`/api/private/start/${userId}`, { method: 'POST' });
        await loadPrivateChats();

        const otherUser = chat.user1.id === Auth.user.id ? chat.user2 : chat.user1;
        selectPrivateChat(chat.id, otherUser.username);
    } catch (error) {
        console.error('Error starting private chat:', error);
    }
}

// Create room
async function createRoom() {
    const input = document.getElementById('new-room-name');
    const name = input.value.trim();

    if (!name) return;

    try {
        const room = await apiCall('/api/rooms', {
            method: 'POST',
            body: JSON.stringify({ name })
        });
        input.value = '';
        await loadRooms();
        selectRoom(room.id);
    } catch (error) {
        console.error('Error creating room:', error);
    }
}

// Send message
function sendMessage(event) {
    event.preventDefault();

    const input = document.getElementById('message-input');
    const content = input.value.trim();

    if (!content || !AppState.currentChat) return false;

    if (AppState.currentChat.type === 'room') {
        WebSocketClient.sendRoomMessage(AppState.currentChat.id, content);
        WebSocketClient.sendStopTyping(AppState.currentChat.id);
    } else {
        // For private chat, need to get recipient ID
        const chat = AppState.privateChats.find(c => c.id === AppState.currentChat.id);
        const recipientId = chat.user1.id === Auth.user.id ? chat.user2.id : chat.user1.id;
        WebSocketClient.sendPrivateMessage(recipientId, content);
    }

    input.value = '';
    return false;
}

// Handle private message
function handlePrivateMessage(msg) {
    // Add to chat if current private chat
    if (AppState.currentChat?.type === 'private' && msg.privateChatId === AppState.currentChat.id) {
        addMessage(msg);
    }

    // Refresh private chats list if needed
    if (!AppState.privateChats.some(c => c.id === msg.privateChatId)) {
        loadPrivateChats();
    }
}

// Handle typing indicator
function handleTypingIndicator(data) {
    const indicator = document.getElementById('typing-indicator');
    const textSpan = indicator.querySelector('.typing-text');

    if (data.isTyping && data.user.id !== Auth.user.id) {
        AppState.typingUsers[data.user.id] = data.user.username;
    } else {
        delete AppState.typingUsers[data.user.id];
    }

    const typingUsernames = Object.values(AppState.typingUsers);

    if (typingUsernames.length > 0) {
        if (typingUsernames.length === 1) {
            textSpan.textContent = `${typingUsernames[0]} is typing`;
        } else {
            textSpan.textContent = `${typingUsernames.length} people are typing`;
        }
        indicator.classList.remove('hidden');
    } else {
        indicator.classList.add('hidden');
    }
}

// Search by phone number
async function searchByPhone() {
    const phoneInput = document.getElementById('search-phone');
    const resultDiv = document.getElementById('search-result');
    const phoneNumber = phoneInput.value.trim();

    if (!phoneNumber) {
        resultDiv.innerHTML = '<div class="not-found">Please enter a phone number</div>';
        resultDiv.classList.remove('hidden');
        return;
    }

    try {
        const user = await apiCall(`/api/users/search/phone/${phoneNumber}`);

        if (user.id === Auth.user.id) {
            resultDiv.innerHTML = '<div class="not-found">This is your own number!</div>';
        } else {
            resultDiv.innerHTML = `
                <div class="user-found">
                    <div class="user-info-found">
                        <span class="user-name">${escapeHtml(user.username)}</span>
                        <span class="user-phone">${escapeHtml(user.phoneNumber)}</span>
                    </div>
                    <button class="btn btn-small btn-chat" onclick="startChatWithUser(${user.id}, '${escapeHtml(user.username)}')">
                        Chat
                    </button>
                </div>
            `;
        }
        resultDiv.classList.remove('hidden');
    } catch (error) {
        resultDiv.innerHTML = '<div class="not-found">User not found with this phone number</div>';
        resultDiv.classList.remove('hidden');
    }
}

// Start chat with found user
async function startChatWithUser(userId, username) {
    try {
        const chat = await apiCall(`/api/private/start/${userId}`, { method: 'POST' });
        await loadPrivateChats();
        selectPrivateChat(chat.id, username);

        // Clear search
        document.getElementById('search-phone').value = '';
        document.getElementById('search-result').classList.add('hidden');
    } catch (error) {
        console.error('Error starting chat:', error);
    }
}

// Utility
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
