# Rule: Real-time & WebSockets for All Projects

**ALWAYS** apply the Real-time & WebSockets skill and workflow when implementing real-time communication. Don't use WebSockets for everything — choose the right pattern for the communication need.

## Skill
`~/.codeium/windsurf/skills/real-time-websockets.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/real-time.md` — invoke with `/real-time`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/realtime-engineer.md` (parent: Feature Engineer)

## How to follow this rule:
1. When implementing real-time features, invoke the `/real-time` workflow
2. Follow the workflow steps in order: Assess → Pattern → Implementation → Server → Client → React → Presence → Security → Scaling → Reliability → Test → Document
3. Always choose the right pattern — WebSocket (bidirectional), SSE (server→client), WebRTC (P2P)
4. Always use Socket.io for production WebSocket implementations — auto-reconnection, fallback, rooms
5. Always implement reconnection with exponential backoff and message queue for offline buffering
6. Always authenticate WebSocket connections — verify JWT in handshake
7. Always implement heartbeat/ping-pong to detect stale connections
8. Always use Redis adapter for multi-server scaling — never rely on single-server in-memory state

## When this rule applies:
- Implementing chat, messaging, or live updates
- Setting up Socket.io or Server-Sent Events
- Building presence/online status system
- Scaling WebSocket infrastructure
- User asks about real-time or WebSockets

## When this rule does NOT apply:
- Projects with no real-time requirements
- User explicitly says to skip real-time setup
