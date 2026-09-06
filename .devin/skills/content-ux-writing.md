---
name: Content & UX Writing Skill
description: Comprehensive methodology for crafting user-centered content and UX copy — 2025-2026 practices with voice/tone systems, microcopy patterns, content architecture, and AI-aware content strategy
version: 1.0.0
tags: [content, ux-writing, microcopy, voice-tone, content-strategy, copywriting, messaging]
---

# Content & UX Writing Skill

## Purpose
This skill provides a comprehensive methodology for crafting user-centered content and UX copy across any kind of web project. It reflects **modern 2025-2026 content practices** — voice and tone systems, microcopy patterns, content architecture, accessibility-first writing, and AI-aware content strategy. Not just filling in text but designing content as a user experience.

## Core Philosophy

**Content is the interface.** Users don't read — they scan, they search, they act. Every word either helps them accomplish their goal or gets in their way. Good UX writing is invisible: users don't notice it, they just succeed.

**The #1 rule:** Write for the user, not for the brand. Users don't care about your brand voice — they care about completing their task. Help them do that as quickly and clearly as possible.

---

## Part 1: Voice & Tone Systems

### 1.1 Defining Brand Voice
- **Voice is constant:** Your brand personality doesn't change — it's who you are
- **Tone adapts:** How you express your voice changes based on context and user emotion
- **Voice attributes:** Choose 3-5 adjectives (e.g., confident, warm, clear, practical, human)
- **Anti-attributes:** What you're NOT (e.g., not corporate, not playful, not jargon-heavy)
- **Voice examples:** Show do/don't pairs for each attribute

### 1.2 Tone Matrix by Context

| Context | Tone | Example |
|---|---|---|
| **Onboarding** | Welcoming, encouraging | "Welcome! Let's set up your account." |
| **Success** | Confirming, positive | "Saved! Your changes are live." |
| **Error** | Empathetic, helpful | "Something went wrong. Here's what you can do." |
| **Warning** | Clear, cautious | "This will delete all your data. This can't be undone." |
| **Empty state** | Encouraging, informative | "No projects yet. Create your first one!" |
| **Loading** | Reassuring, brief | "Loading your dashboard..." |
| **Payment** | Trustworthy, precise | "You'll be charged $12/month. Cancel anytime." |
| **Danger** | Serious, direct | "Deleting your account is permanent." |

### 1.3 Voice Guidelines
- **Write like a person:** Use contractions (you're, we're, don't), not "you are", "we are", "do not"
- **Be direct:** "Save changes" not "You can save your changes"
- **Be positive:** "Save" not "Don't forget to save"
- **Be specific:** "3 items deleted" not "Items were deleted"
- **Be honest:** Don't over-promise, don't minimize problems
- **Be human:** Acknowledge frustration, don't be robotic
- **Avoid humor in errors:** Users are frustrated — don't make jokes

### 1.4 Tone in Error Messages
- **Acknowledge:** "We couldn't process your payment"
- **Explain (if known):** "Your card was declined"
- **Guide:** "Try a different card or contact your bank"
- **Don't blame:** "Something went wrong" not "You entered the wrong value"
- **Don't apologize excessively:** One "sorry" is enough — focus on the solution
- **Be specific:** "Email is required" not "An error occurred"

### 1.5 Tone in Empty States
- **Explain what's empty:** "No projects yet"
- **Explain why:** "Projects you create will appear here"
- **Provide next action:** "Create your first project" (button)
- **Be encouraging:** "Let's get started!" not "You have no projects"
- **Use illustrations:** Visual element + text is more engaging than text alone

---

## Part 2: Microcopy Patterns

### 2.1 Button Copy
- **Action-oriented:** "Save changes" not "Submit" or "OK"
- **Specific:** "Delete project" not "Delete" or "Confirm"
- **Verb-first:** "Create account" not "Account creation"
- **Short:** 1-3 words max
- **No jargon:** "Send email" not "Dispatch communication"

| Bad | Good |
|---|---|
| Submit | Save changes |
| Cancel | Discard changes |
| OK | Got it |
| Click here | View pricing |
| Learn more | Read the docs |

### 2.2 Form Labels and Helper Text
- **Labels:** Noun phrase, short, clear — "Email address" not "Enter your email address here"
- **Helper text:** Below the field, explains format or provides context — "We'll send a confirmation link"
- **Placeholder:** Example value, NOT a label replacement — "you@example.com"
- **Required indicator:** Asterisk + "Required" text for screen readers
- **Validation:** Real-time, specific, actionable — "Password must be at least 12 characters"

### 2.3 Error Messages
- **Specific:** "Email address is required" not "Error"
- **Actionable:** "Please enter a valid email address" not "Invalid input"
- **Blameless:** "We couldn't find an account with that email" not "You entered the wrong email"
- **Helpful:** "Passwords must be 12+ characters with one number" not "Password too weak"
- **Positioned:** Near the field, not at the top of the form
- **Accessible:** `role="alert"`, `aria-describedby`, announced to screen readers

### 2.4 Success Messages
- **Confirm what happened:** "Your profile has been updated"
- **Be specific:** "3 photos uploaded successfully" not "Done!"
- **Next steps (if any):** "Your post is live. Share it with your followers."
- **Duration:** Auto-dismiss after 5 seconds, but allow manual close
- **Non-blocking:** Toast/notification, not a modal (unless action is required)

### 2.5 Confirmation Dialogs
- **Clear consequence:** "Delete 'Project Alpha'? This will permanently remove all files and data."
- **Specific action button:** "Delete project" not "OK" or "Confirm"
- **Destructive styling:** Red for destructive action, neutral for cancel
- **No accidental triggers:** Require explicit click, not hover or auto-focus on destructive button
- **Undo if possible:** "Project deleted. Undo" (toast with undo action)

### 2.6 Loading States
- **Set expectations:** "Loading..." → "Loading your dashboard..." → "Almost there..."
- **Skeleton screens:** Show content structure, not just a spinner — perceived performance
- **Progress indicators:** "Uploading 3 of 7 files..." for multi-step operations
- **Don't block unnecessarily:** Load content progressively if possible
- **Error on timeout:** "This is taking longer than expected. Try again."

### 2.7 Tooltips and Help Text
- **When to use:** For clarification, not critical information — critical info should be visible
- **Keep it short:** 1-2 sentences max
- **Trigger:** Hover (desktop) and tap (mobile) — but don't rely on hover for mobile
- **Accessible:** `aria-describedby` linking to tooltip content, or use a tooltip library
- **Don't repeat:** Don't put the same text in the label and the tooltip

### 2.8 Navigation Labels
- **Clear and familiar:** "Home", "About", "Contact", "Pricing" — don't get creative with nav
- **Short:** 1-2 words
- **Consistent:** Same label across the site, same URL
- **Descriptive:** "Blog" not "Thoughts", "Documentation" not "Resources"
- **User language:** Use terms your users use, not internal jargon

### 2.9 Link Copy
- **Descriptive:** "Read our pricing guide" not "Click here" or "Learn more"
- **Action-oriented:** "Download the report" not "Report (PDF)"
- **Indicates destination:** "View our privacy policy" tells users where they're going
- **Screen reader friendly:** "Click here" is meaningless out of context — descriptive links work everywhere
- **External links:** Indicate when leaving the site — "Read on Wikipedia ↗"

### 2.10 Placeholder vs Label
- **Label:** Always present, always visible, always accessible — "Email"
- **Placeholder:** Example value that disappears — "you@example.com"
- **Never use placeholder as label:** It disappears on input, low contrast, not accessible
- **Placeholder is optional:** If the label is clear, you may not need a placeholder at all
- **WCAG:** Labels must be persistent and programmatically associated

---

## Part 3: Content Architecture

### 3.1 Information Hierarchy
```
H1: Page Title (one per page)
  H2: Major Section
    H3: Subsection
      H4: Detail
  H2: Another Major Section
```
- **One H1:** Main page heading — describes the page
- **H2 for sections:** Major content areas
- **H3-H6 for subsections:** Don't skip levels
- **Headings for structure:** Not for visual size — use CSS for styling
- **Screen reader navigation:** Headings are the primary navigation method

### 3.2 Content Grouping
- **Related content together:** Don't interleave unrelated sections
- **Progressive disclosure:** Summary first, details on demand
- **Visual grouping:** Use whitespace, borders, or backgrounds to group related content
- **Logical flow:** Introduction → Details → Summary → Next steps
- **Inverted pyramid:** Most important information first

### 3.3 Page Structure Patterns
- **Landing page:** Hero → Value props → Social proof → Features → CTA
- **Product page:** Title → Image → Price → Description → Specs → Reviews → CTA
- **Blog post:** Title → Meta → Intro → Body → Conclusion → Author bio → Related
- **Documentation:** Title → Overview → Quick start → Details → Examples → API reference
- **Dashboard:** Header → Key metrics → Charts → Recent activity → Actions

### 3.4 Content Templates
- **Reusable:** Create templates for common page types
- **Consistent:** Same structure across similar pages
- **Placeholder text:** Show what content goes where
- **Content model:** Define fields, types, and relationships
- **CMS-friendly:** Structure content for headless CMS or markdown

### 3.5 Content Reuse and Modularity
- **Components:** Reusable content blocks (CTA, testimonial, feature card)
- **Snippets:** Reusable text fragments (company description, value proposition)
- **Variables:** Dynamic content (user name, plan type, date)
- **Conditional content:** Show different content based on user state, plan, location
- **Centralized:** Single source of truth for shared content — update once, reflects everywhere

---

## Part 4: SEO Content Strategy

### 4.1 Keyword Integration (Natural, Not Stuffed)
- **Primary keyword:** In H1, first paragraph, meta title, URL
- **Secondary keywords:** In H2s, body content, image alt text
- **Long-tail keywords:** In FAQ sections, blog posts, guides
- **Natural placement:** Read the sentence aloud — if it sounds forced, rewrite
- **No keyword stuffing:** "Our SEO services are the best SEO services for SEO" — never

### 4.2 Meta Descriptions
- **Length:** 150-160 characters (desktop), 120 characters (mobile)
- **Compelling:** Include value proposition and CTA
- **Keyword:** Include primary keyword naturally
- **Unique:** Different for each page
- **Not a ranking factor directly:** But affects CTR from search results

### 4.3 Heading Hierarchy for SEO
- **H1:** Primary keyword, page title — one per page
- **H2:** Secondary keywords, major sections
- **H3:** Related terms, subsections
- **Hierarchy:** Logical nesting, no skipped levels
- **Don't style with headings:** Use CSS for visual size, headings for structure

### 4.4 Content-Length Guidelines
- **Homepage:** 300-500 words — concise, focused on conversion
- **Product page:** 500-1000 words — features, benefits, specs, reviews
- **Blog post:** 1500-3000 words — comprehensive, valuable, shareable
- **Landing page:** 500-1500 words — focused on one conversion goal
- **Documentation:** As long as needed — completeness over brevity
- **Quality > quantity:** 500 words of value > 3000 words of fluff

### 4.5 Featured Snippet Optimization
- **Question → Answer format:** "What is X? X is..."
- **40-60 word answer:** Concise, self-contained answer block
- **List format:** Numbered or bulleted lists for process/comparison content
- **Table format:** Data in tables for comparison content
- **Position:** Answer immediately after the question heading

### 4.6 Internal Linking Strategy
- **Contextual:** Link within content, not just "related posts" at the bottom
- **Descriptive anchor text:** "Learn about our pricing model" not "click here"
- **Relevant:** Link to related, relevant content
- **Not excessive:** 3-5 internal links per page, not 30
- **Hub pages:** Link to and from hub/category pages to build topical authority

---

## Part 5: AI-Aware Content (GEO)

### 5.1 Passage-Level Optimization
- **Self-contained passages:** 200-400 tokens that make sense on their own
- **Answer-first structure:** Lead with the answer, then elaborate
- **Heading-to-passage anchoring:** Heading clearly describes the passage content
- **Citation-worthy formatting:** Facts, statistics, definitions that AI can cite
- **No fluff:** AI models extract information — make it extractable

### 5.2 Entity-Based Content
- **Define entities:** "Acme Corp is a B2B SaaS company founded in 2020"
- **Consistent facts:** Same name, same founding date, same locations everywhere
- **Schema.org:** `Organization`, `Product`, `FAQPage`, `HowTo` structured data
- **sameAs:** Link to Wikidata, Wikipedia, Crunchbase, LinkedIn
- **Brand facts:** Keep a canonical list of brand facts for consistency

### 5.3 Question-Based Content Structuring
- **FAQ sections:** Natural questions users ask
- **Question headings:** "How does X work?" as H2/H3
- **Direct answers:** Immediately after the question, 40-60 words
- **Elaborate below:** Details, examples, caveats after the direct answer
- **FAQ schema:** `FAQPage` structured data for rich results

### 5.4 Citation-Worthy Formatting
- **Statistics:** "According to a 2025 study by..."
- **Definitions:** "X is defined as..."
- **Comparisons:** "Compared to Y, X offers..."
- **Step-by-step:** Numbered lists for processes
- **Data tables:** Comparison tables with clear headers
- **Quotes:** Expert quotes with attribution

### 5.5 llms.txt File
```
# Title
> Brief description

## About
Company description and key facts

## Products
Product descriptions with links

## Documentation
Links to docs, API references, guides

## FAQ
Common questions and answers
```
- Place at root: `/llms.txt`
- Provides AI models with structured information about your site
- Include key facts, product info, documentation links

---

## Part 6: Accessibility in Content

### 6.1 Plain Language
- **Short sentences:** 15-20 words average
- **Common words:** Avoid jargon — define if necessary
- **Active voice:** "We updated your settings" not "Your settings were updated"
- **Second person:** "You can..." not "Users can..."
- **Reading level:** Aim for 8th grade (Flesch-Kincaid 60+)
- **Tools:** Hemingway Editor, Grammarly

### 6.2 Alt Text for Images
- **Describe the image:** "Chart showing 40% growth in Q3 2025"
- **Context matters:** Alt text depends on why the image is there
- **Decorative images:** `alt=""` (empty) — screen readers skip them
- **Informative images:** Describe what the image conveys
- **Functional images:** Describe the function — "Search" for a magnifying glass icon
- **Charts/graphs:** Include data summary in alt text or adjacent text
- **No "image of":** Screen readers already announce "image" — just describe

### 6.3 Descriptive Link Text
- **Describes destination:** "Read our privacy policy" not "Click here"
- **Action-oriented:** "Download the PDF" not "PDF"
- **Unique:** Don't have multiple "Learn more" links on the same page
- **Screen reader context:** Links are read out of context — must be self-describing
- **Length:** Under 100 characters, but descriptive enough to understand

### 6.4 Heading Structure for Screen Readers
- **Navigation:** Screen reader users navigate by headings
- **Descriptive:** "Pricing Plans" not "Section 3"
- **Hierarchical:** H2 under H1, H3 under H2 — no skipping
- **Informative:** Heading should tell user what the section is about
- **Concise:** 3-7 words typically

### 6.5 Form Content for Accessibility
- **Labels:** Every input has a visible, persistent label
- **Instructions:** Before the form, not after the field
- **Error messages:** Specific, actionable, blameless, near the field
- **Required indicators:** Not just color — use text "*" and `aria-required`
- **Autocomplete:** Use `autocomplete` attributes for common fields
- **Grouping:** `<fieldset>` + `<legend>` for radio/checkbox groups

### 6.6 Reading Level and Cognitive Load
- **Target:** 8th grade for general audience
- **Lower for critical info:** 5th-6th grade for emergency/safety
- **Short paragraphs:** 3-5 sentences max
- **Bullet points:** Break up long lists
- **Bold key terms:** Help scanners find important info
- **Summary first:** TL;DR at the top for long content

---

## Part 7: Content for Different Page Types

### 7.1 Landing Page Copy
- **Hero headline:** Clear value proposition, 5-10 words — "Build better websites, faster"
- **Hero subheadline:** Expand on headline, 10-20 words — "The AI-powered platform that helps teams design, build, and ship."
- **CTA:** Action-oriented, specific — "Start free trial" not "Submit"
- **Social proof:** "Trusted by 10,000+ teams" — logos, testimonials, metrics
- **Features:** Benefit-first, not feature-first — "Save hours every week" not "Automated workflows"
- **Objection handling:** FAQ section addressing common concerns
- **Final CTA:** Repeat the primary CTA at the bottom

### 7.2 Product Page Copy
- **Product name:** Clear, memorable
- **Tagline:** One-line description — "The simplest way to manage your projects"
- **Description:** 2-3 paragraphs, benefit-first, then features
- **Specs:** Technical details in a table or list
- **Pricing:** Clear, transparent, no hidden fees
- **Reviews/testimonials:** Social proof from real users
- **CTA:** "Add to cart" or "Start free trial"
- **FAQ:** Common questions about the product

### 7.3 Onboarding Flow Copy
- **Welcome:** "Welcome to [Product]! Let's get you set up."
- **Step indicators:** "Step 1 of 4: Tell us about yourself"
- **Progress:** "You're almost done! Just one more step."
- **Completion:** "All set! Here's what you can do next."
- **Skip option:** "Skip for now — you can complete this later"
- **Encouraging:** "Great! Now let's connect your calendar."
- **No jargon:** Don't use technical terms in onboarding

### 7.4 Empty State Copy
- **What's empty:** "No projects yet"
- **Why:** "Projects you create will appear here"
- **What to do:** "Create your first project" (button)
- **Encouragement:** "Let's get started!"
- **Illustration:** Visual element to make it less stark
- **Help link:** "Learn how to create a project" for users who need guidance

### 7.5 Error State Copy
- **What happened:** "We couldn't save your changes"
- **Why (if known):** "Your session expired"
- **What to do:** "Please log in and try again"
- **Reassurance:** "Your work is saved as a draft"
- **Support:** "Still having trouble? Contact support"
- **No blame:** Never "You did X wrong" — always "We couldn't" or "Something went wrong"

### 7.6 Email Copy
- **Subject line:** Clear, specific, not clickbait — "Your weekly project summary"
- **Preview text:** Complements subject line, 35-90 characters
- **Body:** Concise, scannable, action-oriented
- **CTA:** One primary action per email — "View your dashboard"
- **Personalization:** Use name, but don't overdo it
- **Unsubscribe:** Clear, easy, one-click — don't hide it
- **Plain text version:** Always provide a text alternative

### 7.7 Notification Copy
- **Push notifications:** 40-50 characters, clear, actionable — "Your order has shipped! Track it here."
- **In-app notifications:** Brief, informative, with action — "Sarah commented on your task"
- **SMS:** 160 characters max, clear, include opt-out — "Your code expires in 5 min. Reply STOP to opt out."
- **Slack/Teams:** Rich formatting, actionable buttons, concise summary

---

## Part 8: Writing Process

### 8.1 Content Audit
- **Inventory:** List all pages, their purpose, their content
- **Evaluate:** Is content accurate? Current? Useful? Well-written?
- **Identify gaps:** What's missing? What questions aren't answered?
- **Identify redundancies:** Duplicate content, conflicting information
- **Prioritize:** Fix high-traffic pages first, then high-impact pages

### 8.2 Content Calendar
- **Plan:** What content to create, when, by whom
- **Themes:** Monthly/quarterly content themes
- **Cadence:** How often to publish — be realistic
- **Channels:** Where content will be published (blog, docs, social, email)
- **Dependencies:** Content that depends on product launches, events, seasons

### 8.3 Review and Approval
- **Draft:** Write first draft
- **Review:** Check for accuracy, clarity, brand voice, SEO
- **Edit:** Refine language, structure, flow
- **Approve:** Stakeholder sign-off
- **Publish:** Schedule and publish
- **Measure:** Track performance, iterate

### 8.4 Content Maintenance
- **Review schedule:** Quarterly review of key pages
- **Update:** Keep content current — dates, prices, features, links
- **Archive:** Remove or archive outdated content
- **Redirect:** Set up redirects for removed pages
- **Monitor:** Check for broken links regularly

### 8.5 Style Guide
- **Voice and tone:** Document brand voice attributes and tone matrix
- **Grammar rules:** Oxford comma, capitalization, abbreviations
- **Terminology:** Product names, feature names, industry terms
- **Formatting:** Headings, lists, bold, links, images
- **Do/Don't pairs:** Examples of good and bad copy
- **Word list:** Preferred terms (e.g., "log in" not "login", "website" not "web site")

---

## Part 9: Internationalization Considerations

### 9.1 Writing for Translation
- **Short sentences:** Easier to translate accurately
- **No idioms:** "Ballpark figure" doesn't translate — use "approximate number"
- **No cultural references:** Sports, holidays, pop culture may not translate
- **Consistent terminology:** Use the same word for the same concept
- **Avoid phrasal verbs:** "Look up" → "search", "figure out" → "determine"
- **No abbreviations:** "ASAP" → "as soon as possible"

### 9.2 Text Expansion
- **German:** +30% character count
- **French:** +15-20%
- **Spanish:** +20-25%
- **Design for expansion:** UI must accommodate longer text
- **Flexible layouts:** Don't hardcode widths based on English text

### 9.3 Date/Time/Number Formats
- **Dates:** Use `Intl.DateTimeFormat` — don't hardcode "MM/DD/YYYY"
- **Times:** Use timezone-aware formatting
- **Numbers:** Use `Intl.NumberFormat` — decimal separators, thousands separators vary
- **Currency:** Use `Intl.NumberFormat` with currency option
- **Pluralization:** Use `Intl.PluralRules` — not all languages have simple plural rules

### 9.4 RTL (Right-to-Left) Considerations
- **Arabic, Hebrew:** Text flows right to left
- **CSS logical properties:** `margin-inline-start` not `margin-left`
- **Icons:** Directional icons may need to flip
- **Layout:** Flexbox/Grid with logical properties handles RTL automatically
- **Test:** Set `dir="rtl"` and test the layout

---

## Part 10: Measuring Content Effectiveness

### 10.1 Content Metrics
- **Time on page:** Are users reading? (but note: scanning is normal)
- **Bounce rate:** Are users leaving immediately? (may indicate poor content or wrong audience)
- **Scroll depth:** Are users reaching key content?
- **Conversion rate:** Does the content drive action?
- **Search queries:** What are users searching for on your site?
- **Heatmaps:** Where are users clicking and scrolling?

### 10.2 User Testing Content
- **5-second test:** Can users understand the page after 5 seconds?
- **Cloze test:** Remove every 5th word — can users fill in the blanks?
- **Read-aloud:** Users read content aloud — where do they stumble?
- **Comprehension:** After reading, can users explain what they learned?
- **Task completion:** Can users complete a task using the content?

### 10.3 A/B Testing Copy
- **One variable:** Test one change at a time (headline, CTA, etc.)
- **Sample size:** Ensure statistical significance
- **Duration:** Run for at least 1-2 weeks
- **Primary metric:** What are you optimizing for? (CTR, conversion, engagement)
- **Document:** Record what was tested, results, and learnings

### 10.4 Content ROI
- **Organic traffic:** SEO content drives free traffic
- **Conversion rate:** Content that converts is worth more
- **Support reduction:** Good content reduces support tickets
- **Brand authority:** Content builds trust and authority
- **Customer retention:** Educational content keeps users engaged

---

## Execution Instructions for Cascade

When this skill is activated for content & UX writing:

1. **Read the project context** — purpose, audience, brand, existing content
2. **Define voice and tone** — brand attributes, tone matrix, do/don't examples
3. **Audit existing content** — inventory, evaluate, identify gaps and redundancies
4. **Create content architecture** — information hierarchy, page structure, templates
5. **Write microcopy** — buttons, forms, errors, empty states, loading states
6. **Write page content** — landing pages, product pages, onboarding, documentation
7. **Optimize for SEO** — keywords, meta descriptions, headings, internal links
8. **Optimize for AI search** — passage-level, entity-based, question-based, llms.txt
9. **Ensure accessibility** — plain language, alt text, descriptive links, reading level
10. **Plan for i18n** — write for translation, account for text expansion, RTL support
11. **Create style guide** — voice, grammar, terminology, formatting, do/don't pairs
12. **Measure and iterate** — metrics, user testing, A/B testing, content ROI
