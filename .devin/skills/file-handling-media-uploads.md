---
name: File Handling & Media Uploads Skill
description: Comprehensive methodology for handling file uploads and media processing in web applications — 2025-2026 practices with presigned URLs, chunked uploads, image processing, drag-and-drop, and security
version: 1.0.0
tags: [file-upload, media-upload, presigned-urls, s3, chunked-upload, image-processing, drag-and-drop, file-validation, security, sharp]
---

# File Handling & Media Uploads Skill

## Purpose
This skill provides a comprehensive methodology for handling file uploads and media processing across any kind of web project. It reflects **modern 2025-2026 practices** — presigned URLs for direct-to-storage uploads, chunked uploads for large files, server-side image processing with Sharp, drag-and-drop with paste support, and comprehensive security validation.

## Core Philosophy

**Upload directly to storage, not through your server.** The biggest mistake is routing file uploads through your application server. This wastes bandwidth, blocks server resources, and limits file size. Instead, use presigned URLs to let clients upload directly to S3/R2/Cloudinary. Your server only generates the URL and processes the result.

**The #1 rule:** Never trust user-uploaded files. Validate type, size, and content. Scan for malware. Strip metadata if needed. Process images server-side. Store files in isolated paths. Serve files from a CDN, never from your application server.

---

## Part 1: Upload Architecture

### 1.1 Presigned URL Upload (Recommended)
```
1. Client → Server: "I want to upload file.jpg (2MB, image/jpeg)"
2. Server → S3: Generate presigned URL with conditions
3. Server → Client: Return presigned URL
4. Client → S3: Upload file directly to S3 via presigned URL
5. Client → Server: "Upload complete, key is uploads/abc.jpg"
6. Server: Verify file exists, process if needed, save metadata to DB
```

### 1.2 Server-Mediated Upload (Small Files Only)
```
1. Client → Server: Upload file via multipart form
2. Server: Validate, process, upload to S3
3. Server → Client: Return file URL
```
- **Use for:** Files < 5MB, when you need server-side processing before storage
- **Don't use for:** Large files, media files, anything > 5MB

### 1.3 Chunked/Resumable Upload (Large Files)
```
1. Client → Server: "I want to upload video.mp4 (500MB)"
2. Server: Create upload session, return upload ID
3. Client: Split file into 5MB chunks
4. Client → S3: Upload each chunk with presigned URL (parallel)
5. Client → Server: "All chunks uploaded"
6. Server → S3: Complete multipart upload
7. Server → Client: "Upload complete"
```

---

## Part 2: Presigned URL Implementation

### 2.1 S3 Presigned URL (AWS SDK)
```typescript
import { S3Client, PutObjectCommand } from '@aws-sdk/client-s3';
import { getSignedUrl } from '@aws-sdk/s3-request-presigner';

const s3 = new S3Client({ region: 'us-east-1' });

export async function POST(request: Request) {
  const { filename, contentType, fileSize } = await request.json();

  // Validate
  if (!allowedTypes.includes(contentType)) {
    return Response.json({ error: 'Invalid file type' }, { status: 400 });
  }
  if (fileSize > MAX_FILE_SIZE) {
    return Response.json({ error: 'File too large' }, { status: 400 });
  }

  // Generate unique key
  const key = `uploads/${crypto.randomUUID()}-${filename}`;

  // Create presigned URL
  const command = new PutObjectCommand({
    Bucket: process.env.S3_BUCKET!,
    Key: key,
    ContentType: contentType,
    ContentLength: fileSize,
  });

  const uploadUrl = await getSignedUrl(s3, command, { expiresIn: 3600 });

  return Response.json({ uploadUrl, key });
}
```

### 2.2 Cloudflare R2 Presigned URL
```typescript
import { S3Client, PutObjectCommand } from '@aws-sdk/client-s3';
import { getSignedUrl } from '@aws-sdk/s3-request-presigner';

const r2 = new S3Client({
  region: 'auto',
  endpoint: `https://${process.env.R2_ACCOUNT_ID}.r2.cloudflarestorage.com`,
  credentials: {
    accessKeyId: process.env.R2_ACCESS_KEY!,
    secretAccessKey: process.env.R2_SECRET_KEY!,
  },
});

// Same presigned URL logic as S3 — R2 is S3-compatible
```

### 2.3 Cloudinary Upload Widget (Alternative)
```tsx
import { Cloudinary } from '@cloudinary/url-gen';

// Upload widget — handles everything client-side
const widget = cloudinary.createUploadWidget(
  {
    cloudName: process.env.NEXT_PUBLIC_CLOUDINARY_CLOUD_NAME,
    uploadPreset: 'my-preset',
    maxFileSize: 10000000, // 10MB
    allowedFormats: ['jpg', 'png', 'webp', 'avif'],
  },
  (error, result) => {
    if (!error && result.event === 'success') {
      console.log(result.info.secure_url);
    }
  }
);

<button onClick={() => widget.open()}>Upload</button>
```

### 2.4 Client-Side Upload with Presigned URL
```typescript
async function uploadFile(file: File) {
  // 1. Get presigned URL from server
  const { uploadUrl, key } = await fetch('/api/upload/presign', {
    method: 'POST',
    body: JSON.stringify({
      filename: file.name,
      contentType: file.type,
      fileSize: file.size,
    }),
  }).then(r => r.json());

  // 2. Upload directly to S3
  const response = await fetch(uploadUrl, {
    method: 'PUT',
    body: file,
    headers: { 'Content-Type': file.type },
  });

  if (!response.ok) throw new Error('Upload failed');

  // 3. Notify server of completion
  await fetch('/api/upload/complete', {
    method: 'POST',
    body: JSON.stringify({ key }),
  });

  return key;
}
```

---

## Part 3: File Validation & Security

### 3.1 Client-Side Validation
```typescript
const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/webp', 'image/avif', 'application/pdf'];
const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

function validateFile(file: File): string | null {
  if (!ALLOWED_TYPES.includes(file.type)) {
    return `File type ${file.type} is not allowed`;
  }
  if (file.size > MAX_FILE_SIZE) {
    return `File size exceeds ${MAX_FILE_SIZE / 1024 / 1024}MB limit`;
  }
  return null;
}
```

### 3.2 Server-Side Validation
```typescript
// Never trust client-side validation alone — always validate on server

// 1. Validate content type
const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/webp', 'image/avif'];
if (!ALLOWED_TYPES.includes(contentType)) {
  return Response.json({ error: 'Invalid file type' }, { status: 400 });
}

// 2. Validate file size (presigned URL condition)
const command = new PutObjectCommand({
  Bucket: bucket,
  Key: key,
  ContentType: contentType,
  ContentLength: fileSize, // S3 will reject if size doesn't match
});

// 3. Verify file after upload — check actual content type
import { fileTypeFromBuffer } from 'file-type';

const response = await fetch(s3Url);
const buffer = await response.arrayBuffer();
const type = await fileTypeFromBuffer(buffer);

if (!type || !ALLOWED_TYPES.includes(type.mime)) {
  await s3.deleteObject({ Bucket: bucket, Key: key });
  throw new Error('File content does not match declared type');
}
```

### 3.3 Security Best Practices
- **Validate content type:** Check both declared type and actual file magic bytes
- **Validate file size:** Set limits in presigned URL conditions and server
- **Generate unique filenames:** Use UUIDs — don't use user-supplied filenames
- **Isolate upload path:** Store uploads in separate bucket/prefix
- **Scan for malware:** Use ClamAV or cloud scanning service for user uploads
- **Strip EXIF metadata:** Remove GPS and personal data from images
- **Set CORS on storage:** Only allow uploads from your domain
- **Use private buckets:** Don't make buckets public — serve via CDN with signed URLs
- **Rate limit uploads:** Prevent abuse with per-user rate limits
- **Virus scan:** For high-security applications, scan all uploaded files

---

## Part 4: Image Processing

### 4.1 Server-Side Processing with Sharp
```typescript
import sharp from 'sharp';

async function processImage(buffer: Buffer, key: string) {
  // Get metadata
  const metadata = await sharp(buffer).metadata();
  console.log(`${metadata.width}x${metadata.height}, ${metadata.format}`);

  // Generate multiple sizes
  const sizes = [
    { width: 400, suffix: 'small' },
    { width: 800, suffix: 'medium' },
    { width: 1200, suffix: 'large' },
    { width: 1600, suffix: 'xlarge' },
  ];

  for (const { width, suffix } of sizes) {
    const outputKey = key.replace(/\.\w+$/, `-${suffix}.avif`);
    const processed = await sharp(buffer)
      .resize(width, null, { withoutEnlargement: true })
      .avif({ quality: 80 })
      .toBuffer();

    await s3.putObject({
      Bucket: bucket,
      Key: outputKey,
      Body: processed,
      ContentType: 'image/avif',
    });
  }

  // Generate WebP fallbacks
  for (const { width, suffix } of sizes) {
    const outputKey = key.replace(/\.\w+$/, `-${suffix}.webp`);
    const processed = await sharp(buffer)
      .resize(width, null, { withoutEnlargement: true })
      .webp({ quality: 75 })
      .toBuffer();

    await s3.putObject({
      Bucket: bucket,
      Key: outputKey,
      Body: processed,
      ContentType: 'image/webp',
    });
  }

  // Strip EXIF and generate original
  const cleanOriginal = await sharp(buffer)
    .rotate() // auto-rotate based on EXIF
    .removeExif()
    .jpeg({ quality: 85, progressive: true })
    .toBuffer();

  await s3.putObject({
    Bucket: bucket,
    Key: key,
    Body: cleanOriginal,
    ContentType: 'image/jpeg',
  });
}
```

### 4.2 Thumbnail Generation
```typescript
async function generateThumbnail(buffer: Buffer, key: string) {
  const thumbnail = await sharp(buffer)
    .resize(200, 200, { fit: 'cover', position: 'center' })
    .avif({ quality: 70 })
    .toBuffer();

  const thumbKey = key.replace(/\.\w+$/, '-thumb.avif');
  await s3.putObject({ Bucket: bucket, Key: thumbKey, Body: thumbnail, ContentType: 'image/avif' });
  return thumbKey;
}
```

### 4.3 Image Optimization Pipeline
```
Upload → Validate → Process with Sharp → Store variants → Return URLs

1. Validate: Check type, size, magic bytes
2. Auto-rotate: Based on EXIF orientation
3. Strip EXIF: Remove GPS, camera info
4. Generate sizes: 400w, 800w, 1200w, 1600w
5. Generate formats: AVIF (primary), WebP (fallback), JPEG (fallback)
6. Generate thumbnail: 200x200 cover
7. Store all variants in S3
8. Return URLs for all variants
```

---

## Part 5: Drag-and-Drop UI

### 5.1 React Drop Zone
```tsx
import { useDropzone } from 'react-dropzone';

function UploadZone({ onUpload }) {
  const { getRootProps, getInputProps, isDragActive, isDragReject } = useDropzone({
    accept: {
      'image/*': ['.jpg', '.jpeg', '.png', '.webp', '.avif'],
      'application/pdf': ['.pdf'],
    },
    maxSize: 10 * 1024 * 1024, // 10MB
    onDrop: (acceptedFiles, rejectedFiles) => {
      if (rejectedFiles.length > 0) {
        // Show errors for rejected files
        rejectedFiles.forEach(file => {
          console.error(file.errors);
        });
      }
      acceptedFiles.forEach(file => onUpload(file));
    },
  });

  return (
    <div
      {...getRootProps()}
      className={`border-2 border-dashed rounded-lg p-8 text-center cursor-pointer transition-colors
        ${isDragActive ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-gray-400'}
        ${isDragReject ? 'border-red-500 bg-red-50' : ''}`}
    >
      <input {...getInputProps()} />
      {isDragActive ? (
        <p>Drop the files here...</p>
      ) : (
        <p>Drag & drop files here, or click to select</p>
      )}
      <p className="text-sm text-gray-500 mt-2">Max 10MB • JPG, PNG, WebP, AVIF, PDF</p>
    </div>
  );
}
```

### 5.2 Paste from Clipboard
```tsx
useEffect(() => {
  const handlePaste = (event: ClipboardEvent) => {
    const items = event.clipboardData?.items;
    if (!items) return;

    for (const item of items) {
      if (item.type.startsWith('image/')) {
        const file = item.getAsFile();
        if (file) onUpload(file);
      }
    }
  };

  window.addEventListener('paste', handlePaste);
  return () => window.removeEventListener('paste', handlePaste);
}, [onUpload]);
```

### 5.3 Upload Progress
```tsx
function useUploadProgress() {
  const [progress, setProgress] = useState(0);

  const upload = (file: File, presignedUrl: string) => {
    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest();

      xhr.upload.addEventListener('progress', (event) => {
        if (event.lengthComputable) {
          setProgress((event.loaded / event.total) * 100);
        }
      });

      xhr.addEventListener('load', () => resolve(xhr.response));
      xhr.addEventListener('error', () => reject(new Error('Upload failed')));

      xhr.open('PUT', presignedUrl);
      xhr.setRequestHeader('Content-Type', file.type);
      xhr.send(file);
    });
  };

  return { progress, upload };
}
```

### 5.4 File Preview
```tsx
function FilePreview({ file }) {
  const [preview, setPreview] = useState<string>();

  useEffect(() => {
    if (file.type.startsWith('image/')) {
      const url = URL.createObjectURL(file);
      setPreview(url);
      return () => URL.revokeObjectURL(url);
    }
  }, [file]);

  if (preview) {
    return <img src={preview} alt={file.name} className="max-h-32 rounded" />;
  }
  return <div className="flex items-center gap-2"><FileIcon /> {file.name}</div>;
}
```

---

## Part 6: Chunked Uploads (Large Files)

### 6.1 S3 Multipart Upload
```typescript
// Server — initiate multipart upload
import { CreateMultipartUploadCommand, UploadPartCommand, CompleteMultipartUploadCommand } from '@aws-sdk/client-s3';

// 1. Create multipart upload
const { UploadId } = await s3.send(new CreateMultipartUploadCommand({
  Bucket: bucket,
  Key: key,
  ContentType: contentType,
}));

// 2. Generate presigned URLs for each part
const partCount = Math.ceil(fileSize / CHUNK_SIZE);
const presignedUrls = [];
for (let i = 1; i <= partCount; i++) {
  const command = new UploadPartCommand({
    Bucket: bucket,
    Key: key,
    UploadId,
    PartNumber: i,
  });
  const url = await getSignedUrl(s3, command, { expiresIn: 3600 });
  presignedUrls.push(url);
}

return Response.json({ uploadId: UploadId, presignedUrls });

// 3. Client uploads each part
for (let i = 0; i < parts.length; i++) {
  const start = i * CHUNK_SIZE;
  const end = Math.min(start + CHUNK_SIZE, file.size);
  const chunk = file.slice(start, end);

  await fetch(presignedUrls[i], { method: 'PUT', body: chunk });
  // Collect ETag from response header
}

// 4. Server — complete multipart upload
await s3.send(new CompleteMultipartUploadCommand({
  Bucket: bucket,
  Key: key,
  UploadId,
  MultipartUpload: {
    Parts: parts.map(({ ETag }, i) => ({ ETag, PartNumber: i + 1 })),
  },
}));
```

### 6.2 Resumable Uploads
- **Track progress:** Store completed parts in IndexedDB
- **Resume:** On reconnect, check which parts are already uploaded
- **Retry:** Retry failed parts individually
- **Abort:** Allow user to cancel and clean up incomplete uploads

---

## Part 7: File Storage Strategy

### 7.1 Storage Options

| Service | Pros | Cons | Pricing |
|---|---|---|---|
| **AWS S3** | Industry standard, reliable | Egress fees | $0.023/GB |
| **Cloudflare R2** | No egress fees, S3-compatible | Newer, fewer features | $0.015/GB, no egress |
| **Backblaze B2** | Cheap, S3-compatible | Slower than S3 | $0.006/GB |
| **Cloudinary** | Built-in transformations | More expensive | Per-credit |
| **Uploadthing** | Developer-friendly, handles everything | Less control | Per-file |

### 7.2 File Organization
```
uploads/
  images/
    {uuid}-small.avif
    {uuid}-medium.avif
    {uuid}-large.avif
    {uuid}-thumb.avif
    {uuid}-original.jpg
  documents/
    {uuid}.pdf
  videos/
    {uuid}.mp4
    {uuid}-thumb.jpg
```

### 7.3 CDN Delivery
```typescript
// Serve files via CDN — not directly from S3
// CloudFront (AWS), Cloudflare CDN, or R2 public access

// Signed URLs for private files
import { getSignedUrl } from '@aws-sdk/s3-request-presigner';
import { GetObjectCommand } from '@aws-sdk/client-s3';

const url = await getSignedUrl(s3, new GetObjectCommand({
  Bucket: bucket,
  Key: key,
}), { expiresIn: 3600 }); // 1 hour access
```

---

## Part 8: Uploadthing (Simpler Alternative)

### 8.1 Setup
```typescript
// app/api/uploadthing/core.ts
import { createUploadthing, FileRouter } from 'uploadthing/next';
import { UploadThingError } from 'uploadthing/server';

const f = createUploadthing();

export const ourFileRouter = {
  imageUploader: f({ image: { maxFileSize: '4MB', maxFileCount: 4 } })
    .middleware(async ({ req }) => {
      const user = await getUser(req);
      if (!user) throw new UploadThingError('Unauthorized');
      return { userId: user.id };
    })
    .onUploadComplete(async ({ metadata, file }) => {
      console.log('Upload complete:', file.url);
      return { url: file.url };
    }),
} satisfies FileRouter;

export type OurFileRouter = typeof ourFileRouter;
```

### 8.2 Client Component
```tsx
import { UploadButton } from '@/utils/uploadthing';

function Upload() {
  return (
    <UploadButton
      endpoint="imageUploader"
      onClientUploadComplete={(res) => {
        console.log('Files:', res);
      }}
      onUploadError={(error) => {
        console.error('Error:', error);
      }}
    />
  );
}
```

---

## Execution Instructions for Cascade

When this skill is activated for file handling & media uploads:

1. **Read the project context** — file types, sizes, storage needs, processing requirements
2. **Choose upload architecture** — presigned URLs (default), server-mediated (small files), chunked (large files)
3. **Choose storage** — S3, R2 (no egress fees), Cloudinary (built-in transforms), Uploadthing (simplest)
4. **Implement presigned URL flow** — server generates URL, client uploads directly to storage
5. **Implement validation** — client-side + server-side: type, size, magic bytes
6. **Implement image processing** — Sharp for resize, format conversion, EXIF stripping, thumbnails
7. **Implement drag-and-drop UI** — react-dropzone with file preview and paste support
8. **Implement upload progress** — XHR for progress events
9. **Implement chunked uploads** — for large files (> 100MB), with resume support
10. **Set up CDN delivery** — serve files via CDN, signed URLs for private files
11. **Implement security** — unique filenames, isolated paths, malware scanning, CORS, rate limits
12. **Test** — all file types, large files, rejected files, network failures, concurrent uploads
13. **Document** — upload limits, supported formats, storage architecture, processing pipeline
