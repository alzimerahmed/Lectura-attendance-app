---
name: real-time
description: Comprehensive real-time & WebSockets workflow — pattern selection, Socket.io, SSE, reconnection, presence, scaling, security, and testing
---

# Real-time & WebSockets Workflow

This workflow applies the **Real-time & WebSockets Skill** (`~/.codeium/windsurf/skills/real-time-websockets.md`) to implement reliable, scalable real-time communication.

## When to Run
- When implementing real-time features (chat, live updates, notifications)
- When the user says `/real-time` or asks about WebSockets
- When setting up Socket.io or Server-Sent Events
- When building presence/online status system
- When scaling WebSocket infrastructure

---

## Step 1: Assess Real-Time Needs

1. Read the project context — what needs to be real-time, communication pattern, scale
2. Determine communication direction: bidirectional (chat, collaboration) vs server→client (notifications, feeds)
3. Estimate concurrent connections — < 1K, 1K-10K, 10K-100K, > 100K
4. Identify message types: messages, typing, presence, system events
5. Determine if presence/online status is needed
6. Determine if reconnection and message queuing are needed
7. Check if real-time is critical or an enhancement (affects fallback strategy)

## Step 2: Choose Communication Pattern

1. **WebSocket:** Bidirectional, low-latency — chat, collaboration, gaming, live editing
2. **Server-Sent Events (SSE):** Server→client only — notifications, live feeds, AI streaming
3. **Long polling:** Fallback when WebSocket/SSE unavailable
4. **WebRTC:** Peer-to-peer — video/audio calls, P2P file sharing
5. **Streaming HTTP:** For AI token streaming or progressive responses
6. Don't use WebSockets if SSE suffices — SSE is simpler, uses standard HTTP, auto-reconnects

## Step 3: Choose Implementation

1. **Socket.io (recommended):** Auto-reconnection, fallback, rooms, acknowledgements, Redis adapter
2. **Native `ws`:** Lightweight, no extra dependencies — for simple use cases
3. **Managed service (Pusher/Ably):** No infrastructure management — for teams without WebSocket expertise
4. **Next.js custom server:** Socket.io with custom Next.js server (not serverless — WebSockets need persistent connection)
5. Install and configure chosen library

## Step 4: Implement Server

1. Set up WebSocket/SSE server (separate from HTTP API or integrated)
2. Implement authentication middleware — verify JWT from handshake
3. Implement connection handler — store user connection, join default rooms
4. Implement room management — join, leave, broadcast to room
5. Implement message routing — route messages to correct room/recipient
6. Implement disconnection handler — clean up rooms, update presence
7. Set up heartbeat/ping-pong — detect stale connections
8. Configure CORS — only allow connections from your domain

## Step 5: Implement Client

1. Create connection function with authentication token
2. Implement reconnection with exponential backoff (1s, 2s, 4s, 8s... max 30s)
3. Implement message queue — buffer messages while disconnected, flush on reconnect
4. Implement heartbeat — send ping every 25s to keep connection alive
5. Handle connection states: connecting, connected, disconnected, error
6. Show connection status to user — subtle indicator (online/offline/reconnecting)
7. Handle errors gracefully — show message, offer retry

## Step 6: Implement React Integration

1. Create `useSocket` hook — manages connection lifecycle
2. Create `useRealtimeEvent` hook — subscribes to specific event types
3. Create `usePresence` hook — tracks online users
4. Manage real-time state alongside server state (TanStack Query for initial data, WebSocket for updates)
5. Update TanStack Query cache from WebSocket events — `queryClient.setQueryData()`
6. Clean up listeners on unmount — `socket.off('event')`
7. Handle reconnection — rejoin rooms, re-fetch missed data

## Step 7: Implement Presence & Typing

1. Track online users in memory (single server) or Redis (multi-server)
2. Emit `presence` event when user comes online/goes offline
3. Show online status indicators in UI (green dot, "online" label)
4. Implement typing indicators — `typing` event with 3-second timeout
5. Show "X is typing..." in chat UI
6. Handle multiple devices — user may be online on multiple connections

## Step 8: Implement Security

1. **Authentication:** Verify JWT in WebSocket handshake or SSE request
2. **Authorization:** Check if user can access room before joining
3. **Rate limiting:** Limit messages per user per time window
4. **Input validation:** Validate all incoming messages with Zod schema
5. **Message size limit:** Reject oversized messages
6. **CORS:** Only allow connections from your domain
7. **WSS/TLS:** Always use encrypted WebSocket connections (wss://)
8. **Origin checking:** Verify Origin header to prevent CSRF

## Step 9: Set Up Scaling

1. **< 1K connections:** Single server with Socket.io — no special setup
2. **1K-10K connections:** Redis adapter for Socket.io — broadcast across servers
3. **Sticky sessions:** Configure load balancer with sticky sessions (IP hash or cookie)
4. **10K+ connections:** Dedicated WebSocket servers, separate from HTTP API
5. **Redis for shared state:** Room membership, presence, message pub/sub
6. **100K+ connections:** Consider managed service (Pusher, Ably) or dedicated infrastructure
7. **Monitor:** Track connection count, message rate, memory usage per server

## Step 10: Implement Reliability

1. **Reconnection:** Exponential backoff with jitter (1s → 2s → 4s → 8s → max 30s)
2. **Message queue:** Buffer outgoing messages while disconnected, flush on reconnect
3. **Message dedup:** Use message IDs to handle duplicate delivery
4. **Heartbeat:** Ping/pong every 25-30s to detect stale connections
5. **Graceful degradation:** If WebSocket fails, fall back to polling or show "reconnecting"
6. **Connection state UI:** Show online/offline/reconnecting status to user
7. **Missed messages:** On reconnect, fetch missed messages from REST API

## Step 11: Test Real-Time Features

1. Test connection — connect, authenticate, join room
2. Test messaging — send, receive, broadcast to room
3. Test disconnection — close tab, lose network, server restart
4. Test reconnection — verify auto-reconnect with backoff
5. Test message queue — send while offline, verify delivery on reconnect
6. Test presence — user online/offline transitions
7. Test typing indicators — start/stop typing, timeout
8. Test concurrent users — multiple users in same room
9. Test message ordering — verify messages arrive in order
10. Test rate limiting — verify limits enforced
11. Test scaling — multiple server instances with Redis adapter

## Step 12: Document & Monitor

1. Document communication protocol — message types, format, events
2. Document room structure — how rooms are named, who can join
3. Document scaling strategy — Redis adapter, sticky sessions, dedicated servers
4. Document security model — authentication, authorization, rate limiting
5. Set up monitoring — connection count, message rate, error rate, latency
6. Set up alerts — connection drops, error spikes, server memory
7. Document fallback strategy — what happens when WebSocket fails
