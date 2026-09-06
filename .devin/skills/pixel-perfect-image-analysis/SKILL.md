---
name: pixel-perfect-image-analysis
description: Pixel-perfect image analysis and visual diff — read images at the pixel level, extract exact visual data, and compare two images to identify, quantify, classify, and visualize every difference
---

# Pixel-Perfect Image Analysis & Visual Diff Workflow

**Activation**: `/pixel-perfect`, `/pixel-diff`, `/image-diff`, "compare these images", "pixel perfect", "what changed between these images", "analyze this image"

**Skill**: `pixel-perfect-image-analysis.md`

## When to Run
- Comparing a design screenshot against an implementation
- Verifying pixel-perfect clone fidelity
- Detecting visual regressions between builds
- Analyzing an image's color palette, dimensions, and structure
- User provides two images and asks "what's different"
- QA gate before shipping visual changes
- Validating lossless image processing
- Extracting design tokens from an image

---

## Mode A — Single Image Analysis

### Step 1: Load & Extract Metadata

1. Read the image file to confirm it exists and is a valid image format
2. Extract metadata using `sharp().metadata()`:
   - Format, width, height, channels, depth, color space, density, alpha, EXIF orientation, pages
3. Record all metadata in the analysis output
4. If the image is animated (GIF/WebP/AVIF), note frame count and delays

### Step 2: Compute Channel Statistics

1. Run `sharp().stats()` to get per-channel statistics:
   - Min, max, mean, standard deviation per channel (R, G, B, Alpha)
   - Min/max pixel coordinates
2. Extract whole-image metrics:
   - `isOpaque` — whether image has any transparency
   - `entropy` — information density score
   - `sharpness` — edge intensity estimate
   - `dominant` — most dominant sRGB color { r, g, b }

### Step 3: Extract Color Palette

1. Extract dominant color from `stats().dominant`
2. Generate full 6-color palette via quantization:
   - Primary, secondary, accent, background, text candidate, supplementary
3. For each palette entry, compute: hex, RGB, HSL, population count, population percentage
4. Identify color temperature (warm vs cool) from palette balance

### Step 4: Detect Layout Structure

1. Decode image to raw pixel data using `sharp().raw().toBuffer()`
2. Compute row projection profile (average luminance per row)
3. Compute column projection profile (average luminance per column)
4. Detect horizontal bands — rows with similar luminance (sections, headers, footers)
5. Detect vertical alignment — columns with content vs whitespace
6. Identify content regions — bounding boxes of non-background areas
7. Identify whitespace regions — large uniform areas (padding, margins)
8. Detect text-heavy regions — high-frequency vertical edges
9. Detect image/photo regions — low-frequency smooth areas

### Step 5: Compute Perceptual Characteristics

1. Calculate average luminance (perceived brightness, 0-255)
2. Determine color temperature (warm/cool/neutral)
3. Compute average saturation and saturation distribution
4. Calculate contrast ratio (brightest vs darkest regions)
5. Compute visual complexity score (entropy + edge density)

### Step 6: Compile Single-Image Report

Save as `docs/pixel-analysis/<imagename>-report.json`:
```json
{
    "image": { "path", "format", "width", "height", "channels", "colorSpace", "hasAlpha", "density" },
    "statistics": { "perChannel": { r, g, b, alpha }, "isOpaque", "entropy", "sharpness" },
    "palette": [{ "hex", "rgb", "hsl", "population", "populationPct", "role" }],
    "layout": { "horizontalBands", "contentRegions", "whitespaceRegions", "textRegions", "imageRegions" },
    "perceptual": { "averageLuminance", "colorTemperature", "avgSaturation", "contrastRatio", "visualComplexity" },
    "timestamp": "ISO string"
}
```

---

## Mode B — Two Image Comparison (Visual Diff)

### Step 1: Load Both Images & Extract Metadata

1. Read both image files, confirm they exist and are valid
2. Extract metadata for both images using `sharp().metadata()`
3. Compare dimensions:
   - If equal → proceed to Step 2
   - If different → determine alignment strategy (see Step 1b)
4. Record both sets of metadata

### Step 1b: Alignment Strategy (only if dimensions differ)

1. Determine alignment mode based on user intent:
   - **strict** (default for pixel-perfect) — error if dimensions differ
   - **crop** — center-crop larger image to match smaller
   - **scale** — resize larger image to match smaller (note: introduces interpolation artifacts)
   - **letterbox** — pad smaller image with background color to match larger
2. Apply alignment to produce two equally-sized raw RGBA buffers
3. Record `dimensionMismatch` in report: `{ widthDiff, heightDiff, alignmentMode }`

### Step 2: Pixel-Level Diff (pixelmatch)

1. Decode both images to raw RGBA `Uint8Array` using `sharp().ensureAlpha().raw().toBuffer()`
2. Run `pixelmatch(img1, img2, output, width, height, options)`:
   - `threshold`: 0.1 (default), 0.0 for strict, 0.05 for design clone verification
   - `includeAA`: false (default — ignore anti-aliased edge pixels)
   - `alpha`: 0.1 (diff image brightness for unchanged pixels)
   - `diffColor`: [255, 0, 0] (red for differing pixels)
   - `aaColor`: [255, 255, 0] (yellow for anti-aliased pixels)
3. Record:
   - `mismatchedPixels` — count of differing pixels
   - `totalPixels` — width × height
   - `mismatchPercentage` — (mismatched / total) × 100
   - `match` — boolean (true if mismatchedPixels === 0)

### Step 3: Perceptual Quality Score (SSIM)

1. Convert both images to luma channel
2. Compute SSIM using 11×11 Gaussian sliding window:
   - Luminance comparison, contrast comparison, structure comparison
3. Record mean SSIM score (-1 to 1, where 1 = identical)
4. If SSIM > 0.95 → images are perceptually near-identical
5. If SSIM 0.80-0.95 → minor perceptual differences
6. If SSIM < 0.80 → significant perceptual differences

### Step 4: Region Detection & Classification

1. Build binary diff mask from pixelmatch results (1 = different, 0 = same)
2. **Denoise** — remove clusters smaller than `denoise` threshold (default 25px)
3. **Dilate** — optionally expand mask by `dilate` pixels to bridge nearby changes
4. **Connected component labeling** — group adjacent diff pixels into regions using 8-connectivity
5. **Merge** — combine regions within `mergeDistance` (default 50px) of each other
6. **Filter** — remove regions smaller than `minRegionSize` (default 25px)
7. **Classify** each region:
   - `added` — content in image 2 but not image 1
   - `removed` — content in image 1 but not image 2
   - `color-change` — same position/shape, different colors
   - `content-change` — same region, different content
   - `layout-shift` — content moved position
8. **Assess severity** per region:
   - `critical` — >5% of total pixels or >10,000px
   - `major` — 1-5% or 1,000-10,000px
   - `minor` — 0.1-1% or 100-1,000px
   - `negligible` — <0.1% or <100px

### Step 5: Generate Diff Image

1. Use pixelmatch output buffer (RGBA with red highlights on diffs)
2. Save as `docs/pixel-analysis/diff.png` using `sharp().png().toFile()`
3. Optionally generate side-by-side composite (image1 | image2 | diff) for human review

### Step 6: Compile Diff Report

Save as `docs/pixel-analysis/diff-report.json`:
```json
{
    "summary": {
        "match": false,
        "mismatchedPixels": 4312,
        "totalPixels": 921600,
        "mismatchPercentage": 0.47,
        "ssimScore": 0.987,
        "maxDeltaY": 0.83,
        "avgDeltaY": 0.24,
        "dimensionMismatch": null
    },
    "regions": [
        {
            "id": 1,
            "boundingBox": { "x": 120, "y": 340, "width": 200, "height": 60 },
            "label": "color-change",
            "pixelCount": 3200,
            "pixelPercentage": 0.35,
            "severity": "major"
        }
    ],
    "image1": { "path", "format", "width", "height", "channels", "dominantColor", "entropy", "sharpness" },
    "image2": { "path", "format", "width", "height", "channels", "dominantColor", "entropy", "sharpness" },
    "diffImagePath": "docs/pixel-analysis/diff.png",
    "options": { "threshold": 0.1, "includeAA": false, "alignmentMode": "strict" },
    "timestamp": "ISO string"
}
```

### Step 7: Present Results to User

1. Show summary: match/no-match, mismatch percentage, SSIM score
2. List regions sorted by severity (critical → negligible)
3. Show diff image path for visual review
4. Provide actionable guidance:
   - For design clone verification: list specific regions to fix
   - For regression detection: identify what changed and where
   - For lossless verification: confirm 0% mismatch or quantify quality loss

---

## Mode C — Design Clone Verification

When invoked to verify a design clone against a reference:

### Step 1: Capture Screenshots
1. If comparing live URLs, use Playwright to screenshot both at identical viewport
2. If comparing image files, use them directly
3. Ensure both screenshots are at the same viewport size and device pixel ratio

### Step 2: Run Full Diff (Mode B)
1. Use strict threshold (0.05) for design clone verification
2. Generate full report with region detection

### Step 3: Fidelity Assessment
Classify overall fidelity:
- **near-perfect** — mismatch < 1%
- **good** — mismatch 1-5%
- **fair** — mismatch 5-15%
- **poor** — mismatch > 15%

### Step 4: Actionable Fix List
For each region with severity `major` or `critical`:
1. Describe what differs (color change, content change, layout shift, etc.)
2. Provide bounding box coordinates
3. Suggest what to inspect in the code to fix it
4. Prioritize fixes by severity

### Step 5: Iterate
1. After fixes, re-screenshot and re-run diff
2. Compare new mismatch percentage against previous
3. Stop when mismatch < target threshold (default 1% for clones)

---

## Mode D — Playwright Visual Regression

When used as a visual regression gate in Playwright tests:

### Step 1: Baseline Management
1. First run: capture screenshot, save as baseline
2. Subsequent runs: capture screenshot, compare against baseline

### Step 2: Comparison
Use Playwright's built-in `toHaveScreenshot()`:
```js
await expect(page).toHaveScreenshot('name.png', {
    maxDiffPixelRatio: 0.001,
    threshold: 0.1,
    animations: 'disabled',
});
```

Or run custom comparison:
1. Screenshot current page
2. Load baseline
3. Run pixelmatch diff
4. Check against thresholds

### Step 3: Report
1. If pass: log "Visual regression: PASS (N pixels diff, X%)"
2. If fail: save diff image, list changed regions, fail the test

---

## Quick Reference: Tool Usage

| Goal | Tool | Method |
|------|------|--------|
| Read image metadata | `sharp` | `sharp(path).metadata()` |
| Read pixel statistics | `sharp` | `sharp(path).stats()` |
| Get raw pixel data | `sharp` | `sharp(path).ensureAlpha().raw().toBuffer({ resolveWithObject: true })` |
| Pixel-level diff | `pixelmatch` | `pixelmatch(img1, img2, output, w, h, options)` |
| Perceptual quality | SSIM | Custom implementation or library |
| Generate diff image | `sharp` | `sharp(diffBuffer, { raw: { width, height, channels: 4 } }).png().toFile(path)` |
| Playwright screenshot | Playwright | `page.screenshot({ path, fullPage })` |
| Playwright visual test | Playwright | `expect(page).toHaveScreenshot(name, options)` |
| Read image visually | `read_file` | Cascade reads image files and presents them visually |

---

## Completion Checklist

### Single Image Analysis
- [ ] Image loaded and validated
- [ ] Metadata extracted (format, dimensions, channels, color space)
- [ ] Channel statistics computed (min/max/mean/stdev per channel)
- [ ] Dominant color and palette extracted
- [ ] Layout structure detected (bands, content regions, whitespace)
- [ ] Perceptual characteristics computed (luminance, temperature, contrast, complexity)
- [ ] Report saved to `docs/pixel-analysis/`

### Two Image Comparison
- [ ] Both images loaded and metadata extracted
- [ ] Dimensions checked and alignment applied if needed
- [ ] Pixel-level diff run with appropriate threshold
- [ ] SSIM score computed
- [ ] Regions detected, classified, and severity-assessed
- [ ] Diff image generated and saved
- [ ] Comprehensive report compiled and saved
- [ ] Results presented to user with actionable guidance

### Design Clone Verification
- [ ] Screenshots captured at identical viewport
- [ ] Full diff run with strict threshold (0.05)
- [ ] Fidelity classified (near-perfect / good / fair / poor)
- [ ] Fix list generated for major/critical regions
- [ ] Iteration loop completed until mismatch < target

## Invoke

```
/pixel-perfect image.png
/pixel-diff before.png after.png
/image-diff reference.png implementation.png
compare these images: a.png b.png
what changed between these screenshots?
analyze this image: design.png
verify clone fidelity: reference.png clone.png
```
