// WebSocket Module
const WebSocketClient = {
    stompClient: null,
    subscriptions: {},
    connected: false,

    connect(onConnected) {
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);

        // Disable debug logs
        this.stompClient.debug = null;

        this.stompClient.connect({}, (frame) => {
            this.connected = true;
            console.log('WebSocket Connected');

            // Register user
            this.stompClient.send('/app/chat.register', {}, JSON.stringify({
                username: Auth.user.username
            }));

            // Subscribe to online status updates
            this.subscribe('/topic/online', (message) => {
                const onlineUsers = JSON.parse(message.body);
                updateOnlineUsers(onlineUsers);
            });

            // Subscribe to private messages
            this.subscribe('/queue/private/' + Auth.user.id, (message) => {
                const msg = JSON.parse(message.body);
                handlePrivateMessage(msg);
            });

            if (onConnected) {
                onConnected();
            }
        }, (error) => {
            console.error('WebSocket Error:', error);
            this.connected = false;
            // Attempt reconnection after 5 seconds
            setTimeout(() => this.connect(onConnected), 5000);
        });
    },

    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect();
            this.connected = false;
        }
    },

    subscribe(destination, callback) {
        if (this.subscriptions[destination]) {
            this.subscriptions[destination].unsubscribe();
        }
        this.subscriptions[destination] = this.stompClient.subscribe(destination, callback);
        return this.subscriptions[destination];
    },

    unsubscribe(destination) {
        if (this.subscriptions[destination]) {
            this.subscriptions[destination].unsubscribe();
            delete this.subscriptions[destination];
        }
    },

    send(destination, payload) {
        if (this.connected) {
            this.stompClient.send(destination, {}, JSON.stringify(payload));
        }
    },

    subscribeToRoom(roomId, callback) {
        // Subscribe to room messages
        this.subscribe('/topic/room/' + roomId, (message) => {
            const msg = JSON.parse(message.body);
            callback(msg);
        });

        // Subscribe to typing indicators
        this.subscribe('/topic/typing/' + roomId, (message) => {
            const data = JSON.parse(message.body);
            handleTypingIndicator(data);
        });
    },

    unsubscribeFromRoom(roomId) {
        this.unsubscribe('/topic/room/' + roomId);
        this.unsubscribe('/topic/typing/' + roomId);
    },

    sendRoomMessage(roomId, content) {
        this.send('/app/chat.room.' + roomId, {
            content: content,
            type: 'CHAT'
        });
    },

    sendPrivateMessage(recipientId, content) {
        this.send('/app/chat.private.' + recipientId, {
            content: content
        });
    },

    sendTyping(roomId) {
        this.send('/app/chat.room.' + roomId, {
            type: 'TYPING'
        });
    },

    sendStopTyping(roomId) {
        this.send('/app/chat.stopTyping.' + roomId, {});
    },

    sendJoinRoom(roomId) {
        this.send('/app/chat.join.' + roomId, {});
    },

    sendLeaveRoom(roomId) {
        this.send('/app/chat.leave.' + roomId, {});
    }
};
