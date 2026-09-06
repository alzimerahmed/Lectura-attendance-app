# Rule: File Handling & Media Uploads for All Projects

**ALWAYS** apply the File Handling & Media Uploads skill and workflow when implementing file upload functionality. Upload directly to storage, not through your server — and never trust user-uploaded files.

## Skill
`~/.codeium/windsurf/skills/file-handling-media-uploads.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/file-handling.md` — invoke with `/file-handling`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/file-handler.md` (parent: Feature Engineer)

## How to follow this rule:
1. When implementing file uploads, invoke the `/file-handling` workflow
2. Follow the workflow steps in order: Assess → Architecture → Storage → Presigned URLs → Validation → Processing → UI → Progress → Chunked → CDN → Security → Test
3. Always use presigned URLs for direct-to-storage uploads — don't route through server
4. Always validate file type and size on both client and server — verify magic bytes
5. Always process images server-side with Sharp — resize, convert to AVIF/WebP, strip EXIF
6. Always generate unique filenames with UUIDs — never use user-supplied filenames
7. Always implement drag-and-drop with paste support and upload progress
8. Always use chunked/multipart uploads for large files (> 100MB) with resume support

## When this rule applies:
- Implementing file upload functionality
- Setting up image processing or media management
- Building drag-and-drop upload UI
- Configuring file storage (S3, R2, Cloudinary)
- User asks about file handling or media uploads

## When this rule does NOT apply:
- Projects with no file upload functionality
- User explicitly says to skip file handling setup
