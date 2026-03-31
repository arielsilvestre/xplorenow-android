# XploreNow Android — Debugging History

## Bugs Resueltos

### 1. App crash on startup — NoClassDefFoundError: ReportFragment$ActivityInitializationListener
- **Causa:** `datastore-rxjava2` jalaba una versión vieja de `lifecycle-runtime`
- **Fix:** Fijar explícitamente todas las dependencias lifecycle a `2.7.0`:
  ```groovy
  implementation 'androidx.lifecycle:lifecycle-viewmodel:2.7.0'
  implementation 'androidx.lifecycle:lifecycle-livedata:2.7.0'
  implementation 'androidx.lifecycle:lifecycle-runtime:2.7.0'
  implementation 'androidx.lifecycle:lifecycle-process:2.7.0'
  ```
- **IMPORTANTE:** No subir lifecycle a 2.8.x — incompatible con `navigation-fragment:2.7.7`

### 2. RxDataStore — static vs instance method
- **Error:** `Non-static method 'data()' cannot be referenced from a static context`
- **Causa:** Se usaba `RxDataStore.data(dataStore)` como llamada estática
- **Fix:** Llamar como instancia: `dataStore.data()` y `dataStore.updateDataAsync(...)`

### 3. ProfileViewModel — NullPointerException en RxJava2
- **Causa 1:** `AndroidViewModel` con `ViewModelProvider(this)` sin factory custom
- **Fix 1:** Cambiar a `ViewModel` puro, usar `SessionManager.getInstance()` (sin context)
- **Causa 2:** `getUser()` mapeaba a `null` cuando no había sesión — RxJava2 no permite emitir null
- **Fix 2:** Cambiar `.map(prefs -> null)` a `.flatMap(prefs -> Flowable.empty())`

### 4. ViewModelProvider incompatibility
- **Error:** `cannot be converted to ViewModelProviderImpl`
- **Causa:** lifecycle `2.8.7` incompatible con `navigation-fragment:2.7.7`
- **Fix:** Downgrade lifecycle a `2.7.0` en todos los módulos

### 5. Wrong nav action ID in RegisterFragment
- **Error:** `cannot find symbol: action_registerFragment_to_loginFragment`
- **Fix:** Cambiar a `R.id.action_register_to_login` en `RegisterFragment.java:42`

### 6. DataStore cast — Single<MutablePreferences> vs Single<Preferences>
- **Error:** compilación / tipo incorrecto en `updateDataAsync`
- **Fix:** Cast explícito: `return Single.just((Preferences) mutablePrefs);`

---

## Bug Pendiente — "Error al guardar sesión"

### Estado actual (fin de sesión 2026-03-30)
- Login API call: ✅ HTTP 200 (confirmado en logcat)
- `cachedToken` se setea: ✅ (línea `cachedToken = token` en `saveSession`)
- `saveSession().subscribe(onSuccess, onError)`: el `onError` **siempre se ejecuta**
- Excepción real: **desconocida** — logcat solo mostraba logs INFO de OkHttp, no el stack trace

### Diagnóstico aplicado
En `LoginFragment.java` se agregó:
```java
import android.util.Log;
// ...
case SUCCESS:
    // null guard en user
    com.uade.xplorenow.data.model.User loginUser = result.getData().getUser();
    Log.d("XploreNow", "Login SUCCESS — token=" + result.getData().getToken() + " user=" + loginUser);
    if (loginUser == null) {
        loginUser = new com.uade.xplorenow.data.model.User("", "", "", "user");
    }
    final com.uade.xplorenow.data.model.User finalUser = loginUser;
    // ...
    error -> {
        Log.e("XploreNow", "saveSession FAILED", error);
        showError("Error al guardar sesión: "
                + error.getClass().getSimpleName()
                + " — " + error.getMessage());
    }
```

### Próximos pasos al retomar
1. **Correr la app** con este logging activo
2. **Leer el mensaje de error en pantalla** (ahora muestra clase + mensaje de la excepción)
3. **Filtrar Logcat por tag `XploreNow`** para ver el stack trace completo
4. Según el error:
   - `NullPointerException` → `user` o algún campo es null (verificar respuesta API)
   - `IllegalStateException` → problema de thread / DataStore inicialización
   - `ClassCastException` → el cast `(Preferences) mutablePrefs` falla en runtime
   - Cualquier otro → ajustar según el tipo

### Hipótesis más probables
- La API puede devolver `id` como integer (no String) — Gson lo ignora y `user.getId()` es null
- `user` puede ser null si la respuesta JSON tiene campos distintos a los esperados (`_id` vs `id`, etc.)
- El cast `(Preferences) mutablePrefs` puede fallar en algunas versiones del DataStore
- Posible `io.reactivex.exceptions.OnErrorNotImplementedException` si el error no se propaga bien

### Archivos relevantes
- `ui/auth/LoginFragment.java` — tiene el logging de diagnóstico (lineas ~142-170)
- `data/local/SessionManager.java` — `saveSession()` método problemático
- `data/remote/dto/LoginResponse.java` — wrapper de respuesta del login
- `data/model/User.java` — tiene constructor con params pero NO no-arg constructor (Gson usa UnsafeAllocator)
