---
name: Pixel-Perfect Image Analysis & Visual Diff Skill
description: Comprehensive methodology for reading images at the pixel level, extracting exact visual data (dimensions, colors, layout, entropy, sharpness), and differentiating between two images using perceptual and structural similarity metrics
version: 1.0.0
tags: [image, pixel, diff, comparison, visual-regression, analysis, sharp, pixelmatch, ssim]
---

# Pixel-Perfect Image Analysis & Visual Diff Skill

## Purpose
This skill provides a comprehensive methodology for two core capabilities:
1. **Image Reading** — extracting pixel-perfect data from a single image: exact dimensions, color palette, dominant colors, channel statistics, entropy, sharpness, layout structure, and perceptual characteristics
2. **Image Differentiation** — comparing two images pixel-by-pixel and perceptually to identify, quantify, classify, and visualize every difference between them

Use this skill when you need to analyze an image at the pixel level, compare two images for visual regression, verify pixel-perfect design clones, or detect subtle differences between renders.

## When to Use
- Comparing a design screenshot against an implementation screenshot
- Verifying pixel-perfect clone fidelity
- Detecting visual regressions between builds
- Analyzing an image's color palette, dimensions, and structure
- Comparing two versions of an image to identify what changed
- Validating that image processing (compression, resize, format conversion) is lossless
- QA gate before shipping visual changes
- Extracting design tokens (colors, dimensions) from an image

---

## Part 1: Image Reading — Pixel-Perfect Extraction

### 1.1 Metadata Extraction
Use `sharp` to read image header data without decoding pixels:

```js
const sharp = require('sharp');
const meta = await sharp(inputPath).metadata();
// Returns: format, width, height, channels, depth, density, space, hasAlpha, orientation, pages
```

**Capture:**
- **Format** — jpeg, png, webp, avif, gif, tiff, svg
- **Dimensions** — exact width × height in pixels
- **Color space** — srgb, rgb, cmyk, lab, b-w
- **Channels** — number of bands (3 for sRGB, 4 for CMYK, +1 for alpha)
- **Depth** — uchar, char, ushort, float
- **Density** — DPI if present
- **Alpha** — whether image has transparency
- **EXIF orientation** — rotation/flip metadata
- **Pages** — frame count for animated images

### 1.2 Channel Statistics
Use `sharp.stats()` for per-channel pixel-derived statistics:

```js
const stats = await sharp(inputPath).stats();
// stats.channels = [{ min, max, sum, squaresSum, mean, stdev, minX, minY, maxX, maxY }, ...]
// stats.isOpaque — boolean
// stats.entropy — histogram-based greyscale entropy estimate
// stats.sharpness — Laplacian convolution sharpness estimate
// stats.dominant — { r, g, b } most dominant sRGB color via 4096-bin 3D histogram
```

**Capture per channel (R, G, B, Alpha):**
- **Min / Max** — extreme values with pixel coordinates
- **Mean** — average value
- **Standard deviation** — spread of values
- **Sum / SquaresSum** — aggregate values

**Whole-image metrics:**
- **IsOpaque** — whether all pixels are fully opaque
- **Entropy** — information density (higher = more detail/complexity)
- **Sharpness** — edge intensity estimate
- **Dominant color** — most frequent sRGB color

### 1.3 Color Palette Extraction
Extract a quantized color palette using modified median cut or k-means:

```js
// Method 1: sharp dominant color (fast, single color)
const { dominant } = await sharp(inputPath).stats();

// Method 2: Full palette via color-thief-style quantization
// Resize to small thumbnail, then quantize pixel colors
const palette = await extractPalette(inputPath, { count: 6 });
// Returns: [{ r, g, b, hex, population, population_pct }, ...]
```

**Palette structure:**
- **Primary** — most populous color
- **Secondary** — second most populous
- **Accent** — most saturated non-gray color
- **Background** — most common edge/border pixel color
- **Text** — darkest common color (heuristic)
- Each entry: hex, RGB, HSL, population count, population percentage

### 1.4 Layout Structure Detection
Analyze spatial distribution of visual content:

```js
// Edge detection via row/column projection profiles
const rawData = await sharp(inputPath).raw().toBuffer({ resolveWithObject: true });
const { data, info } = rawData;
// data is Uint8Array of raw pixel values [r,g,b,r,g,b,...] or [r,g,b,a,...]

// Row projection: average luminance per row → detect horizontal bands
// Column projection: average luminance per column → detect vertical alignment
// Segment detection: find transitions between uniform regions
```

**Detect:**
- **Horizontal bands** — rows with similar luminance (sections, headers, footers)
- **Vertical alignment** — columns with content vs whitespace
- **Content regions** — bounding boxes of non-background areas
- **Whitespace regions** — large uniform areas (padding, margins)
- **Text-heavy regions** — high-frequency vertical edges (text strokes)
- **Image regions** — low-frequency smooth areas (photos, gradients)

### 1.5 Perceptual Characteristics
Compute human-perception-aligned metrics:

- **Average luminance** — perceived brightness (0-255 scale)
- **Color temperature** — warm (red/yellow dominant) vs cool (blue/green dominant)
- **Saturation profile** — average saturation, saturation distribution
- **Contrast ratio** — difference between brightest and darkest regions
- **Visual complexity** — entropy + edge density combined score

---

## Part 2: Image Differentiation — Visual Diff

### 2.1 Pre-Comparison Alignment
Before comparing, ensure images are comparable:

1. **Dimension check** — if widths/heights differ, choose strategy:
   - **Strict mode** — reject if dimensions differ (pixel-perfect requires equal size)
   - **Crop mode** — center-crop the larger image to match the smaller
   - **Scale mode** — resize the larger image to match the smaller (introduces interpolation artifacts)
   - **Letterbox mode** — pad the smaller image with background color to match the larger
2. **Format normalization** — decode both to raw RGBA pixel arrays
3. **Color space alignment** — convert both to sRGB if needed
4. **EXIF orientation** — apply orientation metadata before comparison

```js
const sharp = require('sharp');

async function alignImages(path1, path2, mode = 'strict') {
    const meta1 = await sharp(path1).metadata();
    const meta2 = await sharp(path2).metadata();

    if (meta1.width === meta2.width && meta1.height === meta2.height) {
        // Already aligned — decode both to raw RGBA
        const img1 = await sharp(path1).ensureAlpha().raw().toBuffer({ resolveWithObject: true });
        const img2 = await sharp(path2).ensureAlpha().raw().toBuffer({ resolveWithObject: true });
        return { img1, img2, width: meta1.width, height: meta1.height, resized: false };
    }

    if (mode === 'strict') throw new Error(`Dimension mismatch: ${meta1.width}x${meta1.height} vs ${meta2.width}x${meta2.height}`);

    const targetW = Math.min(meta1.width, meta2.width);
    const targetH = Math.min(meta1.height, meta2.height);

    if (mode === 'scale') {
        const img1 = await sharp(path1).resize(targetW, targetH, { fit: 'fill' }).ensureAlpha().raw().toBuffer({ resolveWithObject: true });
        const img2 = await sharp(path2).resize(targetW, targetH, { fit: 'fill' }).ensureAlpha().raw().toBuffer({ resolveWithObject: true });
        return { img1, img2, width: targetW, height: targetH, resized: true };
    }

    if (mode === 'crop') {
        const img1 = await sharp(path1).extract({
            left: Math.floor((meta1.width - targetW) / 2),
            top: Math.floor((meta1.height - targetH) / 2),
            width: targetW, height: targetH
        }).ensureAlpha().raw().toBuffer({ resolveWithObject: true });
        const img2 = await sharp(path2).extract({
            left: Math.floor((meta2.width - targetW) / 2),
            top: Math.floor((meta2.height - targetH) / 2),
            width: targetW, height: targetH
        }).ensureAlpha().raw().toBuffer({ resolveWithObject: true });
        return { img1, img2, width: targetW, height: targetH, resized: true };
    }
}
```

### 2.2 Pixel-Level Diff (pixelmatch)
The core comparison engine. Uses YIQ perceptual color space:

```js
const pixelmatch = require('pixelmatch');
const { PNG } = require('pngjs');

function pixelDiff(img1Data, img2Data, width, height, options = {}) {
    const {
        threshold = 0.1,        // 0-1, smaller = more sensitive
        includeAA = false,       // if true, count anti-aliased pixels as diffs
        alpha = 0.1,             // diff image opacity of unchanged pixels
        diffColor = [255, 0, 0], // red for differing pixels
        aaColor = [255, 255, 0], // yellow for anti-aliased pixels
        diffMask = false,        // transparent background for diff
        windowSize = Infinity,   // sliding window for density-based diff
    } = options;

    const output = new Uint8Array(width * height * 4);
    const mismatchedPixels = pixelmatch(img1Data, img2Data, output, width, height, {
        threshold, includeAA, alpha, diffColor, aaColor, diffMask
    });

    return {
        mismatchedPixels,
        totalPixels: width * height,
        mismatchPercentage: (mismatchedPixels / (width * height)) * 100,
        diffImage: output, // Uint8Array of RGBA pixel data
    };
}
```

**YIQ perceptual color difference formula:**
```
Y = 0.29889531 * r + 0.58662247 * g + 0.11448223 * b
delta = |Y1 - Y2| / 255
If delta > threshold → pixel is different
```

**Anti-aliasing detection:**
For each flagged pixel, scan the 3×3 neighborhood. A pixel is anti-aliased if:
- It has ≥2 neighbors with the same color
- It has ≥2 neighbors with a very different color
- One neighbor matches a corresponding extreme in the other image
AA pixels are excluded from the diff count (reduces false positives from font rendering).

**Windowed diff density:**
With `windowSize: N`, returns the maximum number of differing pixels in any N×N sliding window instead of total count. This makes the result robust to scattered noise (GPU dithering, sub-pixel AA) while catching genuine regressions.

### 2.3 Structural Similarity Index (SSIM)
SSIM measures perceived quality by comparing luminance, contrast, and structure:

```
SSIM(x, y) = [l(x,y)]^α · [c(x,y)]^β · [s(x,y)]^γ

l(x,y) = (2·μx·μy + C1) / (μx² + μy² + C1)          — luminance comparison
c(x,y) = (2·σx·σy + C2) / (σx² + σy² + C2)          — contrast comparison
s(x,y) = (σxy + C3) / (σx·σy + C3)                   — structure comparison
```

- **Range:** -1 to 1 (1 = identical, 0 = no correlation, -1 = anti-correlated)
- **Window:** 11×11 Gaussian sliding window (or 8×8 block)
- **Application:** Applied on luma channel; can extend to RGB/YCbCr
- **Advantage over MSE/PSNR:** Correlates with human perception of quality
- **Use case:** When pixelmatch is too strict (e.g., minor compression artifacts) and you need a perceptual quality score

```js
// SSIM implementation (simplified, luma channel)
function ssim(img1Data, img2Data, width, height, windowSize = 11) {
    const C1 = (0.01 * 255) ** 2;
    const C2 = (0.03 * 255) ** 2;
    // Convert to luma, compute local means/stds/covariances via sliding window
    // Return mean SSIM score across all windows
}
```

### 2.4 CIE94 Color Difference
More accurate perceptual color metric than YIQ for specific color comparisons:

```
ΔE_CIE94 = sqrt( (ΔL / kL·SL)² + (ΔC / kC·SC)² + (ΔH / kH·SH)² )
```

- If ΔE_CIE94 < 1.0 → pixels are perceptually identical (just-noticeable difference threshold)
- Used by Playwright's `ssim-cie94` comparator for visual regression

### 2.5 Region Detection & Classification
After pixel diff, cluster changed pixels into meaningful regions:

```js
function detectRegions(diffMask, width, height, options = {}) {
    const {
        minRegionSize = 25,    // filter out noise clusters
        mergeDistance = 50,    // merge regions within N pixels
        connectivity = 8,      // 4 (cross) or 8 (with diagonals)
        denoise = 25,          // remove clusters smaller than N pixels
        dilate = 0,            // expand diff mask by N pixels
    } = options;

    // 1. Denoise — remove small clusters
    // 2. Dilate — expand mask to bridge nearby changes
    // 3. Connected component labeling — group pixels into regions
    // 4. Merge — combine regions within mergeDistance
    // 5. Filter — remove regions smaller than minRegionSize
    // 6. Classify — label each region

    return regions; // [{ id, boundingBox: {x, y, width, height}, label, pixelCount }]
}
```

**Region classification labels:**
- **`added`** — content present in image 2 but not image 1 (new element appeared)
- **`removed`** — content present in image 1 but not image 2 (element disappeared)
- **`color-change`** — same shape/position but different colors (restyle, theme change)
- **`content-change`** — same region but different content (text changed, image swapped)
- **`layout-shift`** — content moved position (element repositioned)

**Classification heuristics:**
- `added`: Region has high delta in image 2 direction (img2 brighter/darker than img1 in a new area)
- `removed`: Region has high delta in image 1 direction (img1 has content, img2 is background)
- `color-change`: Region exists in both images at same position but color channels differ uniformly
- `content-change`: Region exists in both but pixel patterns differ significantly (high entropy diff)
- `layout-shift`: Detect by checking if a diff region in one location corresponds to a matching region shifted elsewhere

### 2.6 Diff Image Generation
Produce a visual diff image for human review:

```js
const sharp = require('sharp');

async function generateDiffImage(diffPixelData, width, height, outputPath) {
    await sharp(diffPixelData, {
        raw: { width, height, channels: 4 }
    }).png().toFile(outputPath);
}
```

**Diff image conventions:**
- **Unchanged pixels** — dimmed original (alpha controls brightness, default 0.1 = mostly white)
- **Different pixels** — highlighted in red `[255, 0, 0]`
- **Anti-aliased pixels** — highlighted in yellow `[255, 255, 0]`
- **Alternative:** `diffColorAlt` for dark-on-light vs light-on-dark differentiation
- **Mask mode:** Transparent background with only diff pixels visible

### 2.7 Comprehensive Diff Report
Combine all metrics into a structured report:

```js
{
    summary: {
        match: boolean,              // true if images are identical within threshold
        mismatchedPixels: number,
        totalPixels: number,
        mismatchPercentage: number,  // 0-100
        ssimScore: number,           // -1 to 1
        maxDeltaY: number,           // max perceptual luminance difference
        avgDeltaY: number,           // average luminance difference over changed pixels
        dimensionMismatch: { widthDiff, heightDiff } | null,
    },
    regions: [
        {
            id: number,
            boundingBox: { x, y, width, height },
            label: 'added' | 'removed' | 'color-change' | 'content-change' | 'layout-shift',
            pixelCount: number,
            pixelPercentage: number,
            severity: 'critical' | 'major' | 'minor' | 'negligible',
        }
    ],
    image1: {
        path, format, width, height, channels, dominantColor, entropy, sharpness,
    },
    image2: {
        path, format, width, height, channels, dominantColor, entropy, sharpness,
    },
    diffImagePath: string,  // path to visual diff image
    options: { threshold, includeAA, alignmentMode, ... },
    timestamp: ISO string,
}
```

**Severity classification:**
- **critical** — region > 5% of total pixels or > 10,000 pixels
- **major** — region 1-5% of total pixels or 1,000-10,000 pixels
- **minor** — region 0.1-1% of total pixels or 100-1,000 pixels
- **negligible** — region < 0.1% of total pixels or < 100 pixels

---

## Part 3: Tool Selection Guide

### Single Image Analysis
| Need | Tool | Why |
|------|------|-----|
| Metadata (dimensions, format) | `sharp().metadata()` | Header-only read, no pixel decode |
| Channel stats (min/max/mean/stdev) | `sharp().stats()` | Pixel-derived statistics |
| Dominant color | `sharp().stats().dominant` | 4096-bin 3D histogram |
| Full palette | color-thief / custom quantization | Multiple colors with population |
| Entropy / sharpness | `sharp().stats()` | Built-in estimates |
| Raw pixel access | `sharp().raw().toBuffer()` | Uint8Array of pixel values |
| Layout structure | Custom projection profiles | Row/column luminance analysis |

### Two Image Comparison
| Need | Tool | Why |
|------|------|-----|
| Fast pixel diff | `pixelmatch` | 150 LOC, no deps, YIQ perceptual, AA detection |
| Perceptual quality score | SSIM | Luminance + contrast + structure, matches human perception |
| Strict color accuracy | CIE94 ΔE | Just-noticeable difference threshold |
| Region detection | Custom clustering | Connected components + classification |
| Cross-format comparison | `odiff` | Native Zig, SIMD, handles jpg-vs-png |
| Playwright screenshot diff | `toHaveScreenshot()` | Built-in pixelmatch, baseline management |
| High-performance diff | `odiff` | SIMD-optimized, fastest single-thread |
| AI agent structured output | `agent-image-diff` | JSON output, region classification, compact |

### Threshold Guidelines
| Use Case | Threshold | Rationale |
|----------|-----------|-----------|
| Strict pixel equality | 0.0 | Every pixel must match exactly |
| Lossless verification | 0.0 + `includeAA: true` | No tolerance for any difference |
| Screenshot regression | 0.1 (default) | Tolerates AA differences across DPIs |
| Font rendering tolerant | 0.1 + `includeAA: false` | Ignores subpixel font rendering |
| Compression artifact tolerant | 0.15-0.2 | Ignores JPEG ringing noise |
| Design clone verification | 0.05 | Strict but allows minor AA variance |
| Cross-browser comparison | 0.2 | Different render engines produce AA variance |

---

## Part 4: Implementation Patterns

### Pattern A: Quick Diff Check
```js
const sharp = require('sharp');
const pixelmatch = require('pixelmatch');

async function quickDiff(path1, path2) {
    const img1 = await sharp(path1).ensureAlpha().raw().toBuffer({ resolveWithObject: true });
    const img2 = await sharp(path2).ensureAlpha().raw().toBuffer({ resolveWithObject: true });

    if (img1.info.width !== img2.info.width || img1.info.height !== img2.info.height) {
        throw new Error('Dimension mismatch — use alignImages() first');
    }

    const { width, height } = img1.info;
    const diff = new Uint8Array(width * height * 4);
    const mismatched = pixelmatch(img1.data, img2.data, diff, width, height, { threshold: 0.1 });

    return {
        match: mismatched === 0,
        mismatchedPixels: mismatched,
        mismatchPercentage: (mismatched / (width * height)) * 100,
    };
}
```

### Pattern B: Full Analysis Report
```js
async function fullAnalysis(path1, path2, options = {}) {
    // 1. Extract metadata for both images
    // 2. Align images (strict/crop/scale)
    // 3. Run pixelmatch diff
    // 4. Compute SSIM score
    // 5. Detect and classify regions
    // 6. Generate diff image
    // 7. Compile comprehensive report
    // 8. Save report + diff image
}
```

### Pattern C: Playwright Integration
```js
// In Playwright test — built-in visual regression
import { test, expect } from '@playwright/test';

test('pixel-perfect match', async ({ page }) => {
    await page.goto('http://localhost:5173');
    await expect(page).toHaveScreenshot('baseline.png', {
        maxDiffPixelRatio: 0.001,  // 0.1% tolerance
        threshold: 0.1,
        animations: 'disabled',
    });
});

// Custom comparison outside Playwright test runner
async function compareWithBaseline(page, baselinePath) {
    await page.screenshot({ path: 'current.png', fullPage: true });
    const result = await quickDiff(baselinePath, 'current.png');
    if (!result.match) {
        // Generate diff image, save report, fail test
    }
}
```

### Pattern D: Design Clone Verification
```js
async function verifyClone(cloneScreenshotPath, referenceScreenshotPath) {
    const report = await fullAnalysis(referenceScreenshotPath, cloneScreenshotPath, {
        threshold: 0.05,      // strict for design clones
        alignmentMode: 'strict',
        generateDiffImage: true,
        detectRegions: true,
        minRegionSize: 50,
    });

    // Fidelity assessment
    const fidelity = report.summary.mismatchPercentage < 1.0 ? 'near-perfect'
                   : report.summary.mismatchPercentage < 5.0 ? 'good'
                   : report.summary.mismatchPercentage < 15.0 ? 'fair'
                   : 'poor';

    return { ...report, fidelity };
}
```

---

## Part 5: Output Artifacts

### Report File (`pixel-diff-report.json`)
Full structured JSON report with all metrics, regions, and metadata.

### Diff Image (`diff.png`)
Visual representation of differences with red highlights on changed pixels.

### Side-by-Side Composite (`comparison.png`)
Three-panel image: image 1 | image 2 | diff, for human review.

### Region Map (`regions.json`)
List of detected change regions with bounding boxes, labels, and severity.

---

## Part 6: Integration with Existing System

### With Design Cloner
After building a design clone, use this skill to verify pixel-perfect fidelity:
1. Screenshot the clone at same viewport as reference
2. Run full diff analysis
3. Classify fidelity: near-perfect / good / fair / poor
4. List specific regions that need fixing
5. Iterate until mismatch percentage is below threshold

### With Quality Engineer
As a QA gate before shipping:
1. Compare current build screenshots against baseline
2. Detect any visual regressions
3. Classify severity of each change
4. Block deployment if critical regions differ

### With Vibe Coding Guardian
As part of anti-vibe-coding audit:
1. Compare implementation against design reference
2. Detect "close enough" but not pixel-perfect areas
3. Identify lazy approximations vs exact implementations
4. Verify craft signals are present (not just "looks okay")

### With Performance Engineer
When optimizing images:
1. Compare original vs optimized image
2. Verify lossless claims (0% mismatch with `threshold: 0`)
3. Quantify quality loss for lossy compression
4. Find optimal compression level with acceptable quality

---

## References

- **pixelmatch** — https://github.com/mapbox/pixelmatch (6.8K stars, ISC license, zero deps)
- **odiff** — https://github.com/dmtrKovalenko/odiff (3K stars, SIMD-optimized, Zig)
- **sharp** — https://sharp.pixelplumbing.com/ (Node.js image processing, libvips)
- **SSIM** — Wang et al., "Image Quality Assessment: From Error Visibility to Structural Similarity" (IEEE 2004)
- **CIE94** — ISO/CIE color difference standard, used by Playwright `ssim-cie94` comparator
- **agent-image-diff** — https://github.com/chickencoder/agent-image-diff (structured JSON output for AI agents)
- **Playwright visual comparisons** — https://playwright.dev/docs/test-snapshots
