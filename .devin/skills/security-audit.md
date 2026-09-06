---
name: Security Audit Skill
description: Comprehensive methodology for auditing web application security — 2025-2026 practices with OWASP Top 10, automated scanning, manual penetration testing, and remediation tracking
version: 1.0.0
tags: [security, audit, owasp, penetration-testing, vulnerability, remediation, compliance]
---

# Security Audit Skill

## Purpose
This skill provides a comprehensive methodology for auditing web application security across any kind of web project. It reflects **modern 2025-2026 security practices** — OWASP Top 10 coverage, automated scanning combined with manual penetration testing, and structured remediation tracking. Not just running tools but understanding what they find and what they miss.

## Core Philosophy

**Security is a process, not a checkbox.** Running a scanner and fixing what it finds is necessary but insufficient. Automated tools catch ~30-40% of vulnerabilities. The rest require manual review, threat modeling, and understanding the application's business logic.

**The #1 rule:** Think like an attacker. Every input is an attack vector. Every endpoint is a target. Every user is potentially malicious. Trust nothing, validate everything.

---

## Part 1: OWASP Top 10 (2025-2026)

### A01: Broken Access Control
- **IDOR (Insecure Direct Object Reference):** Can user A access user B's resources by changing the ID?
- **Missing authorization checks:** Does every endpoint verify the user is authorized?
- **Privilege escalation:** Can a regular user access admin functionality?
- **Force browsing:** Can users access pages they shouldn't by typing URLs?
- **API exposure:** Are internal APIs accessible without authentication?
- **Testing:** Try accessing resources with different user accounts, modify IDs in URLs/requests

### A02: Cryptographic Failures
- **Weak algorithms:** MD5, SHA1 for passwords — use bcrypt, argon2, scrypt
- **Plaintext storage:** Are passwords hashed? Are sensitive data encrypted at rest?
- **Weak TLS:** TLS 1.0/1.1, weak cipher suites, self-signed certs in production
- **Hardcoded secrets:** API keys, passwords in source code
- **Insecure key management:** Keys in env files, not rotated, not stored in secrets manager
- **Testing:** Check TLS configuration (SSL Labs), search for hardcoded secrets (git-secrets, TruffleHog)

### A03: Injection
- **SQL injection:** String-concatenated queries, raw SQL with user input
- **NoSQL injection:** User input as query objects (`{ $gt: "" }`)
- **Command injection:** `exec()`, `execSync()` with user input
- **LDAP injection:** User input in LDAP queries
- **Template injection:** User input in template engines
- **XPath injection:** User input in XPath queries
- **Testing:** Try `'`, `"`, `;`, `--`, `${}`, `{$gt: ""}` in all input fields and API parameters

### A04: Insecure Design
- **Missing threat modeling:** No analysis of attack surfaces and threat actors
- **No rate limiting:** Brute force, credential stuffing, API abuse
- **No defense in depth:** Single layer of security, no redundancy
- **Business logic flaws:** Negative quantities, price manipulation, race conditions in checkout
- **Testing:** Think about what an attacker could do with the business logic, not just technical exploits

### A05: Security Misconfiguration
- **Default credentials:** Admin/admin, test/test still active
- **Verbose errors:** Stack traces, internal paths exposed to users
- **Unnecessary features:** Debug endpoints, admin panels enabled in production
- **Open storage:** S3 buckets, databases without authentication
- **Missing security headers:** No HSTS, X-Content-Type-Options, X-Frame-Options, CSP
- **Testing:** Check response headers, try debug endpoints, scan for open ports

### A06: Vulnerable Components
- **Known CVEs:** Outdated dependencies with known vulnerabilities
- **Typosquatting:** Malicious packages with names similar to popular ones
- **Transitive dependencies:** Vulnerabilities in dependencies of dependencies
- **Testing:** `npm audit`, `pnpm audit`, Snyk, Dependabot, Trivy on Docker images

### A07: Authentication Failures
- **Weak passwords:** No minimum complexity, no breach checking
- **Credential stuffing:** No rate limiting on login, no CAPTCHA after failures
- **Session fixation:** Session ID not regenerated after login
- **Missing MFA:** No multi-factor authentication for sensitive accounts
- **JWT issues:** `algorithm: none`, not verifying signature, not checking expiry
- **Testing:** Try brute force login, check session handling, test JWT manipulation

### A08: Software/Data Integrity Failures
- **Insecure deserialization:** Untrusted data deserialized without validation
- **Unsigned updates:** CI/CD pipeline can be manipulated
- **Supply chain attacks:** Dependencies compromised, build tools tampered
- **Testing:** Check deserialization of user input, verify CI/CD security, review dependency sources

### A09: Logging/Monitoring Failures
- **Missing audit logs:** No log of security events (login, logout, failed login, data access)
- **No alerting:** Breaches go undetected for months
- **No incident response:** No plan for when a breach occurs
- **Testing:** Check what's logged, verify alerts fire on security events, review incident response plan

### A10: SSRF (Server-Side Request Forgery)
- **Unvalidated URLs:** Server fetches user-provided URLs
- **Internal network access:** Server can access internal services (169.254.169.254 on AWS)
- **Testing:** Provide URLs pointing to internal services, localhost, cloud metadata endpoints

---

## Part 2: Authentication & Session Security

### 2.1 Password Storage
- **Hashing:** bcrypt (cost factor ≥ 12), argon2id (recommended), scrypt
- **Never store plaintext:** Not even temporarily, not even in logs
- **Never roll your own crypto:** Use established libraries
- **Password breach checking:** Use HaveIBeenPwned API or k-anonymity model
- **Password requirements:** Minimum 12 characters, check against common passwords, don't enforce arbitrary complexity rules

### 2.2 Session Management
- **Session ID:** Long, random, unpredictable — use `crypto.randomUUID()` or library
- **Session storage:** HttpOnly, Secure, SameSite cookies — not localStorage
- **Session timeout:** Idle timeout (30 min), absolute timeout (24 hours)
- **Session regeneration:** New session ID after login, privilege change
- **Session revocation:** Server-side session store, ability to invalidate sessions
- **Concurrent sessions:** Limit or track concurrent sessions per user

### 2.3 JWT Security
- **Algorithm:** Always specify expected algorithm — don't accept `none`
- **Verification:** Verify signature, expiration, issuer, audience
- **Storage:** HttpOnly cookies, not localStorage (XSS can steal from localStorage)
- **Expiration:** Short-lived access tokens (15 min), long-lived refresh tokens
- **Revocation:** Token blacklist or version-based invalidation
- **Claims:** Include minimum necessary claims — don't leak sensitive data

### 2.4 MFA Implementation
- **TOTP:** Google Authenticator, Authy — use `otplib` or similar
- **Backup codes:** Generate one-time use codes, store hashed
- **Recovery:** Secure recovery flow — don't bypass MFA with email-only reset
- **Enforcement:** Require MFA for sensitive operations (password change, data export)
- **WebAuthn/Passkeys:** Platform authenticators (Face ID, Touch ID, security keys)

### 2.5 OAuth/OIDC Integration
- **State parameter:** Random, verified on callback — prevents CSRF
- **PKCE:** Use for public clients (SPAs, mobile) — prevents authorization code interception
- **Nonce:** For OpenID Connect — prevents token replay
- **Token validation:** Verify ID token signature, issuer, audience, expiration
- **Scopes:** Request minimum necessary scopes — don't over-permission

### 2.6 Password Reset Flows
- **Token:** Random, single-use, time-limited (15 min), stored hashed
- **Channel:** Send to verified email only — don't reveal whether email exists
- **After reset:** Invalidate all existing sessions, require new password
- **No email enumeration:** Same message whether email exists or not
- **Rate limit:** Prevent reset email flooding

---

## Part 3: Authorization & Access Control

### 3.1 Role-Based Access Control (RBAC)
- **Roles:** Define roles (admin, editor, viewer) with specific permissions
- **Enforcement:** Check role on every request — not just in UI
- **Default deny:** If no role matches, deny access
- **Principle of least privilege:** Give users the minimum permissions they need
- **Testing:** Test each role against each endpoint — verify access matrix

### 3.2 Attribute-Based Access Control (ABAC)
- **Attributes:** User attributes (department, location), resource attributes (owner, sensitivity), environment (time, IP)
- **Policies:** "Users can only access resources in their department"
- **More flexible than RBAC** but more complex to implement and test
- **Testing:** Test with different attribute combinations

### 3.3 Resource-Level Authorization
- **Ownership check:** `if (resource.userId !== currentUser.id) return 403`
- **Tenant isolation:** Multi-tenant apps must verify tenant on every request
- **Hierarchical access:** Team members can access team resources, not other teams'
- **Testing:** User A tries to access user B's resources — must get 403

### 3.4 API Authorization
- **Every endpoint:** Authenticated and authorized — no exceptions
- **GraphQL:** Field-level authorization — not just query-level
- **WebSockets:** Authenticate connection, authorize each message
- **Public APIs:** Rate limited, API key required, CORS restricted
- **Testing:** Call every endpoint without auth (should 401), with wrong user (should 403)

### 3.5 Frontend Authorization (Not Security)
- **UI hiding is not security:** Hiding admin buttons doesn't prevent access
- **Server must enforce:** Every API call must be authorized server-side
- **Route guards:** Redirect unauthorized users, but server must also reject
- **Testing:** Bypass UI by calling API directly — must still be rejected

---

## Part 4: Input Validation & Output Encoding

### 4.1 Server-Side Validation (Always)
- **Validate at API boundary:** Every request body, query param, path param
- **Schema validation:** Use Zod, Valibot, or similar — define expected shape
- **Type checking:** Ensure correct types (string, number, boolean, date)
- **Range checking:** Min/max for numbers, length for strings, size for arrays
- **Format checking:** Email, URL, UUID, phone, date format
- **Allowlist:** Prefer allowlist (only these values) over blocklist (not these values)

### 4.2 Client-Side Validation (UX Only)
- **For user experience:** Immediate feedback, better UX
- **Never as security:** Client validation can be bypassed — server must re-validate
- **Progressive enhancement:** Validate on submit, then enhance with client-side
- **Testing:** Disable JavaScript, submit form — server must still validate

### 4.3 SQL Injection Prevention
- **Parameterized queries:** `db.query('SELECT * FROM users WHERE id = $1', [id])`
- **ORM methods:** Use ORM's query builder — don't concatenate SQL strings
- **Never:** `db.query(`SELECT * FROM users WHERE id = ${id}`)` — SQL injection
- **Testing:** Try `' OR 1=1 --`, `'; DROP TABLE users; --`, `1 UNION SELECT * FROM admin--`

### 4.4 XSS Prevention
- **Context-aware encoding:** Different encoding for HTML, attribute, JavaScript, URL contexts
- **React/Vue/Svelte:** Auto-escape by default — don't use `dangerouslySetInnerHTML`, `v-html`, `{@html}`
- **Content Security Policy:** Restrict scripts to same origin, no inline scripts
- **Sanitize HTML:** Use DOMPurify for user-provided HTML
- **Testing:** Try `<script>alert(1)</script>`, `<img onerror=alert(1)>`, `javascript:alert(1)` in all inputs

### 4.5 Command Injection Prevention
- **Avoid shell:** Use `execFile()` with array arguments, not `exec()` with string
- **Never:** `exec(`ls ${userInput}`)` — command injection
- **Sanitize:** If shell is necessary, sanitize input and use quoting
- **Testing:** Try `; rm -rf /`, `| cat /etc/passwd`, `$(whoami)` in command inputs

### 4.6 File Upload Security
- **Validate type:** Check MIME type AND magic number (file signature)
- **Validate size:** Enforce maximum file size
- **Validate name:** Sanitize filename — no path traversal (`../`)
- **Store outside webroot:** Don't serve uploaded files from the app directory
- **Scan for malware:** ClamAV or similar for user uploads
- **Don't execute:** Set `Content-Disposition: attachment` for downloads
- **Testing:** Upload files with double extensions, malicious content, oversized files

### 4.7 Output Encoding by Context

| Context | Encoding | Example |
|---|---|---|
| HTML text | HTML entity encode | `<` → `&lt;` |
| HTML attribute | Attribute encode | `"` → `&quot;` |
| JavaScript string | JavaScript encode | `'` → `\x27` |
| URL | URL encode | `&` → `%26` |
| CSS | CSS escape | `<` → `\3c` |

---

## Part 5: API Security

### 5.1 Rate Limiting
- **Per IP:** Limit requests per IP address (e.g., 100 req/min)
- **Per user:** Limit requests per authenticated user (e.g., 1000 req/hour)
- **Per endpoint:** Stricter limits on expensive endpoints (search, export)
- **Response:** 429 Too Many Requests with `Retry-After` header
- **Storage:** Redis for distributed rate limiting
- **Testing:** Send requests rapidly, verify 429 response

### 5.2 API Key Management
- **Generation:** `crypto.randomUUID()` or `crypto.randomBytes(32).toString('hex')`
- **Storage:** Hashed in database — like passwords
- **Rotation:** Allow key rotation, support multiple active keys
- **Scoping:** Limit keys to specific permissions
- **Revocation:** Ability to revoke keys immediately
- **Testing:** Try revoked key, expired key, invalid key

### 5.3 CORS Configuration
- **Allowlist origins:** Specific domains, not `*`
- **Credentials:** `credentials: true` only with specific origins (never with `*`)
- **Headers:** Only allow necessary headers
- **Methods:** Only allow necessary methods
- **Preflight:** `Access-Control-Max-Age` to cache preflight responses
- **Testing:** Send request from different origin, verify CORS headers

### 5.4 Request Size Limits
- **Body size:** Limit request body size (e.g., 1MB for JSON, 10MB for file uploads)
- **URL length:** Limit URL length
- **Header size:** Limit header size
- **Number of parameters:** Limit number of form fields
- **Testing:** Send oversized payloads, verify rejection

### 5.5 Response Data Filtering
- **Never return everything:** Filter response fields based on authorization
- **No sensitive fields:** Don't return password hashes, tokens, internal IDs
- **Pagination:** Always paginate list endpoints — prevent data dumping
- **Field selection:** Don't allow arbitrary field selection (`?fields=password`)
- **Testing:** Check API responses for sensitive data, try to access all records

### 5.6 GraphQL Security
- **Depth limiting:** Limit query depth to prevent deeply nested queries
- **Cost analysis:** Assign cost to fields, limit total query cost
- **Rate limiting:** Per-query rate limiting, not just per-request
- **Introspection:** Disable in production
- **Authorization:** Field-level authorization — not just query-level
- **Testing:** Send deeply nested queries, expensive queries, introspection queries

---

## Part 6: Infrastructure Security

### 6.1 Security Headers

| Header | Purpose | Value |
|---|---|---|
| `Strict-Transport-Security` | Force HTTPS | `max-age=31536000; includeSubDomains; preload` |
| `X-Content-Type-Options` | Prevent MIME sniffing | `nosniff` |
| `X-Frame-Options` | Prevent clickjacking | `DENY` or `SAMEORIGIN` |
| `Content-Security-Policy` | Restrict resource loading | `default-src 'self'` |
| `X-XSS-Protection` | Legacy XSS protection | `0` (disable, use CSP instead) |
| `Referrer-Policy` | Control referrer leakage | `strict-origin-when-cross-origin` |
| `Permissions-Policy` | Control browser features | `camera=(), microphone=(), geolocation=()` |
| `Cross-Origin-Opener-Policy` | Isolate browsing context | `same-origin` |
| `Cross-Origin-Embedder-Policy` | Isolate loaded resources | `require-corp` |

### 6.2 TLS Configuration
- **TLS 1.2+ minimum:** Disable TLS 1.0 and 1.1
- **Strong ciphers:** AES-GCM, ChaCha20-Poly1305 — disable weak ciphers
- **HSTS:** `Strict-Transport-Security` header with preload
- **Certificate:** Valid, not expired, from trusted CA (Let's Encrypt, ACM)
- **OCSP stapling:** Reduce TLS handshake latency
- **Testing:** SSL Labs SSL Test, `sslyze` CLI

### 6.3 Firewall Configuration
- **WAF (Web Application Firewall):** Cloudflare, AWS WAF — filter malicious traffic
- **Security groups:** Only allow necessary ports (80, 443) — block everything else
- **Rate limiting at firewall:** Cloudflare rate rules, AWS WAF rate-based rules
- **Geo-blocking:** Block traffic from unexpected countries if applicable
- **DDoS protection:** Cloudflare, AWS Shield — absorb volumetric attacks

### 6.4 Network Security
- **VPC:** Private network for internal resources
- **Private subnets:** Database, cache in private subnet — no internet access
- **Security groups:** Restrict inbound/outbound traffic
- **NACLs:** Network ACLs for subnet-level filtering
- **VPN/Bastion:** Secure access to internal resources
- **Testing:** Port scan (`nmap`), verify only expected ports are open

### 6.5 Container Security
- **Non-root user:** Run containers as non-root
- **Read-only filesystem:** `readOnlyRootFilesystem: true` in Kubernetes
- **Resource limits:** CPU and memory limits to prevent resource exhaustion
- **Image scanning:** Trivy, Snyk — scan for known vulnerabilities
- **Minimal images:** Use `slim` or `distroless` base images
- **No secrets in images:** Use runtime secrets (env vars, mounted files)

### 6.6 Cloud Storage Security
- **S3/R2 buckets:** Private by default, no public access unless intended
- **Bucket policies:** Restrict access to specific IAM roles
- **Encryption:** Server-side encryption (SSE-S3, SSE-KMS)
- **Versioning:** Enable for accidental deletion recovery
- **Access logs:** Enable bucket access logging
- **Testing:** Try accessing bucket without auth, check bucket policy

---

## Part 7: Dependency Security

### 7.1 npm audit / pnpm audit
- Run regularly in CI — fail on high/critical vulnerabilities
- Review results — not all vulnerabilities affect your code
- Fix or upgrade affected dependencies
- Use `npm audit fix` for automated fixes, `npm audit fix --force` for breaking changes

### 7.2 Snyk / Dependabot
- **Snyk:** Continuous vulnerability monitoring, automated PRs for fixes
- **Dependabot:** GitHub-native, automated dependency updates
- **Renovate:** Alternative to Dependabot, more configurable
- **Review:** Don't auto-merge dependency updates — review and test

### 7.3 License Compliance
- **Scan licenses:** `license-checker`, `license-compatibility-checker`
- **Avoid copyleft:** GPL/AGPL in commercial projects (check legal)
- **Record licenses:** Keep a list of all dependencies and their licenses
- **Review transitive deps:** Licenses apply to all dependencies, not just direct

### 7.4 Supply Chain Attacks
- **Lockfile:** Use `pnpm-lock.yaml` / `package-lock.json` — pin exact versions
- **`npm ci` / `pnpm install --frozen-lockfile`:** Install from lockfile, don't update
- **Package signing:** `npm pkg sigstore` — verify package integrity
- **Registry:** Use official npm registry, not random registries
- **Review new deps:** Check maintainer reputation, download count, security history

### 7.5 SBOM (Software Bill of Materials)
- Generate SBOM: `npm sbom` or `cyclonedx-npm`
- Track all components and their versions
- Use for vulnerability matching when new CVEs are announced
- Required for some compliance frameworks (EO 14028)

---

## Part 8: Automated Security Scanning

### 8.1 SAST (Static Application Security Testing)
- **ESLint security plugin:** `eslint-plugin-security` — catch common vulnerabilities
- **Semgrep:** Pattern-based static analysis — custom rules for project-specific patterns
- **CodeQL:** GitHub's semantic code analysis — runs in GitHub Actions
- **SonarQube:** Continuous inspection for security hotspots
- **Limitations:** False positives, can't catch business logic flaws

### 8.2 DAST (Dynamic Application Security Testing)
- **OWASP ZAP:** Open-source web app scanner — intercepting proxy + automated scanner
- **Burp Suite:** Commercial, more powerful — intercept, scan, intruder
- **Nuclei:** Template-based scanner — community-maintained templates
- **Running:** Scan staging environment regularly, review findings
- **Limitations:** Can't authenticate (unless configured), can't catch business logic flaws

### 8.3 IAST (Interactive Application Security Testing)
- **Contrast Security:** Instrumented at runtime — detects vulnerabilities during normal use
- **Snyk Code:** Real-time feedback in IDE + CI
- **Advantage:** Fewer false positives than SAST/DAST
- **Limitations:** Only covers code that's executed during testing

### 8.4 Secret Scanning
- **git-secrets:** Prevent committing secrets — pre-commit hook
- **TruffleHog:** Scan git history for secrets
- **GitHub Secret Scanning:** Built-in, scans for known secret patterns
- **Gitleaks:** Fast, open-source secret scanner
- **Action:** If secrets found, rotate immediately — don't just remove from git

### 8.5 Container Image Scanning
- **Trivy:** Fast, comprehensive — OS packages, language deps, IaC configs
- **Snyk Container:** Vulnerability scanning + fix recommendations
- **Grype:** Fast vulnerability scanner for container images
- **In CI:** Scan image before push, fail on critical vulnerabilities

### 8.6 IaC Scanning
- **Checkov:** Scan Terraform, CloudFormation, Kubernetes for misconfigurations
- **tfsec:** Terraform-specific security scanner
- **Snyk IaC:** Scan Terraform, Kubernetes, CloudFormation
- **In CI:** Scan IaC files on every change

---

## Part 9: Manual Penetration Testing

### 9.1 Threat Modeling
1. **Identify assets:** What data and functionality needs protection?
2. **Identify threats:** Who might attack? What are their capabilities?
3. **Identify attack surfaces:** Every input, endpoint, interface is an attack surface
4. **Map data flow:** How does sensitive data flow through the system?
5. **Identify trust boundaries:** Where does untrusted data enter trusted areas?
6. **Prioritize:** Focus on high-value assets and likely attack vectors

### 9.2 Attack Surface Mapping
- **All endpoints:** API routes, GraphQL queries/mutations, WebSocket handlers
- **All inputs:** Form fields, URL params, headers, cookies, file uploads, request bodies
- **All outputs:** API responses, rendered pages, redirects, error messages
- **All integrations:** Third-party APIs, webhooks, OAuth providers
- **All storage:** Database, file storage, cache, session store

### 9.3 Business Logic Testing
- **Price manipulation:** Can users change prices in checkout?
- **Quantity manipulation:** Can users order negative quantities?
- **Race conditions:** Can users use a coupon twice by sending concurrent requests?
- **State manipulation:** Can users skip steps in a multi-step flow?
- **Privilege escalation:** Can users access other users' data or admin functions?
- **Workflow bypass:** Can users skip payment, bypass rate limits, avoid verification?

### 9.4 Session and Cookie Testing
- **Session fixation:** Does session ID change after login?
- **Session hijacking:** Can session IDs be stolen (XSS, network sniffing)?
- **Cookie attributes:** HttpOnly, Secure, SameSite, Path, Domain?
- **Session timeout:** Does session expire after idle/absolute timeout?
- **Concurrent sessions:** Can same user have unlimited sessions?

### 9.5 API Penetration Testing
- **Authentication bypass:** Can API be accessed without auth?
- **Authorization bypass:** Can user access other users' data?
- **Parameter tampering:** Can users add/modify parameters not intended?
- **Mass assignment:** Can users set fields they shouldn't (e.g., `isAdmin: true`)?
- **IDOR:** Can users change IDs to access other resources?
- **Rate limit bypass:** Can users bypass rate limits (different IPs, headers)?

---

## Part 10: Remediation & Reporting

### 10.1 Vulnerability Classification

| Severity | CVSS | Description | Timeline |
|---|---|---|---|
| **Critical** | 9.0-10.0 | Remote code execution, data breach, auth bypass | Fix immediately (within 24h) |
| **High** | 7.0-8.9 | SQL injection, XSS, privilege escalation | Fix within 7 days |
| **Medium** | 4.0-6.9 | Information disclosure, missing rate limiting | Fix within 30 days |
| **Low** | 0.1-3.9 | Verbose errors, missing security header | Fix within 90 days |

### 10.2 Remediation Tracking
- **Issue per vulnerability:** Create issue with details, severity, reproduction steps
- **Assign owner:** Each vulnerability has a responsible person
- **Track status:** Open → In Progress → Fixed → Verified → Closed
- **SLA enforcement:** Critical/High must be fixed within timeline
- **Re-scan after fix:** Verify the fix actually resolves the vulnerability

### 10.3 Security Report Structure
1. **Executive summary:** High-level findings, risk level, business impact
2. **Methodology:** What was tested, what tools were used, what was manual
3. **Findings:** Each vulnerability with:
   - Title, severity, CVSS score
   - Description of the vulnerability
   - Reproduction steps
   - Evidence (screenshots, request/response)
   - Remediation recommendation
   - Status (open/fixed)
4. **Appendix:** Full scan results, raw data

### 10.4 Verification of Fixes
- **Re-test:** After fix, re-run the same test that found the vulnerability
- **Regression:** Verify the fix doesn't break functionality
- **Re-scan:** Run automated scanners to verify no new vulnerabilities introduced
- **Sign-off:** Security team verifies fix before closing the issue

### 10.5 Continuous Security
- **Regular audits:** Quarterly security audits, not just one-time
- **Dependency updates:** Monthly dependency review and updates
- **Security training:** Developers trained on secure coding practices
- **Bug bounty:** Consider a bug bounty program for external researchers
- **Security champions:** Designate a security champion on each team

---

## Execution Instructions for Cascade

When this skill is activated for security auditing:

1. **Read the project structure** — framework, endpoints, auth system, database
2. **Threat model** — identify assets, threats, attack surfaces, trust boundaries
3. **Run automated scans** — SAST (ESLint, Semgrep), DAST (ZAP), dependency audit, secret scanning
4. **Review OWASP Top 10** — go through each category systematically
5. **Manual testing** — business logic, session management, API authorization, IDOR
6. **Check security headers** — HSTS, CSP, X-Content-Type-Options, X-Frame-Options
7. **Check TLS configuration** — SSL Labs test, cipher suites, certificate validity
8. **Check authentication** — password storage, session management, JWT, MFA, OAuth
9. **Check authorization** — RBAC, resource-level auth, API auth, frontend auth (not security)
10. **Check input validation** — server-side validation, SQL injection, XSS, command injection
11. **Check infrastructure** — firewall, network, containers, cloud storage
12. **Compile findings** — classify by severity, create issues, track remediation
13. **Verify fixes** — re-test after remediation, re-scan for new vulnerabilities
