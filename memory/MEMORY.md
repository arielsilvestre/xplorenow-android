# XploreNow Android — Project Memory

## Repos
- Android: `C:/Users/Pugliese/Documents/GitHub/xplorenow-android` (this repo)
- API: `C:/Users/Pugliese/Documents/GitHub/xplorenow-api` (Node/Express, GitHub: https://github.com/arielsilvestre/xplorenow-api)

## Context
- Academic TPO for "Desarrollo de Aplicaciones I" at UADE
- Standalone repo: `xplorenow-android` (Java, Android Studio)
- Full spec: `TPO/consigna_TPO_XploreNow.pdf`
- Class slides: `ppts/` (3 files as of session 2026-03-30)
- First delivery: ~28/04 — Backend API + Android Native (Java)
- GitHub user: arielsilvestre (único integrante activo)
- Casos de uso asignados: Login + Registro + Actividades + Reservas

## Implementation Status (2026-03-30)
All 6 phases implemented, but login flow has a pending bug (see debugging.md):
- ✅ Fase 1: Auth flow (LoginFragment, RegisterFragment, AuthViewModel, AuthRepository)
- ✅ Fase 2: DataStore session (SessionManager con RxDataStore, auto-login, logout, ProfileFragment)
- ✅ Fase 3: Activities module (TourActivity, ActivityRepository, ActivityViewModel, ActivityAdapter, 3 layouts)
- ✅ Fase 4: Reservations module (Reservation, ReservationRepository, ReservationViewModel, ReservationAdapter, 3 layouts)
- ✅ Fase 5: Biometrics (BiometricPrompt en LoginFragment, semana 6)
- ✅ Fase 6: Photo gallery (ActivityResultLauncher en ProfileFragment, semana 7)
- ✅ HomeFragment implemented with navigation cards
- 🔴 BUG: "Error al guardar sesión" after successful login (HTTP 200) — see debugging.md

## Architecture Decisions
- MVVM with LiveData/ViewModel (Jetpack)
- DataStore (RxDataStore via `datastore-preferences-rxjava2:1.1.3`) for JWT persistence
- `SessionManager` singleton: persists token+user, provides in-memory cache for AuthInterceptor
- AuthInterceptor in ApiClient: reads `SessionManager.getCachedToken()` synchronously
- Retrofit + OkHttp for API calls (BASE_URL: https://xplorenow-api-production.up.railway.app/)
- Jetpack Navigation Component (Fragments, SafeArgs)
- Material Design 3 + ConstraintLayout

## Key Classes
- `data/local/SessionManager.java` — RxDataStore wrapper, token cache
- `data/remote/ApiClient.java` — Retrofit singleton + AuthInterceptor
- `data/remote/ApiService.java` — endpoints: auth + activities + reservations
- `data/model/TourActivity.java` — renamed to avoid android.app.Activity collision
- `util/Resource.java` — generic state wrapper (LOADING/SUCCESS/ERROR)

## Nav Graph Actions
- `action_login_to_home` — login → home (popUpTo inclusive)
- `action_login_to_register` — login → register
- `action_register_to_login` — register → login (popUpTo inclusive)
- `action_activityList_to_detail` — activity list → detail
- `action_activityDetail_to_reservationCreate` — detail → create reservation
- `action_reservationList_to_create` — reservation list → create
- `action_profile_to_login` — profile → login (popUpTo nav_graph inclusive)

## Dependencies
- `lifecycle-viewmodel/livedata/runtime/process:2.7.0` (must stay at 2.7.0 — 2.8.x incompatible with navigation-fragment:2.7.7)
- `datastore-preferences:1.1.3` + `datastore-preferences-rxjava2:1.1.3`
- `rxandroid:2.1.1`
- `navigation-fragment/ui:2.7.7`
- `biometric:1.2.0-alpha05`

## Git Workflow
- No direct pushes to main — all via PRs from feature/* or fix/* branches
- PR naming: feature/auth-flow, feature/session-datastore, feature/activities-module, feature/reservations-module, feature/biometric-login, feature/photo-gallery
- Each PR must be committed from arielsilvestre GitHub account

## Backend API Status (2026-03-30)
- Implemented: POST /auth/register, POST /auth/login, GET /auth/me
- Models defined but routes NOT yet implemented: activities, reservations, destinations, guides
- Android app expects: GET /api/v1/activities, GET /api/v1/activities/{id}, GET /api/v1/reservations/me, POST /api/v1/reservations

## See Also
- `memory/debugging.md` — historial de bugs resueltos y pendientes
