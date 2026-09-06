---
name: email
description: Comprehensive email & notifications workflow — provider selection, React Email templates, async queue, push notifications, in-app notifications, SMS, preferences, and deliverability
---

# Email & Notifications Workflow

This workflow applies the **Email & Notifications Skill** (`~/.codeium/windsurf/skills/email-notifications.md`) to implement a complete notification system.

## When to Run
- When implementing email functionality
- When the user says `/email` or asks about notifications
- When setting up push notifications or in-app notifications
- When configuring SMS notifications
- When building notification preferences

---

## Step 1: Choose Email Provider

1. Read the project context — email volume, budget, deliverability needs
2. **Resend (recommended for modern apps):** Best DX, React Email integration, webhooks
3. **SendGrid (enterprise):** Mature, high volume, templates, analytics
4. **Postmark (maximum deliverability):** Transactional only, fast, high deliverability
5. **Amazon SES (low cost, high volume):** Cheapest, raw API, more setup required
6. Sign up and get API key
7. Verify sending domain in provider dashboard
8. Add DNS records: SPF, DKIM, DMARC

## Step 2: Set Up React Email Templates

1. Install: `npm install @react-email/components`
2. Create `emails/` directory in project
3. Create templates for each email type:
   - `welcome.tsx` — Welcome email
   - `verification.tsx` — Email verification
   - `password-reset.tsx` — Password reset
   - `receipt.tsx` — Purchase receipt
   - `invoice.tsx` — Billing invoice
   - `digest.tsx` — Weekly/monthly digest
4. Use React Email components: `Html`, `Body`, `Container`, `Button`, `Text`, `Heading`
5. Style with inline styles (email clients don't support CSS classes)
6. Set up preview pages at `/emails/preview/[template]`
7. Test rendering in multiple email clients (Gmail, Outlook, Apple Mail)

## Step 3: Set Up Async Email Queue

1. Never send emails synchronously in request handlers
2. Choose a job queue: Inngest (managed), BullMQ (Redis-based), Trigger.dev (managed)
3. Create email job handler:
   - Receive: to, template name, data
   - Render: React Email template to HTML
   - Send: via Resend/SendGrid API
   - Retry: 3 attempts with exponential backoff
   - Dead letter: log permanently failed emails
4. Enqueue from application code: `await emailQueue.add({ to, template, data })`
5. Monitor queue: pending jobs, failed jobs, processing time

## Step 4: Create Email Templates

1. **Welcome email:** Triggered on user signup — greeting, next steps, CTA
2. **Email verification:** Triggered on signup/email change — verification link (expires 1h)
3. **Password reset:** Triggered on forgot password — reset link (expires 1h)
4. **Receipt:** Triggered on purchase — order details, amount, items
5. **Invoice:** Triggered on subscription billing — invoice PDF, amount, period
6. **Trial expiring:** Triggered 3 days before trial ends — upgrade CTA
7. **Subscription canceled:** Triggered on cancellation — confirmation, rejoin CTA
8. Each template: mobile-first, single CTA, plain text fallback, unsubscribe link

## Step 5: Set Up Email Authentication

1. **SPF record:** Authorize sending server — `v=spf1 include:_spf.resend.com ~all`
2. **DKIM record:** Cryptographic signing — provider-specific key
3. **DMARC record:** Policy for failed auth — `v=DMARC1; p=quarantine; rua=mailto:dmarc@domain.com`
4. Verify all records in provider dashboard
5. Monitor: bounce rate (< 5%), complaint rate (< 0.1%), delivery rate
6. Warm up new domains: gradually increase volume over 2-4 weeks
7. Test deliverability: send to mail-tester.com, check spam score

## Step 6: Set Up Push Notifications

1. Generate VAPID keys: `npx web-push generate-vapid-keys`
2. Store keys in environment variables
3. Request notification permission: `Notification.requestPermission()`
4. Subscribe to push: `pushManager.subscribe({ userVisibleOnly: true, applicationServerKey })`
5. Send subscription to server: store in database per user
6. Set up service worker: handle `push` and `notificationclick` events
7. Server-side sending: use `web-push` library to send to subscriptions
8. Handle subscription expiration: remove invalid subscriptions
9. Test: send test notification, verify delivery and click handling

## Step 7: Implement In-App Notifications

1. Create notification database model: id, userId, type, title, body, link, read, createdAt
2. Create API endpoints: list, mark as read, mark all as read, delete
3. Create notification center UI: bell icon with unread badge, dropdown panel
4. Show notifications in real-time via WebSocket: `socket.on('notification', ...)`
5. Show toast for new notifications: `toast(title, { description: body })`
6. Update TanStack Query cache from WebSocket events
7. Pagination for notification list (cursor-based)
8. Auto-mark as read when clicked

## Step 8: Implement SMS Notifications

1. Sign up for Twilio: get account SID, auth token, phone number
2. Install: `npm install twilio`
3. Create SMS sending function with error handling
4. Use cases: 2FA codes, critical alerts, appointment reminders, delivery updates
5. Always include opt-out instructions for marketing messages
6. Rate limit: respect Twilio's rate limits
7. Log all SMS sends for audit trail
8. Test: send test SMS, verify delivery

## Step 9: Create Notification Preferences

1. Create preferences model: per-user, per-channel (email, push, SMS), per-type
2. Security notifications: always enabled — user can't disable
3. Create preferences UI: toggles for each notification type and channel
4. Save preferences to database
5. Check preferences before sending any notification:
   - If user disabled email for "productUpdates", don't send
   - Always send security alerts regardless of preferences
6. Create digest preferences: daily, weekly, monthly, or disabled
7. Test: verify preferences are respected when sending notifications

## Step 10: Test Email & Notifications

1. **Email rendering:** Test in Gmail, Outlook, Apple Mail, Yahoo
2. **Spam score:** Send to mail-tester.com, aim for 9/10 or higher
3. **Deliverability:** Check SPF, DKIM, DMARC are passing
4. **Push notifications:** Test on Chrome, Firefox, Safari, mobile browsers
5. **In-app notifications:** Test real-time delivery, read/unread states
6. **SMS:** Test delivery, verify content and opt-out
7. **Preferences:** Verify disabled notifications are not sent
8. **Queue:** Test retry on failure, dead letter handling
9. **Load test:** Send batch of 1000 emails, verify queue handles it

## Step 11: Document & Monitor

1. Document email templates: when sent, content, trigger
2. Document notification types: email, push, in-app, SMS — when and how
3. Document preference model: which notifications can be disabled
4. Document provider configuration: API keys, domain verification, DNS records
5. Set up monitoring: delivery rate, bounce rate, complaint rate, open rate, click rate
6. Set up alerts: bounce rate > 5%, complaint rate > 0.1%, queue backlog
7. Log all sends: recipient, template, status, timestamp
8. Regular deliverability audit: check DNS records, sender reputation, spam score
