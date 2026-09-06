---
name: Search & Filtering Architecture Skill
description: Comprehensive methodology for implementing search and filtering in web applications — 2025-2026 practices with PostgreSQL FTS, Meilisearch, Typesense, faceted search, debounced search, and URL-based filters
version: 1.0.0
tags: [search, filtering, faceted-search, postgresql-fts, meilisearch, typesense, elasticsearch, debouncing, instant-search, url-filters]
---

# Search & Filtering Architecture Skill

## Purpose
This skill provides a comprehensive methodology for implementing search and filtering functionality across any kind of web project. It reflects **modern 2025-2026 practices** — PostgreSQL Full-Text Search for small datasets, Meilisearch/Typesense for instant search at scale, faceted filtering with aggregated counts, debounced search with abort controllers, and URL-based filter state.

## Core Philosophy

**Search should be instant, forgiving, and relevant.** Users expect search results to appear as they type, to find what they're looking for despite typos, and to see the most relevant results first. If search is slow, strict, or returns irrelevant results, users will abandon it.

**The #1 rule:** Filters should be in the URL. Every filter state should be reflected in the URL so it's shareable, bookmarkable, and survives refresh. Never store filter state only in component state — always sync with URL search params.

---

## Part 1: Search Engine Selection

### 1.1 Decision Matrix

| Scale | Solution | When to Use |
|---|---|---|
| < 10K records | PostgreSQL FTS | Built-in, no extra infrastructure |
| 10K - 1M records | PostgreSQL FTS + GIN index | With proper indexing, handles moderate scale |
| 100K - 10M records | Meilisearch / Typesense | Instant search, typo-tolerance, faceted |
| > 10M records | Elasticsearch / OpenSearch | Complex search, aggregations, massive scale |
| AI/semantic search | pgvector / Pinecone | Vector similarity, semantic search, RAG |

### 1.2 PostgreSQL Full-Text Search
```sql
-- Create tsvector column (generated)
ALTER TABLE products ADD COLUMN search_vector tsvector
GENERATED ALWAYS AS (
  setweight(to_tsvector('english', coalesce(name, '')), 'A') ||
  setweight(to_tsvector('english', coalesce(description, '')), 'B') ||
  setweight(to_tsvector('english', coalesce(category, '')), 'C')
) STORED;

-- GIN index
CREATE INDEX idx_products_search ON products USING gin(search_vector);

-- Search query
SELECT *, ts_rank(search_vector, query) as rank
FROM products, to_tsquery('english', 'laptop & bag') query
WHERE search_vector @@ query
ORDER BY rank DESC
LIMIT 20;

-- With plainto_tsquery (user input)
SELECT *, ts_rank(search_vector, query) as rank
FROM products, plainto_tsquery('english', 'laptop bag') query
WHERE search_vector @@ query
ORDER BY rank DESC
LIMIT 20;

-- Highlighting
SELECT ts_headline('english', name, query, 'MaxWords=10') as headline
FROM products, to_tsquery('english', 'laptop') query
WHERE search_vector @@ query;
```

### 1.3 Meilisearch
```typescript
// Setup
import { MeiliSearch } from 'meilisearch';

const client = new MeiliSearch({
  host: 'http://localhost:7700',
  apiKey: 'masterKey',
});

// Index documents
await client.index('products').addDocuments(products);

// Configure searchable attributes and ranking
await client.index('products').updateSearchableAttributes(['name', 'description', 'category']);
await client.index('products').updateRankingRules([
  'words', 'typo', 'proximity', 'attribute', 'sort', 'exactness'
]);

// Search
const results = await client.index('products').search('laptop bag', {
  limit: 20,
  attributesToRetrieve: ['id', 'name', 'price', 'image'],
  attributesToHighlight: ['name'],
  filter: 'price < 1000 AND category = "electronics"',
  sort: ['price:asc'],
});

// Faceted search
const results = await client.index('products').search('', {
  facets: ['category', 'brand', 'color'],
});
// Returns facetDistribution: { category: { electronics: 42, clothing: 18 } }
```

### 1.4 Typesense
```typescript
import TypesenseClient from 'typesense';

const client = new TypesenseClient({
  nodes: [{ host: 'localhost', port: '8108', protocol: 'http' }],
  apiKey: 'xyz',
});

// Create collection
await client.collections().create({
  name: 'products',
  fields: [
    { name: 'name', type: 'string' },
    { name: 'description', type: 'string' },
    { name: 'price', type: 'float', facet: true },
    { name: 'category', type: 'string', facet: true },
    { name: 'brand', type: 'string', facet: true },
  ],
  default_sorting_field: 'popularity',
});

// Search with facets
const results = await client.collections('products').documents().search({
  q: 'laptop',
  query_by: 'name,description',
  facet_by: 'category,brand,price',
  filter_by: 'price:<1000 && category:=electronics',
  sort_by: 'price:asc',
  per_page: 20,
});
```

---

## Part 2: Search UX Patterns

### 2.1 Instant Search (Search-as-You-Type)
```tsx
import { useState, useEffect, useRef } from 'react';

function useDebounce<T>(value: T, delay: number): T {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const timer = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(timer);
  }, [value, delay]);
  return debounced;
}

function SearchInput() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const debouncedQuery = useDebounce(query, 300);
  const abortControllerRef = useRef<AbortController>();

  useEffect(() => {
    if (!debouncedQuery) {
      setResults([]);
      return;
    }

    // Cancel previous request
    abortControllerRef.current?.abort();
    abortControllerRef.current = new AbortController();

    setLoading(true);
    fetch(`/api/search?q=${encodeURIComponent(debouncedQuery)}`, {
      signal: abortControllerRef.current.signal,
    })
      .then(res => res.json())
      .then(data => {
        setResults(data.results);
        setLoading(false);
      })
      .catch(err => {
        if (err.name !== 'AbortError') {
          console.error(err);
          setLoading(false);
        }
      });

    return () => abortControllerRef.current?.abort();
  }, [debouncedQuery]);

  return (
    <div>
      <input
        type="search"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search..."
        aria-label="Search"
      />
      {loading && <Spinner />}
      {results.length > 0 && <SearchResults results={results} />}
    </div>
  );
}
```

### 2.2 Search Debounce Timing
| Delay | Feel | Use Case |
|---|---|---|
| 0ms | Instant | Local search (client-side) |
| 150ms | Very responsive | Fast API, small dataset |
| 300ms | Responsive | Default — good balance |
| 500ms | Deliberate | Slow API, large dataset |
| 750ms+ | Sluggish | Avoid — feels broken |

### 2.3 Search Results Display
```tsx
function SearchResults({ results, query }) {
  if (results.length === 0) {
    return (
      <div className="empty-state">
        <p>No results found for "{query}"</p>
        <p>Try different keywords or check spelling</p>
      </div>
    );
  }

  return (
    <ul role="listbox">
      {results.map(result => (
        <li key={result.id} role="option" aria-selected="false">
          {/* Highlight matched text */}
          <HighlightedText text={result.name} query={query} />
          <p className="text-sm text-gray-500">{result.description}</p>
        </li>
      ))}
    </ul>
  );
}

function HighlightedText({ text, query }) {
  const parts = text.split(new RegExp(`(${query})`, 'gi'));
  return (
    <span>
      {parts.map((part, i) =>
        part.toLowerCase() === query.toLowerCase() ? (
          <mark key={i}>{part}</mark>
        ) : (
          part
        )
      )}
    </span>
  );
}
```

### 2.4 Keyboard Navigation
```tsx
function SearchInput() {
  const [activeIndex, setActiveIndex] = useState(-1);

  const handleKeyDown = (e: KeyboardEvent) => {
    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        setActiveIndex(prev => Math.min(prev + 1, results.length - 1));
        break;
      case 'ArrowUp':
        e.preventDefault();
        setActiveIndex(prev => Math.max(prev - 1, -1));
        break;
      case 'Enter':
        if (activeIndex >= 0) {
          selectResult(results[activeIndex]);
        }
        break;
      case 'Escape':
        setQuery('');
        setActiveIndex(-1);
        break;
    }
  };

  return (
    <input
      onKeyDown={handleKeyDown}
      aria-activedescendant={activeIndex >= 0 ? `result-${activeIndex}` : undefined}
      aria-expanded={results.length > 0}
      aria-controls="search-results"
      role="combobox"
    />
  );
}
```

---

## Part 3: Filtering Architecture

### 3.1 URL-Based Filters
```tsx
import { useSearchParams, useRouter, usePathname } from 'next/navigation';

function ProductFilters() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const pathname = usePathname();

  const updateFilter = (key: string, value: string | null) => {
    const params = new URLSearchParams(searchParams);
    if (value === null) {
      params.delete(key);
    } else {
      params.set(key, value);
    }
    router.push(`${pathname}?${params.toString()}`, { scroll: false });
  };

  const category = searchParams.get('category') || '';
  const minPrice = searchParams.get('minPrice') || '';
  const maxPrice = searchParams.get('maxPrice') || '';
  const sort = searchParams.get('sort') || 'relevance';

  return (
    <aside>
      {/* Category filter */}
      <FilterGroup title="Category">
        {categories.map(cat => (
          <label key={cat}>
            <input
              type="radio"
              name="category"
              checked={category === cat.value}
              onChange={() => updateFilter('category', cat.value)}
            />
            {cat.label}
          </label>
        ))}
      </FilterGroup>

      {/* Price range */}
      <FilterGroup title="Price">
        <input
          type="number"
          placeholder="Min"
          value={minPrice}
          onChange={(e) => updateFilter('minPrice', e.target.value || null)}
        />
        <input
          type="number"
          placeholder="Max"
          value={maxPrice}
          onChange={(e) => updateFilter('maxPrice', e.target.value || null)}
        />
      </FilterGroup>

      {/* Sort */}
      <select value={sort} onChange={(e) => updateFilter('sort', e.target.value)}>
        <option value="relevance">Relevance</option>
        <option value="price-asc">Price: Low to High</option>
        <option value="price-desc">Price: High to Low</option>
        <option value="newest">Newest</option>
      </select>

      {/* Clear all */}
      <button onClick={() => router.push(pathname)}>Clear all filters</button>
    </aside>
  );
}
```

### 3.2 Faceted Search (Filter with Counts)
```tsx
function FacetedFilters({ facets, selectedFilters, onFilterChange }) {
  return (
    <aside>
      {facets.map(facet => (
        <FilterGroup key={facet.name} title={facet.label}>
          {facet.values.map(value => (
            <label key={value.value}>
              <input
                type="checkbox"
                checked={selectedFilters[facet.name]?.includes(value.value)}
                onChange={(e) => onFilterChange(facet.name, value.value, e.target.checked)}
              />
              {value.label}
              <span className="count">({value.count})</span>
            </label>
          ))}
        </FilterGroup>
      ))}
    </aside>
  );
}

// API response includes facet counts
{
  results: [...],
  facets: {
    category: [
      { value: 'electronics', label: 'Electronics', count: 42 },
      { value: 'clothing', label: 'Clothing', count: 18 },
    ],
    brand: [
      { value: 'apple', label: 'Apple', count: 12 },
      { value: 'samsung', label: 'Samsung', count: 8 },
    ],
  }
}
```

### 3.3 Server-Side Filtering
```typescript
// api/search/route.ts
export async function GET(request: Request) {
  const { searchParams } = new URL(request.url);
  const query = searchParams.get('q') || '';
  const category = searchParams.get('category');
  const minPrice = searchParams.get('minPrice');
  const maxPrice = searchParams.get('maxPrice');
  const sort = searchParams.get('sort') || 'relevance';
  const page = parseInt(searchParams.get('page') || '1');
  const limit = parseInt(searchParams.get('limit') || '20');

  // Build SQL query with parameterized inputs
  let sql = `
    SELECT *, ts_rank(search_vector, query) as rank
    FROM products, plainto_tsquery('english', $1) query
    WHERE search_vector @@ query
  `;
  const params = [query];
  let paramIndex = 2;

  if (category) {
    sql += ` AND category = $${paramIndex++}`;
    params.push(category);
  }
  if (minPrice) {
    sql += ` AND price >= $${paramIndex++}`;
    params.push(parseFloat(minPrice));
  }
  if (maxPrice) {
    sql += ` AND price <= $${paramIndex++}`;
    params.push(parseFloat(maxPrice));
  }

  // Sort
  switch (sort) {
    case 'price-asc': sql += ' ORDER BY price ASC'; break;
    case 'price-desc': sql += ' ORDER BY price DESC'; break;
    case 'newest': sql += ' ORDER BY created_at DESC'; break;
    default: sql += ' ORDER BY rank DESC'; break;
  }

  // Pagination
  sql += ` LIMIT $${paramIndex++} OFFSET $${paramIndex++}`;
  params.push(limit, (page - 1) * limit);

  const results = await db.query(sql, params);

  // Get facet counts (separate query)
  const facetQuery = `
    SELECT category, count(*) as count
    FROM products
    WHERE search_vector @@ plainto_tsquery('english', $1)
    ${category ? '' : ''}
    GROUP BY category
  `;
  const facets = await db.query(facetQuery, [query]);

  return Response.json({
    results: results.rows,
    facets: facets.rows,
    page,
    totalPages: Math.ceil(results.rows.length / limit),
  });
}
```

---

## Part 4: Search Features

### 4.1 Typo Tolerance
- **Meilisearch/Typesense:** Built-in typo tolerance with configurable distance
- **PostgreSQL:** Use `pg_trgm` extension for trigram similarity
```sql
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_products_name_trgm ON products USING gin(name gin_trgm_ops);

SELECT * FROM products WHERE name % 'laptob' ORDER BY similarity(name, 'laptob') DESC;
```

### 4.2 Autocomplete / Suggestions
```typescript
// API endpoint for suggestions
export async function GET(request: Request) {
  const q = new URL(request.url).searchParams.get('q') || '';
  if (q.length < 2) return Response.json({ suggestions: [] });

  const suggestions = await db.query(`
    SELECT DISTINCT name
    FROM products
    WHERE name ILIKE $1 || '%'
    ORDER BY name
    LIMIT 5
  `, [q]);

  return Response.json({ suggestions: suggestions.rows.map(r => r.name) });
}
```

### 4.3 Search Analytics
```typescript
// Track search queries for analytics
async function trackSearch(query: string, resultsCount: number) {
  await fetch('/api/analytics/search', {
    method: 'POST',
    body: JSON.stringify({ query, resultsCount, timestamp: Date.now() }),
  });
}

// Track zero-result searches — important for identifying content gaps
if (results.length === 0) {
  trackSearch(query, 0);
  // Show suggestions: "Did you mean...?" or popular searches
}
```

### 4.4 Recent Searches
```typescript
// Store recent searches in localStorage
function useRecentSearches() {
  const [recent, setRecent] = useState<string[]>([]);

  useEffect(() => {
    const stored = localStorage.getItem('recent-searches');
    if (stored) setRecent(JSON.parse(stored));
  }, []);

  const addRecent = (query: string) => {
    const updated = [query, ...recent.filter(r => r !== query)].slice(0, 5);
    setRecent(updated);
    localStorage.setItem('recent-searches', JSON.stringify(updated));
  };

  return { recent, addRecent };
}
```

### 4.5 Synonyms
```typescript
// Meilisearch synonyms
await client.index('products').updateSynonyms({
  'laptop': ['notebook', 'laptop computer'],
  'phone': ['smartphone', 'mobile phone', 'cell phone'],
  'tv': ['television', 'teley'],
});

// PostgreSQL synonyms — use thesaurus dictionary
// Create custom thesaurus file in PostgreSQL config
```

---

## Part 5: Pagination

### 5.1 Cursor (Keyset) Pagination — Recommended
```sql
-- Efficient — constant time, no counting
SELECT * FROM products
WHERE id > :last_id
ORDER BY id ASC
LIMIT 20;
```

### 5.2 Page-Based Pagination
```sql
-- Simple but gets slow with large offsets
SELECT * FROM products
ORDER BY created_at DESC
LIMIT 20 OFFSET 40; -- Page 3
```

### 5.3 Infinite Scroll
```tsx
function useInfiniteSearch(query, filters) {
  const { data, fetchNextPage, hasNextPage, isFetchingNextPage } = useInfiniteQuery({
    queryKey: ['search', query, filters],
    queryFn: ({ pageParam = 1 }) => searchAPI({ query, filters, page: pageParam }),
    getNextPageParam: (lastPage) => lastPage.hasNext ? lastPage.page + 1 : undefined,
    initialPageParam: 1,
  });

  // Intersection observer for auto-load
  const { ref, inView } = useInView();
  useEffect(() => {
    if (inView && hasNextPage) fetchNextPage();
  }, [inView, hasNextPage, fetchNextPage]);

  return { data, ref, isFetchingNextPage };
}
```

---

## Part 6: Performance

### 6.1 Database Optimization
- **GIN index:** On tsvector column for full-text search
- **B-tree index:** On filter columns (category, price, created_at)
- **Composite index:** On commonly combined filters
- **Materialized view:** For expensive aggregations (facet counts)
- **Connection pooling:** For high-traffic search APIs

### 6.2 Caching
```typescript
// Cache search results with TanStack Query
const { data } = useQuery({
  queryKey: ['search', query, filters],
  queryFn: () => searchAPI({ query, filters }),
  staleTime: 60 * 1000, // 1 minute
  keepPreviousData: true, // Show old results while fetching new
});

// Server-side caching with Redis
const cacheKey = `search:${query}:${JSON.stringify(filters)}`;
const cached = await redis.get(cacheKey);
if (cached) return Response.json(JSON.parse(cached));

const results = await performSearch(query, filters);
await redis.setex(cacheKey, 60, JSON.stringify(results)); // 1 minute TTL
return Response.json(results);
```

### 6.3 Client-Side Performance
- **Debounce:** 300ms default — don't search on every keystroke
- **AbortController:** Cancel in-flight requests when query changes
- **Virtualization:** For large result lists (react-virtual)
- **Lazy rendering:** Only render visible results
- **keepPreviousData:** Show old results while fetching new ones

---

## Part 7: Accessibility

### 7.1 Search Input Accessibility
```tsx
<div role="search">
  <label htmlFor="search-input" className="sr-only">Search products</label>
  <input
    id="search-input"
    type="search"
    role="combobox"
    aria-expanded={isOpen}
    aria-controls="search-results"
    aria-activedescendant={activeId}
    aria-autocomplete="list"
    placeholder="Search products..."
  />
  <ul id="search-results" role="listbox">
    {results.map(result => (
      <li key={result.id} role="option" id={`result-${result.id}`} aria-selected={false}>
        {result.name}
      </li>
    ))}
  </ul>
</div>
```

### 7.2 Filter Accessibility
- **Fieldset and legend:** Group related filters
- **Labels:** All filter inputs have associated labels
- **Keyboard accessible:** All filters operable with keyboard
- **Announce results:** "Showing 24 results" via `aria-live`
- **Clear button:** Accessible "Clear all filters" button

---

## Execution Instructions for Cascade

When this skill is activated for search & filtering architecture:

1. **Read the project context** — data volume, search requirements, filtering needs
2. **Choose search engine** — PostgreSQL FTS (< 1M), Meilisearch/Typesense (1M-10M), Elasticsearch (> 10M)
3. **Implement search index** — tsvector column or external search engine index
4. **Implement search API** — parameterized queries, sort, pagination, facet counts
5. **Implement instant search UI** — debounced input, abort controller, loading state, results
6. **Implement keyboard navigation** — arrow keys, enter, escape for search results
7. **Implement URL-based filters** — all filter state in URL search params
8. **Implement faceted filtering** — checkboxes with counts, multi-select, clear all
9. **Implement pagination** — cursor-based (recommended) or infinite scroll
10. **Implement search features** — typo tolerance, autocomplete, synonyms, recent searches
11. **Optimize performance** — indexes, caching, debounce, abort, virtualization
12. **Ensure accessibility** — ARIA roles, keyboard navigation, live regions, labels
13. **Track search analytics** — queries, zero-result searches, popular searches
14. **Document** — search architecture, filter options, API endpoints, indexing strategy
