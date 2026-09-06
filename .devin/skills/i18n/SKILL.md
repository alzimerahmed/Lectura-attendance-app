---
name: i18n
description: Comprehensive i18n & localization workflow — framework setup, locale routing, translation management, RTL support, formatting, SEO, and testing
---

# i18n & Localization Workflow

This workflow applies the **i18n & Localization Skill** (`~/.codeium/windsurf/skills/i18n-localization.md`) to internationalize and localize web applications.

## When to Run
- When building a multi-lingual website or app
- When the user says `/i18n` or asks about localization
- When adding a new language to an existing app
- When setting up RTL support
- When implementing locale-aware formatting (dates, numbers, currency)

---

## Step 1: Assess i18n Needs

1. Read the project context — framework, target locales, current state
2. Identify target languages and regions (e.g., en-US, fr-FR, ar-SA, ja-JP)
3. Determine if RTL support is needed (Arabic, Hebrew, Persian, Urdu)
4. Check compliance requirements (GDPR requires privacy policy in user's language)
5. Identify content that needs localization (text, images, dates, numbers, currency)

## Step 2: Choose i18n Library

1. Select library based on framework:
   - Next.js: `next-intl` (recommended) or `next-i18next`
   - React: `react-i18next` or `react-intl` (FormatJS)
   - Vue: `vue-i18n`
   - Svelte: `svelte-i18n` or Paraglide
2. Install and configure the library
3. Set up locale provider with fallback locale
4. Create initial translation file structure

## Step 3: Set Up Locale Routing

1. Choose URL structure: subdirectories (`/en/`, `/fr/`) — best for SEO
2. Implement locale detection: user setting > URL > cookie > Accept-Language > default
3. Set up middleware for locale detection and redirect
4. Generate static params for each locale (SSG/SSR)
5. Set `<html lang={locale} dir={isRTL(locale) ? 'rtl' : 'ltr'}>` on each page

## Step 4: Create Translation Files

1. Create JSON files per locale: `en.json`, `fr.json`, `ar.json`, etc.
2. Organize by namespace: common, nav, homepage, auth, dashboard, errors, emails
3. Use nested keys for readability: `homepage.hero.title`
4. Start with English as the source language
5. Create a translation key naming convention document

## Step 5: Implement ICU MessageFormat

1. Replace hardcoded strings with translation function calls: `t('homepage.hero.title')`
2. Implement pluralization: `{count, plural, =0 {} one {} other {}}`
3. Implement gender: `{gender, select, male {} female {} other {}}`
4. Implement select: `{status, select, pending {} approved {} other {}}`
5. Implement variables: `Hello, {name}!`
6. Test with different plural forms (especially Arabic with 6 forms)

## Step 6: Implement Locale-Aware Formatting

1. **Dates:** Use `Intl.DateTimeFormat` — never hardcode date format strings
2. **Numbers:** Use `Intl.NumberFormat` — decimal and thousands separators vary
3. **Currency:** Use `Intl.NumberFormat` with currency option — symbol position varies
4. **Relative time:** Use `Intl.RelativeTimeFormat` — "yesterday", "il y a 1 jour"
5. **Lists:** Use `Intl.ListFormat` — "a, b, and c" vs "a, b et c"
6. **Sorting:** Use `Intl.Collator` — language-specific sort order
7. **Plural rules:** Use `Intl.PluralRules` — different plural categories per language

## Step 7: Set Up RTL Support

1. Replace physical CSS properties with logical properties:
   - `margin-left` → `margin-inline-start`
   - `padding-right` → `padding-inline-end`
   - `text-align: left` → `text-align: start`
2. Set `dir="rtl"` on `<html>` for RTL locales
3. Flip directional icons: `transform: scaleX(-1)` or conditional rendering
4. Test all layouts with `dir="rtl"` — flexbox, grid, absolute positioning
5. Use Tailwind logical properties: `ms-*`, `me-*`, `ps-*`, `pe-*`
6. Test with real Arabic/Hebrew text, not lorem ipsum

## Step 8: Add hreflang and SEO

1. Add `<link rel="alternate" hreflang="...">` tags for each locale
2. Add `x-default` hreflang for fallback
3. Localize metadata: title, description, Open Graph tags per locale
4. Create localized sitemaps with hreflang annotations
5. Set `og:locale` meta tag for each page
6. Research local keywords — don't just translate English keywords
7. Add `inLanguage` property to schema.org structured data

## Step 9: Set Up Translation Management

1. Choose translation tool: Crowdin, Lokalise, Phrase, Tolgee, or i18n Ally (VS Code)
2. Export English strings to translation tool
3. Set up CI/CD integration: push new strings → auto-send to tool → auto-import when ready
4. Configure machine translation (DeepL, Google Translate) + human review workflow
5. Create glossary of brand terms, product names, technical terms
6. Create translation style guide (tone, formality, punctuation)
7. Set up missing key alerts in development

## Step 10: Localize Content Beyond Text

1. **Images:** Replace text-in-images with HTML text overlays; use culturally appropriate imagery
2. **Dates:** Display in user's locale format; handle different calendar systems if needed
3. **Names:** Handle name order variations (given-first vs family-first)
4. **Addresses:** Support different address formats by country
5. **Phone:** Use `libphonenumber` for formatting
6. **Currency:** Display in local currency; handle tax inclusion (EU includes VAT)
7. **Legal:** Translate privacy policy, terms of service for compliance

## Step 11: Test i18n Implementation

1. **Pseudo-localization:** Test with accented, expanded strings before translation
2. **Missing keys:** Verify all keys exist in all locales — log missing keys in dev
3. **RTL visual testing:** Set `dir="rtl"` and screenshot all pages
4. **Text expansion:** Verify layout handles German (+30%), French (+20%) text
5. **Formatting:** Test date, number, currency formatting for each locale
6. **Pluralization:** Test 0, 1, 2, many for each locale (especially Arabic)
7. **SEO:** Verify hreflang tags, localized metadata, sitemaps
8. **Performance:** Verify translation loading doesn't impact page load

## Step 12: Document & Maintain

1. Document translation workflow for developers and translators
2. Maintain glossary and style guide
3. Create locale support matrix (which locales are supported, which are in progress)
4. Set up continuous localization — new strings automatically sent for translation
5. Schedule regular translation reviews — keep content current
6. Monitor for missing translations in production
