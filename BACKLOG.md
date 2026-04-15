# Backlog — XploreNow Android

> Tareas pendientes ordenadas por prioridad. Actualizar este archivo al completar cada ítem.

---

## 🔴 Alta prioridad (antes de la primera entrega ~28/04)

### [UI] Rediseñar `fragment_reservation_create.xml`
- **Qué:** Aplicar el mismo estilo MD3 Nature & Adventure al formulario de nueva reserva. Actualmente quedó con el estilo viejo.
- **Cómo:** Header de color primario + card blanca con los campos. Mismo patrón que login/register.
- **Branch sugerida:** `feature/ui-redesign-md3` (agregar antes de mergear el PR #7)

### [Java] Colorear barra de acento en `ReservationAdapter`
- **Qué:** `item_reservation.xml` tiene un `view_status_bar` (5dp a la izquierda) que debe colorearse según el estado de la reserva.
- **Cómo:** En el adapter, según `reservation.getStatus()`:
  - `"confirmed"` → `@color/status_confirmed` (verde)
  - `"pending"` → `@color/status_pending` (ámbar)
  - `"cancelled"` → `@color/status_cancelled` (rojo)
- **Archivo:** `ReservationAdapter.java` (o donde se bindee el item)

### [PR] Mergear PR #7 (UI redesign)
- **Qué:** El redesign de UI está en `feature/ui-redesign-md3` sin mergear. Bloquea el trabajo del equipo sobre la versión actualizada.
- **URL:** https://github.com/arielsilvestre/xplorenow-android/pull/7

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

_Última actualización: 2026-04-14_
