# Rule: Search & Filtering Architecture for All Projects

**ALWAYS** apply the Search & Filtering Architecture skill and workflow when implementing search and filtering functionality. Search should be instant, forgiving, and relevant — and filters should always be in the URL.

## Skill
`~/.codeium/windsurf/skills/search-filtering-architecture.md`

## Workflow
`~/.codeium/windsurf/windsurf/workflows/search.md` — invoke with `/search`

## Sub-Agent
`~/.codeium/windsurf/windsurf/sub-agents/search-architect.md` (parent: Feature Engineer)

## How to follow this rule:
1. When implementing search, invoke the `/search` workflow
2. Follow the workflow steps in order: Assess → Choose Engine → Index → API → Instant Search → URL Filters → Facets → Pagination → Features → Performance → Accessibility → Test
3. Always choose the right search engine for data volume — PostgreSQL FTS (< 1M), Meilisearch/Typesense (1M-10M), Elasticsearch (> 10M)
4. Always store filter state in URL search params — shareable, bookmarkable, survives refresh
5. Always implement debounced instant search with AbortController for request cancellation
6. Always implement faceted filtering with counts for filter UI
7. Always use cursor-based pagination (not OFFSET) for large datasets
8. Always ensure search accessibility — ARIA combobox role, keyboard navigation, live regions

## When this rule applies:
- Implementing search functionality
- Setting up Meilisearch, Typesense, or PostgreSQL FTS
- Building faceted search or filter UI
- Optimizing search performance
- User asks about search or filtering

## When this rule does NOT apply:
- Projects with no search functionality
- User explicitly says to skip search architecture
