# Rule: Media Optimization for All Projects

**ALWAYS** apply the Media Optimization skill and workflow when handling images, video, and audio. Media is the biggest performance bottleneck — never serve an image larger than what's displayed.

## Skill
`~/.codeium/windsurf/skills/media-optimization.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/media.md` — invoke with `/media`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/media-optimizer.md` (parent: Design Engineer)

## How to follow this rule:
1. When optimizing media, invoke the `/media` workflow
2. Follow the workflow steps in order: Audit → Formats → Responsive → Lazy Loading → CLS → CDN → Video → Audio → Build → Accessibility → Measure
3. Always use AVIF as primary format with WebP and JPEG fallbacks
4. Always use `srcset` and `sizes` for responsive images — never serve oversized images
5. Always set `width` and `height` on images to prevent CLS
6. Always lazy load below-fold images and prioritize LCP images
7. Always convert GIFs to video (10-100x smaller, better quality)
8. Always provide alt text for accessibility and track Core Web Vitals impact

## When this rule applies:
- Optimizing media for a web project
- Improving Core Web Vitals related to media (LCP, CLS)
- Setting up responsive images or CDN
- Converting legacy media to modern formats
- User asks about image, video, or audio optimization

## When this rule does NOT apply:
- Projects with no media content (text-only sites)
- User explicitly says to skip media optimization
