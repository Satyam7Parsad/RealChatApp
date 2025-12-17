# RealChat - Real-Time Chat Application

A WhatsApp-like real-time chat application built with Spring Boot and WebSocket.

## Features

- **User Registration** with phone number (like WhatsApp)
- **Search Users** by phone number
- **Private Messaging** - 1-on-1 real-time chat
- **Group Chat Rooms** - Create and join chat rooms
- **Typing Indicators** - See when someone is typing
- **Online/Offline Status** - Real-time user presence
- **Message History** - Persistent chat history
- **JWT Authentication** - Secure login system

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 3.2 (Java 17) |
| Database | MySQL |
| Real-time | WebSocket + STOMP |
| Security | Spring Security + JWT |
| Frontend | HTML5, CSS3, JavaScript |

## Project Structure

```
src/
├── main/
│   ├── java/com/chatapp/
│   │   ├── config/          # WebSocket & Security config
│   │   ├── controller/      # REST & WebSocket controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── model/           # JPA Entities
│   │   ├── repository/      # Database repositories
│   │   ├── security/        # JWT authentication
│   │   └── service/         # Business logic
│   └── resources/
│       ├── static/          # Frontend (HTML, CSS, JS)
│       └── application.properties
```

## Setup & Run

### Prerequisites
- Java 17+
- MySQL
- Maven (or use included wrapper)

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/Satyam7Parsad/RealChatApp.git
   cd RealChatApp
   ```

2. **Create MySQL database**
   ```sql
   CREATE DATABASE chatapp;
   ```

3. **Update database credentials**

   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Open in browser**
   ```
   http://localhost:8080
   ```

## How to Use

### Register
1. Click "Register"
2. Enter username, phone number, email, password
3. Click Register

### Find & Chat with Users
1. Enter friend's phone number in "Find User by Phone"
2. Click Search
3. Click "Chat" to start conversation

### Create Group Chat
1. Enter room name in "Chat Rooms" section
2. Click "+" to create
3. Invite others to join

## Testing with Multiple Users

### Same WiFi Network
1. Find your IP: `ipconfig getifaddr en0` (Mac) or `ipconfig` (Windows)
2. Others open: `http://YOUR_IP:8080`

### Over Internet (using ngrok)
```bash
ngrok http 8080
```
Share the ngrok URL with anyone.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login |
| GET | `/api/users/search/phone/{phone}` | Search by phone |
| GET | `/api/rooms` | Get all rooms |
| POST | `/api/rooms` | Create room |
| POST | `/api/private/start/{userId}` | Start private chat |

## WebSocket Endpoints

| Purpose | Subscribe | Send |
|---------|-----------|------|
| Room messages | `/topic/room.{roomId}` | `/app/chat.room.{roomId}` |
| Private messages | `/queue/private` | `/app/chat.private.{recipientId}` |
| Online status | `/topic/online` | Auto |
| Typing indicator | `/topic/typing.{roomId}` | `/app/chat.typing.{roomId}` |

## Screenshots

### Login Page
Clean login/register interface with gradient design.

### Chat Interface
- Left sidebar: Online users, chat rooms, private chats
- Center: Message area with real-time updates
- Phone search to find and chat with users

## License

MIT License

## Author

**Satyam Parsad**

---

Made with Spring Boot + WebSocket
