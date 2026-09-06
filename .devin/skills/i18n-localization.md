---
name: i18n & Localization Skill
description: Comprehensive methodology for internationalizing and localizing web applications — 2025-2026 practices with ICU MessageFormat, RTL support, locale routing, translation management, and cultural adaptation
version: 1.0.0
tags: [i18n, l10n, internationalization, localization, translation, rtl, locale, multilingual]
---

# i18n & Localization Skill

## Purpose
This skill provides a comprehensive methodology for internationalizing and localizing web applications across any kind of web project. It reflects **modern 2025-2026 practices** — ICU MessageFormat for pluralization and gender, RTL support with CSS logical properties, locale-based routing, translation management workflows, and cultural adaptation beyond just translating text.

## Core Philosophy

**Internationalization is architecture, localization is content.** i18n is building the app to support multiple locales — it's a structural decision made early. l10n is adapting content for a specific locale — it's an ongoing process. You can't localize what wasn't internationalized first.

**The #1 rule:** Design for the world, not just your locale. Hardcoded strings, date formats, number formats, and LTR-only layouts are technical debt. Build i18n-ready from day one, even if you only launch in one language.

---

## Part 1: Framework Selection

### 1.1 Next.js (next-intl)
```tsx
// app/[locale]/layout.tsx
import { NextIntlClientProvider } from 'next-intl';
import { getMessages } from 'next-intl/server';

export default async function LocaleLayout({ children, params: { locale } }) {
  const messages = await getMessages();
  return (
    <html lang={locale}>
      <NextIntlClientProvider messages={messages}>
        {children}
      </NextIntlClientProvider>
    </html>
  );
}

// Usage
import { useTranslations } from 'next-intl';
const t = useTranslations('HomePage');
<h1>{t('title')}</h1>
```

### 1.2 React (react-i18next / i18next)
```tsx
import i18n from 'i18next';
import { initReactI18next, useTranslation } from 'react-i18next';

i18n.use(initReactI18next).init({
  resources: {
    en: { translation: { welcome: 'Welcome' } },
    fr: { translation: { welcome: 'Bienvenue' } },
  },
  lng: 'en',
  fallbackLng: 'en',
});

function Component() {
  const { t } = useTranslation();
  return <h1>{t('welcome')}</h1>;
}
```

### 1.3 Vue (vue-i18n)
```tsx
import { createI18n } from 'vue-i18n';

const i18n = createI18n({
  legacy: false,
  locale: 'en',
  fallbackLocale: 'en',
  messages: {
    en: { welcome: 'Welcome' },
    fr: { welcome: 'Bienvenue' },
  },
});
```

### 1.4 Svelte (svelte-i18n / Paraglide)
```tsx
import { init, register } from 'svelte-i18n';

init({ fallbackLocale: 'en', initialLocale: 'en' });
```

### 1.5 Framework-Agnostic (FormatJS / ICU)
```tsx
import { IntlProvider, FormattedMessage, FormattedDate, FormattedNumber } from 'react-intl';

<IntlProvider locale="fr" messages={messages}>
  <h1><FormattedMessage id="welcome" /></h1>
  <FormattedDate value={date} />
  <FormattedNumber value={1234.5} style="currency" currency="EUR" />
</IntlProvider>
```

---

## Part 2: Locale Management

### 2.1 Locale Codes (BCP 47)
```
en         — English (generic)
en-US      — English (United States)
en-GB      — English (United Kingdom)
fr         — French
fr-FR      — French (France)
fr-CA      — French (Canada)
ar         — Arabic
ar-SA      — Arabic (Saudi Arabia)
he         — Hebrew
ja         — Japanese
zh         — Chinese (generic)
zh-Hans    — Chinese (Simplified)
zh-Hant    — Chinese (Traditional)
zh-CN      — Chinese (China, Simplified)
zh-TW      — Chinese (Taiwan, Traditional)
```

### 2.2 Locale Detection Strategy
1. **URL path:** `/en/about`, `/fr/a-propos` — most explicit, SEO-friendly
2. **Subdomain:** `en.example.com`, `fr.example.com` — good for large sites
3. **Domain:** `example.com`, `example.fr` — best for country-specific
4. **Cookie:** Store preference — good for returning users
5. **Accept-Language:** Browser header — for initial detection
6. **User setting:** Explicit user preference — highest priority

**Priority order:** User setting > URL path > Cookie > Accept-Language > Default

### 2.3 Locale Routing
```tsx
// Next.js app router with locale segment
// app/[locale]/page.tsx
export function generateStaticParams() {
  return locales.map((locale) => ({ locale }));
}

// Middleware for locale detection and redirect
export function middleware(request) {
  const locale = getLocale(request);
  const pathname = request.nextUrl.pathname;
  if (!pathname.startsWith(`/${locale}`)) {
    return Response.redirect(new URL(`/${locale}${pathname}`, request.url));
  }
}
```

### 2.4 hreflang Tags
```html
<link rel="alternate" hreflang="en" href="https://example.com/en/page" />
<link rel="alternate" hreflang="fr" href="https://example.com/fr/page" />
<link rel="alternate" hreflang="de" href="https://example.com/de/page" />
<link rel="alternate" hreflang="x-default" href="https://example.com/en/page" />
```
- **One per locale:** Each language version of the page
- **x-default:** Fallback for users whose locale isn't available
- **Bidirectional:** Each page must reference all other language versions
- **SEO:** Critical for Google to serve the right language version

### 2.5 Sitemap for Multiple Locales
```xml
<url>
  <loc>https://example.com/en/about</loc>
  <xhtml:link rel="alternate" hreflang="en" href="https://example.com/en/about"/>
  <xhtml:link rel="alternate" hreflang="fr" href="https://example.com/fr/a-propos"/>
  <xhtml:link rel="alternate" hreflang="de" href="https://example.com/de/uber-uns"/>
</url>
```

---

## Part 3: Translation File Structure

### 3.1 JSON Structure
```json
{
  "common": {
    "buttons": {
      "save": "Save",
      "cancel": "Cancel",
      "delete": "Delete",
      "submit": "Submit"
    },
    "labels": {
      "email": "Email address",
      "password": "Password"
    }
  },
  "nav": {
    "home": "Home",
    "about": "About",
    "pricing": "Pricing",
    "contact": "Contact"
  },
  "homepage": {
    "hero": {
      "title": "Build better websites, faster",
      "subtitle": "The AI-powered platform for modern teams.",
      "cta": "Start free trial"
    }
  },
  "errors": {
    "required": "{field} is required",
    "invalidEmail": "Please enter a valid email address",
    "minLength": "{field} must be at least {min} characters"
  }
}
```

### 3.2 Namespace Organization
- **common:** Shared strings (buttons, labels, validation messages)
- **nav:** Navigation labels
- **homepage:** Homepage-specific content
- **auth:** Login, signup, password reset
- **dashboard:** Dashboard-specific content
- **settings:** Settings pages
- **errors:** Error messages
- **emails:** Email templates

### 3.3 Nested vs Flat Keys
```json
// Nested (recommended for readability)
{ "homepage": { "hero": { "title": "Welcome" } } }
// Key: homepage.hero.title

// Flat (simpler for some tools)
{ "homepage.hero.title": "Welcome" }
```

---

## Part 4: ICU MessageFormat

### 4.1 Pluralization
```
// English
{count, plural,
  =0 {No items}
  one {One item}
  other {# items}
}

// Arabic (6 plural forms)
{count, plural,
  zero {لا عناصر}
  one {عنصر واحد}
  two {عنصران}
  few {# عناصر}
  many {# عنصرًا}
  other {# عنصر}
}
```

### 4.2 Gender
```
{userGender, select,
  male {He added a comment}
  female {She added a comment}
  other {They added a comment}
}
```

### 4.3 Select (for discrete choices)
```
{status, select,
  pending {Awaiting approval}
  approved {Approved}
  rejected {Rejected}
  other {Unknown status}
}
```

### 4.4 Variables and Formatting
```
Hello, {name}!                    // Simple variable
You have {count, number} points   // Number formatting
Total: {amount, number, ::currency/USD}  // Currency
Date: {date, date, ::medium}      // Date formatting
```

### 4.5 Complex Messages
```
{count, plural,
  =0 {No comments yet}
  one {{name} added a comment}
  other {{count} comments, latest by {name}}
}
```

---

## Part 5: RTL (Right-to-Left) Support

### 5.1 CSS Logical Properties
```css
/* Bad: physical properties */
margin-left: 16px;
padding-right: 24px;
text-align: left;
border-left: 1px solid;

/* Good: logical properties */
margin-inline-start: 16px;
padding-inline-end: 24px;
text-align: start;
border-inline-start: 1px solid;
```

### 5.2 Logical Property Mapping

| Physical (LTR) | Logical | RTL equivalent |
|---|---|---|
| `margin-left` | `margin-inline-start` | `margin-right` |
| `margin-right` | `margin-inline-end` | `margin-left` |
| `padding-left` | `padding-inline-start` | `padding-right` |
| `padding-right` | `padding-inline-end` | `padding-left` |
| `border-left` | `border-inline-start` | `border-right` |
| `left` | `inset-inline-start` | `right` |
| `right` | `inset-inline-end` | `left` |
| `text-align: left` | `text-align: start` | `text-align: right` |
| `float: left` | `float: inline-start` | `float: right` |

### 5.3 Setting Direction
```tsx
<html lang="ar" dir="rtl">
// or dynamically
<html lang={locale} dir={isRTL(locale) ? 'rtl' : 'ltr'}>
```

### 5.4 RTL-Aware Components
```tsx
function isRTL(locale: string): boolean {
  return ['ar', 'he', 'fa', 'ur', 'ps', 'sd'].includes(locale);
}

// Tailwind CSS RTL support
<div className="flex flex-row">
  <div className="me-4">Start (margin-inline-end)</div>
  <div>End</div>
</div>
// Tailwind: ms-* (margin-inline-start), me-* (margin-inline-end)
// Tailwind: ps-* (padding-inline-start), pe-* (padding-inline-end)
```

### 5.5 Icon Direction
- **Directional icons:** Arrows, chevrons may need to flip in RTL
- **Flip:** `transform: scaleX(-1)` or conditional icon
- **Not all icons:** Non-directional icons (home, settings) don't flip
- **Test:** Set `dir="rtl"` and check all directional icons

### 5.6 RTL Testing
- **Browser:** Set `dir="rtl"` on `<html>` and test
- **Hebrew/Arabic content:** Use real text, not lorem ipsum
- **Layout:** Check flexbox, grid, absolute positioning
- **Forms:** Labels and inputs should align correctly
- **Icons:** Verify directional icons flip
- **Numbers:** Numbers stay LTR even in RTL context

---

## Part 6: Date, Time, Number, and Currency Formatting

### 6.1 Intl API (Built-in)
```typescript
// Date formatting
new Intl.DateTimeFormat('en-US', { dateStyle: 'long' }).format(date);
// "January 15, 2025"

new Intl.DateTimeFormat('fr-FR', { dateStyle: 'long' }).format(date);
// "15 janvier 2025"

new Intl.DateTimeFormat('ja-JP', { dateStyle: 'long' }).format(date);
// "2025年1月15日"

// Number formatting
new Intl.NumberFormat('en-US').format(1234567.89);
// "1,234,567.89"

new Intl.NumberFormat('de-DE').format(1234567.89);
// "1.234.567,89"

new Intl.NumberFormat('ar-EG').format(1234567.89);
// "١٬٢٣٤٬٥٦٧٫٨٩"

// Currency formatting
new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(99.5);
// "$99.50"

new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(99.5);
// "99,50 €"

new Intl.NumberFormat('ja-JP', { style: 'currency', currency: 'JPY' }).format(1500);
// "￥1,500"
```

### 6.2 Relative Time Formatting
```typescript
new Intl.RelativeTimeFormat('en', { numeric: 'auto' }).format(-1, 'day');
// "yesterday"

new Intl.RelativeTimeFormat('fr', { numeric: 'auto' }).format(-1, 'day');
// "hier"

new Intl.RelativeTimeFormat('ar', { numeric: 'auto' }).format(-1, 'day');
// "أمس"
```

### 6.3 List Formatting
```typescript
new Intl.ListFormat('en', { style: 'long', type: 'conjunction' }).format(['a', 'b', 'c']);
// "a, b, and c"

new Intl.ListFormat('fr', { style: 'long', type: 'conjunction' }).format(['a', 'b', 'c']);
// "a, b et c"
```

### 6.4 Collation (Sorting)
```typescript
['é', 'a', 'z', 'c'].sort(new Intl.Collator('fr').compare);
// ['a', 'c', 'é', 'z']

['é', 'a', 'z', 'c'].sort(new Intl.Collator('sv').compare);
// ['a', 'e', 'z', 'c'] — Swedish puts 'e' before 'z' but 'é' as variant of 'e'
```

### 6.5 Plural Rules
```typescript
new Intl.PluralRules('en').select(0); // "other"
new Intl.PluralRules('en').select(1); // "one"
new Intl.PluralRules('en').select(2); // "other"

new Intl.PluralRules('ar').select(0); // "zero"
new Intl.PluralRules('ar').select(1); // "one"
new Intl.PluralRules('ar').select(2); // "two"
new Intl.PluralRules('ar').select(5); // "few"
new Intl.PluralRules('ar').select(15); // "many"
new Intl.PluralRules('ar').select(100); // "other"
```

---

## Part 7: Translation Management

### 7.1 Translation Workflow
```
Developer → English strings → Translation tool → Translator → Review → Import
```

### 7.2 Translation Tools
- **Crowdin:** Popular, GitHub integration, community translations
- **Lokalise:** Enterprise-grade, workflow management, API
- **Phrase:** Comprehensive, in-context editing, quality checks
- **POEditor:** Simple, affordable for small teams
- **Tolgee:** Open-source, in-context editing
- **i18n Ally:** VS Code extension for local translation management

### 7.3 Machine Translation + Human Review
- **DeepL:** Best quality machine translation, supports 30+ languages
- **Google Translate API:** Fast, wide language support
- **ChatGPT/Claude:** Good for context-aware translation with prompts
- **Workflow:** Machine translate → human review → approve
- **Quality:** Machine translation is ~80% accurate — human review essential

### 7.4 Continuous Localization
- **CI/CD integration:** Push new strings → auto-send to translation tool → auto-import when ready
- **Webhooks:** Translation tool notifies when translations are complete
- **Fallback:** Show English (or fallback locale) until translation is ready
- **Missing key alerts:** Alert when keys are missing in a locale

### 7.5 Translation Quality
- **Context:** Provide context for each string (screenshot, location, description)
- **Character limits:** Some languages expand (German +30%), some contract (Chinese -20%)
- **Glossary:** Maintain a glossary of brand terms, product names, technical terms
- **Style guide:** Tone, formality (tu vs vous in French), punctuation
- **Review:** Native speaker review, not just machine translation

---

## Part 8: Content Localization Beyond Text

### 8.1 Image Localization
- **Text in images:** Avoid — use HTML text overlaid on images instead
- **Culturally appropriate:** Images should be appropriate for the target culture
- **People:** Diverse representation that matches target audience
- **Colors:** Colors have different meanings in different cultures (red = luck in China, danger in West)
- **Gestures:** Hand gestures vary in meaning across cultures
- **Direction:** Images with direction should flip in RTL

### 8.2 Date and Calendar Localization
- **Calendar systems:** Gregorian (most), Hijri (Arabic), Hebrew, Japanese, Persian
- **Week start:** Sunday (US), Monday (Europe), Saturday (Middle East)
- **Date formats:** MM/DD/YYYY (US), DD/MM/YYYY (Europe), YYYY-MM-DD (ISO)
- **Time zones:** Display in user's local timezone
- **DST:** Handle daylight saving time transitions

### 8.3 Name and Address Formats
- **Name order:** Given name first (West) vs family name first (East Asia)
- **Honorifics:** Mr./Ms. (English), -san (Japanese), Herr/Frau (German)
- **Address format:** Street, city, state, zip — order varies by country
- **Postal codes:** Format varies (US: 12345, UK: SW1A 1AA, DE: 10115)
- **Phone numbers:** Format varies, use `libphonenumber` for formatting

### 8.4 Legal and Compliance Localization
- **Privacy policy:** Must be in the user's language for GDPR compliance
- **Terms of service:** Legal requirements vary by country
- **Cookie consent:** Different requirements by region (GDPR, CCPA, LGPD)
- **Age restrictions:** Vary by country (13, 16, 18)
- **Currency:** Display in local currency, handle tax inclusion (EU includes VAT, US doesn't)

---

## Part 9: SEO for Multi-Lingual Sites

### 9.1 hreflang Implementation
```html
<!-- In <head> of each page -->
<link rel="alternate" hreflang="en" href="https://example.com/en/page" />
<link rel="alternate" hreflang="fr" href="https://example.com/fr/page" />
<link rel="alternate" hreflang="de" href="https://example.com/de/page" />
<link rel="alternate" hreflang="x-default" href="https://example.com/en/page" />
```

### 9.2 URL Structure for SEO
- **Subdirectories:** `example.com/en/`, `example.com/fr/` — best for SEO (single domain authority)
- **Subdomains:** `en.example.com` — separate domain authority
- **ccTLDs:** `example.fr` — strongest geo-targeting but separate domains
- **Recommendation:** Subdirectories with locale segment in URL

### 9.3 Localized Metadata
```tsx
// Each locale version should have:
<title>{t('meta.title')}</title>
<meta name="description" content={t('meta.description')} />
<meta property="og:title" content={t('meta.ogTitle')} />
<meta property="og:locale" content={locale} />
```

### 9.4 Localized Sitemaps
- **One sitemap per locale** or **one sitemap with hreflang annotations**
- **Submit to Google Search Console** for each locale
- **Include all language versions** in each sitemap entry

### 9.5 Content Localization for SEO
- **Translate all content:** Not just navigation and buttons
- **Local keywords:** Research keywords in target language — don't just translate English keywords
- **Local backlinks:** Build backlinks from local websites
- **Local server/CDN:** Host content close to users for speed
- **Schema.org:** Use `inLanguage` property in structured data

---

## Part 10: Testing i18n

### 10.1 Pseudo-Localization
```
// Before translation, test with pseudo-localized strings
"Welcome" → "[Wéélçóméé!!!]"
// Adds accents, lengthens text, wraps in brackets
// Tests: text expansion, special characters, layout
```

### 10.2 Missing Translation Detection
```typescript
// In development, log missing keys
i18n.on('missingKey', (lng, ns, key) => {
  console.warn(`Missing translation: ${lng}.${ns}.${key}`);
});

// In tests, verify all keys exist
test('all keys have translations', () => {
  const enKeys = Object.keys(getKeys(en));
  const frKeys = Object.keys(getKeys(fr));
  expect(frKeys).toEqual(enKeys);
});
```

### 10.3 Visual Testing for RTL
- **Playwright:** Set `dir="rtl"` and take screenshots
- **Visual regression:** Compare LTR and RTL layouts
- **Real content:** Test with actual Arabic/Hebrew text, not lorem ipsum

### 10.4 Locale-Specific Testing
- **Date/time:** Verify formatting for each locale
- **Numbers:** Verify number and currency formatting
- **Pluralization:** Test 0, 1, 2, many for each locale
- **Text expansion:** Verify layout doesn't break with longer text
- **RTL:** Verify all pages in RTL mode
- **Fonts:** Verify fonts support all required characters

---

## Execution Instructions for Cascade

When this skill is activated for i18n & localization:

1. **Read the project context** — framework, target locales, current state
2. **Choose i18n library** — next-intl (Next.js), react-i18next (React), vue-i18n (Vue), FormatJS (framework-agnostic)
3. **Set up locale routing** — URL path with locale segment, middleware for detection
4. **Create translation file structure** — namespaces, nested keys, JSON files per locale
5. **Implement ICU MessageFormat** — pluralization, gender, select, variables
6. **Implement formatting** — dates, numbers, currency using `Intl` API
7. **Set up RTL support** — CSS logical properties, `dir` attribute, icon flipping
8. **Add hreflang tags** — for SEO, one per locale + x-default
9. **Set up translation management** — choose tool (Crowdin, Lokalise, Phrase), CI/CD integration
10. **Localize content beyond text** — images, dates, names, addresses, legal
11. **Set up SEO for multi-lingual** — localized metadata, sitemaps, local keywords
12. **Test i18n** — pseudo-localization, missing key detection, RTL visual testing, locale-specific formatting
13. **Document** — translation workflow, glossary, style guide, locale support matrix
