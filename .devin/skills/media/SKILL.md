---
name: media
description: Comprehensive media optimization workflow — image formats, responsive images, lazy loading, CDN, video, audio, Core Web Vitals, and accessibility
---

# Media Optimization Workflow

This workflow applies the **Media Optimization Skill** (`~/.codeium/windsurf/skills/media-optimization.md`) to optimize images, video, and audio for the web.

## When to Run
- When optimizing media for a web project
- When the user says `/media` or asks about image/video optimization
- When improving Core Web Vitals (LCP, CLS) related to media
- When setting up responsive images or CDN
- When converting legacy media to modern formats

---

## Step 1: Audit Existing Media

1. Inventory all images, videos, and audio on the site
2. Check formats — are they using AVIF/WebP or legacy JPEG/PNG/GIF?
3. Check sizes — are images served at appropriate resolution for display?
4. Check loading — are below-fold images lazy loaded? Is LCP image prioritized?
5. Check CLS — do images have width/height attributes to prevent layout shift?
6. Check file sizes — identify images larger than 200KB
7. Check for GIFs — should be converted to video
8. Measure current LCP, CLS, and total page weight from media

## Step 2: Choose Image Formats

1. Set AVIF as primary format (50% smaller than JPEG)
2. Set WebP as fallback (30% smaller than JPEG)
3. Keep JPEG as final fallback for legacy browsers
4. Use SVG for icons, logos, and illustrations
5. Use PNG only for transparency when AVIF/WebP alpha isn't available
6. Set up `<picture>` element with format fallbacks for all content images

## Step 3: Set Up Responsive Images

1. Generate 3-5 sizes per image (e.g., 400w, 800w, 1200w, 1600w, 2400w)
2. Add `srcset` and `sizes` attributes to all content images
3. Use art direction with `<picture>` for hero images (different crop for mobile/desktop)
4. Set appropriate quality: 80% for AVIF, 75% for WebP
5. Don't generate images wider than 2400px (even for retina)
6. For Next.js: use `<Image>` component which handles this automatically

## Step 4: Set Up Lazy Loading

1. Add `loading="lazy"` to all below-fold images
2. Add `fetchpriority="high"` to LCP (hero) images — don't lazy load
3. Add `decoding="async"` to all images for non-blocking decode
4. Preload LCP image: `<link rel="preload" as="image" href="..." fetchpriority="high">`
5. Set up blur placeholders (LQIP) for above-fold images
6. For Next.js: use `priority` prop on LCP images

## Step 5: Prevent CLS

1. Add `width` and `height` attributes to every `<img>`
2. Use CSS `aspect-ratio` for responsive image containers
3. Reserve space for media before it loads — no layout shift
4. For background images, ensure container has defined dimensions
5. For video, set dimensions and use `poster` image
6. Test with WebPageTest or Lighthouse — CLS should be < 0.1

## Step 6: Set Up CDN and Image Optimization

1. Choose CDN/image service: Cloudinary, Imgix, Cloudflare, Vercel, or self-hosted with Sharp
2. Configure on-the-fly transformations: resize, format conversion, quality
3. Set up CDN caching — long TTL for optimized images
4. Configure custom loader if using external CDN with Next.js
5. Set up image domains in `next.config.js` for remote images
6. Enable Cloudflare Polish/Mirage if using Cloudflare

## Step 7: Optimize Video

1. Encode in WebM (VP9 or AV1) as primary, MP4 (H.264) as fallback
2. Remove audio track for background/hero videos
3. Set appropriate bitrate: 1-2 Mbps for 720p, 2-4 Mbps for 1080p
4. Add `poster` image (optimized as AVIF/WebP)
5. Use `preload="metadata"` — don't preload full video
6. For background video: `autoplay muted loop playsinline`
7. Convert all GIFs to video — 10-100x smaller, better quality
8. Consider disabling background video on mobile (battery/data)

## Step 8: Optimize Audio

1. Encode in Opus as primary, AAC/MP3 as fallback
2. Set appropriate bitrate: 64-128kbps for speech, 128-256kbps for music
3. Use `preload="metadata"` or `preload="none"`
4. Lazy load audio — only load when user interacts
5. Never autoplay audio — it's disruptive and inaccessible

## Step 9: Set Up Build-Time Optimization

1. Install Sharp for image processing (Node.js)
2. Set up Vite imagetools or Next.js Image Optimization
3. Generate multiple sizes and formats at build time
4. Add image optimization step to CI pipeline
5. Optimize SVGs with SVGO
6. Set up automated conversion of new images

## Step 10: Ensure Accessibility

1. Write meaningful alt text for all content images
2. Use `alt=""` for decorative images
3. Add captions (`<track>`) for all video content
4. Provide transcripts for audio and video
5. Ensure no flashing > 3 times per second (seizure risk)
6. Never autoplay audio
7. Test with screen reader — verify all media is accessible

## Step 11: Measure and Verify

1. Run Lighthouse — check LCP, CLS, and image optimization score
2. Run WebPageTest — check image sizes, format usage, loading order
3. Verify LCP image loads with high priority
4. Verify no CLS from images (width/height set)
5. Check total page weight — images should be < 500KB for most pages
6. Test on mobile — verify responsive images serve correct size
7. Test on slow connection — verify lazy loading works
8. Track Core Web Vitals over time — should improve after optimization
