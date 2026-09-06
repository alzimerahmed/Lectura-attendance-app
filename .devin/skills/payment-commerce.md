---
name: Payment & Commerce Skill
description: Comprehensive methodology for implementing payment processing and e-commerce functionality — 2025-2026 practices with Stripe, PayPal, Apple Pay, Google Pay, PCI compliance, subscriptions, and fraud prevention
version: 1.0.0
tags: [payment, commerce, stripe, paypal, apple-pay, google-pay, pci-compliance, subscriptions, fraud-prevention, checkout, e-commerce]
---

# Payment & Commerce Skill

## Purpose
This skill provides a comprehensive methodology for implementing payment processing and e-commerce functionality across any kind of web project. It reflects **modern 2025-2026 practices** — Stripe as the default payment processor, Stripe Elements for secure card input, Apple Pay and Google Pay for one-tap checkout, subscription billing with Stripe Billing, PCI DSS compliance, and fraud prevention with Stripe Radar.

## Core Philosophy

**Never touch card data.** The #1 rule of payment processing: let the payment processor handle card data. Use Stripe Elements, Stripe Checkout, or payment sheets that render in an iframe controlled by the processor. Your servers should never see, store, or transmit card numbers. This keeps you at PCI DSS SAQ-A (the simplest compliance level).

**The #1 UX rule:** Reduce checkout friction. Every additional step in checkout reduces conversion. Offer guest checkout, one-tap payments (Apple Pay/Google Pay), saved payment methods, and minimal form fields. The best checkout is the one with the fewest steps.

---

## Part 1: Payment Processor Selection

### 1.1 Stripe (Recommended Default)
- **Market leader:** Most popular, best documentation, widest feature set
- **Stripe Elements:** Pre-built, customizable, PCI-compliant card input
- **Stripe Checkout:** Hosted checkout page — zero PCI scope
- **Payment Methods:** Cards, Apple Pay, Google Pay, ACH, SEPA, Klarna, Afterpay, etc.
- **Subscriptions:** Stripe Billing for recurring payments
- **Connect:** Marketplace/split payments
- **Radar:** Fraud detection built-in
- **Pricing:** 2.9% + 30¢ per transaction (US), varies by country

### 1.2 PayPal
- **Consumer trust:** Widely recognized, many users have PayPal balance
- **PayPal Checkout:** Hosted buttons and checkout
- **Venmo:** Popular among US millennials/Gen Z
- **Pricing:** 3.49% + fixed fee per transaction

### 1.3 Square
- **Omnichannel:** Good for businesses with online + in-person
- **Square Online:** E-commerce platform
- **Pricing:** 2.9% + 30¢ per transaction

### 1.4 Adyen
- **Enterprise:** Global, multi-acquirer, many payment methods
- **Best for:** Large businesses with international presence
- **Pricing:** Custom (interchange-plus)

### 1.5 Lemon Squeezy / Paddle
- **Merchant of Record:** They handle tax compliance globally
- **Best for:** Digital products, SaaS, solo developers
- **Tax handling:** VAT, GST, sales tax automatically calculated and remitted
- **Pricing:** ~5% + 50¢ per transaction

### 1.6 Decision Matrix

| Need | Processor |
|---|---|
| General e-commerce | Stripe |
| Maximum payment methods | Stripe or Adyen |
| Subscriptions/SaaS | Stripe Billing or Paddle |
| Digital products (tax handled) | Lemon Squeezy or Paddle |
| Marketplace/split payments | Stripe Connect |
| PayPal support | Stripe + PayPal or PayPal separately |
| Enterprise/global | Adyen |
| Simple, no server needed | Stripe Payment Links |

---

## Part 2: Stripe Integration

### 2.1 Stripe Elements (Custom Checkout)
```tsx
// Client-side — Stripe Elements for card input
import { Elements, CardElement, useElements, useStripe } from '@stripe/react-stripe-js';
import { loadStripe } from '@stripe/stripe-js';

const stripePromise = loadStripe(process.env.NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY!);

function CheckoutForm() {
  const stripe = useStripe();
  const elements = useElements();

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!stripe || !elements) return;

    // Create payment method
    const { error, paymentMethod } = await stripe.createPaymentMethod({
      type: 'card',
      card: elements.getElement(CardElement)!,
    });

    if (error) {
      console.error(error);
      return;
    }

    // Send payment method ID to server — never send card data
    const response = await fetch('/api/payment', {
      method: 'POST',
      body: JSON.stringify({ paymentMethodId: paymentMethod.id }),
    });
    const result = await response.json();
    // Handle result
  };

  return (
    <form onSubmit={handleSubmit}>
      <CardElement options={{ style: { base: { fontSize: '16px' } } }} />
      <button type="submit" disabled={!stripe}>Pay</button>
    </form>
  );
}

function Checkout() {
  return (
    <Elements stripe={stripePromise}>
      <CheckoutForm />
    </Elements>
  );
}
```

### 2.2 Stripe Checkout (Hosted — Simplest)
```tsx
// Server-side — create checkout session
import Stripe from 'stripe';
const stripe = new Stripe(process.env.STRIPE_SECRET_KEY!);

export async function POST(request) {
  const { items } = await request.json();

  const session = await stripe.checkout.sessions.create({
    mode: 'payment',
    line_items: items.map(item => ({
      price_data: {
        currency: 'usd',
        product_data: { name: item.name },
        unit_amount: item.price * 100, // Stripe uses cents
      },
      quantity: item.quantity,
    })),
    success_url: 'https://example.com/success?session_id={CHECKOUT_SESSION_ID}',
    cancel_url: 'https://example.com/cancel',
    automatic_tax: { enabled: true },
  });

  return Response.json({ url: session.url });
}

// Client-side — redirect to Checkout
async function checkout() {
  const { url } = await fetch('/api/checkout', { method: 'POST', body: JSON.stringify({ items }) }).then(r => r.json());
  window.location.href = url;
}
```

### 2.3 Payment Element (Unified — All Methods)
```tsx
// Payment Element supports cards, Apple Pay, Google Pay, etc. in one element
import { Elements, PaymentElement, useElements, useStripe } from '@stripe/react-stripe-js';

function CheckoutForm() {
  const stripe = useStripe();
  const elements = useElements();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!stripe || !elements) return;

    const { error } = await stripe.confirmPayment({
      elements,
      confirmParams: {
        return_url: 'https://example.com/payment-result',
      },
    });

    if (error) console.error(error);
  };

  return (
    <form onSubmit={handleSubmit}>
      <PaymentElement />
      <button type="submit">Pay</button>
    </form>
  );
}

// Server — create PaymentIntent
const paymentIntent = await stripe.paymentIntents.create({
  amount: 5000, // $50.00 in cents
  currency: 'usd',
  automatic_payment_methods: { enabled: true },
});
// Return client_secret to client
```

### 2.4 Server-Side Payment Processing
```typescript
// api/payment/route.ts
import Stripe from 'stripe';
const stripe = new Stripe(process.env.STRIPE_SECRET_KEY!);

export async function POST(request: Request) {
  const { paymentMethodId, amount, orderId } = await request.json();

  try {
    const paymentIntent = await stripe.paymentIntents.create({
      amount: Math.round(amount * 100), // Convert to cents
      currency: 'usd',
      payment_method: paymentMethodId,
      confirm: true,
      metadata: { orderId },
      automatic_payment_methods: { enabled: true, allow_redirects: 'never' },
    });

    if (paymentIntent.status === 'succeeded') {
      // Fulfill order — update database, send confirmation email
      await fulfillOrder(orderId);
      return Response.json({ success: true, paymentIntent });
    }

    // Handle additional actions (3DS authentication)
    if (paymentIntent.status === 'requires_action') {
      return Response.json({
        requiresAction: true,
        clientSecret: paymentIntent.client_secret,
      });
    }

    return Response.json({ success: false, status: paymentIntent.status });
  } catch (error) {
    return Response.json({ error: error.message }, { status: 400 });
  }
}
```

---

## Part 3: Apple Pay & Google Pay

### 3.1 Stripe Payment Sheet (Mobile + Web)
```tsx
// Stripe handles Apple Pay and Google Pay through Payment Element
// When using Payment Element with automatic_payment_methods, Apple Pay and Google Pay
// are automatically available if the device supports them

// For explicit Apple Pay button:
import { useStripe, PaymentRequestButtonElement } from '@stripe/react-stripe-js';

function ApplePayButton() {
  const stripe = useStripe();

  const paymentRequest = stripe?.paymentRequest({
    country: 'US',
    currency: 'usd',
    total: { label: 'Total', amount: 5000 },
    requestPayerName: true,
    requestPayerEmail: true,
  });

  if (paymentRequest) {
    paymentRequest.on('paymentmethod', async (event) => {
      // Process payment on server
      const response = await fetch('/api/payment', {
        method: 'POST',
        body: JSON.stringify({ paymentMethodId: event.paymentMethod.id }),
      });
      const result = await response.json();

      if (result.success) {
        event.complete('success');
      } else {
        event.complete('fail');
      }
    });
  }

  return paymentRequest && <PaymentRequestButtonElement options={{ paymentRequest }} />;
}
```

### 3.2 Apple Pay on the Web (Direct)
```typescript
// Check if Apple Pay is available
if (window.ApplePaySession && ApplePaySession.canMakePayments()) {
  // Show Apple Pay button
}

// Create Apple Pay session
const session = new ApplePaySession(3, {
  countryCode: 'US',
  currencyCode: 'USD',
  supportedNetworks: ['visa', 'masterCard', 'amex'],
  merchantCapabilities: ['supports3DS'],
  total: { label: 'My Store', type: 'final', amount: '50.00' },
});
```

### 3.3 Google Pay (Direct)
```typescript
const googlePayClient = new google.payments.api.PaymentsClient({
  environment: 'PRODUCTION',
});

const paymentDataRequest = {
  apiVersion: 2,
  apiVersionMinor: 0,
  allowedPaymentMethods: [{
    type: 'CARD',
    parameters: { allowedAuthMethods: ['PAN_ONLY', 'CRYPTOGRAM_3DS'], allowedCardNetworks: ['VISA', 'MASTERCARD'] },
    tokenizationSpecification: { type: 'PAYMENT_GATEWAY', parameters: { gateway: 'stripe', gatewayMerchantId: '...' } },
  }],
  merchantInfo: { merchantName: 'My Store' },
  transactionInfo: { totalPriceStatus: 'FINAL', totalPrice: '50.00', currencyCode: 'USD' },
};
```

---

## Part 4: Subscriptions & Recurring Billing

### 4.1 Stripe Billing Setup
```typescript
// Create product and price in Stripe Dashboard or via API
const product = await stripe.products.create({
  name: 'Pro Plan',
  description: 'Access to all premium features',
});

const price = await stripe.prices.create({
  product: product.id,
  unit_amount: 1999, // $19.99/month
  currency: 'usd',
  recurring: { interval: 'month' },
});

// Create subscription
const subscription = await stripe.subscriptions.create({
  customer: customerId,
  items: [{ price: price.id }],
  payment_behavior: 'default_incomplete',
  expand: ['latest_invoice.payment_intent'],
});

// Return client secret for payment confirmation
return { clientSecret: subscription.latest_invoice.payment_intent.client_secret };
```

### 4.2 Subscription Management
```typescript
// Upgrade/downgrade subscription
const subscription = await stripe.subscriptions.update(subId, {
  items: [{ id: itemId, price: newPriceId }],
  proration_behavior: 'create_prorations',
});

// Cancel at period end
await stripe.subscriptions.update(subId, { cancel_at_period_end: true });

// Cancel immediately
await stripe.subscriptions.cancel(subId);

// Pause subscription
await stripe.subscriptions.update(subId, { pause_collection: { behavior: 'void' } });
```

### 4.3 Webhook Handling
```typescript
// api/webhooks/stripe/route.ts
import Stripe from 'stripe';
const stripe = new Stripe(process.env.STRIPE_SECRET_KEY!);

export async function POST(request: Request) {
  const body = await request.text();
  const signature = request.headers.get('stripe-signature')!;

  let event: Stripe.Event;
  try {
    event = stripe.webhooks.constructEvent(body, signature, process.env.STRIPE_WEBHOOK_SECRET!);
  } catch (error) {
    return new Response('Invalid signature', { status: 400 });
  }

  switch (event.type) {
    case 'checkout.session.completed':
      await handleCheckoutCompleted(event.data.object);
      break;
    case 'invoice.paid':
      await handleInvoicePaid(event.data.object);
      break;
    case 'invoice.payment_failed':
      await handlePaymentFailed(event.data.object);
      break;
    case 'customer.subscription.deleted':
      await handleSubscriptionCanceled(event.data.object);
      break;
    case 'customer.subscription.updated':
      await handleSubscriptionUpdated(event.data.object);
      break;
  }

  return new Response('OK', { status: 200 });
}
```

### 4.4 Customer Portal (Self-Service)
```typescript
// Let customers manage their own subscriptions
const session = await stripe.billingPortal.sessions.create({
  customer: customerId,
  return_url: 'https://example.com/account',
});

// Redirect customer to session.url
```

---

## Part 5: PCI DSS Compliance

### 5.1 PCI DSS Levels

| Level | Transactions | Requirement |
|---|---|---|
| **Level 1** | > 6M/year | Full PCI DSS audit by QSA |
| **Level 2** | 1M-6M/year | Self-assessment (SAQ D) + quarterly scan |
| **Level 3** | 20K-1M/year | Self-assessment (SAQ C) + quarterly scan |
| **Level 4** | < 20K/year | Self-assessment (SAQ A) + quarterly scan |

### 5.2 SAQ A (Simplest — Using Stripe Elements/Checkout)
- **Requirement:** Card data never touches your servers
- **How:** Use Stripe Elements (iframe), Stripe Checkout (hosted), or Payment Links
- **You need:** SSL/TLS, don't store card data, use Stripe's PCI-compliant forms
- **Self-assessment:** Complete SAQ A annually

### 5.3 What NOT to Do
- **Don't store card numbers:** Never save PANs in your database
- **Don't log card data:** Never log full card numbers, CVVs, or magnetic stripe data
- **Don't transmit card data unencrypted:** Always use HTTPS/TLS
- **Don't use your own form:** Use Stripe Elements or hosted checkout
- **Don't process on client:** Payment processing happens on server with secret key

### 5.4 Security Checklist
- [ ] HTTPS everywhere (TLS 1.2+)
- [ ] Stripe secret key on server only (never client)
- [ ] Stripe publishable key on client (safe to expose)
- [ ] Webhook signature verification
- [ ] No card data in logs, databases, or analytics
- [ ] Idempotency keys for payment requests
- [ ] PCI DSS SAQ completed annually
- [ ] Quarterly ASV vulnerability scans

---

## Part 6: E-Commerce Architecture

### 6.1 Data Model
```sql
-- Products
CREATE TABLE products (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v7(),
  name TEXT NOT NULL,
  description TEXT,
  price NUMERIC(10,2) NOT NULL,
  compare_at_price NUMERIC(10,2), -- for sales
  sku TEXT UNIQUE,
  stock INTEGER DEFAULT 0,
  status TEXT DEFAULT 'active', -- active, draft, archived
  images TEXT[], -- array of image URLs
  metadata JSONB DEFAULT '{}',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Product variants (size, color, etc.)
CREATE TABLE product_variants (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v7(),
  product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
  name TEXT NOT NULL, -- "Large / Red"
  price NUMERIC(10,2) NOT NULL,
  sku TEXT UNIQUE,
  stock INTEGER DEFAULT 0,
  attributes JSONB DEFAULT '{}' -- { size: "L", color: "red" }
);

-- Cart
CREATE TABLE carts (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v7(),
  user_id UUID REFERENCES users(id),
  session_id TEXT, -- for guest carts
  status TEXT DEFAULT 'active', -- active, abandoned, converted
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE cart_items (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v7(),
  cart_id UUID NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
  product_id UUID NOT NULL REFERENCES products(id),
  variant_id UUID REFERENCES product_variants(id),
  quantity INTEGER NOT NULL DEFAULT 1,
  price_at_add NUMERIC(10,2) NOT NULL -- snapshot price
);

-- Orders
CREATE TABLE orders (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v7(),
  user_id UUID REFERENCES users(id),
  email TEXT NOT NULL,
  status TEXT DEFAULT 'pending', -- pending, paid, fulfilled, cancelled, refunded
  subtotal NUMERIC(10,2) NOT NULL,
  tax NUMERIC(10,2) NOT NULL DEFAULT 0,
  shipping NUMERIC(10,2) NOT NULL DEFAULT 0,
  total NUMERIC(10,2) NOT NULL,
  currency TEXT DEFAULT 'usd',
  stripe_payment_intent_id TEXT,
  stripe_checkout_session_id TEXT,
  shipping_address JSONB,
  billing_address JSONB,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE order_items (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v7(),
  order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  product_id UUID NOT NULL REFERENCES products(id),
  variant_id UUID REFERENCES product_variants(id),
  name TEXT NOT NULL, -- snapshot
  quantity INTEGER NOT NULL,
  price NUMERIC(10,2) NOT NULL, -- snapshot
  total NUMERIC(10,2) NOT NULL
);
```

### 6.2 Order Flow
```
1. Add to cart → Cart stored in DB/session
2. Checkout → Enter shipping, select payment method
3. Payment → Stripe processes payment (server-side)
4. Confirmation → Order created in DB, confirmation email sent
5. Fulfillment → Admin marks order as fulfilled
6. Shipping → Tracking number added, notification sent
```

### 6.3 Inventory Management
```typescript
// Use database transactions for atomic inventory updates
async function placeOrder(orderData) {
  return await db.transaction(async (trx) => {
    // Check and decrement inventory
    for (const item of orderData.items) {
      const product = await trx.query(
        'UPDATE products SET stock = stock - $1 WHERE id = $2 AND stock >= $1 RETURNING *',
        [item.quantity, item.productId]
      );
      if (product.rows.length === 0) {
        throw new Error(`Insufficient stock for product ${item.productId}`);
      }
    }

    // Create order
    const order = await trx.query(
      'INSERT INTO orders (...) VALUES (...) RETURNING *',
      [orderData]
    );

    // Create order items
    for (const item of orderData.items) {
      await trx.query('INSERT INTO order_items (...) VALUES (...)', [order.rows[0].id, item]);
    }

    return order.rows[0];
  });
}
```

---

## Part 7: Tax Handling

### 7.1 Stripe Tax (Recommended)
```typescript
// Enable automatic tax in checkout session
const session = await stripe.checkout.sessions.create({
  mode: 'payment',
  line_items: [...],
  automatic_tax: { enabled: true },
  // Stripe calculates tax based on customer location
});
```

### 7.2 Tax Considerations
- **US:** Sales tax varies by state, county, city — use Stripe Tax or TaxJar
- **EU:** VAT included in price (B2C) or added at checkout (B2B with VAT number)
- **UK:** VAT 20% — included in price for B2C
- **Canada:** GST/HST/PST varies by province
- **Australia:** GST 10%
- **Digital products:** Different rules per country — use Merchant of Record (Paddle/Lemon Squeezy) if selling globally

### 7.3 Merchant of Record (Simplest for Digital)
- **Paddle/Lemon Squeezy:** They are the seller of record, handle all tax compliance
- **No tax registration needed:** They register for VAT/GST/sales tax in every jurisdiction
- **Higher fees:** ~5% vs 2.9% — but no tax compliance overhead
- **Best for:** Digital products, SaaS, solo developers selling globally

---

## Part 8: Fraud Prevention

### 8.1 Stripe Radar
```typescript
// Radar rules (configured in Stripe Dashboard)
// Block if risk score > 75
// Review if risk score > 50
// Block if IP country != card country
// Block if multiple cards from same IP

// Manual review
const paymentIntent = await stripe.paymentIntents.retrieve(piId);
if (paymentIntent.charges.data[0].outcome.risk_level === 'elevated') {
  // Hold order for manual review
}
```

### 8.2 Fraud Prevention Best Practices
- **Stripe Radar:** Enable and configure rules
- **3D Secure:** Enable for cards that require it (SCA compliance in EU)
- **Velocity checks:** Limit orders per IP/email in time window
- **Address verification:** AVS and CVC checks
- **Blacklist:** Block known bad IPs, emails, cards
- **CAPTCHA:** Add to checkout for suspicious traffic
- **Order limits:** Maximum order value for first-time customers
- **Email verification:** Verify email before order confirmation

---

## Part 9: Checkout UX Best Practices

### 9.1 Reduce Friction
- **Guest checkout:** Don't require account creation
- **One-tap pay:** Apple Pay / Google Pay buttons prominently
- **Saved methods:** For returning customers, save payment method
- **Autofill:** Use browser autofill for addresses (`autocomplete` attributes)
- **Minimal fields:** Only ask for what's needed
- **Progress indicator:** Show checkout steps (1. Shipping → 2. Payment → 3. Review)
- **No surprises:** Show shipping and tax before final payment

### 9.2 Trust Signals
- **Security badges:** "Secured by Stripe", SSL certificate
- **Return policy:** Visible link to return policy
- **Contact info:** Customer service email/phone visible
- **Reviews:** Product reviews and ratings
- **Guarantee:** Money-back guarantee if applicable
- **HTTPS:** Lock icon in browser (don't break it with mixed content)

### 9.3 Error Handling
- **Inline validation:** Validate fields as user types, not just on submit
- **Clear error messages:** "Card number is invalid" not "Error 4002"
- **Recovery:** Allow user to fix and retry without re-entering everything
- **Declined cards:** Show specific decline message from Stripe
- **Network errors:** "Something went wrong. Please try again." with retry button

---

## Part 10: Testing Payments

### 10.1 Stripe Test Mode
```typescript
// Use test API keys
const stripe = new Stripe(process.env.STRIPE_TEST_SECRET_KEY!);

// Test card numbers
4242 4242 4242 4242  — Visa, succeeds
4000 0027 6000 3184  — Visa, requires 3DS
4000 0000 0000 9995  — Visa, declined (insufficient funds)
4000 0000 0000 0069  — Visa, declined (expired)
5555 5555 5555 4444  — Mastercard, succeeds
3782 822463 10005    — Amex, succeeds
```

### 10.2 Test Scenarios
- [ ] Successful payment
- [ ] Declined payment (insufficient funds)
- [ ] Declined payment (expired card)
- [ ] 3D Secure authentication required
- [ ] Refund (full and partial)
- [ ] Subscription creation
- [ ] Subscription cancellation
- [ ] Failed subscription renewal
- [ ] Webhook delivery
- [ ] Apple Pay / Google Pay
- [ ] Multiple payment methods
- [ ] Tax calculation
- [ ] Discount codes

### 10.3 Webhook Testing
```bash
# Install Stripe CLI
stripe listen --forward-to localhost:3000/api/webhooks/stripe

# Trigger events
stripe trigger checkout.session.completed
stripe trigger invoice.payment_failed
stripe trigger customer.subscription.updated
```

---

## Execution Instructions for Cascade

When this skill is activated for payment & commerce:

1. **Read the project context** — products, pricing, subscription vs one-time, target countries
2. **Choose payment processor** — Stripe (default), Paddle/Lemon Squeezy (Merchant of Record for digital)
3. **Set up Stripe** — API keys, webhooks, test mode
4. **Implement checkout** — Stripe Checkout (simplest) or Payment Element (custom)
5. **Enable one-tap payments** — Apple Pay, Google Pay via Stripe
6. **Set up server-side processing** — PaymentIntents, never process on client
7. **Implement webhooks** — Handle payment events (success, failure, subscription updates)
8. **Set up subscriptions** — Stripe Billing if recurring payments needed
9. **Handle tax** — Stripe Tax or Merchant of Record for global
10. **Implement fraud prevention** — Radar rules, 3DS, velocity checks
11. **Design checkout UX** — Guest checkout, minimal fields, trust signals, inline validation
12. **Set up data model** — Products, variants, cart, orders, order items
13. **Test thoroughly** — Test mode, all card scenarios, webhooks, subscriptions
14. **Ensure PCI compliance** — SAQ A, no card data on server, HTTPS, webhook verification
