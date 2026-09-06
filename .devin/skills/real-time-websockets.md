---
name: Real-time & WebSockets Skill
description: Comprehensive methodology for implementing real-time communication in web applications — 2025-2026 practices with WebSockets, Server-Sent Events, Socket.io, reconnection, presence, and scaling
version: 1.0.0
tags: [real-time, websockets, sse, server-sent-events, socket-io, presence, reconnection, pub-sub, scaling, live-updates]
---

# Real-time & WebSockets Skill

## Purpose
This skill provides a comprehensive methodology for implementing real-time communication across any kind of web project. It reflects **modern 2025-2026 practices** — WebSockets for bidirectional communication, Server-Sent Events for server-to-client streaming, Socket.io for battle-tested reliability, reconnection with exponential backoff, presence detection, and horizontal scaling with Redis adapter.

## Core Philosophy

**Real-time should degrade gracefully.** Not all users have stable connections. Not all environments support WebSockets. Always provide fallbacks (SSE, long polling), handle reconnection automatically, and ensure the app works even if real-time fails. Real-time is an enhancement, not a requirement.

**The #1 rule:** Don't use WebSockets for everything. WebSockets are for bidirectional, low-latency communication. If you only need server-to-client updates (notifications, live feeds), use Server-Sent Events — they're simpler, use standard HTTP, and auto-reconnect. Choose the right tool for the communication pattern.

---

## Part 1: Communication Patterns

### 1.1 Pattern Selection

| Pattern | Direction | Protocol | Use Case |
|---|---|---|---|
| **WebSocket** | Bidirectional | WS/WSS | Chat, collaboration, gaming, live editing |
| **Server-Sent Events** | Server → Client | HTTP | Notifications, live feeds, stock prices |
| **Long polling** | Bidirectional | HTTP | Fallback when WS/SSE unavailable |
| **WebRTC** | Peer-to-peer | UDP | Video/audio calls, P2P file sharing |
| **Streaming responses** | Server → Client | HTTP | AI token streaming, large responses |

### 1.2 When to Use WebSockets
- **Chat/messaging:** Bidirectional, low-latency message exchange
- **Collaborative editing:** Real-time document/code editing
- **Multiplayer games:** Low-latency bidirectional state sync
- **Live dashboards:** When client also sends data (telemetry, interactions)
- **Notifications with actions:** Server pushes notification, client responds

### 1.3 When to Use SSE
- **Notifications:** Server pushes notifications to client
- **Live feeds:** News feed, social media timeline, activity feed
- **Stock/crypto prices:** Server pushes price updates
- **AI token streaming:** LLM response streaming (OpenAI, Anthropic)
- **Progress updates:** Long-running task progress
- **Any server-to-client only pattern:** Simpler than WebSockets

---

## Part 2: WebSockets

### 2.1 Native WebSocket API (Client)
```typescript
const ws = new WebSocket('wss://example.com/ws');

ws.onopen = () => {
  console.log('Connected');
  ws.send(JSON.stringify({ type: 'join', room: 'general' }));
};

ws.onmessage = (event) => {
  const data = JSON.parse(event.data);
  console.log('Message:', data);
};

ws.onerror = (error) => {
  console.error('WebSocket error:', error);
};

ws.onclose = (event) => {
  console.log('Disconnected:', event.code, event.reason);
  // Reconnect
  setTimeout(() => connect(), 1000);
};

// Send message
ws.send(JSON.stringify({ type: 'message', text: 'Hello' }));

// Close
ws.close(1000, 'Normal closure');
```

### 2.2 WebSocket Server (Node.js with `ws`)
```typescript
import { WebSocketServer } from 'ws';

const wss = new WebSocketServer({ port: 8080 });

wss.on('connection', (ws, req) => {
  // Authenticate from query params or headers
  const userId = authenticate(req);

  // Store connection
  ws.userId = userId;

  ws.on('message', (data) => {
    const message = JSON.parse(data.toString());

    switch (message.type) {
      case 'join':
        joinRoom(ws, message.room);
        break;
      case 'message':
        broadcastToRoom(message.room, {
          type: 'message',
          userId,
          text: message.text,
          timestamp: Date.now(),
        });
        break;
    }
  });

  ws.on('close', () => {
    removeFromAllRooms(ws);
    broadcastPresence(ws.userId, 'offline');
  });

  // Send initial state
  ws.send(JSON.stringify({ type: 'connected', userId }));
});
```

### 2.3 Room Management
```typescript
const rooms = new Map<string, Set<WebSocket>>();

function joinRoom(ws: WebSocket, room: string) {
  if (!rooms.has(room)) rooms.set(room, new Set());
  rooms.get(room)!.add(ws);
  ws.room = room;

  // Notify room of new user
  broadcastToRoom(room, {
    type: 'user_joined',
    userId: ws.userId,
    timestamp: Date.now(),
  });
}

function broadcastToRoom(room: string, message: any) {
  const members = rooms.get(room);
  if (!members) return;

  const data = JSON.stringify(message);
  members.forEach((ws) => {
    if (ws.readyState === WebSocket.OPEN) {
      ws.send(data);
    }
  });
}

function removeFromAllRooms(ws: WebSocket) {
  rooms.forEach((members, room) => {
    members.delete(ws);
    if (members.size === 0) rooms.delete(room);
  });
}
```

---

## Part 3: Socket.io (Recommended for Production)

### 3.1 Why Socket.io
- **Auto-reconnection:** Built-in with configurable backoff
- **Fallback:** Automatically falls back to long polling if WS unavailable
- **Rooms and namespaces:** Built-in room management
- **Acknowledgements:** Request-response pattern over WebSocket
- **Binary support:** Efficient binary data transmission
- **Middleware:** Authentication and authorization middleware
- **Redis adapter:** Horizontal scaling across multiple servers

### 3.2 Server Setup
```typescript
import { Server } from 'socket.io';
import { createAdapter } from '@socket.io/redis-adapter';
import { createClient } from 'redis';

const io = new Server(httpServer, {
  cors: { origin: 'https://example.com', credentials: true },
});

// Redis adapter for scaling
const pubClient = createClient({ url: process.env.REDIS_URL });
const subClient = pubClient.duplicate();
await Promise.all([pubClient.connect(), subClient.connect()]);
io.adapter(createAdapter(pubClient, subClient));

// Authentication middleware
io.use((socket, next) => {
  const token = socket.handshake.auth.token;
  const user = verifyToken(token);
  if (!user) return next(new Error('Unauthorized'));
  socket.user = user;
  next();
});

// Connection handler
io.on('connection', (socket) => {
  console.log(`User ${socket.user.id} connected`);

  // Join room
  socket.on('join', (room) => {
    socket.join(room);
    socket.to(room).emit('user_joined', { userId: socket.user.id });
  });

  // Message
  socket.on('message', (data, callback) => {
    io.to(data.room).emit('message', {
      userId: socket.user.id,
      text: data.text,
      timestamp: Date.now(),
    });
    callback({ status: 'ok' }); // Acknowledgement
  });

  // Typing indicator
  socket.on('typing', (room) => {
    socket.to(room).emit('typing', { userId: socket.user.id });
  });

  // Disconnect
  socket.on('disconnect', () => {
    io.emit('user_offline', { userId: socket.user.id });
  });
});
```

### 3.3 Client Setup
```typescript
import { io } from 'socket.io-client';

const socket = io('https://example.com', {
  auth: { token: getAuthToken() },
  transports: ['websocket'], // Prefer WebSocket, fallback auto
  reconnection: true,
  reconnectionAttempts: Infinity,
  reconnectionDelay: 1000,
  reconnectionDelayMax: 5000,
});

socket.on('connect', () => {
  console.log('Connected:', socket.id);
});

socket.on('message', (data) => {
  console.log('Message:', data);
});

socket.on('disconnect', (reason) => {
  console.log('Disconnected:', reason);
});

// Send with acknowledgement
socket.emit('message', { room: 'general', text: 'Hello' }, (response) => {
  console.log('Server acknowledged:', response);
});
```

### 3.4 React Integration
```tsx
import { useEffect, useState, useRef } from 'react';
import { io, Socket } from 'socket.io-client';

function useSocket(url: string, token: string | null) {
  const socketRef = useRef<Socket>();

  useEffect(() => {
    if (!token) return;

    const socket = io(url, {
      auth: { token },
      transports: ['websocket'],
    });

    socketRef.current = socket;

    return () => {
      socket.disconnect();
    };
  }, [url, token]);

  return socketRef.current;
}

function ChatRoom({ roomId, token }) {
  const socket = useSocket('https://example.com', token);
  const [messages, setMessages] = useState<Message[]>([]);
  const [typingUsers, setTypingUsers] = useState<Set<string>>(new Set());

  useEffect(() => {
    if (!socket) return;

    socket.emit('join', roomId);

    socket.on('message', (message) => {
      setMessages(prev => [...prev, message]);
    });

    socket.on('typing', ({ userId }) => {
      setTypingUsers(prev => new Set(prev).add(userId));
      setTimeout(() => {
        setTypingUsers(prev => {
          const next = new Set(prev);
          next.delete(userId);
          return next;
        });
      }, 3000);
    });

    return () => {
      socket.off('message');
      socket.off('typing');
    };
  }, [socket, roomId]);

  const sendMessage = (text: string) => {
    socket?.emit('message', { room: roomId, text });
  };

  return (
    <div>
      {messages.map(msg => <Message key={msg.id} {...msg} />)}
      {typingUsers.size > 0 && <p>Someone is typing...</p>}
      <MessageInput onSend={sendMessage} />
    </div>
  );
}
```

---

## Part 4: Server-Sent Events (SSE)

### 4.1 SSE Server (Node.js)
```typescript
export async function GET(request: Request) {
  const headers = new Headers({
    'Content-Type': 'text/event-stream',
    'Cache-Control': 'no-cache, no-transform',
    'Connection': 'keep-alive',
    'X-Accel-Buffering': 'no', // Disable Nginx buffering
  });

  const stream = new ReadableStream({
    start(controller) {
      // Send initial event
      controller.enqueue(`data: ${JSON.stringify({ type: 'connected' })}\n\n`);

      // Set up event listener
      const onMessage = (data) => {
        controller.enqueue(`data: ${JSON.stringify(data)}\n\n`);
      };

      eventEmitter.on('message', onMessage);

      // Heartbeat
      const heartbeat = setInterval(() => {
        controller.enqueue(`: heartbeat\n\n`);
      }, 30000);

      // Cleanup
      request.signal.addEventListener('abort', () => {
        clearInterval(heartbeat);
        eventEmitter.off('message', onMessage);
        controller.close();
      });
    },
  });

  return new Response(stream, { headers });
}
```

### 4.2 SSE Client
```typescript
const eventSource = new EventSource('/api/events');

eventSource.onmessage = (event) => {
  const data = JSON.parse(event.data);
  console.log('Event:', data);
};

eventSource.onerror = (event) => {
  console.log('SSE error, reconnecting...');
  // EventSource auto-reconnects
};

// Named events
eventSource.addEventListener('notification', (event) => {
  const data = JSON.parse(event.data);
  showNotification(data);
});

// Close
eventSource.close();
```

### 4.3 SSE for AI Token Streaming
```typescript
// Server — stream LLM response
export async function POST(request: Request) {
  const { prompt } = await request.json();

  const stream = new ReadableStream({
    async start(controller) {
      const encoder = new TextEncoder();

      const response = await openai.chat.completions.create({
        model: 'gpt-4',
        messages: [{ role: 'user', content: prompt }],
        stream: true,
      });

      for await (const chunk of response) {
        const token = chunk.choices[0]?.delta?.content || '';
        controller.enqueue(encoder.encode(`data: ${JSON.stringify({ token })}\n\n`));
      }

      controller.enqueue(encoder.encode(`data: [DONE]\n\n`));
      controller.close();
    },
  });

  return new Response(stream, {
    headers: {
      'Content-Type': 'text/event-stream',
      'Cache-Control': 'no-cache',
      'Connection': 'keep-alive',
    },
  });
}

// Client — consume stream
async function streamAIResponse(prompt: string) {
  const response = await fetch('/api/chat', {
    method: 'POST',
    body: JSON.stringify({ prompt }),
  });

  const reader = response.body!.getReader();
  const decoder = new TextDecoder();

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;

    const text = decoder.decode(value);
    const lines = text.split('\n');

    for (const line of lines) {
      if (line.startsWith('data: ')) {
        const data = line.slice(6);
        if (data === '[DONE]') return;
        const { token } = JSON.parse(data);
        appendToUI(token);
      }
    }
  }
}
```

---

## Part 5: Reconnection & Reliability

### 5.1 Exponential Backoff
```typescript
function connectWithBackoff(url: string, maxDelay: number = 30000) {
  let attempt = 0;
  let ws: WebSocket;

  const connect = () => {
    ws = new WebSocket(url);

    ws.onopen = () => {
      attempt = 0; // Reset on successful connection
      console.log('Connected');
    };

    ws.onclose = (event) => {
      if (event.code !== 1000) { // Not a normal closure
        const delay = Math.min(1000 * 2 ** attempt, maxDelay);
        attempt++;
        console.log(`Reconnecting in ${delay}ms (attempt ${attempt})`);
        setTimeout(connect, delay);
      }
    };

    ws.onerror = () => {
      ws.close();
    };
  };

  connect();

  return {
    close: () => {
      attempt = -1; // Prevent reconnection
      ws.close(1000, 'Normal closure');
    },
  };
}
```

### 5.2 Message Queue (Offline Buffering)
```typescript
class MessageQueue {
  private queue: any[] = [];
  private ws: WebSocket | null = null;

  connect(url: string) {
    this.ws = new WebSocket(url);

    this.ws.onopen = () => {
      // Flush queued messages
      while (this.queue.length > 0) {
        this.ws!.send(JSON.stringify(this.queue.shift()));
      }
    };

    this.ws.onclose = () => {
      this.ws = null;
      setTimeout(() => this.connect(url), 1000);
    };
  }

  send(message: any) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(message));
    } else {
      this.queue.push(message); // Queue for later
    }
  }
}
```

### 5.3 Heartbeat/Ping-Pong
```typescript
// Server
setInterval(() => {
  wss.clients.forEach((ws) => {
    if (!ws.isAlive) return ws.terminate();
    ws.isAlive = false;
    ws.ping();
  });
}, 30000);

wss.on('connection', (ws) => {
  ws.isAlive = true;
  ws.on('pong', () => { ws.isAlive = true; });
});

// Client
function startHeartbeat(ws: WebSocket) {
  setInterval(() => {
    if (ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify({ type: 'ping' }));
    }
  }, 25000);
}
```

---

## Part 6: Presence & Online Status

### 6.1 Presence System
```typescript
const onlineUsers = new Map<string, Set<string>>(); // userId -> Set<socketId>

function userConnected(userId: string, socketId: string) {
  if (!onlineUsers.has(userId)) {
    onlineUsers.set(userId, new Set());
    // User just came online
    io.emit('presence', { userId, status: 'online' });
  }
  onlineUsers.get(userId)!.add(socketId);
}

function userDisconnected(userId: string, socketId: string) {
  const sockets = onlineUsers.get(userId);
  if (sockets) {
    sockets.delete(socketId);
    if (sockets.size === 0) {
      onlineUsers.delete(userId);
      // User went offline
      io.emit('presence', { userId, status: 'offline' });
    }
  }
}

// Get online status
function isOnline(userId: string): boolean {
  return onlineUsers.has(userId);
}

// Get all online users
function getOnlineUsers(): string[] {
  return Array.from(onlineUsers.keys());
}
```

### 6.2 Presence with Redis (for scaling)
```typescript
// Store presence in Redis for multi-server
async function setUserOnline(userId: string, socketId: string, serverId: string) {
  await redis.sadd(`presence:${userId}`, `${serverId}:${socketId}`);
  await redis.sadd('online_users', userId);
  await redis.publish('presence:update', JSON.stringify({ userId, status: 'online' }));
}

async function setUserOffline(userId: string, socketId: string, serverId: string) {
  await redis.srem(`presence:${userId}`, `${serverId}:${socketId}`);
  const remaining = await redis.scard(`presence:${userId}`);
  if (remaining === 0) {
    await redis.srem('online_users', userId);
    await redis.publish('presence:update', JSON.stringify({ userId, status: 'offline' }));
  }
}
```

---

## Part 7: Scaling WebSockets

### 7.1 Single Server (Small Scale)
- **< 1000 concurrent connections:** Single Node.js process with `ws` or Socket.io
- **Memory:** Track rooms and connections in memory
- **Limitation:** No horizontal scaling — all connections on one server

### 7.2 Redis Adapter (Medium Scale)
```typescript
// Socket.io with Redis adapter — messages broadcast across all servers
import { createAdapter } from '@socket.io/redis-adapter';

io.adapter(createAdapter(pubClient, subClient));

// Now io.to('room').emit() reaches clients on all servers
```

### 7.3 Sticky Sessions (Load Balancer)
- **Requirement:** WebSocket upgrade request must reach same server
- **Configure:** Load balancer with sticky sessions (by cookie or IP)
- **Nginx:** `ip_hash` directive
- **Cloudflare:** WebSocket support with sticky sessions

### 7.4 Dedicated WebSocket Server
- **Separate process:** Run WebSocket server separately from HTTP API
- **Scale independently:** Scale WS servers based on connection count
- **Shared state:** Use Redis for shared room/presence state
- **Load balance:** Use HAProxy or Nginx for WS load balancing

### 7.5 Managed WebSocket Services
- **Pusher:** Hosted WebSocket service, channels, presence
- **Ably:** Real-time data streaming, presence, history
- **Cloudflare Durable Objects:** Edge WebSocket handling
- **AWS API Gateway WebSocket:** Managed WebSocket API

---

## Part 8: Security

### 8.1 Authentication
```typescript
// Socket.io authentication middleware
io.use((socket, next) => {
  const token = socket.handshake.auth.token;
  try {
    const user = jwt.verify(token, process.env.JWT_SECRET);
    socket.user = user;
    next();
  } catch (err) {
    next(new Error('Invalid token'));
  }
});

// Native WebSocket — authenticate from query param or subprotocol
wss.on('connection', (ws, req) => {
  const url = new URL(req.url!, `http://${req.headers.host}`);
  const token = url.searchParams.get('token');
  const user = verifyToken(token);
  if (!user) {
    ws.close(4001, 'Unauthorized');
    return;
  }
  ws.user = user;
});
```

### 8.2 Authorization
```typescript
// Check if user can join room
socket.on('join', (roomId, callback) => {
  if (!canAccessRoom(socket.user.id, roomId)) {
    callback({ error: 'Forbidden' });
    return;
  }
  socket.join(roomId);
  callback({ status: 'ok' });
});

// Rate limiting
const rateLimiter = new Map<string, { count: number; resetTime: number }>();

function rateLimit(socket: Socket, event: string, limit: number, windowMs: number) {
  const key = `${socket.user.id}:${event}`;
  const now = Date.now();
  const record = rateLimiter.get(key);

  if (!record || now > record.resetTime) {
    rateLimiter.set(key, { count: 1, resetTime: now + windowMs });
  } else {
    record.count++;
    if (record.count > limit) {
      socket.emit('error', { message: 'Rate limit exceeded' });
      return false;
    }
  }
  return true;
}
```

### 8.3 Input Validation
```typescript
import { z } from 'zod';

const messageSchema = z.object({
  room: z.string().max(100),
  text: z.string().min(1).max(5000),
});

socket.on('message', (data, callback) => {
  const result = messageSchema.safeParse(data);
  if (!result.success) {
    callback({ error: result.error.issues[0].message });
    return;
  }
  // Process valid message
  io.to(result.data.room).emit('message', {
    userId: socket.user.id,
    text: result.data.text,
    timestamp: Date.now(),
  });
  callback({ status: 'ok' });
});
```

---

## Part 9: Message Types & Protocol

### 9.1 Message Format
```typescript
interface RealtimeMessage {
  type: string;          // 'message' | 'typing' | 'presence' | 'system'
  room?: string;         // Target room
  userId: string;        // Sender
  data: any;             // Payload
  timestamp: number;     // Server timestamp
  id: string;            // Unique message ID (for dedup)
}
```

### 9.2 Common Event Types
| Event | Direction | Description |
|---|---|---|
| `join` | Client → Server | Join a room |
| `leave` | Client → Server | Leave a room |
| `message` | Client → Server | Send a message |
| `message` | Server → Client | Receive a message |
| `typing` | Client → Server | Typing indicator |
| `typing` | Server → Client | Someone is typing |
| `presence` | Server → Client | User online/offline |
| `system` | Server → Client | System message |
| `error` | Server → Client | Error message |
| `ping`/`pong` | Bidirectional | Heartbeat |

---

## Execution Instructions for Cascade

When this skill is activated for real-time & WebSockets:

1. **Read the project context** — real-time needs, communication pattern, scale requirements
2. **Choose communication pattern** — WebSocket (bidirectional), SSE (server→client), WebRTC (P2P)
3. **Choose implementation** — Socket.io (recommended for production), native `ws` (simple), managed service (Pusher/Ably)
4. **Implement server** — connection handling, authentication, room management, message routing
5. **Implement client** — connection, reconnection with backoff, message queue, heartbeat
6. **Implement React integration** — custom hooks, state management for real-time data
7. **Implement presence** — online/offline status, typing indicators
8. **Implement security** — authentication, authorization, rate limiting, input validation
9. **Set up scaling** — Redis adapter for multi-server, sticky sessions, shared state
10. **Implement reliability** — reconnection, message queue, heartbeat, deduplication
11. **Test** — connection, disconnection, reconnection, concurrent users, message ordering
12. **Document** — protocol, message types, scaling strategy, security model
