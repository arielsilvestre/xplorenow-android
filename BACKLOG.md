# Backlog — XploreNow Android

> Tareas pendientes ordenadas por prioridad. Actualizar este archivo al completar cada ítem.

---

## 🔴 Alta prioridad (antes de la primera entrega ~28/04)

### [PR] Mergear PR #7 (UI redesign) ✅ LISTO PARA MERGEAR
- **URL:** https://github.com/arielsilvestre/xplorenow-android/pull/7

### [CRÍTICO] Migrar `SessionManager` → SharedPreferences (compliance con docente)
- **Qué:** Actualmente usamos DataStore + RxJava2. El profesor usa SharedPreferences simple en `TokenManager`. Los apuntes de clase 14/4 dicen explícitamente: *"no usamos DataStore por complejidad de implementación"*.
- **Cómo:** Reemplazar `SessionManager.java` por `TokenManager.java` con SharedPreferences sincrónico. Eliminar dependencias de DataStore y RxJava2.
- **Impacto:** Desbloquea todo lo biométrico y simplifica el interceptor de Retrofit.
- **Ref. docente:** rama `feature/storage` → `TokenManager.java`

### [CRÍTICO] Implementar flujo biométrico completo (compliance con docente)
El profesor tiene dos ramas para esto (`feature/biometric` + `feature/biometric-reauth`). Falta todo:
- **`BiometricFragment`** — standalone con verificación de disponibilidad (4 estados: success / no hardware / hw unavailable / none enrolled) + botón "Ir a ajustes" si no hay enrolamiento
- **Login bifurcado** — si `isBiometricEnabled()` → mostrar prompt biométrico; si no → formulario de credenciales
- **`isBiometricEnabled()` en TokenManager** — flag en SharedPreferences
- **`saveEncryptedToken()` / `getEncryptedToken()`** — token guardado en `EncryptedSharedPreferences` para uso post-biometría
- **AlertDialog post-login** — ofrecer activar biometría tras primer login exitoso con credenciales
- **Fallback a credenciales** — si el prompt da error → volver al formulario
- **Ref. docente:** ramas `feature/biometric` y `feature/biometric-reauth`

### [CRÍTICO] Implementar Room (compliance con docente + modo offline TPO)
- **Qué:** No existe Room en el proyecto. El profesor lo tiene en `feature/storage`. Además el TPO requiere modo offline (módulo 8).
- **Cómo:** Crear `AppDatabase.java`, entidades relevantes (reservas, actividades), DAOs, y `StorageModule.java` para Hilt. Usar `ExecutorService` para operaciones en background.
- **Ref. docente:** rama `feature/storage` → `AppDatabase`, `NoteDao`, `StorageModule`

### [VERIFICAR] Foto de perfil con `ActivityResultLauncher`
- **Qué:** Los permisos están declarados en el Manifest pero hay que confirmar que el flujo en `ProfileFragment` usa exactamente el patrón del docente.
- **Patrón correcto:** `ActivityResultContracts.RequestPermission()` + `ActivityResultContracts.GetContent()` + check `Build.VERSION.SDK_INT >= TIRAMISU` + guardar URI en SharedPreferences + Glide con `circleCrop()`
- **Ref. docente:** rama `feature/profile-image` → `ProfileImageFragment.java`

---

## 🟡 Funcionalidades TPO faltantes (módulos no implementados)

### [Feature] OTP — Autenticación por código de 6 dígitos
- Flujo: email → envío de código → confirmación → sesión
- Recupero: reenvío de OTP si no llegó o expiró
- **Módulo TPO:** 1 (Autenticación)

### [Feature] Perfil — Edición de datos y preferencias
- Editar nombre, email, teléfono, foto de perfil
- Seleccionar preferencias de viaje: aventura, cultura, gastronomía, naturaleza, relax
- Las preferencias personalizan la sección de recomendadas en Home
- **Módulo TPO:** 2 (Perfil del Viajero)

### [Feature] Catálogo — Filtros y recomendadas
- Filtros combinados: destino, categoría, fecha, rango de precio
- Paginado del listado
- Sección "Destacadas / Recomendadas" según preferencias del perfil
- Galería de fotos en el detalle de actividad
- **Módulo TPO:** 3 (Catálogo)

### [Feature] Reservas — Cancelación y validación de cupos
- Cancelar reserva con política de cancelación
- Validación de cupos disponibles en tiempo real al crear reserva
- **Módulo TPO:** 4 (Reservas)

### [Feature] Historial de actividades finalizadas
- Listado separado de actividades con estado "finalizada"
- Filtros por rango de fechas y destino
- Acceso a detalle y calificación desde el historial
- **Módulo TPO:** 5 (Historial)

### [Feature] Calificación de actividades y guías
- Ventana de 48hs post-actividad para calificar
- Estrellas 1–5 para actividad y guía por separado
- Comentario opcional (máx. 300 caracteres)
- Calificación visible en historial personal
- **Módulo TPO:** 6 (Calificaciones)

### [Feature] Favoritos y lista de deseos
- Marcar/desmarcar favorita desde catálogo o detalle (ícono corazón)
- Sección "Mis favoritos" con acceso rápido para reservar
- Indicador visual si favorita cambia de precio o libera cupos
- Persistencia entre sesiones
- **Módulo TPO:** 7 (Favoritos)

### [Feature] Modo sin conexión
- Vouchers y detalle de próximas actividades disponibles offline (Room)
- Guardado automático al confirmar reserva
- Sincronización automática al recuperar conexión
- Aviso visual de modo offline
- **Módulo TPO:** 8 (Offline) — **depende de Room**

### [Feature] Noticias, ofertas y destinos destacados
- Tab o sección en Home: novedades, descuentos, nuevos destinos, promociones
- Consumidas desde API externa: imagen, título, descripción breve
- Al tocar → detalle o actividad relacionada
- **Módulo TPO:** 9 (Noticias)

### [Feature] Mapa y punto de encuentro
- Mapa embebido en el detalle de la reserva (punto de encuentro)
- Botón "Cómo llegar" → abre Google Maps en modo navegación
- Para actividades con recorrido: puntos del itinerario en el mapa
- **Módulo TPO:** 10 (Mapa)

---

## 🟡 Media prioridad (si hay use cases asignados)

### [Feature] Implementar pantallas de Destinos
- **Qué:** `DestinationListFragment` y `DestinationDetailFragment` están vacíos (sin layout ni lógica).
- **Cómo:** Definir si son use cases asignados para la primera entrega. Si lo son, implementar con el mismo patrón que Actividades (ViewModel → Repository → ApiService).
- **Depende de:** Confirmar use cases asignados al equipo.

### [Feature] Implementar pantalla de Guías
- **Qué:** `GuideListFragment` está vacío.
- **Mismo criterio que Destinos.**

---

## 🟢 Baja prioridad (mejoras / deuda técnica)

### [Docs] Limpiar nota pendiente en `apuntes.md`
- **Qué:** Hay una nota `(?) => a chequear` sobre el NetworkModule. Ya está resuelto — el interceptor lee el token correctamente via `SessionManager` inyectado por Hilt.
- **Cómo:** Eliminar el `(?)` y confirmar el comportamiento en el apunte.

### [UI] Integrar logo de la app
- **Qué:** Login y Profile usan `@mipmap/ic_launcher` como placeholder.
- **Cómo:** Cuando esté disponible el logo (SVG o PNG 512px con fondo transparente), reemplazar en `fragment_login.xml` (`iv_logo`) y `fragment_profile.xml` (`iv_profile_photo` default).

---

_Última actualización: 2026-04-21_
