# chess-software

Proyecto de la asignatura Ingeniería del Software 2 (ULPGC). Sistema de salas de ajedrez diseñado bajo arquitectura hexagonal, con separación entre cliente (frontend) y servidor (backend).

## 1. Objetivo de la entrega
El propósito de este proyecto es desarrollar un sistema que permita crear salas de ajedrez, conectar usuarios para disputar o reproducir partidas y registrar el historial de las mismas. El diseño prioriza el desacoplamiento entre la lógica de negocio, la interfaz de usuario, las comunicaciones de red y la persistencia de datos mediante el patrón de Puertos y Adaptadores (Arquitectura Hexagonal).

## 2. Dependencias y entorno de desarrollo
<!-- NOTA: Si añades librerías en el pom.xml (ej. SparkJava, Javalin, Jackson, Gson, SQLite...), añádelas a esta lista -->
* **JDK:** Oracle OpenJDK 27.
* **Gestor de construcción:** Apache Maven (`pom.xml` autónomo por módulo).
* **Dependencias actuales:**
  * `JUnit 5 (Jupiter 6.1.3)`: Para la ejecución de pruebas unitarias automatizadas (`scope: test`).
  * `AssertJ Core (3.27.7)`: Librería de aserciones fluidas para las pruebas unitarias (`scope: test`).
  * `Javalin (6.1.3)`: Framework ligero para el servidor web y la API REST HTTP.
  * `SLF4J Simple (2.0.12)`: Implementación de logging para registrar eventos del servidor web.

## 3. Compilación y ejecución

### Compilación del núcleo y ejecución de pruebas
El núcleo de la lógica del ajedrez se encuentra aislado en el módulo `game`. Para compilar y ejecutar las pruebas:

```bash
# Opción 1: Desde la raíz del repositorio indicando el POM del módulo
mvn -f game/pom.xml clean test

# Opción 2: Entrando al directorio del módulo
cd game
mvn clean test
```

### Ejecución del Servidor Backend
Para arrancar el servidor web de ajedrez con Javalin:

1. Ejecutar la clase principal `software.ulpgc.chess.Main` (desde IntelliJ o mediante Maven).
2. El servidor arrancará por defecto en el puerto `7070`.
3. Endpoints disponibles:
   * **Obtener tablero inicial:** `GET http://localhost:7070/api/board/initial`
   * **Aplicar un movimiento:** `GET http://localhost:7070/api/board/move?board=<FEN>&move=<UCI>`
     * Ejemplo: `http://localhost:7070/api/board/move?board=rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR&move=e2e4`

## 4. Estructura del proyecto
<!-- NOTA: Actualiza este árbol si creas nuevos paquetes (ej. application / usecases, persistence, ui, network...) -->
El proyecto se organiza en módulos autónomos e independientes. El módulo `game` refleja una separación estricta de responsabilidades bajo arquitectura hexagonal:

```text
game/
├── pom.xml                                     # Descriptor de construcción del módulo
└── src/
    ├── main/java/software/ulpgc/chess/
    │   ├── application/                        # El Core (Puro, sin dependencias externas)
    │   │   ├── domain/model/                   # Entidades y objetos de valor inmutables
    │   │   ├── ports/                          # Interfaces de entrada y salida
    │   │   │   ├── serializer/                 # Puertos de salida (escritura)
    │   │   │   └── deserializer/               # Puertos de entrada (lectura)
    │   │   └── usecases/                       # Casos de uso (orquestadores de negocio)
    │   │       ├── UseCases.java               # Interfaz funcional base
    │   │       ├── GetInitialBoardUseCase.java # Caso de uso: Tablero inicial
    │   │       └── MovePieceUseCase.java       # Caso de uso: Mover pieza
    │   ├── infrastructure/                     # El Exterior (Traducción y comunicación)
    │   │   └── adapters/                       # Implementaciones concretas
    │   │       ├── algebraic/                  # Notación algebraica (casillas)
    │   │       ├── fen/                        # Notación FEN (tablero y estado)
    │   │       ├── uci/                        # Protocolo UCI (movimientos)
    │   │       └── endpoints/                  # Adaptadores de entrada Web (Javalin)
    │   │           ├── EndPoint.java           # Interfaz base de endpoints
    │   │           ├── GetInitialBoardEndPoint.java
    │   │           └── MovePieceEndPoint.java
    │   └── Main.java                           # Composition Root / cableado y servidor
    └── test/java/tests/                        # Pruebas unitarias automatizadas
        ├── algebraic/                          # Tests de adaptadores algebraicos
        ├── fen/                                # Tests de adaptadores FEN
        ├── uci/                                # Tests de adaptadores UCI
        └── usecases/                           # Tests unitarios de casos de uso
```

## 5. Arquitectura de sistemas
<!-- NOTA: Aquí debes explicar cómo se comunican Frontend, Backend y Base de Datos. 
     - Incluye un esquema o diagrama simple.
     - Documenta el contrato de comunicación: rutas HTTP (GET/POST), mensajes WebSocket o JSON que se envían. -->
El backend expone su funcionalidad a través de una API HTTP implementada con Javalin bajo el patrón Inversión de Dependencias (Pure DI):

* **Contrato de comunicación HTTP:**
  * `GET /api/board/initial`: Devuelve el estado inicial del tablero en formato FEN (`text/plain`).
  * `GET /api/board/move?board={fen}&move={uci}`: Recibe el FEN actual y el movimiento en UCI por parámetros de consulta (`query params`), delega en el caso de uso y responde con el nuevo FEN resultante.

## 6. Arquitectura hexagonal del Backend

### Dominio (`software.ulpgc.chess.application.domain.model`)
<!-- NOTA: Si añades nuevas clases al modelo (ej. Room, User, Game, Rules...) o casos de uso, documéntalos aquí -->
Contiene las estructuras de datos inmutables y la representación lógica del ajedrez, sin dependencias hacia librerías externas ni formatos de transporte:
* `Board`: Gestiona la disposición de las piezas en el tablero mediante una estructura inmutable (`Map<Square, Piece>`). Proporciona la configuración inicial y la transición de estado al aplicar movimientos (`move()`).
* `Square`: Record que representa una casilla del tablero a partir de su columna (`File`) y fila (`Rank`).
* `Move`: Record que modela un desplazamiento entre una casilla de origen (`from`) y una de destino (`to`).
* `Piece`, `Color`, `File`, `Rank`: Enumerados que definen las piezas, bandos y límites geométricos del tablero (`a-h`, `1-8`).
* `State`: Encapsula el estado global de la partida (turnos, derechos de enroque, captura al paso y contadores).

### Casos de Uso (`software.ulpgc.chess.application.usecases`)
Orquestan la lógica pura apoyándose en los puertos y el dominio, totalmente aislados de la web:
* `GetInitialBoardUseCase`: Consulta el tablero inicial en el dominio y lo serializa a texto.
* `MovePieceUseCase`: Recibe un `Input(board, move)`, deserializa ambos elementos mediante los puertos correspondientes, ejecuta `Board.move(move)` y serializa el tablero resultante.

### Puertos (`software.ulpgc.chess.application.ports`)
<!-- NOTA: Cuando crees puertos para guardar partidas en base de datos (ej. GameRepository) o gestionar salas, añádelos a esta lista -->
Interfaces segregadas (ISP extremo) que establecen los contratos para transformar objetos del dominio a representaciones externas y viceversa:

* **Puertos de salida (`ports.serializer`):**
  * `PositionSerializer`: Serialización de coordenadas (`Square`).
  * `BoardSerializer`: Serialización de la disposición del tablero (`Board`).
  * `MoveSerializer`: Serialización de desplazamientos (`Move`).

* **Puertos de entrada (`ports.deserializer`):**
  * `PositionDeserializer`: Deserialización de coordenadas (`Square`).
  * `BoardDeserializer`: Deserialización del tablero (`Board`).
  * `MoveDeserializer`: Deserialización de desplazamientos (`Move`).
  * `StateDeserializer`: Lectura y deserialización del estado global de partida (`State`).

### Adaptadores (`software.ulpgc.chess.infrastructure.adapters`)
<!-- NOTA: Cuando implementes los adaptadores web (controladores HTTP/WebSocket) o de base de datos (SQLite, memoria...), añádelos aquí -->
Implementaciones concretas de los puertos organizadas por tecnología o formato:
* **Notación Algebraica (`adapters.algebraic`):** `AlgebraicPositionSerializer` y `AlgebraicPositionDeserializer` para coordenadas individuales (ej. `"e4"`).
* **Notación FEN (`adapters.fen`):** `FenBoardSerializer`, `FenBoardDeserializer` y `FenStateDeserializer` para representar el tablero y el estado de la partida en cadenas compactas.
* **Protocolo UCI (`adapters.uci`):** `UciMoveSerializer` y `UciMoveDeserializer` para representar movimientos en formato origen-destino (ej. `"e2e4"`), delegando la resolución de coordenadas en los adaptadores algebraicos.
* **Adaptadores de entrada Web (`adapters.endpoints`):** Clases que traducen las peticiones Javalin (`Context`) hacia llamadas a los casos de uso (`GetInitialBoardEndPoint`, `MovePieceEndPoint`).

## 7. Arquitectura hexagonal ligera del Frontend
<!-- NOTA: Documenta aquí las clases/módulos del frontend:
     - Vista / UI del tablero (adaptador de entrada).
     - Casos de uso de interacción del usuario.
     - Puerto de servicio y su adaptador de red (HTTP/WebSocket) para hablar con el backend. -->
*(Pendiente de documentar: organización de vistas, casos de uso de interacción, puertos de servicio y adaptadores de comunicación).*

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
El repositorio incluye 28 pruebas unitarias automatizadas con JUnit 5 y AssertJ en el módulo `game`, organizadas por paquete:
* **`tests.algebraic`:** `AlgebraicPositionSerializerTest` y `AlgebraicPositionDeserializerTest`.
* **`tests.fen`:** `FenBoardSerializerTest`, `FenBoardDeserializerTest` y `FenStateDeserializerTest`.
* **`tests.uci`:** `UciMoveSerializerTest` y `UciMoveDeserializerTest`.
* **`tests.usecases`:** `GetInitialBoardUseCaseTest` y `MovePieceUseCaseTest`.

## 12. Límites conocidos
<!-- NOTA: Actualiza esta sección según avances. Sé transparente con qué reglas de ajedrez validas y cuáles no (ej. si no implementas jaque mate o tablas por triple repetición), o límites de concurrencia. -->
* En la fase actual, el método `Board.move()` actualiza la posición de las piezas en el mapa del tablero pero aún no aplica validación de reglas de movimiento legal (turnos, jaques o trayectorias bloqueadas).
