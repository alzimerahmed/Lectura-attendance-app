# Rule: Pixel-Perfect Image Analysis & Visual Diff for Image Comparison

**ALWAYS** apply the Pixel-Perfect Image Analysis skill and workflow when the user wants to analyze an image at the pixel level or compare/differentiate between two images. Never claim visual match without running pixel-level metrics — measure, don't guess.

## Skill
`~/.codeium/windsurf/skills/pixel-perfect-image-analysis.md`
Project-local: `.devin/skills/pixel-perfect-image-analysis.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/pixel-perfect-image-analysis.md` — invoke with `/pixel-perfect`, `/pixel-diff`, `/image-diff`
Project-local: `.devin/workflows/pixel-perfect-image-analysis.md`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/pixel-analyst.md` (parent: Quality Engineer, Design Engineer)
Project-local: `.devin/sub-agents/pixel-analyst.md`

## Main Agent
`~/.codeium/windsurf/windsurf/agents/quality-engineer.md` (primary), `~/.codeium/windsurf/windsurf/agents/design-engineer.md` (secondary)
Project-local: `.devin/agents/quality-engineer.md`, `.devin/agents/design-engineer.md`

## How to follow this rule:
1. When the user asks to compare two images, invoke the `/pixel-perfect-image-analysis` workflow
2. For single image analysis: follow Mode A — extract metadata, channel stats, palette, layout, perceptual characteristics
3. For two image comparison: follow Mode B — align, pixelmatch diff, SSIM, region detection, diff image, report
4. For design clone verification: follow Mode C — strict threshold (0.05), fidelity assessment, fix list, iterate
5. For Playwright visual regression: follow Mode D — baseline management, comparison, pass/fail report
6. Always specify the threshold used and whether the match is pixel-level or perceptual-level
7. Always generate a diff image — visual evidence is required for any diff claim
8. Always classify region severity — "some pixels differ" is not actionable
9. Never claim "pixel-perfect" unless mismatchedPixels = 0 with threshold 0.0
10. Never claim "100% match" if any residual diffs remain — list them honestly
11. Use `sharp` for metadata/stats/raw pixel extraction, `pixelmatch` for pixel diff, SSIM for perceptual quality
12. Save all reports to `docs/pixel-analysis/`

## When this rule applies:
- User asks to "compare these images" or "what changed between these"
- User says `/pixel-perfect`, `/pixel-diff`, `/image-diff`
- User asks to "analyze this image" at the pixel level
- Design clone verification — comparing reference vs implementation
- Visual regression detection between builds or versions
- User asks "does this match the design?" or "is this pixel perfect?"
- Verifying lossless image processing (compression, resize, format conversion)
- Extracting design tokens (colors, dimensions) from an image
- Quality Engineer runs a visual regression gate
- Design Engineer verifies clone fidelity
- User asks to extract colors, palette, or layout structure from an image
- User provides two screenshots and asks for differences

## When this rule does NOT apply:
- Semantic image comparison ("is this the same dog?") — that's a vision-model job, not pixel diff
- User asks for design improvements (use Claude Taste / frontend-designer)
- User asks to clone a design (use Playwright Design Clone workflow first, then verify with this skill)
- Backend-only changes with no visual impact
- User explicitly says to skip pixel analysis
