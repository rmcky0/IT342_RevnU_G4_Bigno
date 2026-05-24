# RevnU — Restaurant Sales Management System

**RevnU** is a restaurant management system built to digitize and streamline daily financial operations for food establishments. It replaces manual record-keeping (notebooks, spreadsheets) with a centralized platform where restaurateurs can record sales and expenses, manage staff payroll, compute profits automatically, and generate end-of-day email summaries — all secured behind JWT authentication and role-based access control.

The system consists of three client applications sharing a single Spring Boot REST API:
- **Web Application** — full management hub built with React 19 + Vite
- **Mobile Application** — Android Kotlin app for fast on-the-floor data entry
- **Backend API** — Spring Boot 4.x with PostgreSQL, deployed on Render

---

## Table of Contents

1. [Features](#features)
2. [User Roles](#user-roles)
3. [Technology Stack](#technology-stack)
4. [Repository Structure](#repository-structure)
5. [System Architecture](#system-architecture)
6. [API Reference](#api-reference)
7. [Database Schema](#database-schema)
8. [Web Application Routes](#web-application-routes)
9. [Mobile Application Screens](#mobile-application-screens)
10. [Environment Variables](#environment-variables)
11. [Setup and Installation](#setup-and-installation)
12. [Deployment](#deployment)
13. [Author](#author)

---

## Features

| Feature | Details |
|---|---|
| **Authentication** | Email/password login, Google OAuth 2.0, JWT access tokens (30 min) + refresh tokens (7 days), BCrypt password hashing |
| **Forgot Password** | OTP-based password reset flow via SMTP email |
| **Role-Based Access Control** | `ADMIN` and `RESTAURATEUR` roles enforced at Spring Security layer and UI level |
| **Admin Account Management** | Suspend or reactivate Restaurateur accounts; suspended accounts receive `403 Forbidden` on login |
| **Restaurant Setup** | Guided onboarding for restaurant profile: name, location, operating hours, logo upload, optional owner contact details |
| **Sales Tracking** | Full CRUD for daily sales with category tagging, date filtering, and pagination |
| **Expense Tracking** | Full CRUD for expenses with optional receipt file upload (image/PDF, max 5 MB) |
| **Category Management** | Predefined system-wide categories + restaurant-defined custom categories for both Sales and Expenses; predefined categories are protected from edit/delete |
| **Staff Management** | Internal staff directory (name, position, salary rate); staff are records, not system users |
| **Payroll** | Salary payouts recorded as labor expenses; automatically deducted from net profit |
| **End-of-Day (EOD) Closing** | Soft-close daily records to lock them; triggers automated SMTP email with daily financial summary |
| **Archived Reports** | View past EOD summaries and day-level detail; export to PDF or Excel |
| **Analytics Dashboard** | Revenue trends, expense breakdown by category, net profit charts with daily/weekly/monthly filters |
| **Philippine Holiday Integration** | Fetches public holiday data from Calendarific API; displays a holiday badge on the dashboard on matching dates |
| **Notifications** | In-app notification bell with unread count; system alerts and holiday notifications |
| **File Storage** | Supabase Storage (`revnu_files` bucket) for receipt images and restaurant logos |
| **Automated Email (SMTP)** | Welcome email on registration; daily financial summary on EOD close |

---

## User Roles

### Admin
- Platform-level oversight and user management
- Can view all Restaurateur accounts and their status (Active / Suspended)
- Can suspend or reactivate Restaurateur accounts
- Cannot record sales, expenses, or initiate EOD reports
- Seeded automatically on application startup via `AdminAccountSeeder`

### Restaurateur
- Registers and logs in as an account linked to a restaurant profile
- Full access to their own sales, expenses, staff, payroll, analytics, and settings
- Cannot access Admin endpoints
- Accounts can be suspended by Admin (blocks all subsequent requests with `403`)

---

## Technology Stack

### Backend

| Component | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.0.3 |
| Security | Spring Security + OAuth2 Client |
| Persistence | Spring Data JPA / Hibernate (PostgreSQL dialect) |
| Database | PostgreSQL 14+ |
| JWT | jjwt 0.12.6 (jjwt-api, jjwt-impl, jjwt-jackson) |
| File Storage | Supabase Storage (REST API) |
| Email | Spring Mail (Jakarta Mail) — Gmail SMTP port 587, STARTTLS |
| Validation | Spring Boot Starter Validation (Bean Validation) |
| Caching | Spring Boot Starter Cache |
| JSON | Jackson Databind |
| Environment | dotenv-java 3.1.0 |
| Build | Maven (Spring Boot Maven Plugin) |
| Containerization | Docker — multi-stage Alpine build (eclipse-temurin:17-jdk-alpine → eclipse-temurin:17-jre-alpine) |

### Web Frontend

| Component | Technology |
|---|---|
| Framework | React 19.2.0 |
| Build Tool | Vite 7.3.1 |
| Language | JavaScript (JSX) |
| Routing | React Router DOM 7.13.1 |
| HTTP Client | Axios 1.13.6 |
| Styling | Tailwind CSS 4.2.1 + PostCSS |
| Charts | Recharts 3.8.1 |
| Icons | Lucide React 0.576.0 |
| PDF Export | jsPDF 4.2.1 + jspdf-autotable 5.0.8 |
| Excel Export | xlsx 0.18.5 |
| Linting | ESLint 9.39.1 + eslint-plugin-react-hooks + eslint-plugin-react-refresh |

### Mobile (Android)

| Component | Technology |
|---|---|
| Language | Kotlin |
| Min SDK | 24 (Android 7.0 Nougat) |
| Target SDK | 34 (Android 14) |
| Compile SDK | 34 |
| UI | XML Views + ViewBinding |
| Architecture | MVVM (ViewModel + Repository) |
| HTTP Client | Retrofit 2 + OkHttp (with logging interceptor) |
| JSON | Gson converter |
| Image Loading | CircleImageView 3.1.0 |
| Navigation | Activity + Fragment + BottomSheet dialogs |
| Build | Gradle (Kotlin DSL) |

### Infrastructure

| Component | Technology |
|---|---|
| Backend Hosting | Render (Docker, Free plan, Singapore region) |
| Web Hosting | Vercel |
| Database | Supabase PostgreSQL |
| File Storage | Supabase Storage |
| External Holiday API | Calendarific (`https://calendarific.com/api/v2/holidays`) |
| Email | Gmail SMTP |

---

## Repository Structure

```text
IT342_RevnU_G4_Bigno/
│
├── backend/                        # Spring Boot REST API
│   ├── src/
│   │   └── main/
│   │       ├── java/com/revnu/backend/
│   │       │   ├── BackendApplication.java
│   │       │   ├── admin/           # Admin user management
│   │       │   ├── analytics/       # Revenue & profit analytics
│   │       │   ├── auth/            # Authentication, JWT, OAuth2
│   │       │   ├── categories/      # Sales & expense categories
│   │       │   ├── expenses/        # Expense tracking
│   │       │   ├── external/        # Philippine Holiday API integration
│   │       │   ├── files/           # File upload (Supabase Storage)
│   │       │   ├── notifications/   # In-app notifications
│   │       │   ├── reporting/       # EOD closing & daily summaries
│   │       │   ├── restaurants/     # Restaurant entity & repository
│   │       │   ├── sales/           # Sales transaction tracking
│   │       │   ├── settings/        # User & restaurant profile management
│   │       │   ├── staff/           # Staff directory & payroll
│   │       │   └── shared/
│   │       │       ├── config/      # Security, JPA, admin seeder, category seeder
│   │       │       ├── exception/   # GlobalExceptionHandler, ApiResponse
│   │       │       ├── security/    # JwtService, JwtAuthenticationFilter
│   │       │       └── util/        # DateFormatUtil (Asia/Manila), ResponseUtil
│   │       └── resources/
│   │           └── application.properties
│   ├── Dockerfile
│   └── pom.xml
│
├── web/                            # React web application
│   ├── src/
│   │   ├── app/App.jsx             # Root router
│   │   ├── features/
│   │   │   ├── admin/              # Admin dashboard, users, restaurants
│   │   │   ├── analytics/          # Charts, KPIs, holiday hook
│   │   │   ├── archive/            # EOD history list & detail
│   │   │   ├── auth/               # Login, register, OAuth, forgot-password
│   │   │   ├── categories/         # Category API calls
│   │   │   ├── dashboard/          # Dashboard shell & layout
│   │   │   ├── expenses/           # Expense entry & list
│   │   │   ├── external/           # Holiday service
│   │   │   ├── notifications/      # Notification bell
│   │   │   ├── reporting/          # EOD reporting hooks
│   │   │   ├── restaurant/         # Restaurant setup onboarding
│   │   │   ├── sales/              # Sales entry & list
│   │   │   ├── settings/           # Account & restaurant settings
│   │   │   └── staff/              # Staff directory & payroll
│   │   └── shared/
│   │       ├── api/axios.js        # Configured Axios instance
│   │       ├── cache/              # Client-side data cache
│   │       ├── components/         # Shared modals, tables, toasts
│   │       └── routes/             # ProtectedRoute, AdminRoute guards
│   ├── package.json
│   └── vite.config.js
│
├── mobile/                         # Android Kotlin app
│   └── app/src/main/java/com/revnu/mobile/
│       ├── core/
│       │   ├── config/             # Constants
│       │   ├── network/            # ApiService, RetrofitClient, SessionManager
│       │   └── ui/                 # WebRedirectActivity (OAuth flow)
│       └── features/
│           ├── auth/               # Login, Register, ForgotPassword activities
│           ├── dashboard/          # DashboardActivity (bottom nav host)
│           ├── analytics/          # AnalyticsFragment
│           ├── sales/              # SalesFragment, keypad entry
│           ├── expenses/           # ExpensesFragment, receipt capture
│           ├── staff/              # StaffFragment, SalaryFragment
│           ├── categories/         # CategoryPickerBottomSheet
│           ├── settings/           # SettingsFragment, ChangePasswordActivity
│           ├── restaurant/         # RestaurantSetupActivity
│           └── entry/              # AddRecordActivity
│
├── docs/                           # System Design Documents
├── render.yaml                     # Render.com deployment config
└── README.md
```

---

## System Architecture

RevnU follows a **three-tier, feature-modular layered architecture**.

```
┌─────────────────────────────────────────────────────────┐
│                     Clients                             │
│   React Web App (Vercel)   Android App (APK)            │
└────────────────────┬────────────────────────────────────┘
                     │ HTTPS / JSON
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Spring Boot REST API (Render)               │
│                                                         │
│  Controller Layer  ── HTTP endpoints, status codes      │
│  Service Layer     ── Business logic, external calls    │
│  Repository Layer  ── Spring Data JPA / PostgreSQL      │
│  DTO Layer         ── Prevents raw entity exposure      │
│  Security Layer    ── JWT filter, @PreAuthorize          │
└────────────┬──────────────────────┬────────────────────┘
             │                      │
             ▼                      ▼
┌────────────────────┐  ┌──────────────────────────────┐
│  PostgreSQL 14+    │  │  External Services           │
│  (Supabase)        │  │  · Supabase Storage (files)  │
│                    │  │  · Gmail SMTP (email)        │
└────────────────────┘  │  · Google OAuth 2.0          │
                         │  · Calendarific (holidays)  │
                         └──────────────────────────────┘
```

### Backend Layers (per feature module)

| Layer | Responsibility |
|---|---|
| `Controller` | Maps HTTP verbs and paths; delegates to Service; returns `ApiResponse<T>` |
| `Service` | Business logic, validation, external API calls, email triggers |
| `Repository` | `JpaRepository` extensions for PostgreSQL queries |
| `Model / Entity` | JPA-annotated POJOs mapped to database tables |
| `DTO` | Request/response shapes that never expose sensitive fields (e.g., `password_hash`) |

### Security Flow

```
Request → JwtAuthenticationFilter
            → validates Bearer token (jjwt 0.12.6)
            → checks token blacklist (logout invalidation)
            → checks account status (SUSPENDED → 403)
            → sets SecurityContext
          → Spring Security @PreAuthorize
            → enforces ADMIN / RESTAURATEUR role
          → Controller
```

### Seeded Data on Startup

- **`AdminAccountSeeder`** — creates the default Admin account (`admin@revnu.com`) if it does not already exist
- **`CategorySeeder`** — inserts predefined Sales and Expense categories if the table is empty

---

## API Reference

**Base URL:** `https://<host>/api/v1`  
**Auth Header:** `Authorization: Bearer <access_token>`  
**Content-Type:** `application/json`

**Standard Response Envelope:**
```json
{
  "success": true,
  "data": { ... },
  "error": {
    "code": "AUTH-001",
    "message": "Invalid credentials",
    "details": null
  },
  "timestamp": "2026-05-24T10:00:00"
}
```

### Authentication

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/auth/register` | None | Register a new Restaurateur account |
| `POST` | `/auth/login` | None | Login; returns access + refresh tokens |
| `POST` | `/auth/refresh` | None | Exchange refresh token for new access token |
| `POST` | `/auth/logout` | Bearer | Invalidate current token |
| `GET` | `/auth/me` | Bearer | Get authenticated user info |
| `POST` | `/auth/forgot-password` | None | Initiate OTP-based password reset |
| `POST` | `/auth/verify-otp` | None | Verify OTP code |
| `POST` | `/auth/reset-password` | None | Set new password after OTP verification |
| `POST` | `/auth/link-google` | Bearer | Link Google OAuth to existing account |

**Register request:**
```json
{ "email": "owner@restaurant.com", "password": "Str0ng!Pass", "restaurantName": "My Restaurant" }
```

**Login response (data field):**
```json
{ "user": { "email": "...", "role": "RESTAURATEUR" }, "accessToken": "...", "refreshToken": "..." }
```

### Sales

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/sales` | Bearer (Restaurateur) | Record a new sale |
| `GET` | `/sales` | Bearer (Restaurateur) | List sales (paginated; optional `?date=`, `?page=`, `?size=`) |
| `GET` | `/sales/date` | Bearer (Restaurateur) | Sales for a specific `?date=YYYY-MM-DD` |
| `PUT` | `/sales/{id}` | Bearer (Restaurateur) | Update a sale |
| `DELETE` | `/sales/{id}` | Bearer (Restaurateur) | Delete a sale (blocked if day is closed) |

### Expenses

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/expenses` | Bearer (Restaurateur) | Record a new expense |
| `GET` | `/expenses` | Bearer (Restaurateur) | List expenses (paginated; optional `?page=`, `?size=`) |
| `PUT` | `/expenses/{id}` | Bearer (Restaurateur) | Update an expense |
| `DELETE` | `/expenses/{id}` | Bearer (Restaurateur) | Delete an expense |
| `POST` | `/expenses/{id}/upload` | Bearer (Restaurateur) | Upload receipt image/PDF (`multipart/form-data`) |

### Categories

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/categories` | Bearer (Restaurateur) | List all categories (`?type=SALES` or `?type=EXPENSE`) |
| `POST` | `/categories` | Bearer (Restaurateur) | Create a custom category |
| `PUT` | `/categories/{id}` | Bearer (Restaurateur) | Rename a custom category (403 if predefined) |
| `DELETE` | `/categories/{id}` | Bearer (Restaurateur) | Delete a custom category (409 if in use) |

**Predefined Sales categories:** Meals, Drinks, Desserts, Catering, Take-out, Others  
**Predefined Expense categories:** Ingredients / Supplies, Utilities, Equipment, Maintenance, Others

### Staff & Payroll

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/staff` | Bearer (Restaurateur) | List all staff |
| `GET` | `/staff/{id}` | Bearer (Restaurateur) | Get single staff member |
| `POST` | `/staff` | Bearer (Restaurateur) | Create a staff record |
| `PUT` | `/staff/{id}` | Bearer (Restaurateur) | Update a staff record |
| `DELETE` | `/staff/{id}` | Bearer (Restaurateur) | Remove a staff record |
| `POST` | `/salaries` | Bearer (Restaurateur) | Record a salary payout |
| `GET` | `/salaries` | Bearer (Restaurateur) | List all payouts (paginated) |
| `GET` | `/salaries/staff/{staffId}` | Bearer (Restaurateur) | Payouts for a specific staff member |
| `PUT` | `/salaries/{id}` | Bearer (Restaurateur) | Update a payout record |
| `DELETE` | `/salaries/{id}` | Bearer (Restaurateur) | Delete a payout record |

### End-of-Day Reporting

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/day/close` | Bearer (Restaurateur) | Soft-close day; locks records; sends SMTP summary email |
| `GET` | `/day/summary/{date}` | Bearer (Restaurateur) | Get EOD summary for a date |
| `GET` | `/day/summaries` | Bearer (Restaurateur) | List all archived summaries |
| `GET` | `/day/detail/{date}` | Bearer (Restaurateur) | Full day detail (sales + expenses + payroll breakdown) |

### Analytics

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/analytics/daily` | Bearer (Restaurateur) | Daily stats (`?date=YYYY-MM-DD`) |
| `GET` | `/analytics/trends` | Bearer (Restaurateur) | Profit trends (`?period=daily\|weekly\|monthly`) |

### Settings

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/settings/profile` | Bearer | Get user profile |
| `PUT` | `/settings/profile` | Bearer | Update user profile |
| `PATCH` | `/settings/profile/avatar` | Bearer | Upload profile avatar (multipart) |
| `PUT` | `/settings/change-password` | Bearer | Change password |
| `GET` | `/settings/restaurant` | Bearer (Restaurateur) | Get restaurant profile |
| `PUT` | `/settings/restaurant` | Bearer (Restaurateur) | Update restaurant profile |
| `PATCH` | `/settings/restaurant/logo` | Bearer (Restaurateur) | Upload restaurant logo (multipart) |
| `POST` | `/settings/restaurant` | Bearer (Restaurateur) | Initial restaurant setup (multipart: data + logo) |
| `GET` | `/settings/system` | Bearer | Get system settings |
| `PUT` | `/settings/system` | Bearer | Update system settings |

### Admin

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/admin/stats` | Bearer (Admin) | Platform-level statistics |
| `GET` | `/admin/users` | Bearer (Admin) | List all Restaurateur accounts |
| `PUT` | `/admin/users/{id}/status` | Bearer (Admin) | Update account status (ACTIVE / SUSPENDED) |
| `PUT` | `/admin/users/{id}/role` | Bearer (Admin) | Change user role |
| `DELETE` | `/admin/users/{id}` | Bearer (Admin) | Delete a user account |
| `GET` | `/admin/restaurants` | Bearer (Admin) | List all registered restaurants |

### Notifications

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/notifications` | Bearer | Get notifications (`?limit=N`) |
| `GET` | `/notifications/unread-count` | Bearer | Get unread notification count |
| `PATCH` | `/notifications/{id}/read` | Bearer | Mark a notification as read |
| `PATCH` | `/notifications/read-all` | Bearer | Mark all notifications as read |

### External

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/external/holidays` | Bearer | Fetch Philippine holidays (`?year=YYYY`) |

### Error Codes

| Code | Meaning |
|---|---|
| `AUTH-001` | Invalid credentials |
| `AUTH-002` | Token expired |
| `AUTH-003` | Insufficient permissions |
| `AUTH-004` | Account suspended |
| `VALID-001` | Validation failed |
| `DB-001` | Resource not found |
| `DB-002` | Duplicate entry |
| `CATEGORY-001` | Cannot modify a predefined category |
| `CATEGORY-002` | Category is in use by existing records |
| `SYSTEM-001` | Internal server error |

---

## Database Schema

PostgreSQL 14+ via Supabase. Hibernate DDL mode: `update`.

### `users`

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGSERIAL PK` | Auto-generated |
| `email` | `VARCHAR UNIQUE` | Login identifier |
| `password_hash` | `VARCHAR` | BCrypt-hashed; `null` for OAuth-only accounts |
| `role` | `ENUM(ADMIN, RESTAURATEUR)` | Role-based access control |
| `status` | `ENUM(ACTIVE, SUSPENDED)` | Admin can toggle |
| `oauth_id` | `VARCHAR` | Google OAuth subject ID; nullable |
| `created_at` | `TIMESTAMP` | Account creation time |

### `restaurants`

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGSERIAL PK` | |
| `name` | `VARCHAR` | Restaurant display name |
| `logo_file_id` | `BIGINT FK → files` | Nullable |
| `physical_location` | `VARCHAR` | Address |
| `opening_hrs` | `TIME` | Daily open time |
| `closing_hrs` | `TIME` | Daily close time |
| `owner_name` | `VARCHAR` | Optional owner display name |
| `owner_email` | `VARCHAR` | Optional owner contact email |
| `owner_phone` | `VARCHAR` | Optional owner phone |
| `user_id` | `BIGINT FK → users` | One-to-one link to login account |

### `categories`

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGSERIAL PK` | |
| `name` | `VARCHAR` | e.g., `Meals`, `Utilities` |
| `type` | `ENUM(SALES, EXPENSE)` | Scopes to correct module |
| `restaurant_id` | `BIGINT FK → restaurants NULLABLE` | `NULL` = predefined/shared |
| `is_default` | `BOOLEAN DEFAULT false` | `true` = system-defined; protected from edit/delete |
| `created_at` | `TIMESTAMP` | |

### `sales`

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGSERIAL PK` | |
| `amount` | `NUMERIC` | Sale value |
| `category_id` | `BIGINT FK → categories` | Exactly one required |
| `description` | `VARCHAR` | Optional note |
| `restaurant_id` | `BIGINT FK → restaurants` | Data isolation |
| `status` | `ENUM(OPEN, CLOSED)` | Locked to `CLOSED` after EOD |
| `created_at` | `TIMESTAMP` | |

### `expenses`

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGSERIAL PK` | |
| `amount` | `NUMERIC` | Expense value |
| `category_id` | `BIGINT FK → categories` | Exactly one required |
| `description` | `VARCHAR` | Optional note |
| `restaurant_id` | `BIGINT FK → restaurants` | |
| `file_id` | `BIGINT FK → files NULLABLE` | Linked receipt |
| `status` | `ENUM(OPEN, CLOSED)` | |
| `created_at` | `TIMESTAMP` | |

### `staff`

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGSERIAL PK` | |
| `full_name` | `VARCHAR` | |
| `position` | `VARCHAR` | Job title |
| `salary_rate` | `NUMERIC` | Daily/monthly rate |
| `restaurant_id` | `BIGINT FK → restaurants` | |

### `salaries`

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGSERIAL PK` | |
| `amount` | `NUMERIC` | Payout amount |
| `payment_date` | `DATE` | |
| `staff_id` | `BIGINT FK → staff` | |
| `restaurant_id` | `BIGINT FK → restaurants` | |

### `files`

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGSERIAL PK` | |
| `filename` | `VARCHAR` | Original file name |
| `filepath` | `VARCHAR` | Supabase Storage path/URL |
| `filetype` | `VARCHAR` | MIME type |
| `uploaded_at` | `TIMESTAMP` | |

### `daily_summaries`

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGSERIAL PK` | |
| `report_date` | `DATE` | The closed day |
| `total_sales` | `NUMERIC` | |
| `total_expenses` | `NUMERIC` | |
| `total_salaries` | `NUMERIC` | |
| `net_profit` | `NUMERIC` | `total_sales − total_expenses − total_salaries` |
| `generated_at` | `TIMESTAMP` | |
| `restaurant_id` | `BIGINT FK → restaurants` | |

### Key Relationships

```
users          ←1:1→  restaurants
restaurants    ←1:N→  categories   (custom; predefined have restaurant_id = NULL)
restaurants    ←1:N→  sales
restaurants    ←1:N→  expenses
restaurants    ←1:N→  staff
restaurants    ←1:N→  daily_summaries
staff          ←1:N→  salaries
expenses       ←1:1→  files        (optional receipt)
restaurants    ←1:1→  files        (optional logo)
```

---

## Web Application Routes

| Path | Component | Access |
|---|---|---|
| `/` | `Welcome` | Public |
| `/login` | `Login` | Public |
| `/register` | `Register` | Public |
| `/auth/callback` | `AuthCallback` | Public (OAuth redirect) |
| `/auth/link-google` | `LinkGoogle` | Public |
| `/suspended` | `Suspended` | Public |
| `/forgot-password` | `ForgotPassword` | Public |
| `/setup-restaurant` | `SetupRestaurant` | Authenticated |
| `/dashboard` | `Analytics` (inside `Dashboard` shell) | Authenticated |
| `/sales` | `Sales` | Authenticated |
| `/expenses` | `Expenses` | Authenticated |
| `/staff` | `Staff` | Authenticated |
| `/staff/:staffId` | `StaffProfile` | Authenticated |
| `/settings` | `Settings` | Authenticated |
| `/archived` | `ArchivedList` | Authenticated |
| `/archived/:date` | `ArchivedDetail` | Authenticated |
| `/admin` | `AdminHome` | Admin only |
| `/admin/users` | `AdminUsers` | Admin only |
| `/admin/restaurants` | `AdminRestaurants` | Admin only |

Route guards: `ProtectedRoute` (checks JWT + restaurant setup), `AdminRoute` (checks `ADMIN` role).

---

## Mobile Application Screens

| Screen | Type | Description |
|---|---|---|
| `LoginActivity` | Activity | Email/password login |
| `RegisterActivity` | Activity | New account registration |
| `WelcomeActivity` | Activity | Splash / entry point |
| `ForgotPasswordActivity` | Activity | OTP-based password reset |
| `RestaurantSetupActivity` | Activity | First-time restaurant onboarding |
| `DashboardActivity` | Activity | Bottom navigation host |
| `SalesFragment` | Fragment | Sales list + fast keypad entry |
| `ExpensesFragment` | Fragment | Expenses list + camera receipt capture |
| `StaffFragment` | Fragment | Staff directory |
| `AnalyticsFragment` | Fragment | Daily analytics summary |
| `SettingsFragment` | Fragment | Account and restaurant settings |
| `AddRecordActivity` | Activity | Unified add-entry screen (sales/expenses) |
| `CategoryPickerBottomSheet` | BottomSheet | Category selector for entries |
| `StaffFormBottomSheet` | BottomSheet | Add/edit staff |
| `SalaryFormBottomSheet` | BottomSheet | Record salary payout |
| `StaffDetailBottomSheet` | BottomSheet | View staff details |
| `SalaryDetailBottomSheet` | BottomSheet | View payout details |
| `ExpenseDetailBottomSheet` | BottomSheet | View expense details |
| `SaleDetailBottomSheet` | BottomSheet | View sale details |
| `ChangePasswordActivity` | Activity | Password change flow |

**Permissions required (AndroidManifest):** Internet, Camera (receipt capture)  
**Token storage:** `SharedPreferences` via `SessionManager`  
**Token refresh:** OkHttp interceptor auto-refreshes access token using `Call<>` (non-suspend) refresh endpoint

---

## Environment Variables

All environment variables are injected at runtime. The `application.properties` file defines them with fallback defaults for local development.

| Variable | Required | Default (local dev) | Description |
|---|---|---|---|
| `DB_URL` | Yes | `jdbc:postgresql://localhost:5432/postgres` | PostgreSQL JDBC URL |
| `DB_USERNAME` | Yes | `postgres` | DB username |
| `DB_PASSWORD` | Yes | `password` | DB password |
| `JWT_SECRET` | Yes | Long fallback string | Base64 HMAC-SHA secret (must be ≥256 bits) |
| `JWT_EXPIRATION` | No | `1800000` (30 min) | Access token TTL in milliseconds |
| `JWT_REFRESH_EXPIRATION` | No | `604800000` (7 days) | Refresh token TTL in milliseconds |
| `GOOGLE_CLIENT_ID` | Yes | `missing-client-id` | Google OAuth 2.0 client ID |
| `GOOGLE_CLIENT_SECRET` | Yes | `missing-secret` | Google OAuth 2.0 client secret |
| `SMTP_HOST` | No | `smtp.gmail.com` | SMTP server host |
| `SMTP_PORT` | No | `587` | SMTP port (STARTTLS) |
| `SMTP_USER` | Yes | `missing@email.com` | Gmail address |
| `SMTP_PASS` | Yes | `missing-password` | Gmail app password |
| `ADMIN_EMAIL` | No | `admin@revnu.com` | Seed admin email |
| `ADMIN_PASS` | No | `Admin@1234!` | Seed admin password |
| `HOLIDAY_API_KEY` | Yes | `missing-key` | Calendarific API key |
| `SUPABASE_URL` | Yes | `https://placeholder.supabase.co` | Supabase project URL |
| `SUPABASE_KEY` | Yes | `missing-key` | Supabase anon/service key |
| `FRONTEND_URL` | No | `http://localhost:5173` | Web app URL (used for OAuth redirect CORS) |

> All secrets are configured in the Render dashboard (`sync: false`) and must never be committed to version control.

---

## Setup and Installation

### Prerequisites

- JDK 17
- Maven 3.9+ (or use `./mvnw`)
- Node.js 18+ and npm
- Android Studio Hedgehog (2023.1) or newer
- PostgreSQL 14+ instance **or** a free Supabase project

---

### 1. Backend

```bash
cd backend
```

Create a `.env` file or set environment variables, then run:

```bash
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080/api/v1`.

On first startup:
- Hibernate auto-creates/updates tables (`ddl-auto=update`)
- `AdminAccountSeeder` inserts the default admin account
- `CategorySeeder` inserts predefined Sales and Expense categories

To build a production JAR:

```bash
./mvnw package -DskipTests
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

To build and run via Docker:

```bash
docker build -t revnu-backend .
docker run -p 8080:8080 --env-file .env revnu-backend
```

---

### 2. Web

```bash
cd web
npm install
npm run dev
```

The app starts at `http://localhost:5173`. API calls are proxied to `http://localhost:8080` via Vite's dev proxy configured in `vite.config.js`.

To build for production:

```bash
npm run build
# Output: web/dist/
```

---

### 3. Mobile

1. Open the `mobile/` directory in **Android Studio**.
2. Update the base URL in `RetrofitClient.kt` to point to your backend (local or Render URL).
3. Sync Gradle and resolve dependencies.
4. Run on a **physical device or emulator** with API Level 24+.

To generate a release APK: **Build → Generate Signed Bundle/APK → APK**.

---

## Deployment

### Backend (Render)

The `render.yaml` at the project root configures the Render service:

```yaml
services:
  - type: web
    name: revnu-backend
    runtime: docker
    dockerfilePath: ./backend/Dockerfile
    dockerContext: ./backend
    plan: free
    region: singapore
    healthCheckPath: /api/v1/actuator/health
```

Set all `sync: false` env vars in the Render dashboard before the first deploy. The Docker image uses a two-stage Alpine build — the final image runs as a non-root user (`revnu`) for security.

### Web (Vercel)

1. Connect the repository to Vercel.
2. Set the **Root Directory** to `web`.
3. Build command: `npm run build` — Output: `dist`.
4. Add a `VITE_API_BASE_URL` environment variable pointing to the Render backend URL.
5. The production web app is live at `https://revnu-bigno.vercel.app`.

### Database & Storage (Supabase)

- Create a Supabase project and copy the connection string into `DB_URL`.
- Create a storage bucket named `revnu_files` and copy the project URL and anon key into `SUPABASE_URL` and `SUPABASE_KEY`.

---

## Author

**Richemmae Valle Bigno**  
Section: G4 | Course: IT342 — System Integration and Architecture  
Email: richem.miccah.bigno@gmail.com
