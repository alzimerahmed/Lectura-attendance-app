---
name: security
description: Comprehensive security audit workflow — threat model, automated scanning, manual penetration testing, and remediation tracking
---

# Security Audit Workflow

This workflow applies the **Security Audit Skill** (`~/.codeium/windsurf/skills/security-audit.md`) to systematically audit web application security.

## When to Run
- Before launching a new project
- When the user says `/security` or asks for a security audit
- After significant changes to auth, API, or data handling
- Quarterly for ongoing projects
- After a security incident

---

## Step 1: Threat Modeling

1. Identify assets: What data and functionality needs protection?
2. Identify threats: Who might attack? What are their capabilities?
3. Map attack surfaces: Every input, endpoint, interface, integration
4. Map data flow: How does sensitive data flow through the system?
5. Identify trust boundaries: Where does untrusted data enter trusted areas?
6. Prioritize: Focus on high-value assets and likely attack vectors

## Step 2: Automated Scanning

1. **SAST:** Run ESLint security plugin, Semgrep, CodeQL
2. **DAST:** Run OWASP ZAP or Nuclei against staging environment
3. **Dependency audit:** `npm audit` / `pnpm audit`, Snyk, Dependabot
4. **Secret scanning:** git-secrets, TruffleHog, Gitleaks
5. **Container scanning:** Trivy on Docker images (if applicable)
6. **IaC scanning:** Checkov, tfsec on infrastructure files (if applicable)
7. Review all findings — filter false positives, classify real issues

## Step 3: OWASP Top 10 Review

Go through each category systematically:
- [ ] A01: Broken Access Control — IDOR, missing authz, privilege escalation
- [ ] A02: Cryptographic Failures — weak algorithms, plaintext storage, hardcoded secrets
- [ ] A03: Injection — SQL, NoSQL, command, LDAP, template, XPath
- [ ] A04: Insecure Design — missing threat modeling, no rate limiting, business logic flaws
- [ ] A05: Security Misconfiguration — default creds, verbose errors, open storage, missing headers
- [ ] A06: Vulnerable Components — known CVEs, typosquatting, transitive deps
- [ ] A07: Auth Failures — weak passwords, credential stuffing, session fixation, JWT issues
- [ ] A08: Software/Data Integrity — insecure deserialization, unsigned updates, supply chain
- [ ] A09: Logging/Monitoring Failures — missing audit logs, no alerting, no incident response
- [ ] A10: SSRF — unvalidated URLs, internal network access

## Step 4: Authentication & Session Testing

1. Check password storage: bcrypt/argon2, not plaintext or weak hashes
2. Check session management: HttpOnly, Secure, SameSite cookies, session timeout
3. Check JWT: algorithm verification, expiration, signature, not in localStorage
4. Check MFA: TOTP, backup codes, recovery flow, WebAuthn/passkeys
5. Check OAuth: state parameter, PKCE, nonce, token validation
6. Check password reset: token security, no email enumeration, session invalidation
7. Test: brute force login, session fixation, JWT manipulation

## Step 5: Authorization Testing

1. Test RBAC: each role against each endpoint — verify access matrix
2. Test resource-level auth: user A accessing user B's resources (IDOR)
3. Test API authorization: every endpoint authenticated and authorized
4. Test GraphQL: field-level authorization, depth limiting, cost analysis
5. Test WebSocket: connection auth, per-message authorization
6. Test frontend auth bypass: call API directly, bypassing UI guards
7. Test multi-tenant isolation: tenant A cannot access tenant B's data

## Step 6: Input Validation & Output Encoding

1. Check server-side validation: Zod/Valibot schema at every API boundary
2. Test SQL injection: `' OR 1=1 --`, `'; DROP TABLE--`, UNION SELECT
3. Test XSS: `<script>`, `<img onerror>`, `javascript:` in all inputs
4. Test command injection: `; rm -rf`, `| cat /etc/passwd`, `$(whoami)`
5. Test path traversal: `../` in file paths
6. Test SSRF: URLs pointing to localhost, internal IPs, cloud metadata
7. Test file upload: double extensions, malicious content, oversized files
8. Check output encoding: context-aware (HTML, attribute, JS, URL, CSS)

## Step 7: API Security Testing

1. Check rate limiting: per IP, per user, per endpoint — verify 429 response
2. Check API key management: hashed storage, rotation, scoping, revocation
3. Check CORS: allowlist origins, no wildcard with credentials
4. Check request size limits: body, URL, headers
5. Check response data filtering: no sensitive fields, pagination on lists
6. Check GraphQL: depth limiting, cost analysis, introspection disabled in prod
7. Test mass assignment: try setting `isAdmin: true` or other restricted fields

## Step 8: Infrastructure Security Review

1. Check security headers: HSTS, CSP, X-Content-Type-Options, X-Frame-Options, Referrer-Policy
2. Check TLS: TLS 1.2+, strong ciphers, valid certificate, HSTS preload
3. Check firewall: WAF, security groups, port access
4. Check network: VPC, private subnets, no public DB access
5. Check containers: non-root user, read-only filesystem, resource limits, image scanning
6. Check cloud storage: private buckets, encryption, access policies, access logs
7. Test: port scan with nmap, SSL Labs test, verify only expected ports open

## Step 9: Dependency Security Review

1. Run `npm audit` / `pnpm audit` — review all vulnerabilities
2. Check Snyk/Dependabot for ongoing monitoring
3. Review new dependencies: maintainer reputation, security history, transitive deps
4. Check for typosquatting: verify package names
5. Check license compliance: scan for copyleft licenses
6. Generate SBOM for vulnerability matching
7. Verify lockfile integrity: `pnpm install --frozen-lockfile` in CI

## Step 10: Manual Penetration Testing

1. **Business logic:** price manipulation, negative quantities, race conditions, workflow bypass
2. **Session testing:** fixation, hijacking, cookie attributes, timeout, concurrent sessions
3. **API pen testing:** auth bypass, authz bypass, parameter tampering, mass assignment, IDOR
4. **Attack surface mapping:** test every endpoint, every input, every integration
5. Document all findings with reproduction steps and evidence

## Step 11: Compile Findings & Remediation

1. **Classify each finding** by severity:
   - Critical (CVSS 9.0+): RCE, data breach, auth bypass → fix within 24h
   - High (CVSS 7.0-8.9): SQLi, XSS, privilege escalation → fix within 7 days
   - Medium (CVSS 4.0-6.9): info disclosure, missing rate limiting → fix within 30 days
   - Low (CVSS 0.1-3.9): verbose errors, missing headers → fix within 90 days

2. **Create issues** for each vulnerability with:
   - Title, severity, CVSS score
   - Description, reproduction steps, evidence
   - Remediation recommendation
   - Status tracking: Open → In Progress → Fixed → Verified → Closed

3. **Generate security report:**
   - Executive summary
   - Methodology
   - Findings with details
   - Remediation recommendations

4. **Verify fixes:**
   - Re-test after fix
   - Run regression tests
   - Re-scan for new vulnerabilities
   - Security team sign-off

5. **Set up continuous security:**
   - Regular audits (quarterly)
   - Dependency updates (monthly)
   - Security training for developers
   - Bug bounty or responsible disclosure program
