---
name: search
description: Comprehensive search & filtering architecture workflow — engine selection, indexing, instant search, URL filters, facets, pagination, performance, and accessibility
---

# Search & Filtering Architecture Workflow

This workflow applies the **Search & Filtering Architecture Skill** (`~/.codeium/windsurf/skills/search-filtering-architecture.md`) to implement instant, relevant, accessible search and filtering.

## When to Run
- When implementing search functionality
- When the user says `/search` or asks about filtering
- When setting up Meilisearch, Typesense, or PostgreSQL FTS
- When building faceted search or filter UI
- When optimizing search performance

---

## Step 1: Assess Search Needs

1. Read the project context — data volume, search requirements, filtering needs
2. Determine data size: < 10K, 10K-1M, 1M-10M, > 10M records
3. Identify searchable fields: name, description, category, tags, content
4. Identify filterable fields: category, price range, date, brand, color, size
5. Determine sort options: relevance, price, date, popularity, rating
6. Determine if typo tolerance is needed
7. Determine if autocomplete/suggestions are needed
8. Determine if faceted filtering with counts is needed

## Step 2: Choose Search Engine

1. **PostgreSQL FTS:** For < 1M records — built-in, no extra infrastructure, GIN index
2. **Meilisearch:** For 100K-10M records — instant search, typo-tolerance, easy setup
3. **Typesense:** For 100K-10M records — fast, typo-tolerance, geo search, multi-tenant
4. **Elasticsearch/OpenSearch:** For > 10M records — complex search, aggregations, massive scale
5. **pgvector:** For semantic/AI search — vector similarity, embeddings, RAG
6. Install and configure chosen search engine
7. Set up indexing pipeline — sync data from database to search engine

## Step 3: Implement Search Index

1. **PostgreSQL FTS:**
   - Create generated tsvector column with weighted fields
   - Create GIN index on tsvector column
   - Use `plainto_tsquery` for user input
   - Use `ts_rank` for relevance scoring
2. **Meilisearch/Typesense:**
   - Create index/collection with searchable and filterable attributes
   - Configure ranking rules
   - Set up synonyms
   - Index documents from database
   - Set up webhook/sync to keep index updated

## Step 4: Implement Search API

1. Create API endpoint: `GET /api/search`
2. Parse query parameters: q, category, minPrice, maxPrice, sort, page, limit
3. Build search query with parameterized inputs (prevent SQL injection)
4. Apply text search with relevance scoring
5. Apply filters (category, price range, etc.)
6. Apply sorting (relevance, price, date, popularity)
7. Apply pagination (cursor-based recommended)
8. Return results with facet counts for filter UI
9. Return total count and pagination metadata
10. Cache results with Redis or TanStack Query

## Step 5: Implement Instant Search UI

1. Create search input with `type="search"` and `role="combobox"`
2. Implement debounce (300ms default) — don't search on every keystroke
3. Use AbortController to cancel in-flight requests when query changes
4. Show loading state (spinner or skeleton) while searching
5. Show results in dropdown or results page
6. Highlight matched text in results
7. Show "No results found" with helpful suggestions for empty results
8. Implement keyboard navigation: ArrowUp/Down, Enter, Escape
9. Show recent searches when input is focused and empty
10. Track search analytics (queries, zero-results, popular searches)

## Step 6: Implement URL-Based Filters

1. Use `useSearchParams` or `nuqs` for filter state management
2. Map each filter to URL search params: category, minPrice, maxPrice, sort, page
3. Update URL when filters change (without full page reload)
4. Read filters from URL on page load — survive refresh
5. Make filter state shareable and bookmarkable
6. Provide "Clear all filters" button that resets URL params
7. Sync filter state with search API requests
8. Handle browser back/forward navigation

## Step 7: Implement Faceted Filtering

1. API returns facet counts alongside results
2. Render filter sidebar with checkboxes for each facet value
3. Show count next to each filter option (e.g., "Electronics (42)")
4. Support multi-select within a facet (OR logic)
5. Support cross-facet AND logic
6. Update facet counts when other filters change
7. Show active filters with ability to remove individually
8. Collapsible filter groups for mobile
9. Apply filters to URL params (Step 6)

## Step 8: Implement Pagination

1. **Cursor-based (recommended):** Use `WHERE id > last_id` — constant time, no counting
2. **Page-based:** `LIMIT/OFFSET` — simple but slow with large offsets
3. **Infinite scroll:** Use Intersection Observer to auto-load next page
4. Show "Load more" button as alternative to infinite scroll
5. Show total results count and current page
6. Update page number in URL params
7. Reset to page 1 when filters or query change
8. Use TanStack Query `useInfiniteQuery` for infinite scroll

## Step 9: Implement Search Features

1. **Typo tolerance:** Meilisearch/Typesense (built-in) or pg_trgm (PostgreSQL)
2. **Autocomplete:** Suggestions endpoint for type-ahead
3. **Synonyms:** Configure in search engine (laptop = notebook)
4. **Recent searches:** Store in localStorage, show when input focused
5. **Popular searches:** Show trending searches when input focused
6. **Did you mean:** Suggest corrections for zero-result searches
7. **Search analytics:** Track queries, results count, clicks, zero-results

## Step 10: Optimize Performance

1. **Database:** GIN index on tsvector, B-tree on filter columns, composite indexes
2. **Caching:** Redis for search results (1-5 min TTL), TanStack Query for client cache
3. **Client:** Debounce (300ms), AbortController, keepPreviousData, virtualization
4. **Connection pooling:** For high-traffic search APIs
5. **Materialized views:** For expensive facet aggregations
6. **CDN:** Cache search API responses at edge for common queries
7. **Lazy loading:** Only load search engine results when needed

## Step 11: Ensure Accessibility

1. Wrap search in `role="search"` container
2. Label search input with `aria-label` or associated `<label>`
3. Use `role="combobox"`, `aria-expanded`, `aria-controls`, `aria-activedescendant`
4. Results list: `role="listbox"`, items: `role="option"`, `aria-selected`
5. Filter groups: `<fieldset>` with `<legend>`
6. All filter inputs have associated `<label>` elements
7. Announce results count via `aria-live="polite"`
8. All filters and results keyboard accessible
9. Clear button is keyboard accessible and labeled
10. Test with screen reader (NVDA, VoiceOver)

## Step 12: Test & Document

1. Test search with various queries — short, long, typos, special characters
2. Test filters — single, multiple, combined, clear all
3. Test pagination — page navigation, infinite scroll, URL sync
4. Test keyboard navigation — arrow keys, enter, escape, tab
5. Test performance — response time under load
6. Test empty results — "No results" message, suggestions
7. Test accessibility — screen reader, keyboard only
8. Test URL sharing — copy URL with filters, open in new tab
9. Document search architecture, filter options, API endpoints
10. Document indexing strategy and sync pipeline
