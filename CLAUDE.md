# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project: XploreNow

Academic TPO for "Desarrollo de Aplicaciones I" at UADE (Buenos Aires, Argentina). XploreNow is a tourism activity and experience booking platform (tours, guided visits, free tours, excursions, local experiences). Full spec: `TPO/consigna_TPO_XploreNow.pdf`. Slide decks per class are in `ppts/` — code should align with what is taught each week.

## Scope actual

**FASE ACTIVA: Android + Java únicamente.**
No escribir, planificar ni sugerir código React Native hasta que el usuario lo indique explícitamente. Cuando el usuario diga "empezamos con React Native", recién ahí se activa esa fase.

## Repository Structure

Three separate repositories (code should not be mixed):
- `xplorenow-api` — REST API backend, deployed on Railway (Hobby plan)
- `xplorenow-android` — Android Native app (Java, Android Studio)
- `xplorenow-rn` — React Native app (JavaScript, Expo)

This folder (`Aplicaciones/`) holds course materials only and is the parent workspace context.

## Backend

- **Local path:** `C:/Users/Pugliese/Documents/GitHub/xplorenow-api` (also on GitHub: https://github.com/arielsilvestre/xplorenow-api)
- **Deployment:** Railway Hobby plan, synced from a dedicated GitHub repo (`xplorenow-api`)
- **Database:** PostgreSQL (Railway managed instance)
- **Framework:** Node.js + Express
- **ORM:** Sequelize (with PostgreSQL dialect)
- **Auth:** JWT via `jsonwebtoken` + `bcrypt` for password hashing
- **Style:** RESTful, JSON responses, versioned routes (`/api/v1/`)
- **Key packages:** `express`, `sequelize`, `pg`, `jsonwebtoken`, `bcrypt`, `dotenv`, `cors`

**Project structure for the backend:**
```
xplorenow-api/
├── src/
│   ├── config/        # DB connection, environment config
│   ├── models/        # Sequelize models
│   ├── routes/        # Express route definitions
│   ├── controllers/   # Request handlers
│   ├── services/      # Business logic
│   ├── middlewares/   # Auth, error handling, validation
│   └── index.js       # Entry point
├── .env               # Never commit — use Railway env vars
└── package.json
```

Design the API to be consumed by both Android and React Native clients. Anticipate these domains from the TPO:
- Activities / experiences
- Destinations
- Tour guides
- Availability & quotas
- Pricing
- Reservations

## Android Native App (Java)

Developed in Android Studio. Code must match concepts taught in class each week.

**Architecture:** MVVM (Model-View-ViewModel) with LiveData / ViewModel from Jetpack.

**Key layers to implement progressively:**
- Week 2: Activity lifecycle, basic screens
- Week 3: Navigation via Intents and Fragments
- Week 4: Data flow between components (ViewModel, Repository pattern)
- Week 5: DataStore for local persistence (user session, preferences)
- Week 6: Biometric authentication (BiometricPrompt API)
- Week 7: Photo gallery / camera access
- Week 8: Material Design 3, ConstraintLayout

**Libraries to use (aligned with course):**
- Jetpack Navigation Component (Fragments)
- DataStore (Preferences) — not SharedPreferences
- Retrofit + OkHttp for REST API calls
- Glide for image loading
- Material Design Components (MDC-Android)
- BiometricPrompt for biometrics

## React Native App (JavaScript + Expo)

**Architecture:** Component-based. Use Context API or Redux for state management (week 12).

**Key layers to implement progressively:**
- Week 10: Expo setup, basic components, navigation (React Navigation)
- Week 11: JWT authentication flow
- Week 12: State management (Context API or Redux Toolkit)
- Week 13: NativeBase for UI components
- Week 14: Biometric authentication (expo-local-authentication)
- Week 15: Photo gallery (expo-image-picker)

**Advanced features (weeks 17-18, added to whichever tech is chosen for final delivery):**
- QR code scanning (expo-barcode-scanner or react-native-vision-camera)
- Google Maps integration (react-native-maps)
- Push Notifications (Firebase Cloud Messaging / expo-notifications)

## Team & Git Workflow (Required by TPO)

The team has 2 members currently (target: 5). **The professor evaluates individual contribution via Pull Requests** — this is part of the grade, not optional.

Rules — always follow these without exception:
- Branch naming: `feature/<description>` or `fix/<description>`
- **No direct pushes to `main`** — all changes must go through Pull Requests
- Every PR must be linked to a specific team member (committed from their account)
- Minimum 1 merged PR per team member per delivery
- When generating code for a specific person, remind them to commit and open the PR from their own GitHub account
- PRs should have a clear title and short description of what was implemented

## Flexibility Principles

The course adds new requirements each week. When implementing features:
- Keep layers decoupled (Repository, ViewModel/Service, UI) so new features slot in without rewriting existing code
- Avoid hardcoding anything that the backend might provide dynamically
- When a new course topic is introduced (e.g., biometrics, maps), implement it as an additive layer — do not refactor working code unless necessary
- Always check `ppts/` for the latest class material before implementing a new feature; the implementation should reflect what was taught

## Delivery Schedule

| Delivery | Date | Content |
|----------|------|---------|
| First    | ~28/04 | Backend API + Android Native (Java) — assigned use cases at 100% |
| Second   | ~16/06 | Backend API + React Native (Expo) — assigned use cases at 100% |
| Third    | ~21/07 | Full app in one technology (100% of all use cases) |
