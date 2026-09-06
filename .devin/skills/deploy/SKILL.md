---
name: deploy
description: Comprehensive deployment & DevOps workflow — set up CI/CD, infrastructure, monitoring, and deployment strategies for reliable shipping
---

# Deployment & DevOps Workflow

This workflow applies the **Deployment & DevOps Skill** (`~/.codeium/windsurf/skills/deployment-devops.md`) to set up deployment infrastructure and CI/CD pipelines.

## When to Run
- When setting up deployment for a new project
- When the user says `/deploy` or asks about deployment
- When configuring CI/CD pipelines
- When setting up monitoring and alerting

---

## Step 1: Assess Project Needs

1. Read the project structure — framework, build output, dependencies
2. Determine hosting needs: frontend only, full-stack, database, real-time
3. Identify compliance requirements: GDPR, HIPAA, SOC2
4. Estimate traffic and scale requirements
5. Check budget constraints

## Step 2: Choose Hosting Provider

1. Evaluate providers based on project needs:
   - Vercel/Netlify: Frontend + serverless, zero-config
   - AWS/GCP/Azure: Full control, any scale
   - Cloudflare: Edge-first, global
   - Self-hosted: Complete control
2. Consider vendor lock-in and cost
3. Set up account and CLI tools

## Step 3: Set Up CI/CD Pipeline

1. Configure pipeline stages: lint → typecheck → unit tests → build → integration tests → e2e
2. Set up build caching (dependencies, build output, Docker layers)
3. Configure deployment previews for PRs
4. Set up automated rollback on health check failure
5. Configure branch protection rules
6. Set up environment-based deployment gating (staging auto, prod manual)

## Step 4: Configure Environment Management

1. Set up environments: dev, staging, production
2. Configure environment variables per environment
3. Set up secrets management (AWS Secrets Manager, SOPS, Vault)
4. Create `.env.example` template
5. Validate env vars at startup with Zod schema
6. Configure feature flags per environment

## Step 5: Set Up Infrastructure as Code

1. Choose IaC tool: Terraform or Pulumi
2. Define cloud resources declaratively (compute, storage, network, database, DNS, CDN)
3. Set up remote state management with locking
4. Create modular, reusable infrastructure components
5. Configure environment-specific variables
6. Set up drift detection

## Step 6: Configure Deployment Strategy

1. Choose deployment strategy:
   - Rolling updates (default, no extra capacity)
   - Blue-green (instant rollback, 2x capacity)
   - Canary (gradual rollout, monitor metrics)
   - Feature-flag-driven (deploy dark, enable gradually)
2. Set up health checks (liveness, readiness)
3. Configure graceful shutdown
4. Set up zero-downtime deployments with expand-contract migrations

## Step 7: Set Up Monitoring & Alerting

1. Configure APM (Datadog, Sentry, New Relic, OpenTelemetry)
2. Set up uptime monitoring (external pings from multiple regions)
3. Create custom metrics and dashboards (business + technical)
4. Set up log aggregation (Loki, ELK, CloudWatch)
5. Configure alerting rules (alert on symptoms/user impact, not causes)
6. Set up escalation policies and on-call rotation
7. Define SLOs and error budgets

## Step 8: Configure Domain & DNS

1. Register domain or use existing
2. Configure DNS records (A, CNAME, MX, TXT)
3. Set up SSL/TLS certificates (Let's Encrypt, ACM, Cloudflare)
4. Configure CDN with cache rules and compression
5. Set up custom domain on hosting provider
6. Configure email forwarding (MX, SPF, DKIM, DMARC)
7. Set up subdomain strategy (www, api, app, staging, docs)

## Step 9: Set Up Backups & Disaster Recovery

1. Configure automated database backups (daily snapshots + PITR)
2. Set up logical backups (pg_dump) stored off-site
3. Configure replication for disaster recovery
4. Define RTO/RPO
5. Create disaster recovery runbooks
6. Schedule monthly restore testing
7. Configure data retention policies

## Step 10: Containerization (if needed)

1. Write optimized Dockerfile (multi-stage, minimal base, non-root)
2. Set up Docker Compose for local development
3. Configure Kubernetes deployments (if using K8s)
4. Set up health checks and resource limits
5. Configure image scanning for vulnerabilities
6. Set up container registry

## Step 11: Document & Verify

1. Document deployment process and architecture
2. Create runbooks for common operations
3. Test the full pipeline: push → CI → staging → prod → health check
4. Test rollback procedure
5. Verify monitoring and alerting work (trigger a test alert)
6. Review cost optimization opportunities
