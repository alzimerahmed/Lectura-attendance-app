---
name: file-handling
description: Comprehensive file handling & media uploads workflow — architecture, presigned URLs, validation, image processing, drag-and-drop, chunked uploads, storage, and security
---

# File Handling & Media Uploads Workflow

This workflow applies the **File Handling & Media Uploads Skill** (`~/.codeium/windsurf/skills/file-handling-media-uploads.md`) to implement secure, performant file upload systems.

## When to Run
- When implementing file upload functionality
- When the user says `/file-handling` or asks about uploads
- When setting up image processing or media management
- When building drag-and-drop upload UI
- When configuring file storage (S3, R2, Cloudinary)

---

## Step 1: Assess Upload Needs

1. Read the project context — what files will be uploaded, sizes, types
2. Determine file types: images, documents, videos, audio
3. Determine max file size — affects architecture choice
4. Determine if server-side processing is needed (resize, convert, strip EXIF)
5. Determine storage requirements — public vs private, CDN delivery
6. Determine if chunked/resumable uploads are needed (> 100MB files)

## Step 2: Choose Upload Architecture

1. **Presigned URLs (default):** Client uploads directly to S3/R2 — best for most cases
2. **Server-mediated:** For files < 5MB when server-side processing is needed before storage
3. **Chunked/multipart:** For large files > 100MB — resumable, parallel chunk uploads
4. **Uploadthing:** If you want the simplest possible setup (handles everything)
5. **Cloudinary Upload Widget:** If you want built-in transformations and CDN

## Step 3: Choose Storage Provider

1. **Cloudflare R2:** No egress fees, S3-compatible — recommended for cost
2. **AWS S3:** Industry standard, most integrations — reliable but egress fees
3. **Backblaze B2:** Cheapest storage, S3-compatible
4. **Cloudinary:** Built-in transformations, CDN, upload widget — more expensive
5. **Uploadthing:** Simplest, handles storage + upload — least control
6. Set up account, get credentials, configure CORS for your domain

## Step 4: Implement Presigned URL Flow

1. Create API endpoint: `POST /api/upload/presign`
2. Validate request: file type, file size, user authentication
3. Generate unique key: `uploads/{uuid}-{filename}`
4. Create presigned PUT URL with S3/R2 SDK
5. Set URL expiration (1 hour)
6. Set conditions: Content-Type, Content-Length matching
7. Return presigned URL and key to client
8. Client uploads file directly to storage via PUT request
9. Client notifies server of completion: `POST /api/upload/complete`
10. Server verifies file exists, processes if needed, saves metadata to DB

## Step 5: Implement File Validation

1. **Client-side:**
   - Validate file type: check `file.type` against allowed list
   - Validate file size: check `file.size` against max
   - Show clear error messages for rejected files
2. **Server-side:**
   - Validate content type and size before generating presigned URL
   - After upload: verify actual file type with magic bytes (`file-type` library)
   - Delete file if content doesn't match declared type
   - Rate limit upload requests per user
3. **Allowed types:** Define per upload endpoint (images only, documents only, etc.)
4. **Max sizes:** Define per type (images: 10MB, videos: 500MB, documents: 25MB)

## Step 6: Implement Image Processing

1. Install Sharp for server-side image processing
2. After upload completion, download image from storage
3. Auto-rotate based on EXIF orientation
4. Strip EXIF metadata (GPS, camera info)
5. Generate multiple sizes: 400w, 800w, 1200w, 1600w
6. Generate AVIF (primary) and WebP (fallback) for each size
7. Generate 200x200 thumbnail (cover, center)
8. Store all variants in storage with consistent naming
9. Save variant URLs to database
10. Return all URLs to client for responsive `<picture>` element

## Step 7: Implement Drag-and-Drop UI

1. Install `react-dropzone` for drag-and-drop file selection
2. Configure accepted file types and max size
3. Style drop zone: dashed border, active state on drag, reject state
4. Add click-to-select fallback (hidden file input)
5. Add paste-from-clipboard support (Ctrl+V / Cmd+V)
6. Show file previews for images (using `URL.createObjectURL`)
7. Show file name and size for non-images
8. Show upload progress bar for each file
9. Show error messages for rejected files
10. Allow multiple file upload with queue

## Step 8: Implement Upload Progress

1. Use XMLHttpRequest (not fetch) for progress events
2. Track `progress.loaded / progress.total` for each file
3. Show progress bar or percentage for each uploading file
4. Show overall progress if multiple files
5. Handle upload errors with retry button
6. Allow canceling in-progress uploads
7. Show success state when complete

## Step 9: Implement Chunked Uploads (Large Files)

1. If files > 100MB, use S3 multipart upload
2. Server: create multipart upload, return upload ID and presigned URLs per part
3. Client: split file into 5MB chunks
4. Client: upload chunks in parallel (limit concurrency to 3-5)
5. Track progress per chunk and overall
6. Client: collect ETags from each part response
7. Client: send completion request with ETags
8. Server: complete multipart upload
9. Implement resume: store completed parts in IndexedDB, retry failed parts
10. Implement abort: cancel upload, delete incomplete parts

## Step 10: Set Up CDN Delivery

1. Configure CDN (CloudFront, Cloudflare) in front of storage
2. Set long cache TTL for immutable files (1 year)
3. Use content-based filenames or versioning for cache busting
4. For private files: use signed URLs with expiration
5. Set correct Content-Type headers on upload
6. Enable gzip/brotli compression for text-based files
7. Configure CORS headers for cross-origin file access

## Step 11: Implement Security

1. Generate unique filenames with UUIDs — never use user-supplied names
2. Store uploads in isolated bucket/prefix — not with application code
3. Set CORS on storage bucket — only allow from your domain
4. Use private buckets — serve via CDN with signed URLs for private files
5. Scan for malware (ClamAV or cloud service) for user uploads
6. Rate limit uploads per user (e.g., 10 uploads/minute)
7. Validate file content with magic bytes — not just declared type
8. Strip EXIF metadata from images
9. Set max file sizes in presigned URL conditions
10. Log all upload activity for audit

## Step 12: Test & Document

1. Test all allowed file types
2. Test rejected file types (should show error)
3. Test oversized files (should show error)
4. Test large file chunked upload (> 100MB)
5. Test upload cancellation
6. Test network failure and retry
7. Test concurrent uploads
8. Test paste from clipboard
9. Test image processing output (sizes, formats, EXIF stripped)
10. Test CDN delivery and cache headers
11. Document upload limits, supported formats, storage architecture
12. Document processing pipeline and variant naming convention
