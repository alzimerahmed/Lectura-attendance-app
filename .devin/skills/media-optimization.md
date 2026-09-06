---
name: Media Optimization Skill
description: Comprehensive methodology for optimizing images, video, and audio for the web — 2025-2026 practices with AVIF/WebP, responsive images, lazy loading, CDN delivery, and Core Web Vitals impact
version: 1.0.0
tags: [media, images, video, audio, optimization, avif, webp, responsive, lazy-loading, cdn, core-web-vitals]
---

# Media Optimization Skill

## Purpose
This skill provides a comprehensive methodology for optimizing images, video, and audio across any kind of web project. It reflects **modern 2025-2026 practices** — AVIF as the leading format, responsive images with `srcset`, native lazy loading, Next.js Image component patterns, video optimization, and Core Web Vitals impact (LCP, CLS).

## Core Philosophy

**Media is the biggest performance bottleneck.** Images account for ~50% of page weight on average. Unoptimized media destroys LCP, causes CLS, and frustrates users on slow connections. Every image should be the right format, the right size, and loaded at the right time.

**The #1 rule:** Never serve an image larger than what's displayed. A 4000px wide image displayed at 400px is 100x larger than needed. Use `srcset` and `sizes` to serve the right resolution for each device.

---

## Part 1: Image Formats

### 1.1 Format Comparison (2025-2026)

| Format | Compression | Support | Use Case | Browser Support |
|---|---|---|---|---|
| **AVIF** | Best (50% smaller than JPEG) | 96%+ | Photos, complex images | All modern browsers |
| **WebP** | Good (30% smaller than JPEG) | 98%+ | Photos, fallback for AVIF | All modern browsers |
| **JPEG** | Baseline | 100% | Fallback, legacy | All browsers |
| **PNG** | Lossless | 100% | Transparency, UI elements | All browsers |
| **GIF** | Poor | 100% | Don't use — convert to video | All browsers |
| **SVG** | Vector (infinite) | 100% | Icons, logos, illustrations | All browsers |
| **JPEG XL** | Best (better than AVIF) | ~30% | Future — not widely supported yet | Limited |

### 1.2 Format Selection
```
Photo → AVIF (primary) → WebP (fallback) → JPEG (fallback)
Transparent image → AVIF/WebP (with alpha) → PNG (fallback)
Icon/logo/illustration → SVG (always)
Animation → MP4/WebM video (not GIF)
UI element (small, few colors) → PNG or SVG
```

### 1.3 Picture Element for Format Fallbacks
```html
<picture>
  <source srcset="/img/hero.avif" type="image/avif" />
  <source srcset="/img/hero.webp" type="image/webp" />
  <img src="/img/hero.jpg" alt="Hero image" width="1200" height="630" />
</picture>
```
- **Order matters:** Most optimized format first, fallback last
- **`type` attribute:** Browser picks the first format it supports
- **`<img>` fallback:** Required — last source is the default
- **Always set `width` and `height`:** Prevents CLS (Cumulative Layout Shift)

### 1.4 SVG Optimization
```bash
# SVGO — optimize SVG files
npx svgo input.svg --output output.svg

# Or in build: vite-plugin-svgo
```
- **Minify:** Remove metadata, comments, whitespace
- **Simplify paths:** Reduce path complexity
- **Remove unused elements:** Hidden layers, defs, metadata
- **Inline vs file:** Small icons inline (reduces requests), large SVGs as files
- **Accessibility:** Add `<title>` and `<desc>` for meaningful SVGs

---

## Part 2: Responsive Images

### 2.1 srcset and sizes
```html
<img
  src="/img/photo-800.jpg"
  srcset="
    /img/photo-400.jpg 400w,
    /img/photo-800.jpg 800w,
    /img/photo-1200.jpg 1200w,
    /img/photo-1600.jpg 1600w,
    /img/photo-2400.jpg 2400w
  "
  sizes="(max-width: 600px) 100vw, (max-width: 1200px) 50vw, 33vw"
  alt="Description"
  width="800"
  height="600"
  loading="lazy"
/>
```
- **`srcset`:** Available image widths with `w` descriptor
- **`sizes`:** Tell browser how wide the image will be displayed at different viewport widths
- **Browser selects:** Browser downloads the most appropriate size based on `sizes` and device pixel ratio
- **Always include `src`:** Fallback for browsers that don't support `srcset`

### 2.2 Resolution Switching
```html
<!-- 1x, 2x, 3x for retina displays -->
<img
  src="/img/icon-1x.png"
  srcset="/img/icon-1x.png 1x, /img/icon-2x.png 2x, /img/icon-3x.png 3x"
  alt="Icon"
  width="48"
  height="48"
/>
```
- **`x` descriptor:** For fixed-size images with different pixel densities
- **Use for:** Icons, thumbnails, small images
- **Use `w` descriptor for:** Responsive images that change size with viewport

### 2.3 Art Direction with Picture
```html
<picture>
  <source media="(max-width: 600px)" srcset="/img/hero-mobile.avif" type="image/avif" />
  <source media="(max-width: 600px)" srcset="/img/hero-mobile.webp" type="image/webp" />
  <source srcset="/img/hero-desktop.avif" type="image/avif" />
  <source srcset="/img/hero-desktop.webp" type="image/webp" />
  <img src="/img/hero-desktop.jpg" alt="Hero" width="1200" height="630" />
</picture>
```
- **Art direction:** Different image for different viewport sizes (not just different resolution)
- **Mobile:** Cropped/optimized image for small screens
- **Desktop:** Full image for large screens
- **Use for:** Hero images, banners where composition matters

### 2.4 Image Sizing Guidelines

| Display Size | Generate Widths | Max Quality |
|---|---|---|
| Thumbnail (100-200px) | 200w, 400w | 70% |
| Small (200-400px) | 400w, 800w | 75% |
| Medium (400-800px) | 800w, 1200w | 80% |
| Large (800-1200px) | 1200w, 1600w, 2400w | 82% |
| Hero (1200px+) | 1600w, 2400w, 3200w | 85% |

- **Don't over-generate:** 3-5 sizes is usually enough
- **Quality:** AVIF/WebP can use lower quality than JPEG — start at 80% and adjust
- **Max width:** Rarely need images wider than 2400px (even for retina)

---

## Part 3: Lazy Loading

### 3.1 Native Lazy Loading
```html
<img src="photo.jpg" loading="lazy" alt="Photo" width="800" height="600" />
```
- **`loading="lazy"`:** Browser defers loading until near viewport
- **`loading="eager"`:** Default — load immediately
- **Above the fold:** Don't lazy load — use `loading="eager"` or `fetchpriority="high"`
- **Below the fold:** Use `loading="lazy"` — saves bandwidth and improves LCP

### 3.2 LCP Image — Don't Lazy Load
```html
<!-- LCP image — load immediately with high priority -->
<img
  src="/img/hero.avif"
  fetchpriority="high"
  alt="Hero"
  width="1200"
  height="630"
/>
```
- **LCP element:** The largest element visible in viewport — usually a hero image
- **Don't lazy load:** It delays LCP
- **`fetchpriority="high"`:** Tell browser to prioritize this image
- **Preload:** Use `<link rel="preload">` for critical images

### 3.3 Preloading Critical Images
```html
<link rel="preload" as="image" href="/img/hero.aviv" type="image/avif" fetchpriority="high" />
```
- **Preload LCP image:** Starts downloading before CSS/JS parse
- **Only for LCP:** Don't preload everything — wastes bandwidth
- **With `imagesrcset`:** Preload responsive images
```html
<link rel="preload" as="image" imagesrcset="/img/hero-800.avif 800w, /img/hero-1200.avif 1200w" imagesizes="100vw" />
```

### 3.4 Blur Placeholder (LQIP)
```html
<!-- Low Quality Image Placeholder -->
<img
  src="/img/hero.avif"
  alt="Hero"
  width="1200"
  height="630"
  style="background-image: url(data:image/jpeg;base64,/9j/4AAQ...); background-size: cover;"
/>
```
- **LQIP:** Tiny (20px wide) base64-encoded image as placeholder
- **Blur effect:** CSS `filter: blur(20px)` on placeholder, transition to sharp
- **Next.js:** Built-in blur placeholder with `placeholder="blur"`
- **Benefit:** User sees something immediately, not empty space

---

## Part 4: Next.js Image Component

### 4.1 Basic Usage
```tsx
import Image from 'next/image';

<Image
  src="/hero.jpg"
  alt="Hero image"
  width={1200}
  height={630}
  priority  // For LCP images
/>

// Or with fill (for responsive containers)
<Image
  src="/hero.jpg"
  alt="Hero"
  fill
  sizes="100vw"
  priority
/>
```

### 4.2 Next.js Image Features
- **Automatic optimization:** AVIF/WebP based on browser support
- **Responsive:** Automatic `srcset` generation
- **Lazy loading:** Built-in, except for `priority` images
- **Blur placeholder:** `placeholder="blur"` for local images
- **CDN:** Served from Next.js image optimization API
- **Width/height:** Prevents CLS automatically

### 4.3 Remote Images
```tsx
// next.config.js
images: {
  remotePatterns: [
    { protocol: 'https', hostname: 'cdn.example.com' },
  ],
}

// Usage
<Image
  src="https://cdn.example.com/photo.jpg"
  alt="Photo"
  width={800}
  height={600}
/>
```

### 4.4 Custom Loader (for external CDN)
```tsx
const cloudinaryLoader = ({ src, width, quality }) => {
  return `https://res.cloudinary.com/demo/image/upload/f_avif,q_${quality || 75},w_${width}/${src}`;
};

<Image
  src="photo.jpg"
  alt="Photo"
  width={800}
  height={600}
  loader={cloudinaryLoader}
/>
```

---

## Part 5: CDN & Image Services

### 5.1 CDN Benefits
- **Edge caching:** Images cached close to users globally
- **Transformations:** On-the-fly resize, format conversion, compression
- **Offloading:** Image processing offloaded from your server
- **Bandwidth:** Reduced origin bandwidth

### 5.2 Cloudinary
```typescript
// URL-based transformations
https://res.cloudinary.com/demo/image/upload/f_avif,q_auto,w_800/photo.jpg
// f_avif = AVIF format, q_auto = auto quality, w_800 = 800px wide
```

### 5.3 Imgix
```typescript
// URL-based transformations
https://demo.imgix.net/photo.jpg?fm=avif&q=75&w=800
// fm=avif = AVIF format, q=75 = quality, w=800 = width
```

### 5.4 Cloudflare Images
- **Image Resizing:** Cloudflare Workers + Image Resizing
- **Polish:** Automatic image optimization at the edge
- **Mirage:** Serve optimized images for mobile devices

### 5.5 Vercel Image Optimization
- **Built-in:** Next.js Image Optimization runs on Vercel
- **Automatic:** AVIF/WebP, resize, compress
- **On-demand:** Images optimized on first request, then cached

### 5.6 Self-Hosted with Sharp
```typescript
import sharp from 'sharp';

// Resize and convert to AVIF
await sharp('input.jpg')
  .resize(800)
  .avif({ quality: 80 })
  .toFile('output-800.avif');

// Generate multiple sizes
const sizes = [400, 800, 1200, 1600];
for (const width of sizes) {
  await sharp('input.jpg')
    .resize(width)
    .avif({ quality: 80 })
    .toFile(`output-${width}.avif`);
}
```

---

## Part 6: Video Optimization

### 6.1 Video Formats

| Format | Compression | Support | Use Case |
|---|---|---|---|
| **MP4 (H.264)** | Good | 100% | Fallback, broad compatibility |
| **WebM (VP9)** | Better | 96%+ | Primary, smaller than H.264 |
| **WebM (AV1)** | Best | 85%+ | Future — best compression |
| **MOV (ProRes)** | None | N/A | Source format — don't serve |

### 6.2 Video Element with Fallbacks
```html
<video width="1280" height="720" controls preload="metadata" poster="/img/poster.avif">
  <source src="/video/demo.av1.webm" type="video/webm; codecs=av01.0.05M.08" />
  <source src="/video/demo.vp9.webm" type="video/webm; codecs=vp9" />
  <source src="/video/demo.h264.mp4" type="video/mp4; codecs=avc1.42E01E" />
  Your browser does not support the video tag.
</video>
```
- **Order:** Best compression first, fallback last
- **`preload="metadata"`:** Only load metadata initially, not full video
- **`poster`:** Image shown before video plays — optimize like any image
- **`controls`:** Native browser controls (accessible)

### 6.3 Video Best Practices
- **Short clips:** Keep web videos under 30 seconds if possible
- **Resolution:** 720p for most web video, 1080p for hero/background
- **Bitrate:** 1-2 Mbps for 720p, 2-4 Mbps for 1080p
- **Audio:** Remove audio track for background/hero videos — reduces size
- **Autoplay:** `autoplay muted loop playsinline` for background video
- **Lazy load:** Don't load video until needed — use poster image

### 6.4 Background Video
```html
<video autoplay muted loop playsinline poster="/img/hero-poster.avif" class="bg-video">
  <source src="/video/bg.av1.webm" type="video/webm" />
  <source src="/video/bg.vp9.webm" type="video/webm" />
  <source src="/video/bg.h264.mp4" type="video/mp4" />
</video>
```
- **`muted`:** Required for autoplay
- **`playsinline`:** Required for iOS autoplay
- **`loop`:** For background video
- **`poster`:** Show image while video loads
- **Mobile:** Consider disabling on mobile — battery/data concerns

### 6.5 GIF to Video Conversion
```bash
# Convert GIF to WebM (much smaller)
ffmpeg -i input.gif -c:v libvpx-vp9 -b:v 500k -an output.webm

# Convert GIF to MP4
ffmpeg -i input.gif -movflags faststart -pix_fmt yuv420p output.mp4
```
- **10-100x smaller:** Video is dramatically smaller than GIF
- **Better quality:** No 256-color limit
- **Use `<video>` instead of `<img>`:** With `autoplay muted loop playsinline`

### 6.6 Video Hosting Platforms
- **Mux:** Developer-friendly video API, adaptive streaming
- **Cloudflare Stream:** Integrated with Cloudflare CDN
- **Vimeo:** Hosted video with player customization
- **YouTube:** Free but with branding and ads
- **Self-hosted:** Use HLS/DASH for adaptive streaming

---

## Part 7: Audio Optimization

### 7.1 Audio Formats

| Format | Compression | Support | Use Case |
|---|---|---|---|
| **MP3** | Good | 100% | Broad compatibility, music |
| **AAC** | Better | 100% | Better quality at same bitrate |
| **Opus** | Best | 90%+ | Speech, best compression |
| **FLAC** | Lossless | 95%+ | High-quality audio |

### 7.2 Audio Best Practices
- **Bitrate:** 64-128kbps for speech, 128-256kbps for music
- **Preload:** `preload="metadata"` or `preload="none"` — don't preload full audio
- **Lazy load:** Load audio only when user interacts
- **Streaming:** Use HLS/DASH for long audio (podcasts, audiobooks)

### 7.3 Audio Element
```html
<audio controls preload="metadata">
  <source src="/audio/intro.opus" type="audio/ogg; codecs=opus" />
  <source src="/audio/intro.aac" type="audio/aac" />
  <source src="/audio/intro.mp3" type="audio/mpeg" />
  Your browser does not support the audio tag.
</audio>
```

---

## Part 8: Core Web Vitals Impact

### 8.1 LCP (Largest Contentful Paint)
- **LCP element is often an image:** Optimize it aggressively
- **Preload LCP image:** `<link rel="preload" as="image">`
- **Don't lazy load LCP:** Use `priority` or `fetchpriority="high"`
- **Use modern formats:** AVIF/WebP reduce file size → faster download
- **Responsive:** Don't serve 2400px image to 400px display
- **CDN:** Serve from edge close to user

### 8.2 CLS (Cumulative Layout Shift)
- **Always set width/height:** `<img width="800" height="600">`
- **Aspect ratio:** Browser reserves space before image loads
- **CSS aspect-ratio:** `aspect-ratio: 16/9` for responsive containers
- **Next.js Image:** Automatically sets dimensions
- **No layout shift:** User never sees content jump

### 8.3 INP (Interaction to Next Paint)
- **Don't block main thread:** Heavy image processing blocks interaction
- **Use CDN:** Offload processing to CDN, not client
- **Decode asynchronously:** `decoding="async"` on images
- **Avoid JavaScript image processing:** Use canvas in Web Worker if needed

### 8.4 Measuring Media Impact
```typescript
// Track LCP element
import { onLCP } from 'web-vitals';

onLCP((metric) => {
  // metric.element — the LCP element (often an image)
  // metric.value — LCP time in ms
  sendToAnalytics({ name: 'LCP', value: metric.value, element: metric.element?.tagName });
});
```

---

## Part 9: Build-Time Optimization

### 9.1 Vite Image Plugin
```typescript
// vite.config.ts
import { imagetools } from 'vite-imagetools';

export default {
  plugins: [
    imagetools({
      defaultDirectives: (url) => {
        return {
          format: 'avif',
          quality: 80,
        };
      },
    }),
  ],
};
```

### 9.2 Next.js Image Optimization
```javascript
// next.config.js
module.exports = {
  images: {
    formats: ['image/avif', 'image/webp'],
    deviceSizes: [640, 750, 828, 1080, 1200, 1920],
    imageSizes: [16, 32, 48, 64, 96, 128, 256, 384],
    minimumCacheTTL: 60 * 60 * 24 * 30, // 30 days
  },
};
```

### 9.3 Sharp for Batch Processing
```typescript
import sharp from 'sharp';
import glob from 'fast-glob';

const images = await glob('src/images/**/*.{jpg,png}');

for (const image of images) {
  const sizes = [400, 800, 1200, 1600];
  for (const width of sizes) {
    await sharp(image)
      .resize(width)
      .avif({ quality: 80 })
      .toFile(image.replace(/\.(jpg|png)$/, `-${width}.avif`));
  }
}
```

### 9.4 Automated Optimization in CI
```yaml
# .github/workflows/optimize-images.yml
- name: Optimize images
  run: |
    npx sharp-cli -i ./public/images -o ./public/images -f avif -q 80
    npx sharp-cli -i ./public/images -o ./public/images -f webp -q 80
```

---

## Part 10: Accessibility for Media

### 10.1 Image Alt Text
- **Meaningful images:** Describe what the image conveys — "Chart showing 40% growth in Q3"
- **Decorative images:** `alt=""` (empty) — screen readers skip them
- **Functional images:** Describe the function — "Search" for magnifying glass icon
- **Charts/graphs:** Include data summary in alt text or adjacent text
- **No "image of":** Screen readers already announce "image"

### 10.2 Video Accessibility
- **Captions:** `<track kind="subtitles" src="captions.vtt" srclang="en" />`
- **Audio descriptions:** Separate audio track describing visual content
- **Transcript:** Full text transcript of video content
- **Accessible player:** Use native `<video controls>` or accessible custom player
- **No flashing:** Limit flashing to 3 times per second (seizure risk)

### 10.3 Audio Accessibility
- **Transcript:** Full text transcript for audio content
- **Captions:** For audio with visual component (video)
- **Volume control:** User must be able to control volume
- **No autoplay audio:** Never auto-play audio — it's disruptive and inaccessible

---

## Execution Instructions for Cascade

When this skill is activated for media optimization:

1. **Read the project context** — framework, media usage, performance budget
2. **Audit existing media** — formats, sizes, loading behavior, CLS impact
3. **Choose formats** — AVIF (primary), WebP (fallback), JPEG (fallback)
4. **Set up responsive images** — `srcset` and `sizes` for all content images
5. **Set up lazy loading** — `loading="lazy"` for below-fold, `priority` for LCP
6. **Set up CDN** — Cloudinary, Imgix, Cloudflare, or Vercel image optimization
7. **Optimize video** — WebM/MP4 with fallbacks, poster images, no autoplay audio
8. **Set width/height** — On all images to prevent CLS
9. **Use Next.js Image** — If using Next.js, leverage built-in optimization
10. **Convert GIFs to video** — 10-100x smaller, better quality
11. **Set up build-time optimization** — Sharp, Vite imagetools, or CI pipeline
12. **Ensure accessibility** — Alt text, video captions, audio transcripts
13. **Measure impact** — Track LCP, CLS, INP before and after optimization
14. **Document** — Media guidelines, format policy, size standards
