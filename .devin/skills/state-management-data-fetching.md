---
name: State Management & Data Fetching Skill
description: Comprehensive methodology for managing state and fetching data in web applications — 2025-2026 practices with React Server Components, TanStack Query, Zustand, Jotai, URL state, and optimistic updates
version: 1.0.0
tags: [state-management, data-fetching, react-query, tanstack-query, zustand, jotai, rsc, server-components, optimistic-updates, caching]
---

# State Management & Data Fetching Skill

## Purpose
This skill provides a comprehensive methodology for managing state and fetching data across any kind of web project. It reflects **modern 2025-2026 practices** — React Server Components for zero-client-JS data fetching, TanStack Query for client-side caching, Zustand for simple global state, Jotai for atomic state, URL as source of truth, and optimistic updates.

## Core Philosophy

**State should live where it's needed, not in a global store by default.** The biggest state management mistake is putting everything in a global store. Most state is local — keep it in component state. Only elevate state when multiple components need it. The right answer is usually the simplest one that works.

**The #1 rule:** The URL is the best state manager. If state can be in the URL (filters, pagination, sorting, selected tab), put it there. URL state is shareable, bookmarkable, survives refresh, and works with browser back/forward.

---

## Part 1: State Classification

### 1.1 Types of State

| Type | Examples | Where to Store |
|---|---|---|
| **Server state** | API data, user profile, posts | TanStack Query / RSC |
| **URL state** | Filters, pagination, sort, tab | URL search params |
| **Form state** | Input values, validation, dirty | React Hook Form / local |
| **UI state (local)** | Modal open, dropdown, hover | useState |
| **UI state (global)** | Theme, sidebar, locale | Zustand / context |
| **Client cache** | Computed values, derived state | useMemo / selectors |
| **Persistent state** | Preferences, cart, auth token | localStorage / cookie |

### 1.2 Decision Tree
```
Is it from the server?
  → Yes: TanStack Query or React Server Components
  → No: Is it in the URL?
    → Yes: URL search params (nuqs, useSearchParams)
    → No: Is it form data?
      → Yes: React Hook Form
      → No: Is it local to one component?
        → Yes: useState / useReducer
        → No: Is it needed by a few nearby components?
          → Yes: Lift state up / Context
          → No: Is it truly global?
            → Yes: Zustand (simple) or Jotai (atomic)
            → No: Re-evaluate — probably over-engineering
```

---

## Part 2: Server State — TanStack Query

### 2.1 Basic Setup
```tsx
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 60 * 1000, // 1 minute
      gcTime: 5 * 60 * 1000, // 5 minutes (formerly cacheTime)
      retry: 3,
      refetchOnWindowFocus: false,
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Component />
    </QueryClientProvider>
  );
}
```

### 2.2 Queries (Reading Data)
```tsx
import { useQuery } from '@tanstack/react-query';

function UserList() {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['users'],
    queryFn: () => fetch('/api/users').then(res => res.json()),
  });

  if (isLoading) return <Skeleton />;
  if (error) return <ErrorMessage error={error} />;
  return <List items={data} />;
}

// With parameters
function UserDetail({ userId }) {
  const { data } = useQuery({
    queryKey: ['users', userId],
    queryFn: () => fetch(`/api/users/${userId}`).then(res => res.json()),
  });
  return <Profile user={data} />;
}
```

### 2.3 Mutations (Writing Data)
```tsx
import { useMutation, useQueryClient } from '@tanstack/react-query';

function CreateUser() {
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: (newUser) => fetch('/api/users', {
      method: 'POST',
      body: JSON.stringify(newUser),
    }).then(res => res.json()),
    onSuccess: () => {
      // Invalidate and refetch
      queryClient.invalidateQueries({ queryKey: ['users'] });
    },
  });

  return (
    <form onSubmit={(e) => {
      e.preventDefault();
      mutation.mutate({ name: 'John', email: 'john@example.com' });
    }}>
      <button disabled={mutation.isPending}>
        {mutation.isPending ? 'Creating...' : 'Create User'}
      </button>
    </form>
  );
}
```

### 2.4 Optimistic Updates
```tsx
const mutation = useMutation({
  mutationFn: updateTodo,
  onMutate: async (newTodo) => {
    // Cancel outgoing refetches
    await queryClient.cancelQueries({ queryKey: ['todos'] });

    // Snapshot previous value
    const previousTodos = queryClient.getQueryData(['todos']);

    // Optimistically update cache
    queryClient.setQueryData(['todos'], (old) =>
      old.map(todo => todo.id === newTodo.id ? newTodo : todo)
    );

    return { previousTodos };
  },
  onError: (err, newTodo, context) => {
    // Roll back on error
    queryClient.setQueryData(['todos'], context.previousTodos);
  },
  onSettled: () => {
    // Always refetch after error or success
    queryClient.invalidateQueries({ queryKey: ['todos'] });
  },
});
```

### 2.5 Query Keys
```tsx
// Hierarchical keys — invalidate by prefix
['users']                          // All users
['users', 'list', { page: 1 }]    // Users list, page 1
['users', 'detail', userId]       // User detail
['users', 'detail', userId, 'posts'] // User's posts

// Invalidation by prefix
queryClient.invalidateQueries({ queryKey: ['users'] }); // Invalidates all user queries
queryClient.invalidateQueries({ queryKey: ['users', 'list'] }); // Invalidates list only
```

### 2.6 Prefetching
```tsx
// Prefetch on hover
function UserLink({ userId }) {
  const queryClient = useQueryClient();

  const prefetch = () => {
    queryClient.prefetchQuery({
      queryKey: ['users', userId],
      queryFn: () => fetch(`/api/users/${userId}`).then(res => res.json()),
    });
  };

  return (
    <Link href={`/users/${userId}`} onMouseEnter={prefetch}>
      View Profile
    </Link>
  );
}
```

### 2.7 Infinite Queries (Pagination)
```tsx
import { useInfiniteQuery } from '@tanstack/react-query';

function PostList() {
  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useInfiniteQuery({
    queryKey: ['posts'],
    queryFn: ({ pageParam = 1 }) => fetch(`/api/posts?page=${pageParam}`).then(res => res.json()),
    getNextPageParam: (lastPage) => lastPage.nextPage,
    initialPageParam: 1,
  });

  return (
    <div>
      {data.pages.map(page => page.items.map(post => <Post key={post.id} {...post} />))}
      <button onClick={() => fetchNextPage()} disabled={!hasNextPage || isFetchingNextPage}>
        {isFetchingNextPage ? 'Loading...' : 'Load More'}
      </button>
    </div>
  );
}
```

### 2.8 SSR with TanStack Query (Hydration)
```tsx
// Server
import { dehydrate, HydrationBoundary } from '@tanstack/react-query';

export async function getServerSideProps() {
  const queryClient = new QueryClient();
  await queryClient.prefetchQuery({
    queryKey: ['users'],
    queryFn: () => fetch('http://api/users').then(res => res.json()),
  });
  return { props: { dehydratedState: dehydrate(queryClient) } };
}

// Client
function App({ dehydratedState }) {
  return (
    <HydrationBoundary state={dehydratedState}>
      <UserList />
    </HydrationBoundary>
  );
}
```

---

## Part 3: React Server Components (RSC)

### 3.1 Server Components (No Client JS)
```tsx
// app/users/page.tsx — Server Component
async function UserList() {
  const users = await fetch('https://api/users', { next: { revalidate: 60 } }).then(res => res.json());

  return (
    <ul>
      {users.map(user => <li key={user.id}>{user.name}</li>)}
    </ul>
  );
}
```
- **Zero client JS:** Data fetching happens on server, no client bundle
- **Direct database access:** Can query database directly (no API layer needed)
- **Streaming:** Components stream as they resolve
- **Cache:** `revalidate` for ISR, `cache: 'no-store'` for dynamic, `tags` for on-demand revalidation

### 3.2 Client Components
```tsx
'use client';

import { useState } from 'react';

function SearchBar({ onSearch }) {
  const [query, setQuery] = useState('');
  return <input value={query} onChange={(e) => onSearch(e.target.value)} />;
}
```
- **`'use client'`:** Opt-in to client-side rendering
- **When needed:** Interactivity, state, effects, browser APIs
- **Keep minimal:** Most components should be server components

### 3.3 Server Actions (Mutations)
```tsx
// app/actions.ts
'use server';

export async function createUser(formData: FormData) {
  const name = formData.get('name');
  const user = await db.user.create({ data: { name } });
  revalidatePath('/users');
}

// Component
function CreateUserForm() {
  return (
    <form action={createUser}>
      <input name="name" />
      <button type="submit">Create</button>
    </form>
  );
}
```

### 3.4 Data Fetching Patterns in RSC
```tsx
// Parallel fetching
async function Dashboard() {
  const [user, posts, stats] = await Promise.all([
    fetch('/api/user').then(r => r.json()),
    fetch('/api/posts').then(r => r.json()),
    fetch('/api/stats').then(r => r.json()),
  ]);
  return <DashboardView user={user} posts={posts} stats={stats} />;
}

// Sequential fetching (when dependent)
async function UserPosts({ userId }) {
  const user = await fetch(`/api/users/${userId}`).then(r => r.json());
  const posts = await fetch(`/api/users/${user.id}/posts`).then(r => r.json());
  return <Posts posts={posts} />;
}

// With caching and revalidation
async function Products() {
  const products = await fetch('/api/products', {
    next: { revalidate: 3600, tags: ['products'] },
  }).then(r => r.json());
  return <ProductList products={products} />;
}
```

---

## Part 4: URL State

### 4.1 useSearchParams (React Router / Next.js)
```tsx
import { useSearchParams, useRouter, usePathname } from 'next/navigation';

function ProductFilters() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const pathname = usePathname();

  const category = searchParams.get('category') || 'all';
  const sort = searchParams.get('sort') || 'newest';
  const page = parseInt(searchParams.get('page') || '1');

  const updateParams = (key, value) => {
    const params = new URLSearchParams(searchParams);
    params.set(key, value);
    router.push(`${pathname}?${params.toString()}`);
  };

  return (
    <div>
      <select value={category} onChange={(e) => updateParams('category', e.target.value)}>
        <option value="all">All</option>
        <option value="electronics">Electronics</option>
      </select>
      <select value={sort} onChange={(e) => updateParams('sort', e.target.value)}>
        <option value="newest">Newest</option>
        <option value="price">Price</option>
      </select>
    </div>
  );
}
```

### 4.2 nuqs (Next.js URL State)
```tsx
import { useQueryState, useQueryStates } from 'nuqs';

function ProductFilters() {
  const [category, setCategory] = useQueryState('category', { defaultValue: 'all' });
  const [sort, setSort] = useQueryState('sort', { defaultValue: 'newest' });
  const [page, setPage] = useQueryState('page', { parse: parseInt, serialize: String });

  return (
    <div>
      <select value={category} onChange={(e) => setCategory(e.target.value)}>
        <option value="all">All</option>
      </select>
    </div>
  );
}
```

### 4.3 What Should Be in URL
- **Filters:** category, price range, tags
- **Sorting:** sort field, direction
- **Pagination:** page number, items per page
- **Search:** search query
- **Selected tab:** active tab
- **Modal state:** open/closed (for shareable links)
- **Theme:** light/dark (optional, but shareable)

### 4.4 What Should NOT Be in URL
- **Form input values:** Too much data, changes too frequently
- **Hover state:** Not meaningful
- **Authentication tokens:** Security risk
- **Temporary UI state:** Loading spinners, error messages

---

## Part 5: Global Client State — Zustand

### 5.1 Basic Store
```tsx
import { create } from 'zustand';

interface BearStore {
  bears: number;
  addBear: () => void;
  removeBear: () => void;
}

const useBearStore = create<BearStore>((set) => ({
  bears: 0,
  addBear: () => set((state) => ({ bears: state.bears + 1 })),
  removeBear: () => set((state) => ({ bears: state.bears - 1 })),
}));

function BearCounter() {
  const bears = useBearStore((state) => state.bears);
  return <h1>{bears} bears</h1>;
}

function Controls() {
  const addBear = useBearStore((state) => state.addBear);
  return <button onClick={addBear}>Add Bear</button>;
}
```

### 5.2 Selectors (Performance)
```tsx
// Good: select only what you need
const bears = useBearStore((state) => state.bears);
const addBear = useBearStore((state) => state.addBear);

// Bad: subscribes to entire store
const store = useBearStore();
const { bears, addBear } = store;

// With shallow comparison for objects
import { shallow } from 'zustand/shallow';
const { bears, addBear } = useBearStore(
  (state) => ({ bears: state.bears, addBear: state.addBear }),
  shallow
);
```

### 5.3 Persisted Store
```tsx
import { persist } from 'zustand/middleware';

const useAuthStore = create(
  persist(
    (set) => ({
      user: null,
      setUser: (user) => set({ user }),
      logout: () => set({ user: null }),
    }),
    { name: 'auth-storage' } // localStorage key
  )
);
```

### 5.4 When to Use Zustand
- **Global UI state:** Theme, sidebar, language, notifications
- **Cross-component state:** State needed by unrelated components
- **Simple global state:** When Context causes too many re-renders
- **Don't use for:** Server state (use TanStack Query), URL state (use search params)

---

## Part 6: Atomic State — Jotai

### 6.1 Basic Atoms
```tsx
import { atom, useAtom } from 'jotai';

const countAtom = atom(0);

function Counter() {
  const [count, setCount] = useAtom(countAtom);
  return <button onClick={() => setCount(count + 1)}>{count}</button>;
}
```

### 6.2 Derived Atoms
```tsx
const priceAtom = atom(100);
const quantityAtom = atom(2);
const totalAtom = atom((get) => get(priceAtom) * get(quantityAtom));

function Total() {
  const [total] = useAtom(totalAtom);
  return <p>Total: ${total}</p>;
}
```

### 6.3 When to Use Jotai
- **Fine-grained state:** When different components need different pieces of state
- **Derived state:** When state is computed from other state
- **No re-renders for unrelated changes:** Only components using changed atoms re-render
- **Don't use for:** Simple state (useState is fine), server state (use TanStack Query)

---

## Part 7: Form State — React Hook Form

### 7.1 Basic Form
```tsx
import { useForm } from 'react-hook-form';

function SignupForm() {
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({
    defaultValues: { email: '', password: '' },
  });

  const onSubmit = async (data) => {
    await fetch('/api/signup', { method: 'POST', body: JSON.stringify(data) });
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <input {...register('email', { required: 'Email is required' })} type="email" />
      {errors.email && <span>{errors.email.message}</span>}

      <input {...register('password', { required: 'Password is required', minLength: { value: 12, message: 'Min 12 characters' } })} type="password" />
      {errors.password && <span>{errors.password.message}</span>}

      <button type="submit" disabled={isSubmitting}>Sign up</button>
    </form>
  );
}
```

### 7.2 With Zod Validation
```tsx
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

const schema = z.object({
  email: z.string().email('Invalid email'),
  password: z.string().min(12, 'Min 12 characters'),
});

type FormData = z.infer<typeof schema>;

function SignupForm() {
  const { register, handleSubmit, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
  });

  return (/* ... */);
}
```

---

## Part 8: Caching Strategies

### 8.1 TanStack Query Cache
- **`staleTime`:** How long data is considered fresh (no refetch on focus/reconnect)
- **`gcTime`:** How long unused data stays in cache before garbage collection
- **`refetchOnWindowFocus`:** Refetch when user returns to tab
- **`refetchOnReconnect`:** Refetch when network reconnects
- **`retry`:** Number of retries on failure

### 8.2 Next.js Cache (RSC)
```tsx
// Revalidate every 60 seconds (ISR)
fetch('/api/data', { next: { revalidate: 60 } });

// Never revalidate (static)
fetch('/api/data', { next: { revalidate: false } });

// Always fresh (dynamic)
fetch('/api/data', { cache: 'no-store' });

// Tag-based revalidation
fetch('/api/data', { next: { tags: ['products'] } });
// Later: revalidateTag('products');
```

### 8.3 Cache Invalidation Strategies
- **Time-based:** `staleTime` / `revalidate` — data is fresh for N seconds
- **Event-based:** Invalidate on mutation — `queryClient.invalidateQueries()`
- **Manual:** User-triggered refresh — `refetch()` / `revalidateTag()`
- **Optimistic:** Update cache immediately, reconcile on response

---

## Part 9: Patterns & Anti-Patterns

### 9.1 Good Patterns
- **Colocation:** Keep state close to where it's used
- **Single source of truth:** One source per piece of state
- **Derived state:** Compute from existing state, don't duplicate
- **Normalized state:** Store entities by ID, not in arrays
- **Optimistic updates:** Update UI immediately, reconcile on response
- **Prefetching:** Fetch data before user needs it (hover, preconnect)

### 9.2 Anti-Patterns
- **Global everything:** Don't put all state in a global store
- **Duplicated state:** Don't store the same data in multiple places
- **Derived state in store:** Compute derived values with selectors, don't store them
- **Server state in global store:** Use TanStack Query, not Redux/Zustand for server data
- **Prop drilling through many levels:** Use Context or lift state appropriately
- **Over-engineering:** Don't add Redux for a toggle button
- **Stale closures:** Use functional updates `setState(prev => prev + 1)`

---

## Part 10: Data Fetching Patterns

### 10.1 Loading States
```tsx
// Skeleton for initial load
{isLoading && <Skeleton />}

// Stale-while-revalidate (show old data while fetching new)
{isFetching && !isLoading && <RefreshIndicator />}
{data && <Content data={data} />}

// Error state
{error && <ErrorMessage error={error} />}

// Empty state
{data?.length === 0 && <EmptyState />}
```

### 10.2 Error Handling
```tsx
const { data, error, isError, retry } = useQuery({
  queryKey: ['users'],
  queryFn: fetchUsers,
  retry: (failureCount, error) => {
    // Don't retry on 4xx
    if (error.status >= 400 && error.status < 500) return false;
    return failureCount < 3;
  },
});

if (isError) {
  return (
    <div>
      <p>Something went wrong: {error.message}</p>
      <button onClick={retry}>Try again</button>
    </div>
  );
}
```

### 10.3 Race Conditions
```tsx
// TanStack Query handles race conditions automatically
// Only the latest query's response is used

// For manual fetching, use AbortController
useEffect(() => {
  const controller = new AbortController();
  fetch(`/api/users?q=${query}`, { signal: controller.signal })
    .then(res => res.json())
    .then(data => setResults(data));
  return () => controller.abort();
}, [query]);
```

### 10.4 Dependent Queries
```tsx
const { data: user } = useQuery({ queryKey: ['user', userId], queryFn: fetchUser });
const { data: posts } = useQuery({
  queryKey: ['posts', user?.id],
  queryFn: () => fetchPosts(user.id),
  enabled: !!user, // Only fetch when user is available
});
```

### 10.5 Parallel Queries
```tsx
const { data: user } = useQuery({ queryKey: ['user'], queryFn: fetchUser });
const { data: posts } = useQuery({ queryKey: ['posts'], queryFn: fetchPosts });
const { data: stats } = useQuery({ queryKey: ['stats'], queryFn: fetchStats });

// Or use useQueries for dynamic parallel queries
const results = useQueries({
  queries: userIds.map(id => ({
    queryKey: ['user', id],
    queryFn: () => fetchUser(id),
  })),
});
```

---

## Execution Instructions for Cascade

When this skill is activated for state management & data fetching:

1. **Read the project context** — framework, existing state management, data sources
2. **Classify state** — server, URL, form, local UI, global UI, persistent
3. **Choose tools** — TanStack Query (server), URL params (URL state), Zustand (global), Jotai (atomic), React Hook Form (forms)
4. **Set up server state** — TanStack Query or React Server Components for data fetching
5. **Set up URL state** — useSearchParams or nuqs for filters, pagination, sorting
6. **Set up global state** — Zustand for simple global, Jotai for atomic/derived
7. **Set up form state** — React Hook Form + Zod for validation
8. **Implement caching** — staleTime, gcTime, revalidation, prefetching
9. **Implement optimistic updates** — Update UI immediately, rollback on error
10. **Handle loading/error/empty states** — Skeletons, error messages, empty states
11. **Avoid anti-patterns** — No global everything, no duplicated state, no server state in client store
12. **Document** — State architecture, data flow, caching strategy
