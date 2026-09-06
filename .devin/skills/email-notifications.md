---
name: Email & Notifications Skill
description: Comprehensive methodology for email and notifications — 2025-2026 practices with transactional email, React Email templates, Resend/SendGrid, push notifications, in-app notifications, and delivery
version: 1.0.0
tags: [email, notifications, transactional-email, react-email, resend, sendgrid, push-notifications, web-push, in-app-notifications, sms, twilio]
---

# Email & Notifications Skill

## Purpose
This skill provides a comprehensive methodology for implementing email and notification systems across any kind of web project. It reflects **modern 2025-2026 practices** — React Email for templating, Resend for transactional email, Web Push API for browser notifications, in-app notification centers, SMS via Twilio, and unified notification preferences.

## Core Philosophy

**Email is a product surface, not an afterthought.** Every email a user receives shapes their perception of your product. Design emails with the same care as your UI — responsive, accessible, on-brand, and actionable. A well-designed email drives engagement; a poorly designed one drives unsubscribes.

**The #1 rule:** Never send emails synchronously in request handlers. Email delivery is slow and unreliable — it should always be asynchronous via a queue. Use a background job system (BullMQ, Inngest, Trigger.dev) to process emails, with retries, rate limiting, and dead-letter handling. A user's request should never fail because the email server is down.

---

## Part 1: Email Service Providers

### 1.1 Comparison (2025-2026)

| Provider | Best For | Pricing | Features |
|---|---|---|---|
| **Resend** | Modern apps, React Email | $0/3K mo, then $20/50K | DX, React Email, webhooks |
| **SendGrid** | Enterprise, high volume | $19.95/50K mo | Mature, templates, analytics |
| **Postmark** | Transactional only | $15/10K mo | High deliverability, fast |
| **Amazon SES** | High volume, low cost | $0.10/1K mo | Cheap, raw API |
| **Mailgun** | Developers | $15/10K mo | API-first, good docs |
| **Plunk** | Open-source | $9/2K mo | Self-hostable, open-source |

### 1.2 Decision Matrix

| Use Case | Recommended |
|---|---|
| **Modern app (2025+)** | Resend + React Email |
| **Enterprise / high volume** | SendGrid or Amazon SES |
| **Maximum deliverability** | Postmark |
| **Low budget, high volume** | Amazon SES |
| **Self-hosted** | Plunk |
| **Marketing + transactional** | SendGrid or Mailchimp |

---

## Part 2: React Email Templates

### 2.1 Why React Email
- **Component-based:** Build emails with React components, not HTML strings
- **Preview:** Live preview in browser and email clients
- **Type-safe:** TypeScript for props and data
- **Reusable:** Share components across email templates
- **Testing:** Visual testing with Storybook-like preview

### 2.2 Setup
```bash
npm install @react-email/components resend
```

### 2.3 Template Example
```tsx
import { Html, Head, Preview, Body, Container, Section, Heading, Text, Button, Hr, Link } from '@react-email/components';

interface WelcomeEmailProps {
  name: string;
  loginUrl: string;
  supportUrl: string;
}

export function WelcomeEmail({ name, loginUrl, supportUrl }: WelcomeEmailProps) {
  return (
    <Html>
      <Head />
      <Preview>Welcome to our platform — let's get started!</Preview>
      <Body style={{ fontFamily: 'Inter, sans-serif', backgroundColor: '#f6f9fc', padding: '20px' }}>
        <Container style={{ backgroundColor: '#ffffff', borderRadius: '8px', padding: '40px', maxWidth: '600px' }}>
          <Heading style={{ fontSize: '24px', fontWeight: '700', color: '#1a1a1a' }}>
            Welcome, {name}!
          </Heading>
          <Text style={{ fontSize: '16px', color: '#4a4a4a', lineHeight: '1.6' }}>
            Thanks for signing up. We're excited to have you on board.
            Here's what you can do next:
          </Text>
          <Section style={{ margin: '32px 0' }}>
            <Button
              href={loginUrl}
              style={{ backgroundColor: '#6366f1', borderRadius: '6px', color: '#ffffff', padding: '12px 24px', textDecoration: 'none', fontWeight: '600' }}
            >
              Get Started
            </Button>
          </Section>
          <Hr style={{ borderColor: '#e6e6e6', margin: '32px 0' }} />
          <Text style={{ fontSize: '14px', color: '#888888' }}>
            Need help? <Link href={supportUrl}>Contact support</Link>
          </Text>
          <Text style={{ fontSize: '12px', color: '#aaaaaa', marginTop: '16px' }}>
            © 2025 Company Name. All rights reserved.
          </Text>
        </Container>
      </Body>
    </Html>
  );
}
```

### 2.4 Email Preview
```tsx
// app/emails/preview/welcome/page.tsx
import { WelcomeEmail } from '@/emails/welcome';

export default function PreviewPage() {
  return (
    <WelcomeEmail
      name="Alice"
      loginUrl="https://example.com/login"
      supportUrl="https://example.com/support"
    />
  );
}
```

---

## Part 3: Sending Email with Resend

### 3.1 Setup
```typescript
import { Resend } from 'resend';

const resend = new Resend(process.env.RESEND_API_KEY);

// Verify domain in Resend dashboard
// Add DNS records: SPF, DKIM, DMARC
```

### 3.2 Send Transactional Email
```typescript
import { WelcomeEmail } from '@/emails/welcome';
import { render } from '@react-email/render';

async function sendWelcomeEmail(to: string, name: string) {
  const html = await render(WelcomeEmail({ name, loginUrl: 'https://example.com/login', supportUrl: 'https://example.com/support' }));

  const { data, error } = await resend.emails.send({
    from: 'Welcome <welcome@yourdomain.com>',
    to,
    subject: 'Welcome to Our Platform!',
    html,
  });

  if (error) {
    console.error('Email send failed:', error);
    throw error;
  }

  return data;
}
```

### 3.3 Send with React Email Directly
```typescript
import { Resend } from 'resend';
import { WelcomeEmail } from '@/emails/welcome';

const resend = new Resend(process.env.RESEND_API_KEY);

const { data, error } = await resend.emails.send({
  from: 'Welcome <welcome@yourdomain.com>',
  to: 'user@example.com',
  subject: 'Welcome to Our Platform!',
  react: WelcomeEmail({ name: 'Alice', loginUrl: '...', supportUrl: '...' }),
});
```

---

## Part 4: Async Email Queue

### 4.1 Why Async
- **Non-blocking:** Don't slow down user request
- **Retries:** Automatically retry failed sends
- **Rate limiting:** Respect provider limits
- **Batching:** Batch emails for efficiency
- **Dead letter:** Handle permanently failed emails

### 4.2 With Inngest
```typescript
import { inngest } from '@/lib/inngest';
import { resend } from '@/lib/resend';
import { WelcomeEmail } from '@/emails/welcome';

export const sendWelcomeEmail = inngest.createFunction(
  { id: 'send-welcome-email', retries: 3 },
  { event: 'user.created' },
  async ({ event, step }) => {
    const { userId, email, name } = event.data;

    await step.run('send-email', async () => {
      const { data, error } = await resend.emails.send({
        from: 'Welcome <welcome@yourdomain.com>',
        to: email,
        subject: 'Welcome to Our Platform!',
        react: WelcomeEmail({ name, loginUrl: 'https://example.com/login', supportUrl: 'https://example.com/support' }),
      });

      if (error) throw error;
      return data;
    });
  }
);
```

### 4.3 With BullMQ
```typescript
import { Queue, Worker } from 'bullmq';
import { resend } from '@/lib/resend';

const emailQueue = new Queue('email', { connection: redis });

// Add job
async function enqueueEmail(to: string, template: string, data: any) {
  await emailQueue.add('send', { to, template, data }, {
    attempts: 3,
    backoff: { type: 'exponential', delay: 5000 },
  });
}

// Worker
const worker = new Worker('email', async (job) => {
  const { to, template, data } = job.data;
  const html = await renderEmail(template, data);

  await resend.emails.send({
    from: 'noreply@yourdomain.com',
    to,
    subject: data.subject,
    html,
  });
}, { connection: redis });
```

---

## Part 5: Email Types & Templates

### 5.1 Transactional Emails
| Email | Trigger | Content |
|---|---|---|
| **Welcome** | User signup | Greeting, next steps, CTA |
| **Email verification** | Signup, email change | Verification link |
| **Password reset** | Forgot password | Reset link (expires in 1h) |
| **Receipt** | Purchase | Order details, amount, items |
| **Invoice** | Subscription billing | Invoice PDF, amount, period |
| **Trial expiring** | 3 days before trial ends | Upgrade CTA |
| **Subscription canceled** | Cancellation | Confirmation, rejoin CTA |
| **Account deletion** | Account deleted | Confirmation, data retention info |

### 5.2 Notification Emails
| Email | Trigger | Content |
|---|---|---|
| **Weekly digest** | Scheduled | Activity summary, stats |
| **Mention** | User mentioned | Content, link to conversation |
| **Reply** | Someone replied | Reply content, link |
| **New feature** | Feature launched | Feature description, CTA |
| **Re-engagement** | Inactive user | Come back CTA, what's new |

### 5.3 Email Best Practices
- **From name:** Use a human name, not "noreply"
- **Subject line:** Clear, concise, < 50 characters
- **Preview text:** First sentence visible in inbox — make it compelling
- **Single CTA:** One primary action per email
- **Mobile-first:** 60% of emails are opened on mobile
- **Plain text fallback:** Always include text version
- **Unsubscribe:** Required by law (CAN-SPAM, GDPR)
- **DKIM/SPF/DMARC:** Set up email authentication to prevent spoofing

---

## Part 6: Push Notifications

### 6.1 Web Push Setup
```typescript
// 1. Request permission
const permission = await Notification.requestPermission();
if (permission !== 'granted') return;

// 2. Subscribe to push
const registration = await navigator.serviceWorker.ready;
const subscription = await registration.pushManager.subscribe({
  userVisibleOnly: true,
  applicationServerKey: process.env.NEXT_PUBLIC_VAPID_PUBLIC_KEY,
});

// 3. Send subscription to server
await fetch('/api/push/subscribe', {
  method: 'POST',
  body: JSON.stringify(subscription),
});
```

### 6.2 Server-Side Push
```typescript
import webpush from 'web-push';

webpush.setVapidDetails(
  'mailto:admin@yourdomain.com',
  process.env.VAPID_PUBLIC_KEY!,
  process.env.VAPID_PRIVATE_KEY!
);

async function sendPushNotification(subscription: PushSubscription, payload: any) {
  await webpush.sendNotification(
    subscription,
    JSON.stringify(payload),
    {
      headers: { TTL: '86400' }, // 24 hours
    }
  );
}

// Usage
await sendPushNotification(subscription, {
  title: 'New message',
  body: 'Alice sent you a message',
  icon: '/icon-192.png',
  badge: '/badge-72.png',
  data: { url: 'https://example.com/messages' },
  actions: [
    { action: 'reply', title: 'Reply' },
    { action: 'dismiss', title: 'Dismiss' },
  ],
});
```

### 6.3 Service Worker Handler
```javascript
// sw.js
self.addEventListener('push', (event) => {
  const data = event.data.json();
  event.waitUntil(
    self.registration.showNotification(data.title, {
      body: data.body,
      icon: data.icon,
      badge: data.badge,
      data: data.data,
      actions: data.actions,
    })
  );
});

self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  const url = event.notification.data.url;
  event.waitUntil(clients.openWindow(url));
});
```

---

## Part 7: In-App Notifications

### 7.1 Notification Model
```typescript
interface Notification {
  id: string;
  userId: string;
  type: 'message' | 'mention' | 'reply' | 'system' | 'alert';
  title: string;
  body: string;
  link?: string;
  read: boolean;
  createdAt: Date;
  metadata?: Record<string, unknown>;
}
```

### 7.2 Notification Center Component
```tsx
function NotificationCenter({ userId }: { userId: string }) {
  const { notifications, unreadCount, markAsRead, markAllAsRead } = useNotifications(userId);

  return (
    <div className="absolute right-0 mt-2 w-80 rounded-lg border bg-white shadow-lg">
      <div className="flex items-center justify-between border-b p-3">
        <h3 className="font-semibold">Notifications</h3>
        {unreadCount > 0 && (
          <button onClick={markAllAsRead} className="text-sm text-blue-600">
            Mark all read
          </button>
        )}
      </div>
      <div className="max-h-96 overflow-y-auto">
        {notifications.map(notif => (
          <NotificationItem
            key={notif.id}
            notification={notif}
            onClick={() => markAsRead(notif.id)}
          />
        ))}
      </div>
    </div>
  );
}
```

### 7.3 Real-Time Notifications
```typescript
// Push notification via WebSocket
socket.on('notification', (notification: Notification) => {
  // Update notification list
  queryClient.setQueryData(['notifications'], (old) => [notification, ...old]);

  // Show toast
  toast(notification.title, { description: notification.body });

  // Update unread badge
  incrementUnreadCount();
});
```

---

## Part 8: SMS Notifications

### 8.1 Twilio Setup
```typescript
import twilio from 'twilio';

const client = twilio(
  process.env.TWILIO_ACCOUNT_SID,
  process.env.TWILIO_AUTH_TOKEN
);

async function sendSMS(to: string, body: string) {
  const message = await client.messages.create({
    from: process.env.TWILIO_PHONE_NUMBER!,
    to,
    body,
  });

  return message;
}

// Usage
await sendSMS('+1234567890', 'Your verification code is 123456. It expires in 10 minutes.');
```

### 8.2 SMS Use Cases
- **2FA codes:** Verification codes for authentication
- **Critical alerts:** Server down, security breach
- **Appointment reminders:** 24h before appointment
- **Delivery updates:** Order shipped, out for delivery
- **Emergency notifications:** Account locked, suspicious activity

---

## Part 9: Notification Preferences

### 9.1 User Preferences Model
```typescript
interface NotificationPreferences {
  userId: string;
  email: {
    welcome: boolean;
    receipts: boolean;
    productUpdates: boolean;
    marketing: boolean;
    security: boolean; // Always true — can't disable
  };
  push: {
    messages: boolean;
    mentions: boolean;
    replies: boolean;
    system: boolean;
  };
  sms: {
    security: boolean;
    criticalAlerts: boolean;
  };
  digest: {
    enabled: boolean;
    frequency: 'daily' | 'weekly' | 'monthly';
  };
}
```

### 9.2 Preferences UI
```tsx
function NotificationSettings() {
  const [prefs, setPrefs] = useNotificationPreferences();

  return (
    <div className="space-y-6">
      <section>
        <h3>Email Notifications</h3>
        <Toggle
          label="Receipts and invoices"
          checked={prefs.email.receipts}
          onChange={(v) => setPrefs({ ...prefs, email: { ...prefs.email, receipts: v } })}
        />
        <Toggle
          label="Product updates"
          checked={prefs.email.productUpdates}
          onChange={(v) => setPrefs({ ...prefs, email: { ...prefs.email, productUpdates: v } })}
        />
        <Toggle
          label="Security alerts"
          checked={prefs.email.security}
          disabled // Can't disable security alerts
          description="Required for account safety"
        />
      </section>

      <section>
        <h3>Push Notifications</h3>
        <Toggle label="Messages" checked={prefs.push.messages} onChange={...} />
        <Toggle label="Mentions" checked={prefs.push.mentions} onChange={...} />
      </section>
    </div>
  );
}
```

---

## Part 10: Email Authentication & Deliverability

### 10.1 DNS Records
```
# SPF — authorizes sending server
yourdomain.com. TXT "v=spf1 include:_spf.resend.com ~all"

# DKIM — signs emails cryptographically
resend._domainkey.yourdomain.com. TXT "v=DKIM1; k=rsa; p=..."

# DMARC — policy for failed authentication
_dmarc.yourdomain.com. TXT "v=DMARC1; p=quarantine; rua=mailto:dmarc@yourdomain.com"

# MX — for receiving replies (if needed)
yourdomain.com. MX 10 mail.yourdomain.com.
```

### 10.2 Deliverability Best Practices
- **Authenticate:** SPF, DKIM, DMARC — all three required
- **Warm up:** Gradually increase volume for new domains
- **Monitor bounce rate:** Keep < 5% — high bounce hurts reputation
- **Monitor complaint rate:** Keep < 0.1% — unsubscribes are better than spam reports
- **Clean list:** Remove hard bounces and inactive subscribers
- **Use double opt-in:** Prevents spam traps and fake signups
- **Honor unsubscribes:** Within 10 business days (CAN-SPAM requirement)
- **Test rendering:** Use Email on Acid or Litmus for cross-client testing

---

## Execution Instructions for Cascade

When this skill is activated for email & notifications:

1. **Read the project context** — email volume, notification types, user preferences
2. **Choose email provider** — Resend (modern), SendGrid (enterprise), Postmark (deliverability)
3. **Set up React Email** — install, create templates, set up preview
4. **Set up async email queue** — Inngest, BullMQ, or Trigger.dev for background processing
5. **Create email templates** — welcome, verification, password reset, receipts, invoices
6. **Set up email authentication** — SPF, DKIM, DMARC DNS records
7. **Implement push notifications** — Web Push API, VAPID keys, service worker
8. **Implement in-app notifications** — notification model, center UI, real-time updates
9. **Implement SMS** — Twilio for 2FA, critical alerts, reminders
10. **Create notification preferences** — per-user, per-channel, per-type settings
11. **Test email deliverability** — cross-client rendering, spam score, bounce handling
12. **Document** — email templates, notification types, preference model, provider config
