---
name: Deployment & DevOps Skill
description: Comprehensive methodology for deploying, hosting, and managing infrastructure — 2025-2026 practices with IaC, GitOps, progressive delivery, and observability-first operations
version: 1.0.0
tags: [deployment, devops, ci-cd, infrastructure, docker, kubernetes, monitoring, cloud]
---

# Deployment & DevOps Skill

## Purpose
This skill provides a comprehensive methodology for deploying, hosting, and managing infrastructure for any kind of web project. It reflects **modern 2025-2026 DevOps practices** — infrastructure as code, GitOps, progressive delivery, and observability-first operations. Not manual deployments or click-ops.

## Core Philosophy

**Everything automated, everything versioned, everything observable.** If a process is manual, it will fail. If infrastructure isn't in code, it can't be reproduced. If a system isn't observable, you can't operate it.

**The #1 rule:** Deploy through the same pipeline every time. No "special" production deploys. If the pipeline can't handle it, fix the pipeline.

---

## Part 1: CI/CD Pipeline Design

### 1.1 Pipeline Stages
```
Push → Lint → Type Check → Unit Tests → Build → Integration Tests → E2E Tests → Deploy (Staging) → Smoke Tests → Deploy (Prod) → Health Check
```

### 1.2 GitHub Actions Configuration
```yaml
name: CI/CD
on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: pnpm/action-setup@v2
      - run: pnpm install --frozen-lockfile
      - run: pnpm lint
      - run: pnpm typecheck
      - run: pnpm test:unit
      - run: pnpm build
      - uses: actions/upload-artifact@v4
        with:
          name: build
          path: dist/

  deploy-staging:
    needs: test
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    environment: staging
    steps:
      - uses: actions/download-artifact@v4
        with: { name: build, path: dist/ }
      - run: pnpm deploy:staging

  deploy-production:
    needs: deploy-staging
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    environment: production
    steps:
      - uses: actions/download-artifact@v4
        with: { name: build, path: dist/ }
      - run: pnpm deploy:prod
```

### 1.3 Build Caching Strategies
- **Dependencies:** Cache `node_modules` / `.pnpm-store` between runs
- **Build output:** Cache `dist/` / `.next/` / `build/` based on lockfile hash
- **Docker layers:** Cache Docker build layers with `actions/cache` or BuildKit
- **Turborepo remote cache:** Share build cache across CI runners and local dev
- **Key by lockfile:** `key: ${{ hashFiles('pnpm-lock.yaml') }}`

### 1.4 Monorepo vs Polyrepo CI
- **Monorepo:** Use `affected` commands to only build/test changed packages
- **Polyrepo:** Each repo has its own pipeline — simpler but duplicated config
- **Shared CI config:** Extract reusable workflows in `.github/workflows/`

### 1.5 Deployment Previews
- **Vercel:** Automatic preview deployments for every PR
- **Netlify:** Deploy previews with unique URLs for every PR
- **Custom:** Spin up ephemeral environments per PR using Docker/K8s
- **Benefits:** Reviewers can test changes before merge, catch visual/functional issues

### 1.6 Automated Rollback on Failure
- **Health check after deploy:** If health check fails, automatically roll back
- **Error rate spike:** If error rate exceeds threshold post-deploy, roll back
- **Manual rollback:** One-command rollback (`vercel --rollback`, `kubectl rollout undo`)
- **Database rollback:** Only for code — database migrations must be forward-only (expand-contract)

### 1.7 Branch Protection Rules
- Require PR review before merge
- Require CI checks to pass
- Require branches to be up to date before merge
- Require linear history (squash or rebase merges)
- Restrict who can push to main
- Require signed commits for security-sensitive repos

### 1.8 Environment-Based Deployment Gating
- **Staging:** Auto-deploy on merge to main
- **Production:** Manual approval required (GitHub Environments)
- **Canary:** Deploy to subset of instances, monitor, then full rollout
- **Feature flags:** Deploy code dark, enable gradually

---

## Part 2: Infrastructure as Code

### 2.1 Terraform/Pulumi Resource Management
```hcl
# Terraform example
resource "aws_s3_bucket" "static_assets" {
  bucket = "my-app-static-assets"
}

resource "aws_cloudfront_distribution" "cdn" {
  origin {
    domain_name = aws_s3_bucket.static_assets.bucket_regional_domain_name
    origin_id   = "S3-static-assets"
  }
  enabled = true
  # ...
}
```

### 2.2 Defining Cloud Resources Declaratively
- **Compute:** EC2, ECS, Lambda, Cloud Run
- **Storage:** S3, R2, Cloud Storage
- **Network:** VPC, subnets, security groups, load balancers
- **Database:** RDS, DynamoDB, Cloud SQL
- **DNS:** Route53, Cloudflare DNS
- **CDN:** CloudFront, Cloudflare

### 2.3 State Management
- **Remote state:** Store in S3 + DynamoDB lock (Terraform) or Pulumi Cloud
- **State locking:** Prevent concurrent runs from corrupting state
- **State separation:** Separate state per environment (dev, staging, prod)
- **Never commit state files** to git — they contain secrets and are large

### 2.4 Drift Detection
- Run `terraform plan` regularly to detect manual changes
- Alert on drift — manual changes bypass review and versioning
- Use tools like `driftctl` for continuous drift detection

### 2.5 Modular Infrastructure
- Create reusable modules for common patterns (VPC, database, CDN)
- Version modules independently
- Share modules across environments with different variables
- Use `terraform registry` or internal module registry

### 2.6 Environment-Specific Configurations
```hcl
# environments/staging.tfvars
instance_count = 2
instance_type  = "t3.small"
db_instance    = "db.t3.medium"

# environments/production.tfvars
instance_count = 5
instance_type  = "t3.large"
db_instance    = "db.r6g.large"
```

### 2.7 Secrets in IaC (SOPS, Vault)
- **SOPS:** Encrypt secrets in git with KMS/age, decrypt at apply time
- **HashiCorp Vault:** Dynamic secrets, automatic rotation
- **AWS Secrets Manager / Parameter Store:** Cloud-native secret storage
- **Never hardcode secrets** in Terraform/Pulumi files

---

## Part 3: Containerization

### 3.1 Dockerfile Best Practices
```dockerfile
# Multi-stage build
FROM node:22-slim AS builder
WORKDIR /app
COPY pnpm-lock.yaml package.json ./
RUN corepack enable && pnpm install --frozen-lockfile
COPY . .
RUN pnpm build

FROM node:22-slim AS runner
WORKDIR /app
ENV NODE_ENV=production
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package.json ./
EXPOSE 3000
CMD ["node", "dist/index.js"]
```

- **Multi-stage builds:** Separate build and runtime — smaller final image
- **Layer caching:** Copy lockfile first, install deps, then copy source — cache deps layer
- **Minimal base images:** Use `slim` or `alpine` variants — smaller attack surface
- **Non-root user:** Run as non-root for security
- **.dockerignore:** Exclude `node_modules`, `.git`, `dist`, test files

### 3.2 Docker Compose for Local Dev
```yaml
version: '3.9'
services:
  app:
    build: .
    ports: ['3000:3000']
    env_file: .env
    depends_on: [db, redis]
  db:
    image: postgres:17
    environment:
      POSTGRES_DB: app
      POSTGRES_PASSWORD: dev
    ports: ['5432:5432']
    volumes: ['pgdata:/var/lib/postgresql/data']
  redis:
    image: redis:7-alpine
    ports: ['6379:6379']
volumes:
  pgdata:
```

### 3.3 Kubernetes Deployment Patterns
- **Deployments:** Stateless apps with replicas, rolling updates
- **Services:** ClusterIP (internal), LoadBalancer (external), Ingress (HTTP routing)
- **ConfigMaps:** Non-sensitive configuration
- **Secrets:** Sensitive data (base64-encoded, mounted as env or files)
- **Probes:** `livenessProbe` (is it alive?), `readinessProbe` (is it ready?)
- **Resources:** Set CPU/memory requests and limits

### 3.4 Health Checks in Containers
```yaml
livenessProbe:
  httpGet:
    path: /health
    port: 3000
  initialDelaySeconds: 10
  periodSeconds: 30
readinessProbe:
  httpGet:
    path: /ready
    port: 3000
  initialDelaySeconds: 5
  periodSeconds: 10
```

### 3.5 Image Scanning for Vulnerabilities
- **Trivy:** Scan images for known CVEs
- **Snyk Container:** Vulnerability scanning in CI
- **GitHub Dependabot:** Automatic dependency updates
- **Scan in CI:** Block deploy if critical vulnerabilities found
- **Regular base image updates:** Keep base images up to date

---

## Part 4: Cloud Provider Selection

### 4.1 Provider Comparison

| Provider | Best For | Pros | Cons |
|---|---|---|---|
| **Vercel** | Frontend + serverless | Zero-config, preview deploys, edge | Limited backend control |
| **Netlify** | Frontend + serverless | Easy, generous free tier | Limited backend |
| **AWS** | Full control, any scale | Most services, mature | Complex, steep learning curve |
| **Cloudflare** | Edge-first, global | Fast edge, Workers, R2, D1 | Newer ecosystem |
| **GCP** | Data-heavy, ML | BigQuery, Cloud Run, Firebase | Less community content |
| **Azure** | Enterprise, .NET | Enterprise integration, AKS | Less popular for web startups |
| **Self-hosted** | Complete control | No vendor lock-in, cost control | You operate everything |

### 4.2 Cost Optimization
- **Right-size instances:** Monitor usage, downgrade if over-provisioned
- **Reserved instances:** Commit to 1-3 year terms for significant discounts
- **Spot instances:** For non-critical, interruptible workloads (up to 90% cheaper)
- **Auto-scaling:** Scale down during low traffic
- **CDN caching:** Reduce origin requests
- **Serverless:** Pay per request, not per instance — good for spiky traffic
- **Monitor bills:** Set billing alerts, review monthly

### 4.3 Vendor Lock-in Assessment
- **Low lock-in:** Docker containers, standard databases (PostgreSQL), standard APIs
- **Medium lock-in:** Vercel/Netlify functions (serverless but portable)
- **High lock-in:** Proprietary services (DynamoDB, CloudFront only features, AppSync)
- **Mitigation:** Use open standards, abstract provider-specific code, keep exit strategy

---

## Part 5: Environment Management

### 5.1 Dev/Staging/Prod Parity
- **Same database engine:** Don't use SQLite in dev and PostgreSQL in prod
- **Same dependencies:** Same versions, same config structure
- **Same environment variables:** Same keys, different values
- **Same infrastructure:** Staging should mirror prod as closely as possible
- **Differences:** Scale (fewer instances), data (anonymized), third-party (sandbox/test keys)

### 5.2 Environment Variables Management
- **`.env` files:** For local dev only — never commit to git
- **`.env.example`:** Template with all required vars, committed to git
- **CI secrets:** GitHub Actions secrets, AWS Secrets Manager
- **Runtime config:** Fetch config from a config service, not hardcoded
- **Validation:** Validate env vars at startup with Zod schema

### 5.3 Feature Flags in Different Environments
- **Dev:** All features on
- **Staging:** Mirror production flags, test new flags
- **Production:** Gradual rollout, kill switch for emergencies
- **Tools:** LaunchDarkly, GrowthBook, PostHog, custom

### 5.4 Database Seeding for Staging
- Seed with realistic, anonymized data
- Include all entity types and relationships
- Include edge cases (users with no orders, empty categories)
- Reset and re-seed regularly
- Never use production data with real PII

### 5.5 Secrets Per Environment
- Different secrets for dev, staging, production
- Rotate production secrets regularly
- Use a secrets manager — not `.env` files for production
- Audit who has access to production secrets
- Never log secrets — redact in logs

### 5.6 Promoting Builds Between Environments
- Build once, deploy everywhere — same artifact through environments
- Promote by updating the image tag / version reference
- Don't rebuild for each environment — ensures the tested artifact is deployed
- Tag builds with git SHA for traceability

---

## Part 6: Deployment Strategies

### 6.1 Blue-Green Deployments
- **Blue:** Current production environment
- **Green:** New version, deployed and tested
- **Switch:** Route traffic from blue to green (DNS, load balancer)
- **Rollback:** Switch back to blue instantly
- **Requires:** 2x infrastructure capacity

### 6.2 Canary Releases
- Deploy new version to a small subset of instances (5%)
- Monitor error rates, latency, business metrics
- If healthy, gradually increase to 20% → 50% → 100%
- If unhealthy, roll back immediately
- **Tools:** Argo Rollouts, Flagger, Kubernetes canary

### 6.3 Rolling Updates
- Replace instances one at a time with the new version
- Kubernetes default strategy — configurable max surge and max unavailable
- No downtime if health checks are properly configured
- Slower than blue-green but no extra capacity needed

### 6.4 Feature-Flag-Driven Releases
- Deploy code with feature flag off
- Enable for internal team first
- Enable for percentage of users
- Monitor, then enable for all
- Kill switch: disable flag to instantly revert

### 6.5 Zero-Downtime Deployments
- Health checks on load balancer — don't route to unhealthy instances
- Graceful shutdown — finish in-flight requests before stopping
- Connection draining — let existing connections finish
- Database migrations: expand-contract (see Part 2 of backend skill)

### 6.6 Database Migrations During Deployment
1. **Expand:** Add new column/table (backward compatible)
2. **Deploy:** New code that writes to both old and new
3. **Backfill:** Migrate existing data
4. **Switch:** New code reads from new
5. **Contract:** Remove old column/table

### 6.7 Deploy Previews for Review
- Every PR gets a unique, ephemeral URL
- Reviewers can test before merge
- Auto-destroyed when PR is merged or closed
- **Vercel/Netlify:** Built-in
- **Custom:** Kubernetes with PR namespaces

### 6.8 Automatic vs Manual Triggers
- **Staging:** Automatic on merge to main
- **Production:** Manual approval (GitHub Environments, Slack approval)
- **Hotfix:** Fast-track pipeline with reduced gates (but still tested)
- **Scheduled:** Off-hours deployments for high-risk changes

---

## Part 7: Monitoring & Alerting

### 7.1 Application Performance Monitoring
- **Datadog:** Full-stack APM, metrics, logs, traces — enterprise-grade
- **New Relic:** APM with good UX, distributed tracing
- **Sentry:** Error tracking, performance monitoring, release tracking
- **OpenTelemetry:** Vendor-neutral instrumentation — collect once, export anywhere
- **Key metrics:** p50/p95/p99 latency, error rate, throughput, saturation

### 7.2 Uptime Monitoring
- **External:** UptimeRobot, Pingdom, Better Stack — ping from multiple regions
- **Internal:** Health check endpoints (`/health`, `/ready`)
- **Synthetic:** Playwright scripts running on a schedule to test critical flows
- **Alert on:** HTTP 5xx, timeout, content match failure
- **Frequency:** Every 1-5 minutes for critical endpoints

### 7.3 Custom Metrics and Dashboards
- **Business metrics:** Signups, conversions, revenue, active users
- **Application metrics:** Request rate, error rate, response time
- **Infrastructure metrics:** CPU, memory, disk, network
- **Dashboards:** Grafana, Datadog, Looker Studio
- **Red/Amber/Green:** Color-code by threshold for quick scanning

### 7.4 Log Aggregation
- **Loki:** Lightweight, integrates with Grafana, good for Kubernetes
- **ELK (Elasticsearch, Logstash, Kibana):** Powerful search, resource-heavy
- **CloudWatch:** AWS-native, good if already on AWS
- **Structured logging:** JSON logs with consistent fields (timestamp, level, request_id, user_id)
- **Log levels:** ERROR (act), WARN (investigate), INFO (context), DEBUG (dev only)

### 7.5 Alerting Rules and Escalation Policies
- **Alert on symptoms, not causes:** "Error rate > 1%" not "CPU > 80%"
- **Alert on user impact:** "Checkout failing" not "database connection count high"
- **Escalation:** Page on-call → wait 5 min → escalate to secondary → wait 5 min → escalate to manager
- **Runbooks:** Link alert to runbook with diagnosis and mitigation steps
- **Alert fatigue:** Tune alerts — better to have fewer high-signal alerts

### 7.6 On-Call Rotation Design
- **Rotation:** 1 week primary, 1 week secondary
- **Handoff:** Written handoff note at rotation change
- **Follow-the-sun:** Distribute on-call across time zones for 24/7 coverage
- **Compensation:** Pay or time-off for on-call duty
- **Post-incident:** Blameless postmortems, action items tracked

### 7.7 Error Budget and SLO/SLI Definition
- **SLI (Service Level Indicator):** "99.9% of requests succeed" (error rate < 0.1%)
- **SLO (Service Level Objective):** "99.9% availability over 30 days"
- **Error budget:** 0.1% of 30 days = 43 minutes of allowed downtime
- **Budget consumed:** Freeze feature deploys, focus on reliability
- **Budget healthy:** Allow feature development to proceed

---

## Part 8: Domain & DNS Management

### 8.1 Domain Registration
- Use a reputable registrar (Namecheap, Cloudflare, Google Domains)
- Enable auto-renewal
- Enable registrar lock
- Use privacy protection
- Keep registration records organized

### 8.2 DNS Record Management

| Type | Purpose | Example |
|---|---|---|
| **A** | Domain → IPv4 | `example.com → 1.2.3.4` |
| **AAAA** | Domain → IPv6 | `example.com → 2001:db8::1` |
| **CNAME** | Domain → Domain | `www.example.com → example.com` |
| **MX** | Mail server | `example.com → mail.example.com` |
| **TXT** | Verification, SPF, DKIM | `v=spf1 include:_spf.google.com ~all` |
| **NS** | Nameservers | Delegated to hosting provider |

### 8.3 SSL/TLS Certificate Provisioning
- **Let's Encrypt:** Free, auto-renewing, use `certbot` or ACME client
- **AWS ACM:** Free for AWS resources (CloudFront, ALB)
- **Cloudflare:** Free SSL/TLS with edge certificates
- **Auto-renewal:** Certificates expire — automate renewal
- **HSTS:** `Strict-Transport-Security` header to enforce HTTPS

### 8.4 CDN Configuration
- **Origin:** Your server or storage bucket
- **Cache behavior:** Static assets (long TTL), HTML (short TTL or revalidate)
- **Edge locations:** Global coverage for your audience
- **Compression:** Enable Brotli/Gzip at edge
- **Image optimization:** On-the-fly resize and format conversion (Cloudflare Images, Cloudinary)

### 8.5 Custom Domain Setup on Hosting Providers
- **Vercel:** Add domain in dashboard, configure DNS (CNAME or A record)
- **Netlify:** Add domain, configure DNS
- **Cloudflare Pages:** Add domain, configure DNS
- **Custom:** Configure DNS A/CNAME to load balancer, set up SSL

### 8.6 Email Forwarding
- **MX records:** Point to email service (Google Workspace, ImprovMX, Cloudflare Email Routing)
- **SPF:** `v=spf1 include:_spf.google.com ~all`
- **DKIM:** Add TXT record from email provider
- **DMARC:** `v=DMARC1; p=quarantine; rua=mailto:dmarc@example.com`

### 8.7 Subdomain Strategy
- `www` — main site (or apex)
- `api` — API server
- `app` — application dashboard
- `staging` — staging environment
- `admin` — admin panel
- `docs` — documentation
- `assets` / `cdn` — static assets
- `status` — status page

---

## Part 9: Backup & Disaster Recovery

### 9.1 Database Backup Strategies
- **Automated snapshots:** Daily full snapshot, retained 7-30 days
- **Point-in-time recovery (PITR):** Continuous WAL archiving, recover to any second
- **Logical backups:** `pg_dump` weekly, stored off-site (S3, R2)
- **Replication:** Standby replica in another region for disaster recovery

### 9.2 Backup Verification and Restore Testing
- **Restore test:** Monthly restore from backup to verify it works
- **Data integrity:** Check restored data for completeness and correctness
- **Time to restore:** Measure and document — compare against RTO
- **Automate:** Script the restore process, run it in staging

### 9.3 Disaster Recovery Runbooks
- **Scenario:** Database failure, region outage, data corruption
- **Steps:** Detect → Assess → Communicate → Restore → Verify → Postmortem
- **Contacts:** On-call engineer, DBA, infrastructure lead, communication lead
- **Tools:** Runbook stored in wiki/Confluence, linked from alerting

### 9.4 RTO/RPO Definition
- **RTO (Recovery Time Objective):** How fast must we recover? (e.g., 1 hour)
- **RPO (Recovery Point Objective):** How much data can we lose? (e.g., 15 minutes)
- **Design:** Backup frequency and replication strategy must meet RPO
- **Test:** Recovery process must complete within RTO

### 9.5 Multi-Region Failover
- **Active-passive:** Primary region handles traffic, secondary is standby
- **Active-active:** Both regions handle traffic, replicate data bidirectionally
- **DNS failover:** Route53 health checks + failover routing, Cloudflare load balancing
- **Data replication:** Async replication (slight lag) or sync (higher latency)
- **Cost:** Multi-region is expensive — justify with availability requirements

### 9.6 Data Retention Policies
- **User data:** Retain per legal requirements (GDPR: as long as needed, then delete)
- **Logs:** 30-90 days hot, 1 year cold storage
- **Backups:** 7-30 days hot, 90 days cold
- **Audit trails:** 1-7 years depending on compliance requirements
- **Automate:** Lifecycle policies on S3/R2 to auto-transition or delete

---

## Execution Instructions for Cascade

When this skill is activated for deployment:

1. **Read the project structure** — framework, build output, dependencies
2. **Choose the right hosting provider** — Vercel/Netlify for frontend, AWS/Cloudflare for full control
3. **Set up CI/CD pipeline** — lint → test → build → deploy (staging) → deploy (prod)
4. **Configure environment management** — env vars, secrets, feature flags per environment
5. **Set up infrastructure as code** — Terraform/Pulumi for any cloud resources
6. **Configure deployment strategy** — rolling updates (default), canary for risky changes
7. **Set up monitoring** — APM, error tracking, uptime monitoring, log aggregation
8. **Configure alerting** — alert on user impact, with runbooks and escalation
9. **Set up domain and DNS** — domain, SSL, CDN, email forwarding
10. **Configure backups** — automated database snapshots, PITR, restore testing
11. **Document the deployment process** — runbooks, architecture diagrams, contacts
12. **Never deploy manually** — everything through the pipeline
