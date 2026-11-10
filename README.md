# chess-software

Proyecto de la asignatura Ingeniería del Software 2 (ULPGC). Sistema de salas de ajedrez diseñado bajo arquitectura hexagonal, con separación entre cliente (frontend) y servidor (backend).

## 1. Objetivo de la entrega
El propósito de este proyecto es desarrollar un sistema que permita crear salas de ajedrez, conectar usuarios para disputar o reproducir partidas y registrar el historial de las mismas. El diseño prioriza el desacoplamiento entre la lógica de negocio, la interfaz de usuario, las comunicaciones de red y la persistencia de datos mediante el patrón de Puertos y Adaptadores (Arquitectura Hexagonal).

> Para una explicación gráfica paso a paso de los flujos de ejecución, consulta los diagramas en [`docs/architecture.md`](docs/architecture.md) y [`docs/frontend-cycle.md`](docs/frontend-cycle.md).

## 2. Dependencias y entorno de desarrollo
<!-- NOTA: Si añades librerías en el pom.xml (ej. SparkJava, Javalin, Jackson, Gson, SQLite...), añádelas a esta lista -->
* **JDK:** Oracle OpenJDK 27.
* **Gestor de construcción:** Apache Maven (`pom.xml` autónomo por módulo en `backend/`).
* **Dependencias actuales:**
  * `JUnit 5 (Jupiter 6.1.3)`: Para la ejecución de pruebas unitarias automatizadas (`scope: test`).
  * `AssertJ Core (3.27.7)`: Librería de aserciones fluidas para las pruebas unitarias (`scope: test`).
  * `Javalin (6.1.3)`: Framework ligero para el servidor web y la API REST HTTP.
  * `SLF4J Simple (2.0.12)`: Implementación de logging para registrar eventos del servidor web.

## 3. Compilación y ejecución

### Compilación del núcleo y ejecución de pruebas
El núcleo de la lógica del ajedrez se encuentra aislado en el módulo `backend/game`. Para compilar y ejecutar las pruebas:

```bash
# Opción 1: Desde la raíz del repositorio indicando el POM del módulo
mvn -f backend/game/pom.xml clean test

# Opción 2: Entrando al directorio del módulo
cd backend/game
mvn clean test
```

### Ejecución del Servidor Backend y Aplicación Web
Para arrancar el servidor web de ajedrez con Javalin:

1. Ejecutar la clase principal `software.ulpgc.chess.Main` (desde IntelliJ o mediante Maven).
2. El servidor arrancará por defecto en el puerto `7070`.
3. Puntos de acceso:
   * **Aplicación Web interactiva:** Abrir en el navegador [`http://localhost:7070`](http://localhost:7070)
   * **API - Obtener tablero inicial:** `GET http://localhost:7070/api/board/initial`
   * **API - Aplicar un movimiento:** `GET http://localhost:7070/api/board/move?board=<FEN>&move=<UCI>`
     * Ejemplo: `http://localhost:7070/api/board/move?board=rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR&move=e2e4`

## 4. Estructura del proyecto
<!-- NOTA: Actualiza este árbol si creas nuevos paquetes (ej. application / usecases, persistence, ui, network...) -->
El proyecto se organiza en dos grandes áreas: `backend/` (con módulos autónomos) y `frontend/` (para artefactos de cliente):

```text
chess-software/
├── docs/                                       # Documentación técnica, reglas y diagramas
│   ├── agent.md                                # Reglas del agente, convención de nombres y estilo
│   ├── architecture.md                         # Diagramas de flujo y arquitectura hexagonal
│   └── frontend-cycle.md                       # Diagrama de secuencia del cliente web
├── frontend/                                   # Frontend web estático servido por Javalin
│   ├── index.html                              # Estructura del tablero y controles
│   ├── style.css                               # Estilos y cuadrícula de casillas
│   └── app.js                                  # Cliente de ajedrez (interacción y fetch)
└── backend/                                    # Módulos y servicios del servidor
    └── game/                                   # Módulo del núcleo y reglas de juego
        ├── pom.xml                             # Descriptor de construcción del módulo
        └── src/
            ├── main/
            │   └── java/software/ulpgc/chess/
            │       ├── architecture/           # El Core (Puro, sin dependencias externas)
            │       │   ├── domain/model/       # Entidades y objetos de valor inmutables
            │       │   ├── ports/              # Interfaces de entrada y salida
            │       │   │   ├── in/             # Puertos de entrada (lectura / deserializadores)
            │       │   │   └── out/            # Puertos de salida (escritura / serializadores)
            │       │   └── usecases/           # Casos de uso (orquestadores de negocio)
            │       │       ├── UseCases.java
            │       │       ├── GetInitialBoardUseCase.java
            │       │       └── MovePieceUseCase.java
            │       ├── infrastructure/         # El Exterior (Traducción y comunicación)
            │       │   └── adapters/           # Implementaciones concretas
            │       │       ├── in/             # Adaptadores de entrada (Javalin EndPoints y Deserializadores)
            │       │       │   ├── EndPoint.java
            │       │       │   ├── GetInitialBoardEndPoint.java
            │       │       │   ├── MovePieceEndPoint.java
            │       │       │   ├── AlgebraicPositionDeserializer.java
            │       │       │   ├── FenBoardDeserializer.java
            │       │       │   ├── FenStateDeserializer.java
            │       │       │   └── UciMoveDeserializer.java
            │       │       └── out/            # Adaptadores de salida (Serializadores)
            │       │           ├── AlgebraicPositionSerializer.java
            │       │           ├── FenBoardSerializer.java
            │       │           └── UciMoveSerializer.java
            │       └── Main.java               # Composition Root / cableado y servidor
            └── test/java/tests/                # Pruebas unitarias automatizadas
                ├── in/                         # Tests de adaptadores de entrada
                ├── out/                        # Tests de adaptadores de salida
                └── usecases/                   # Tests unitarios de casos de uso
```

## 5. Arquitectura de sistemas
<!-- NOTA: Aquí debes explicar cómo se comunican Frontend, Backend y Base de Datos. 
     - Incluye un esquema o diagrama simple.
     - Documenta el contrato de comunicación: rutas HTTP (GET/POST), mensajes WebSocket o JSON que se envían. -->
El backend expone su funcionalidad a través de una API HTTP implementada con Javalin bajo el patrón Inversión de Dependencias (Pure DI):

* **Contrato de comunicación HTTP:**
  * `GET /`: Entrega la aplicación SPA web (`index.html`, `style.css`, `app.js`).
  * `GET /api/board/initial`: Devuelve el estado inicial del tablero en formato FEN (`text/plain`).
  * `GET /api/board/move?board={fen}&move={uci}`: Recibe el FEN actual y el movimiento en UCI por parámetros de consulta (`query params`), delega en el caso de uso y responde con el nuevo FEN resultante.

## 6. Arquitectura hexagonal del Backend

### Dominio (`software.ulpgc.chess.architecture.domain.model`)
<!-- NOTA: Si añades nuevas clases al modelo (ej. Room, User, Game, Rules...) o casos de uso, documéntalos aquí -->
Contiene las estructuras de datos inmutables y la representación lógica del ajedrez, sin dependencias hacia librerías externas ni formatos de transporte:
* `Board`: Gestiona la disposición de las piezas en el tablero mediante una estructura inmutable (`Map<Square, Piece>`). Proporciona la configuración inicial y la transición de estado al aplicar movimientos (`move()`).
* `Square`: Record que representa una casilla del tablero a partir de su columna (`File`) y fila (`Rank`).
* `Move`: Record que modela un desplazamiento entre una casilla de origen (`from`) y una de destino (`to`).
* `Piece`, `Color`, `File`, `Rank`: Enumerados que definen las piezas, bandos y límites geométricos del tablero (`a-h`, `1-8`).
* `State`: Encapsula el estado global de la partida (turnos, derechos de enroque, captura al paso y contadores).

### Casos de Uso (`software.ulpgc.chess.architecture.usecases`)
Orquestan la lógica pura apoyándose en los puertos y el dominio, totalmente aislados de la web:
* `GetInitialBoardUseCase`: Consulta el tablero inicial en el dominio y lo serializa a texto.
* `MovePieceUseCase`: Recibe un `Input(board, move)`, deserializa ambos elementos mediante los puertos correspondientes, ejecuta `Board.move(move)` y serializa el tablero resultante mediante una tubería de métodos privados `process(...)`.

### Puertos (`software.ulpgc.chess.architecture.ports`)
<!-- NOTA: Cuando crees puertos para guardar partidas en base de datos (ej. GameRepository) o gestionar salas, añádelos a esta lista -->
Interfaces segregadas (ISP extremo) que establecen los contratos para transformar objetos del dominio a representaciones externas y viceversa:

* **Puertos de entrada (`ports.in`):**
  * `PositionDeserializer`: Deserialización de coordenadas (`Square`).
  * `BoardDeserializer`: Deserialización del tablero (`Board`).
  * `MoveDeserializer`: Deserialización de desplazamientos (`Move`).
  * `StateDeserializer`: Lectura y deserialización del estado global de partida (`State`).

* **Puertos de salida (`ports.out`):**
  * `PositionSerializer`: Serialización de coordenadas (`Square`).
  * `BoardSerializer`: Serialización de la disposición del tablero (`Board`).
  * `MoveSerializer`: Serialización de desplazamientos (`Move`).

### Adaptadores (`software.ulpgc.chess.infrastructure.adapters`)
<!-- NOTA: Cuando implementes los adaptadores web (controladores HTTP/WebSocket) o de base de datos (SQLite, memoria...), añádelos aquí -->
Implementaciones concretas de los puertos organizadas en entrada (`in`) y salida (`out`):
* **Adaptadores de entrada (`adapters.in`):**
  * Adaptadores Web Javalin: Clases que implementan `EndPoint` y traducen las peticiones Javalin (`Context`) hacia llamadas a los casos de uso (`GetInitialBoardEndPoint`, `MovePieceEndPoint`).
  * Deserializadores: `AlgebraicPositionDeserializer` (casillas), `FenBoardDeserializer`, `FenStateDeserializer` (tablero/estado FEN) y `UciMoveDeserializer` (movimientos UCI).
* **Adaptadores de salida (`adapters.out`):**
  * Serializadores: `AlgebraicPositionSerializer`, `FenBoardSerializer` y `UciMoveSerializer`.

## 7. Arquitectura hexagonal ligera del Frontend
<!-- NOTA: Documenta aquí las clases/módulos del frontend:
     - Vista / UI del tablero (adaptador de entrada).
     - Casos de uso de interacción del usuario.
     - Puerto de servicio y su adaptador de red (HTTP/WebSocket) para hablar con el backend. -->
El frontend está implementado como una aplicación web ligera (Single Page Application sin dependencias externas) alojada en la raíz `frontend/`:
* **Vista (`index.html` y `style.css`):** Renderiza el tablero de ajedrez en una cuadrícula CSS 8x8 con glifos tipográficos Unicode oficiales (♔, ♕, ♖, ♗, ♘, ♙...), junto a controles de reinicio e indicador de estado/FEN.
* **Controlador (`app.js` - `ChessGameClient`):** Gestiona los clics del usuario en las casillas (origen y destino), compone la jugada en formato UCI (ej. `e2e4`) y se comunica mediante llamadas asíncronas (`fetch`) con los endpoints del backend (`/api/board/initial` y `/api/board/move`).

## 8. Modelo de datos y conexión de usuarios
<!-- NOTA: Explica aquí:
     1. Cómo se guardan los usuarios, salas y partidas en la base de datos (tablas o estructura JSON).
     2. Qué mecanismo elegiste para juntar a dos jugadores (ej. crear sala con código compartido, lista de salas disponibles, etc.). -->
* **Intercambio de estados y jugadas:** Se emplea notación FEN para el estado del tablero y notación UCI para la comunicación de movimientos a través de la API HTTP.
* **Gestión de salas y persistencia:** *(Pendiente de definición).*

## 9. Aportación propia
<!-- NOTA: Describe aquí tu idea propia (15% de la nota). Ejemplos: reloj de partida, chat en la sala, modo espectador, salas privadas con código, reproductor de partidas guardadas. Explica qué aporta y cómo encaja en la arquitectura. -->
*(Pendiente de documentar e implementar).*

## 10. Capturas del sistema
<!-- NOTA: Antes de entregar, mete las imágenes en una carpeta (ej. /docs o /screenshots) y enlázalas aquí con ![Descripción](ruta/imagen.png).
     Obligatorio según rúbrica:
     1. Captura del tablero funcionando.
     2. Captura creando o uniéndose a una sala.
     3. Captura consultando una partida o el histórico registrado. -->
*(Espacio reservado para las capturas de pantalla obligatorias: tablero, gestión de salas y registro de partidas).*

## 11. Verificación
<!-- NOTA: 
     - Si añades más clases de test en el paquete `tests` (incluyendo pruebas con Mocks / dobles de prueba), añádelas al listado.
     - Documenta también las pruebas manuales que pide la rúbrica (conectar 2 usuarios, probar qué pasa si mandas un movimiento inválido o pides una sala que no existe). -->
El repositorio incluye 28 pruebas unitarias automatizadas con JUnit 5 y AssertJ en el módulo `backend/game`, organizadas por paquete:
* **`tests.algebraic`:** `AlgebraicPositionSerializerTest` y `AlgebraicPositionDeserializerTest`.
* **`tests.fen`:** `FenBoardSerializerTest`, `FenBoardDeserializerTest` y `FenStateDeserializerTest`.
* **`tests.uci`:** `UciMoveSerializerTest` y `UciMoveDeserializerTest`.
* **`tests.usecases`:** `GetInitialBoardUseCaseTest` y `MovePieceUseCaseTest`.

## 12. Límites conocidos
<!-- NOTA: Actualiza esta sección según avances. Sé transparente con qué reglas de ajedrez validas y cuáles no (ej. si no implementas jaque mate o tablas por triple repetición), o límites de concurrencia. -->
* En la fase actual, el método `Board.move()` actualiza la posición de las piezas en el mapa del tablero pero aún no aplica validación de reglas de movimiento legal (turnos, jaques o trayectorias bloqueadas).
