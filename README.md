# chess-software

Proyecto de la asignatura Ingeniería del Software 2 (ULPGC). Sistema de salas de ajedrez diseñado bajo arquitectura hexagonal, con separación entre cliente (frontend) y servidor (backend).

## 1. Objetivo de la entrega
El propósito de este proyecto es desarrollar un sistema que permita crear salas de ajedrez, conectar usuarios para disputar o reproducir partidas y registrar el historial de las mismas. El diseño prioriza el desacoplamiento entre la lógica de negocio, la interfaz de usuario, las comunicaciones de red y la persistencia de datos mediante el patrón de Puertos y Adaptadores (Arquitectura Hexagonal).

## 2. Dependencias y entorno de desarrollo
<!-- NOTA: Si añades librerías en el pom.xml (ej. SparkJava, Javalin, Jackson, Gson, SQLite...), añádelas a esta lista -->
* **JDK:** Oracle OpenJDK 27.
* **Gestor de construcción:** Apache Maven (`pom.xml`).
* **Dependencias actuales:**
  * `JUnit 5 (Jupiter)`: Para la ejecución de pruebas unitarias automatizadas.
  * `AssertJ Core (3.27.7)`: Librería de aserciones fluidas para las pruebas unitarias (`scope: test`).

## 3. Compilación y ejecución

### Compilación del núcleo y ejecución de pruebas
Desde el directorio raíz del proyecto, ejecutar:

```bash
mvn clean compile
mvn test
```

### Ejecución del Backend y Frontend
<!-- NOTA: Cuando tengas el servidor y el cliente, pon aquí cómo arrancarlos, qué puertos usan (ej. localhost:8080) y cómo se configura la base de datos -->
*(En desarrollo: se detallarán los comandos de inicio, configuración de puertos y conexión a base de datos conforme se implementen las capas de aplicación y red).*

## 4. Estructura del proyecto
<!-- NOTA: Actualiza este árbol si creas nuevos paquetes (ej. application / usecases, persistence, ui, network...) -->
El código fuente se organiza en paquetes que reflejan la separación de responsabilidades de la arquitectura hexagonal:

```text
src/
├── main/java/software/ulpgc/chess/
│   ├── domain/model/   # Entidades y objetos de valor del dominio
│   ├── ports/          # Interfaces de entrada y salida
│   ├── adapters/       # Implementaciones de serialización y formato
│   └── Main.java
└── test/java/tests/    # Pruebas unitarias
```

## 5. Arquitectura de sistemas
<!-- NOTA: Aquí debes explicar cómo se comunican Frontend, Backend y Base de Datos. 
     - Incluye un esquema o diagrama simple.
     - Documenta el contrato de comunicación: rutas HTTP (GET/POST), mensajes WebSocket o JSON que se envían. -->
*(Pendiente de documentar: diagrama de bloques con la relación entre Frontend, Backend, Base de Datos y definición del contrato de comunicación).*

## 6. Arquitectura hexagonal del Backend

### Dominio (`software.ulpgc.chess.domain.model`)
<!-- NOTA: Si añades nuevas clases al modelo (ej. Room, User, Game, Rules...) o casos de uso, documéntalos aquí -->
Contiene las estructuras de datos inmutables y la representación lógica del ajedrez, sin dependencias hacia librerías externas ni formatos de transporte:
* `Board`: Gestiona la disposición de las piezas en el tablero mediante una estructura inmutable (`Map<Square, Piece>`). Proporciona la configuración inicial y la transición de estado al aplicar movimientos (`move()`).
* `Square`: Record que representa una casilla del tablero a partir de su columna (`File`) y fila (`Rank`).
* `Move`: Record que modela un desplazamiento entre una casilla de origen (`from`) y una de destino (`to`).
* `Piece`, `Color`, `File`, `Rank`: Enumerados que definen las piezas, bandos y límites geométricos del tablero (`a-h`, `1-8`).
* `State`: Clase destinada a encapsular el estado global de la partida (turnos, derechos de enroque, captura al paso y contadores).

### Puertos (`software.ulpgc.chess.ports`)
<!-- NOTA: Cuando crees puertos para guardar partidas en base de datos (ej. GameRepository) o gestionar salas, añádelos a esta lista -->
Interfaces que establecen los contratos para transformar objetos del dominio a representaciones externas y viceversa:
* `PositionSerializer` / `PositionDeserializer`: Conversión de coordenadas (`Square`).
* `BoardSerializer` / `BoardDeserializer`: Conversión de la disposición del tablero (`Board`).
* `MoveSerializer` / `MoveDeserializer`: Conversión de movimientos (`Move`).
* `StateDeserializer`: Lectura del estado de partida (`State`).

### Adaptadores (`software.ulpgc.chess.adapters`)
<!-- NOTA: Cuando implementes los adaptadores web (controladores HTTP/WebSocket) o de base de datos (SQLite, memoria...), añádelos aquí -->
Implementaciones de los puertos utilizando notaciones estándar de ajedrez:
* **Notación Algebraica:** `AlgebraicPositionSerializer` y `AlgebraicPositionDeserializer` para coordenadas individuales (ej. `"e4"`).
* **Notación FEN (Forsyth-Edwards Notation):** `FenBoardSerializer`, `FenBoardDeserializer` y `FenStateDeserializer` para representar el estado del tablero en cadenas de texto compactas.
* **Protocolo UCI (Universal Chess Interface):** `UciMoveSerializer` y `UciMoveDeserializer` para representar movimientos en formato origen-destino (ej. `"e2e4"`), delegando la resolución de coordenadas en los adaptadores algebraicos.

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
* **Intercambio de estados y jugadas:** Se emplea notación FEN para el estado del tablero y notación UCI para la comunicación de movimientos.
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
El repositorio incluye pruebas unitarias automatizadas con JUnit 5 y AssertJ en el paquete `tests`, verificando la conversión de formatos y los casos límite del tablero:
* `AlgebraicPositionSerializerTest` y `AlgebraicPositionDeserializerTest`.
* `FenBoardSerializerTest` y `FenBoardDeserializerTest`.
* `FenStateDeserializerTest`.
* `UciMoveSerializerTest` y `UciMoveDeserializerTest`.

## 12. Límites conocidos
<!-- NOTA: Actualiza esta sección según avances. Sé transparente con qué reglas de ajedrez validas y cuáles no (ej. si no implementas jaque mate o tablas por triple repetición), o límites de concurrencia. -->
* En la fase actual, el método `Board.move()` actualiza la posición de las piezas en el mapa del tablero pero aún no aplica validación de reglas de movimiento legal (turnos, jaques o trayectorias bloqueadas).