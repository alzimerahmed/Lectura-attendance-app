---
name: Type Safety & TypeScript Skill
description: Comprehensive methodology for type safety with TypeScript — 2025-2026 practices with strict config, utility types, generic patterns, branded types, runtime validation with Zod, and anti-patterns
version: 1.0.0
tags: [typescript, type-safety, strict-mode, generics, utility-types, zod, runtime-validation, branded-types, type-narrowing, anti-patterns]
---

# Type Safety & TypeScript Skill

## Purpose
This skill provides a comprehensive methodology for achieving type safety with TypeScript across any kind of web project. It reflects **modern 2025-2026 practices** — strict `tsconfig.json`, Zod for runtime validation at boundaries, branded types for domain primitives, exhaustive type narrowing, discriminated unions for state machines, and zero `any` tolerance.

## Core Philosophy

**Types are your safety net, not your straightjacket.** Good types catch bugs at compile time, provide autocomplete in your editor, and serve as living documentation. Bad types (`any`, `unknown` without narrowing, `as` casts) create a false sense of security while hiding bugs. Write types that are precise enough to catch real errors but readable enough that developers understand them.

**The #1 rule:** Never use `any`. `any` disables type checking entirely — it's worse than JavaScript. Use `unknown` when you don't know the type, then narrow it. Use proper types when you do know. If you're reaching for `any`, you have a type design problem that needs solving, not silencing.

---

## Part 1: TypeScript Configuration

### 1.1 Strict tsconfig.json
```json
{
  "compilerOptions": {
    "target": "ES2022",
    "lib": ["ES2022", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "moduleResolution": "bundler",
    "jsx": "preserve",
    "allowJs": false,
    "checkJs": false,
    "noEmit": true,
    "esModuleInterop": true,
    "forceConsistentCasingInFileNames": true,
    "verbatimModuleSyntax": true,

    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true,
    "strictFunctionTypes": true,
    "strictBindCallApply": true,
    "strictPropertyInitialization": true,
    "noImplicitThis": true,
    "alwaysStrict": true,

    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noImplicitReturns": true,
    "noFallthroughCasesInSwitch": true,
    "noUncheckedIndexedAccess": true,
    "noPropertyAccessFromIndexSignature": true,
    "exactOptionalPropertyTypes": true,

    "skipLibCheck": true,
    "isolatedModules": true,
    "resolveJsonModule": true,
    "incremental": true,
    "tsBuildInfoFile": "node_modules/.cache/tsbuildinfo.json"
  },
  "include": ["src"],
  "exclude": ["node_modules", "dist"]
}
```

### 1.2 Key Strict Flags Explained

| Flag | What It Catches |
|---|---|
| `strict: true` | Enables all strict mode checks |
| `noImplicitAny` | Prevents implicit `any` types |
| `strictNullChecks` | `null` and `undefined` are not assignable to other types |
| `noUncheckedIndexedAccess` | `arr[0]` returns `T \| undefined` (not just `T`) |
| `noPropertyAccessFromIndexSignature` | Forces bracket notation for index signatures |
| `exactOptionalPropertyTypes` | `undefined` is not the same as "not present" |
| `noUnusedLocals` | Error on unused local variables |
| `noUnusedParameters` | Error on unused function parameters |
| `noImplicitReturns` | All code paths must return |
| `noFallthroughCasesInSwitch` | Switch cases must break or return |

---

## Part 2: Type Patterns

### 2.1 Discriminated Unions (State Machines)
```typescript
type RequestState<T, E = Error> =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'success'; data: T }
  | { status: 'error'; error: E };

// Usage — exhaustive checking
function renderState<T>(state: RequestState<T>) {
  switch (state.status) {
    case 'idle':
      return <Idle />;
    case 'loading':
      return <Spinner />;
    case 'success':
      return <Data data={state.data} />;
    case 'error':
      return <Error error={state.error} />;
    // No default needed — TypeScript ensures all cases handled
  }
}
```

### 2.2 Branded Types (Nominal Typing)
```typescript
// Branded types prevent mixing up IDs and domain primitives
declare const brand: unique symbol;

type UserId = string & { readonly [brand]: 'UserId' };
type ProductId = string & { readonly [brand]: 'ProductId' };
type Email = string & { readonly [brand]: 'Email' };

// Constructor functions with validation
function createUserId(id: string): UserId {
  if (!id) throw new Error('Invalid user ID');
  return id as UserId;
}

function createEmail(email: string): Email {
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    throw new Error('Invalid email');
  }
  return email as Email;
}

// Now TypeScript prevents mixing them up
function getUser(id: UserId): User { ... }
getUser(createProductId('123')); // Type error!
```

### 2.3 Utility Types (Built-in)
```typescript
// Partial — all properties optional
type PartialUser = Partial<User>;

// Required — all properties required
type RequiredUser = Required<PartialUser>;

// Pick — select specific properties
type UserPreview = Pick<User, 'id' | 'name' | 'avatar'>;

// Omit — exclude specific properties
type CreateUserInput = Omit<User, 'id' | 'createdAt' | 'updatedAt'>;

// Record — key-value pairs
type UserMap = Record<UserId, User>;

// ReturnType — function return type
type Data = ReturnType<typeof fetchUser>;

// Parameters — function parameter types
type Params = Parameters<typeof fetchUser>;

// Awaited — unwrap Promise type
type UserData = Awaited<ReturnType<typeof fetchUser>>;

// Exclude — remove from union
type NonNull = Exclude<string | null, null>; // string

// Extract — keep from union
type StringOrNumber = Extract<string | number | boolean, string | number>;

// NonNullable — remove null and undefined
type Required = NonNullable<string | null | undefined>; // string

// Readonly — all properties readonly
type FrozenUser = Readonly<User>;

// Partial<T> with specific keys
type OptionalUser = Pick<User, 'id' | 'name'> & Partial<Omit<User, 'id' | 'name'>>;
```

### 2.4 Conditional Types
```typescript
// IsString — check if type extends string
type IsString<T> = T extends string ? true : false;

// ElementOf — get array element type
type ElementOf<T> = T extends (infer E)[] ? E : never;

// PromiseValue — unwrap Promise
type PromiseValue<T> = T extends Promise<infer U> ? U : T;

// DeepPartial — recursively make all properties optional
type DeepPartial<T> = {
  [P in keyof T]?: T[P] extends object ? DeepPartial<T[P]> : T[P];
};

// DeepReadonly — recursively make all properties readonly
type DeepReadonly<T> = {
  readonly [P in keyof T]: T[P] extends object ? DeepReadonly<T[P]> : T[P];
};
```

### 2.5 Mapped Types
```typescript
// Make all properties nullable
type Nullable<T> = {
  [P in keyof T]: T[P] | null;
};

// Make all properties readonly
type Mutable<T> = {
  -readonly [P in keyof T]: T[P];
};

// Change property types
type Stringify<T> = {
  [P in keyof T]: string;
};
```

---

## Part 3: Runtime Validation with Zod

### 3.1 Why Zod
TypeScript types are compile-time only — they don't exist at runtime. When data crosses a boundary (API request, file read, database query, external API), you need runtime validation. Zod provides:
- **Runtime validation:** Schema validates data at runtime
- **Type inference:** `z.infer<typeof schema>` generates TypeScript types
- **Single source of truth:** One schema for both validation and types
- **Error messages:** Detailed, customizable error messages

### 3.2 Basic Zod Usage
```typescript
import { z } from 'zod';

// Define schema
const userSchema = z.object({
  id: z.string().uuid(),
  name: z.string().min(1).max(100),
  email: z.string().email(),
  age: z.number().int().min(0).max(150).optional(),
  role: z.enum(['admin', 'user', 'guest']),
  metadata: z.record(z.string(), z.unknown()).optional(),
  createdAt: z.string().datetime(),
});

// Infer TypeScript type from schema
type User = z.infer<typeof userSchema>;

// Validate data
const result = userSchema.safeParse(data);
if (result.success) {
  const user: User = result.data; // Fully typed
} else {
  console.error(result.error.issues);
}
```

### 3.3 API Boundary Validation
```typescript
// API route — validate input
import { z } from 'zod';

const createPostSchema = z.object({
  title: z.string().min(1).max(200),
  content: z.string().min(1),
  tags: z.array(z.string()).max(10).optional(),
  published: z.boolean().default(false),
});

export async function POST(request: Request) {
  const body = await request.json();

  const result = createPostSchema.safeParse(body);
  if (!result.success) {
    return Response.json(
      { error: 'Validation failed', details: result.error.issues },
      { status: 400 }
    );
  }

  const data = result.data; // Fully typed: { title, content, tags?, published }
  // Process...
}
```

### 3.4 Zod with React Hook Form
```typescript
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';

const formSchema = z.object({
  email: z.string().email('Enter a valid email'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
  confirmPassword: z.string(),
}).refine(data => data.password === data.confirmPassword, {
  message: 'Passwords do not match',
  path: ['confirmPassword'],
});

type FormData = z.infer<typeof formSchema>;

function RegistrationForm() {
  const { register, handleSubmit, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(formSchema),
  });

  const onSubmit = (data: FormData) => {
    // data is fully validated and typed
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <input {...register('email')} />
      {errors.email && <p>{errors.email.message}</p>}
      <input type="password" {...register('password')} />
      {errors.password && <p>{errors.password.message}</p>}
      <input type="password" {...register('confirmPassword')} />
      {errors.confirmPassword && <p>{errors.confirmPassword.message}</p>}
      <button type="submit">Register</button>
    </form>
  );
}
```

### 3.5 Zod with fetch/API Client
```typescript
// Typed API client with Zod validation
async function apiClient<T>(
  url: string,
  schema: z.ZodSchema<T>,
  options?: RequestInit
): Promise<T> {
  const response = await fetch(url, options);
  const data = await response.json();

  const result = schema.safeParse(data);
  if (!result.success) {
    throw new Error(`API response validation failed: ${result.error.message}`);
  }

  return result.data;
}

// Usage
const user = await apiClient('/api/users/123', userSchema);
// user is fully typed as User
```

---

## Part 4: Generic Patterns

### 4.1 Generic Functions
```typescript
// Generic with constraints
function getProperty<T, K extends keyof T>(obj: T, key: K): T[K] {
  return obj[key];
}

const user = { name: 'Alice', age: 30 };
const name = getProperty(user, 'name'); // string
const age = getProperty(user, 'age');   // number

// Generic with default
function createArray<T = string>(items: T[]): T[] {
  return [...items];
}
```

### 4.2 Generic Components (React)
```tsx
// Generic component with typed props
interface ListProps<T> {
  items: T[];
  renderItem: (item: T) => React.ReactNode;
  keyExtractor: (item: T) => string;
}

function List<T>({ items, renderItem, keyExtractor }: ListProps<T>) {
  return (
    <ul>
      {items.map(item => (
        <li key={keyExtractor(item)}>{renderItem(item)}</li>
      ))}
    </ul>
  );
}

// Usage — T is inferred from items
<List
  items={users}
  renderItem={(user) => <UserCard user={user} />}
  keyExtractor={(user) => user.id}
/>
```

### 4.3 Generic API Hooks
```typescript
// Generic data fetching hook with Zod validation
function useFetch<T>(url: string, schema: z.ZodSchema<T>) {
  const [state, setState] = useState<RequestState<T>>({ status: 'idle' });

  useEffect(() => {
    setState({ status: 'loading' });

    fetch(url)
      .then(res => res.json())
      .then(data => {
        const result = schema.safeParse(data);
        if (result.success) {
          setState({ status: 'success', data: result.data });
        } else {
          setState({ status: 'error', error: new Error('Validation failed') });
        }
      })
      .catch(error => setState({ status: 'error', error }));

  }, [url]);

  return state;
}
```

---

## Part 5: Type Narrowing

### 5.1 Type Guards
```typescript
// typeof guard
function process(value: string | number) {
  if (typeof value === 'string') {
    value.toUpperCase(); // string
  } else {
    value.toFixed(2); // number
  }
}

// instanceof guard
function handleError(error: Error | string) {
  if (error instanceof Error) {
    console.log(error.message); // Error
  } else {
    console.log(error); // string
  }
}

// in guard
interface Cat { meow(): void; }
interface Dog { bark(): void; }

function speak(animal: Cat | Dog) {
  if ('meow' in animal) {
    animal.meow(); // Cat
  } else {
    animal.bark(); // Dog
  }
}
```

### 5.2 Custom Type Guards
```typescript
function isError(value: unknown): value is Error {
  return value instanceof Error;
}

function isNonNull<T>(value: T | null | undefined): value is T {
  return value != null;
}

function isApiError(value: unknown): value is { code: string; message: string } {
  return (
    typeof value === 'object' &&
    value !== null &&
    'code' in value &&
    'message' in value &&
    typeof (value as any).code === 'string' &&
    typeof (value as any).message === 'string'
  );
}

// Usage with filter
const errors = results.filter(isError);
const validItems = items.filter(isNonNull);
```

### 5.3 Exhaustive Checking
```typescript
// Assert never for exhaustive checks
function assertNever(x: never): never {
  throw new Error(`Unexpected: ${x}`);
}

type Shape =
  | { kind: 'circle'; radius: number }
  | { kind: 'square'; size: number }
  | { kind: 'rectangle'; width: number; height: number };

function area(shape: Shape): number {
  switch (shape.kind) {
    case 'circle':
      return Math.PI * shape.radius ** 2;
    case 'square':
      return shape.size ** 2;
    case 'rectangle':
      return shape.width * shape.height;
    default:
      return assertNever(shape); // Compile error if new shape added
  }
}
```

---

## Part 6: Common Anti-Patterns

### 6.1 Never Use `any`
```typescript
// BAD
function process(data: any) {
  return data.users.map((u: any) => u.name);
}

// GOOD
function process(data: unknown) {
  const schema = z.object({
    users: z.array(z.object({ name: z.string() })),
  });
  const result = schema.safeParse(data);
  if (!result.success) throw new Error('Invalid data');
  return result.data.users.map(u => u.name);
}
```

### 6.2 Avoid Type Assertions (`as`)
```typescript
// BAD — unsafe assertion
const user = data as User;

// GOOD — validate first
const result = userSchema.safeParse(data);
if (!result.success) throw new Error('Invalid data');
const user: User = result.data;
```

### 6.3 Don't Use `!` Non-Null Assertion
```typescript
// BAD — assumes value is not null, crashes if it is
const name = user!.name!;

// GOOD — handle null explicitly
const name = user?.name ?? 'Unknown';

// Or use type guard
if (user && user.name) {
  const name = user.name;
}
```

### 6.4 Don't Use `enum` — Use Union Types
```typescript
// BAD — enums have runtime overhead and compatibility issues
enum Role {
  Admin = 'admin',
  User = 'user',
}

// GOOD — union types are zero-cost and tree-shakeable
type Role = 'admin' | 'user' | 'guest';
```

### 6.5 Avoid Empty Interfaces
```typescript
// BAD — empty interface
interface User extends Record<string, unknown> {}

// GOOD — use type alias
type User = {
  id: string;
  name: string;
  email: string;
};
```

### 6.6 Don't Use `Function` Type
```typescript
// BAD — too loose
function callback(fn: Function) { fn(); }

// GOOD — specific function type
function callback(fn: () => void) { fn(); }

// Or with parameters
function map<T, U>(arr: T[], fn: (item: T, index: number) => U): U[] {
  return arr.map(fn);
}
```

---

## Part 7: Advanced Patterns

### 7.1 Template Literal Types
```typescript
type EventName = 'click' | 'hover' | 'focus';
type EventHandler = `on${Capitalize<EventName>}`;
// 'onClick' | 'onHover' | 'onFocus'

type ApiEndpoint = `/api/${string}`;
const endpoint: ApiEndpoint = '/api/users';

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE';
type Route = `${HttpMethod} ${string}`;
const route: Route = 'GET /api/users';
```

### 7.2 Infer with Conditional Types
```typescript
// Extract return type of a function
type GetReturnType<T> = T extends (...args: never[]) => infer R ? R : never;

// Extract array element type
type GetArrayElement<T> = T extends (infer E)[] ? E : never;

// Extract object value type
type GetValue<T, K extends keyof T> = T[K];
```

### 7.3 Type-Safe Event Emitters
```typescript
interface EventMap {
  login: { userId: string; timestamp: number };
  logout: { userId: string };
  message: { from: string; text: string };
}

class TypedEventEmitter {
  private listeners: { [K in keyof EventMap]?: Set<(data: EventMap[K]) => void> } = {};

  on<K extends keyof EventMap>(event: K, handler: (data: EventMap[K]) => void) {
    if (!this.listeners[event]) this.listeners[event] = new Set();
    this.listeners[event]!.add(handler);
  }

  emit<K extends keyof EventMap>(event: K, data: EventMap[K]) {
    this.listeners[event]?.forEach(handler => handler(data));
  }
}
```

---

## Part 8: ESLint for TypeScript

### 8.1 Recommended ESLint Config
```javascript
// eslint.config.js
import tseslint from 'typescript-eslint';

export default tseslint.config(
  ...tseslint.configs.strict,
  ...tseslint.configs.stylistic,
  {
    rules: {
      '@typescript-eslint/no-explicit-any': 'error',
      '@typescript-eslint/no-non-null-assertion': 'error',
      '@typescript-eslint/no-unused-vars': ['error', { argsIgnorePattern: '^_' }],
      '@typescript-eslint/consistent-type-imports': 'error',
      '@typescript-eslint/no-floating-promises': 'error',
      '@typescript-eslint/no-misused-promises': 'error',
      '@typescript-eslint/await-thenable': 'error',
      '@typescript-eslint/no-unnecessary-type-assertion': 'error',
      '@typescript-eslint/prefer-nullish-coalescing': 'error',
      '@typescript-eslint/prefer-optional-chain': 'error',
      '@typescript-eslint/no-unsafe-assignment': 'error',
      '@typescript-eslint/no-unsafe-member-access': 'error',
      '@typescript-eslint/no-unsafe-call': 'error',
      '@typescript-eslint/no-unsafe-argument': 'error',
      '@typescript-eslint/no-unsafe-return': 'error',
    },
  },
);
```

---

## Execution Instructions for Cascade

When this skill is activated for type safety & TypeScript:

1. **Read the project context** — existing tsconfig, TypeScript version, strictness level
2. **Configure tsconfig.json** — enable all strict flags, `noUncheckedIndexedAccess`, `exactOptionalPropertyTypes`
3. **Set up Zod** — install, create schemas for all API boundaries, infer types from schemas
4. **Replace `any` with `unknown`** — then narrow with Zod or type guards
5. **Remove type assertions (`as`)** — use type guards, Zod validation, or proper types
6. **Remove `!` non-null assertions** — use optional chaining, nullish coalescing, or type guards
7. **Use discriminated unions** — for state machines, async states, error handling
8. **Use branded types** — for domain primitives (UserId, Email, ProductId)
9. **Set up ESLint** — `typescript-eslint` strict config, `no-explicit-any`, `no-unsafe-*` rules
10. **Add runtime validation** — Zod schemas at all boundaries (API, database, external services)
11. **Use utility types** — `Pick`, `Omit`, `Partial`, `Record` instead of duplicating types
12. **Test type safety** — `npm run typecheck` in CI, `tsc --noEmit` in pre-push hook
