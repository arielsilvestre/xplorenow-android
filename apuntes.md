# Apuntes de clase — Desarrollo de Aplicaciones I (UADE)

> Apuntes tomados en clase para guiar la implementación de XploreNow Android.
### REPO DEL DOCENTE: https://github.com/Horix89/mobile-practices-android ###

### Carpeta de PPTs de clases: C:\Users\asilvestre\OneDrive - PSF\Ariel UADE\2026\1er cuatrimestre\Aplicaciones\ppts ###

---

## Clase 31/3

### Fragment

Sirve para UX → depende de la creación de un Activity.
- Un Activity por cada aplicación.
- Un Fragment se añade, reemplaza o elimina dinámicamente → coordinado por el **Fragment Manager**.
- Se comunican con la Activity y entre sí.
- UI propia (XML) + lógica (Java).

#### Ciclo de vida del Fragment

| Método | Cuándo ocurre | Qué hacer acá |
|---|---|---|
| `onCreate()` | Se crea el fragment | Inicializar variables y argumentos (Bundle). Todavía no hay vistas. |
| `onCreateView()` | Se infla el layout | Retornar el View inflado desde el XML. No tocar vistas acá. |
| `onViewCreated()` | El layout ya existe | `findViewById()`, `setOnClickListener()` y toda la lógica de UI. |
| `onDestroyView()` | Se destruye la vista | Limpiar referencias a vistas para evitar memory leaks. |

#### Componentes de un Fragment

- **Clase** → define la lógica del componente.
- **FragmentContainerView** → donde se renderizan los fragments (como un iframe). Contenedor en el layout de la Activity donde se insertan los Fragments.
- **FragmentTransaction** → operaciones sobre los fragments: `add()`, `replace()`, `remove()`, `commit()`.

---

### Navigation Component

Librería de Android Jetpack que simplifica y organiza la navegación dentro de una app. Corresponde implementar **single activity application**.

- Centraliza toda la navegación en un **Navigation Graph** y maneja el back stack automáticamente.
- Facilita el comportamiento del flujo: qué fragment va a mostrar/renderizar.

#### Elementos principales

1. **Navigation Graph (`nav_graph.xml`)** → Archivo XML que define todos los destinos (Activities y Fragments) y las conexiones entre ellos.
2. **NavHostFragment** → Elemento en el layout que muestra el destino actual definido en el navigation graph.
3. **NavController** → Objeto que gestiona la navegación en tiempo de ejecución. Se usa para navegar entre destinos.

> No se puede acceder a un destino que no esté definido dentro del navGraph.

#### Setup — Pasos

1. Agregar dependencias (hay dos gradle: uno del proyecto y otro de la app).
2. Crear el navigation graph.
3. Configurar `NavHostFragment` en el layout de la Activity.
4. Navegar con `NavController`.

#### Conceptos clave

- **`app:startDestination="@id/auth_nav_graph"`** → elige qué fragment se va a mostrar primero.
- **Inversión de control** → se delega en la librería la navegación según lo definido en las actions. Para modificar la navegación se modifica el navGraph, no el código.
- **Single Activity Architecture** → en lugar de una Activity por pantalla, hay una sola `MainActivity` con el `NavHostFragment`. Cada pantalla es un Fragment navegado por el `NavController`.

---

### API REST — Conceptos generales

- **Richardson Maturity Model** → modelo de madurez para APIs REST.
- Tipos de APIs: REST, GraphQL, SOAP, gRPC, WebSockets.

#### Retrofit

Librería que permite resolver acciones HTTP de APIs.

- Usa anotaciones (`@GET`, `@POST`, etc.) para definir endpoints.
- **GSON / Moshi** → convierte automáticamente JSON a objetos Java (serialización y deserialización).
- **Manejo de errores** → callbacks `onResponse` / `onFailure`.
- Se definen **interfaces** donde, usando anotaciones, se define cómo se hace el request HTTP.

#### Interceptores

- Patrón de comportamiento que actúa entre el flujo de A a B.
- Permite generar un patrón común para las requests (ej: agregar un header fijo a todos los requests).
- Evita tener que configurar con anotaciones cada uno de los pedidos individualmente.

#### Setup de Retrofit

1. Dependencias en `build.gradle`.
2. Crear la instancia de Retrofit.
3. Definir la interfaz de la API.

> Ver ejemplos detallados en el PPT de la clase.


# Clase 7/4

## inyección de dependendencias & retrofit con Hilt

Storage | Persistir información en el dispositivo | para log in

Repaso clase anterior:

- Creamos un singleton manual para retrofit
    - tenia como objetivo unicamente devolver una instancia de retrofit.
    Unit test:
        - Controlado
        - acotado
        - repetible

- ¿para que sirve la IDD?
    - {la D de solid} dependencia SOLID -> Dependency Inversion => todos los módulos deben depender de abstracciones.
    - {Principio} princpio de inversión de control => "no busques tus dependencias, yo te las doy" -> el fragment no controla el cuando ni el como se construyen sus dependencias. un agente externo (el framework) se las entrega.
    - {Técnica} Inyección de dependencias -> es un patrón de diseño doonde las dependencias se pasan desde afuera (por constructor por campo o por método) para generar desacoplamiento. Hilt automatiza...

    la anotation usada es @Inject para inyectar la dependencia de la api.
    llena el objeto api en tiempo de ejecución

    Contenedor / container -> crea y guarda objetos. Evita hacer new
    Scopes -> cuanto tiempo vive una instancia dentro del contenedor @singleton @ActivityScoped 
        |_ se pueden crear patrones de creación sin declarar lineas de código. Solo notations.
        |_ @ActivityScoped deja la instancia creada en el contenedor antes de ...

    ciclo de vida -> sabe donde tiene que inyectar en que momento del tiempo de ejecución

### Anotaciones clave:
- @HiltAndroidApp => en la apliación
- @AndroidEntroPoint => en Acrivity / Fragment
- @Module / provides

Build.Gradle (project)

Vamos a agregar una clase llamada Application => va a ser el punto de entrada a la app
cuando se carga aplication no hay ningún activity cargado en UI aún.

@HiltAndroidApp // ==> arranca el contenedor de hilt va e nla clase application una sola vez
public class MyApp
    extendes Application {

    }

@AndroidEntryPoint
public class MainActivity
    extendes AppCompatActivity{

    }

NetworkModule => ¿reemplaza el retrofitClient?

@provides -> este metodo devuelve una dependencia

paso 4 inyectar el fragmento:

migrar el proyecto a Hilt

### JWT + LocalStorage => Autorización

estructura de un JWT => Header / Payload / Signature

el JWT está encodeado no encriptado. tiene un encode en base64.

=====================================================================

modificamos el networkModule para que Retrofit use un OkHttpCliente con un interceptor que lee el token del TokenManager
|_ declaramos un objeto que va a entrar en el contenedor de Hilt (?) => a chequear



