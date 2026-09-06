---
name: state-management
description: Comprehensive state management & data fetching workflow — state classification, tool selection, server state, URL state, global state, forms, caching, and patterns
---

# State Management & Data Fetching Workflow

This workflow applies the **State Management & Data Fetching Skill** (`~/.codeium/windsurf/skills/state-management-data-fetching.md`) to architect state and data fetching for web applications.

## When to Run
- When architecting state management for a new project
- When the user says `/state-management` or asks about data fetching
- When setting up TanStack Query, Zustand, or other state tools
- When refactoring existing state management
- When implementing data fetching patterns

---

## Step 1: Classify State

1. Identify all state in the application:
   - **Server state:** API data, user profile, posts, products
   - **URL state:** Filters, pagination, sorting, active tab, search query
   - **Form state:** Input values, validation, dirty/pristine, submission
   - **Local UI state:** Modal open, dropdown, hover, accordion
   - **Global UI state:** Theme, sidebar, language, notifications
   - **Persistent state:** Auth token, cart, preferences (localStorage/cookie)
2. Map each piece of state to its appropriate storage location
3. Identify state dependencies — what state depends on other state
4. Document the state architecture

## Step 2: Choose State Tools

1. **Server state:** TanStack Query (client-rendered) or React Server Components (Next.js)
2. **URL state:** `useSearchParams` or `nuqs` (Next.js)
3. **Form state:** React Hook Form + Zod for validation
4. **Local UI state:** `useState` / `useReducer` (component-level)
5. **Global UI state:** Zustand (simple) or Jotai (atomic/derived)
6. **Persistent state:** Zustand `persist` middleware or localStorage/cookie
7. Install and configure chosen tools
8. Set up providers (QueryClientProvider, etc.)

## Step 3: Set Up Server State (Data Fetching)

1. **TanStack Query (client-rendered):**
   - Configure QueryClient with default staleTime, gcTime, retry
   - Create query hooks per data type: `useUsers()`, `useUser(id)`, `usePosts()`
   - Set up query key hierarchy for granular invalidation
   - Configure SSR hydration if needed
2. **React Server Components (Next.js):**
   - Fetch data directly in server components
   - Configure revalidation: `revalidate` (ISR), `cache: 'no-store'` (dynamic), `tags` (on-demand)
   - Use Server Actions for mutations
   - Stream components with Suspense boundaries

## Step 4: Set Up URL State

1. Identify what should be in URL: filters, pagination, sorting, search, active tab
2. Use `useSearchParams` or `nuqs` for URL state management
3. Create helper functions for updating URL params
4. Ensure URL state is shareable and bookmarkable
5. Handle browser back/forward navigation
6. Don't put form input values or temporary UI state in URL

## Step 5: Set Up Global Client State

1. Create Zustand stores for global UI state (theme, sidebar, notifications)
2. Use selectors to subscribe only to needed state — avoid subscribing to entire store
3. Set up persisted stores for auth, cart, preferences
4. Use Jotai for fine-grained atomic state with derived values
5. Keep global state minimal — most state should be local or server state

## Step 6: Set Up Form State

1. Install React Hook Form and Zod
2. Create Zod schemas for each form type
3. Set up forms with `useForm` and `zodResolver`
4. Implement validation: required, format, min/max, custom rules
5. Handle form submission with loading and error states
6. Set up accessible error messages with `aria-describedby`

## Step 7: Implement Caching Strategy

1. **TanStack Query:**
   - Set `staleTime` based on data volatility (1-5 min for most data)
   - Set `gcTime` for cache cleanup (5-30 min)
   - Configure `refetchOnWindowFocus` and `refetchOnReconnect`
   - Set up prefetching for likely-next navigations
2. **Next.js RSC:**
   - Use `revalidate` for ISR (time-based)
   - Use `tags` for on-demand revalidation
   - Use `cache: 'no-store'` for always-fresh data
3. **Cache invalidation:**
   - Event-based: invalidate on mutation success
   - Time-based: staleTime/revalidate
   - Manual: user-triggered refresh

## Step 8: Implement Optimistic Updates

1. For mutations that update existing data, implement optimistic updates
2. Cancel outgoing refetches before mutating
3. Snapshot previous state for rollback
4. Update cache immediately with new data
5. On error: roll back to snapshot
6. On success: invalidate to refetch fresh data
7. Show subtle indicator that update is in progress

## Step 9: Handle Loading, Error, and Empty States

1. **Loading:** Skeleton screens for initial load, stale-while-revalidate for refetch
2. **Error:** Specific error messages with retry button, don't blame the user
3. **Empty:** Explain what's empty, why, and what to do next
4. **Offline:** Graceful degradation with cached data and retry option
5. **Partial loading:** Show available data while fetching the rest

## Step 10: Implement Data Fetching Patterns

1. **Dependent queries:** Use `enabled` flag to wait for prerequisite data
2. **Parallel queries:** Use `Promise.all` or `useQueries` for independent fetches
3. **Infinite queries:** Use `useInfiniteQuery` for pagination/load-more
4. **Prefetching:** Prefetch on hover for likely-next navigations
5. **Background refetch:** Refetch stale data on window focus or reconnect
6. **Race conditions:** TanStack Query handles automatically; use AbortController for manual fetches

## Step 11: Review Anti-Patterns

1. Check for global store overuse — is local state sufficient?
2. Check for duplicated state — is the same data stored in multiple places?
3. Check for server state in client store — should be in TanStack Query
4. Check for derived state stored instead of computed — use selectors
5. Check for prop drilling — should state be lifted or use Context?
6. Check for over-engineering — is the simplest solution being used?
7. Check for stale closures — use functional updates

## Step 12: Document Architecture

1. Document state classification — what state lives where
2. Document data flow — how data moves through the app
3. Document caching strategy — staleTime, invalidation, prefetching
4. Document query keys — hierarchy and invalidation patterns
5. Document store structure — Zustand stores, Jotai atoms
6. Share with team — ensure consistent state management patterns
