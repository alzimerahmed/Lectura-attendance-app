---
name: payment
description: Comprehensive payment & commerce workflow — processor selection, Stripe integration, Apple Pay/Google Pay, subscriptions, PCI compliance, tax, fraud prevention, checkout UX, and testing
---

# Payment & Commerce Workflow

This workflow applies the **Payment & Commerce Skill** (`~/.codeium/windsurf/skills/payment-commerce.md`) to implement secure, conversion-optimized payment processing.

## When to Run
- When implementing payment processing
- When the user says `/payment` or asks about e-commerce
- When setting up Stripe, subscriptions, or checkout
- When building an e-commerce store
- When implementing Apple Pay or Google Pay

---

## Step 1: Assess Commerce Needs

1. Read the project context — products, pricing model, target markets
2. Determine payment type: one-time, subscription, or mixed
3. Identify target countries and currencies
4. Determine tax handling needs (Stripe Tax vs Merchant of Record)
5. Check if marketplace/split payments needed (Stripe Connect)
6. Identify fraud prevention requirements

## Step 2: Choose Payment Processor

1. **Stripe:** Default for most e-commerce and SaaS — best docs, widest features
2. **Paddle/Lemon Squeezy:** If selling digital products globally and want tax handled
3. **PayPal:** Add as secondary method for user trust
4. **Adyen:** If enterprise with global multi-acquirer needs
5. Set up account and get API keys (test + live)
6. Configure webhook endpoints

## Step 3: Set Up Stripe Integration

1. Install Stripe SDK: `stripe` (server), `@stripe/stripe-js` (client)
2. Set environment variables: `STRIPE_SECRET_KEY` (server), `NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY` (client)
3. Choose integration method:
   - **Stripe Checkout:** Hosted page — simplest, zero PCI scope, fastest to implement
   - **Payment Element:** Custom checkout with all payment methods — recommended for custom UX
   - **Stripe Elements (Card):** Custom checkout with card-only — for simple card payments
4. Set up webhook endpoint with signature verification
5. Configure test mode for development

## Step 4: Implement Checkout

1. **Stripe Checkout (simplest):**
   - Server: Create checkout session with line items
   - Client: Redirect to Stripe-hosted checkout URL
   - Handle success/cancel redirects
2. **Payment Element (custom):**
   - Server: Create PaymentIntent with `automatic_payment_methods`
   - Client: Render Payment Element, confirm payment with `stripe.confirmPayment()`
   - Handle 3DS authentication redirects
3. **Server-side processing:**
   - Create PaymentIntent on server with secret key
   - Confirm payment on server
   - Never process payments on client

## Step 5: Enable One-Tap Payments

1. Apple Pay and Google Pay are automatically available with Payment Element
2. Configure `automatic_payment_methods: { enabled: true }` in PaymentIntent
3. For explicit Apple Pay button: use `stripe.paymentRequest()`
4. Verify domain for Apple Pay (Stripe Dashboard or API)
5. Show Apple Pay/Google Pay buttons prominently above card input
6. Test on supported devices (Safari for Apple Pay, Chrome for Google Pay)

## Step 6: Set Up Subscriptions (if needed)

1. Create products and prices in Stripe Dashboard or via API
2. Create subscription with `stripe.subscriptions.create()`
3. Use `expand: ['latest_invoice.payment_intent']` to get client secret
4. Client confirms payment with client secret
5. Set up customer portal for self-service management
6. Handle subscription webhooks: `invoice.paid`, `invoice.payment_failed`, `customer.subscription.updated`, `customer.subscription.deleted`
7. Implement subscription state in database (status, current period, plan)

## Step 7: Handle Webhooks

1. Create webhook endpoint: `/api/webhooks/stripe`
2. Verify webhook signature with `stripe.webhooks.constructEvent()`
3. Handle events:
   - `checkout.session.completed` — fulfill order
   - `payment_intent.succeeded` — mark order as paid
   - `payment_intent.payment_failed` — mark order as failed
   - `invoice.paid` — subscription renewal success
   - `invoice.payment_failed` — subscription renewal failure
   - `customer.subscription.deleted` — revoke access
4. Make webhook handlers idempotent — events may be sent multiple times
5. Return 200 quickly — process async if needed

## Step 8: Set Up Tax Handling

1. **Stripe Tax:** Enable `automatic_tax: { enabled: true }` in checkout sessions
2. **Merchant of Record:** Use Paddle/Lemon Squeezy if selling digital products globally
3. **Manual:** Calculate tax based on customer location (not recommended)
4. Configure tax registrations in Stripe Dashboard
5. Test tax calculation for different regions

## Step 9: Implement Fraud Prevention

1. Enable Stripe Radar with default rules
2. Configure custom Radar rules: block high-risk, review medium-risk
3. Enable 3D Secure for cards that require it (SCA compliance in EU)
4. Implement velocity checks: limit orders per IP/email in time window
5. Use AVS and CVC verification
6. Set up blacklist for known bad actors
7. Add CAPTCHA for suspicious traffic
8. Set maximum order value for first-time customers

## Step 10: Design Checkout UX

1. **Guest checkout:** Don't require account creation
2. **One-tap pay:** Apple Pay / Google Pay buttons prominently displayed
3. **Minimal fields:** Only ask for what's needed — email, shipping, payment
4. **Autofill:** Use `autocomplete` attributes for addresses
5. **Inline validation:** Validate as user types, clear error messages
6. **Trust signals:** Security badges, return policy, contact info
7. **No surprises:** Show shipping, tax, and total before payment
8. **Progress indicator:** Show checkout steps
9. **Error recovery:** Allow retry without re-entering everything
10. **Mobile-first:** Optimize for mobile checkout experience

## Step 11: Set Up Data Model

1. Create products table with name, price, description, images, stock, status
2. Create product_variants table for size/color variations
3. Create carts and cart_items tables for shopping cart
4. Create orders table with status, totals, Stripe IDs, addresses
5. Create order_items table with product snapshot (name, price at time of order)
6. Use database transactions for atomic inventory updates and order creation
7. Store Stripe IDs: `stripe_payment_intent_id`, `stripe_checkout_session_id`

## Step 12: Ensure PCI Compliance

1. Use Stripe Elements or Checkout — never build custom card input
2. Never store, log, or transmit card data on your servers
3. Keep Stripe secret key on server only — never in client code
4. Use HTTPS everywhere (TLS 1.2+)
5. Verify webhook signatures
6. Use idempotency keys for payment requests
7. Complete PCI DSS SAQ A annually
8. Run quarterly ASV vulnerability scans

## Step 13: Test Thoroughly

1. Use Stripe test mode with test API keys
2. Test card numbers: 4242 (success), 9995 (insufficient funds), 0069 (expired), 3184 (3DS)
3. Test scenarios: successful payment, declined, 3DS, refund (full/partial)
4. Test subscriptions: creation, renewal, cancellation, failed renewal
5. Test webhooks: use `stripe listen` CLI to forward to local server
6. Test Apple Pay (Safari) and Google Pay (Chrome)
7. Test tax calculation for different regions
8. Test discount/coupon codes
9. Test checkout on mobile devices
10. Verify no card data in logs, database, or analytics

## Step 14: Document & Launch

1. Document payment architecture — processor, flow, webhooks
2. Document subscription model — plans, billing cycles, portal
3. Document tax handling — which regions, how calculated
4. Document fraud prevention — Radar rules, velocity checks
5. Document refund and cancellation procedures
6. Set up monitoring — payment success rate, webhook delivery
7. Switch from test to live API keys
8. Go live with small volume first, monitor for issues
