# Rule: Deployment & DevOps for All Projects

**ALWAYS** apply the Deployment & DevOps skill and workflow when setting up deployment infrastructure and CI/CD pipelines. Everything automated, everything versioned, everything observable.

## Skill
`~/.codeium/windsurf/skills/deployment-devops.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/deploy.md` — invoke with `/deploy`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/devops-engineer.md` (parent: Infrastructure Engineer)

## How to follow this rule:
1. When setting up deployment or DevOps, invoke the `/deploy` workflow
2. Follow the workflow steps in order: Assess → Choose Provider → CI/CD → Environments → IaC → Deployment Strategy → Monitoring → Domain/DNS → Backups → Containerization → Document
3. Never deploy manually — everything through the CI/CD pipeline
4. Always use infrastructure as code — Terraform/Pulumi, not click-ops
5. Always set up monitoring and alerting — APM, error tracking, uptime, logs
6. Always configure automated database backups and restore testing
7. Always use environment-based deployment gating — staging auto, production manual
8. Always set up health checks and automated rollback

## When this rule applies:
- Setting up deployment for a new project
- Configuring CI/CD pipelines
- Setting up monitoring and alerting
- Configuring domains, DNS, or SSL
- Setting up infrastructure (cloud resources, containers, Kubernetes)

## When this rule does NOT apply:
- Local development environment setup
- User explicitly says to skip DevOps setup
