# Roadmap del Proyecto — Chess Software (ULPGC)

Resumen del estado actual y de las tareas pendientes, ordenadas por área con su **dificultad estimada** y su **peso en la rúbrica de IS2**.

---

## ✅ Ya completado (`backend/game` + `frontend/`)

- Modelo de dominio inmutable (`Board`, `Square`, `Move`, `Piece`, `Color`, `File`, `Rank`, `State`).
- Serializadores y deserializadores FEN, UCI y notación algebraica.
- Reglas de movimiento por pieza (`RookRule`, `BishopRule`, `QueenRule`, `KingRule`, `KnightRule`, `PawnRule`).
- Validación de jugadas legales con detección de jaque (`PseudoLegalMoveValidator`, `CheckDetector`, `ChessMoveValidator`).
- Casos de uso (`GetInitialBoardUseCase`, `MovePieceUseCase`, `GetAvailableMovesUseCase`) y endpoints HTTP en Javalin.
- Frontend SPA: tablero 8x8 con coordenadas (`a`–`h`, `1`–`8`), resaltado de jugadas válidas/capturas y aviso de jugada inválida.
- Suite de **45 tests unitarios** (JUnit 5 + AssertJ) en verde.

---

## 📋 Tareas pendientes

### 1. Reglas de ajedrez y experiencia de tablero (`backend/game` + `frontend/`)

| Tarea | Dificultad | Qué aporta |
| :--- | :---: | :--- |
| **Refactor `PathGenerator` en `rules/`** *(en curso)* | **Baja** | Elimina código duplicado separando `SlidingPathGenerator` y `SingleStepPathGenerator`. |
| **Alinear modelo `Position` / `State` / `Game` y `Operation`** | **Media** | `Position(Board, State)` con `State(side, castlingRights)` y respuestas REST con código HTTP (`status` + `result`). |
| **Control de turno activo (`w` / `b`)** | **Baja** | Evita mover piezas del rival en backend y muestra de quién es el turno en la UI. |
| **Aviso visual de Jaque + Jaque Mate / Ahogado** | **Media** | Marca al rey amenazado y detecta el fin de partida automático. |
| **Promoción de peón** | **Media** | Permite coronar al llegar a la última fila (UCI tipo `e7e8q` + selector visual). |
| **Enroque y captura al paso (*en passant*)** | **Media-Alta** | Completa las reglas oficiales del ajedrez usando `State` (`castlingRights` y casilla *en passant*). |
| **API REST real en `game-service` (`GET` / `POST` / `PUT`)** | **Media** | `POST` crea partida y devuelve `id_game` JSON; `GET ?id_game=` consulta; `PUT` aplica jugada con validación estricta (400/404). |

---

### 2. Arquitectura del Frontend (`frontend/`)

| Tarea | Dificultad | Qué aporta |
| :--- | :---: | :--- |
| **Desacoplar `fetch` de la UI** | **Baja** | Separa la comunicación HTTP de `ChessGameClient` en un adaptador/servicio sustituible por mocks (requisito de rúbrica). |

---

### 3. Microservicios, Usuarios, Salas e Infraestructura (`Nginx` + Servicios)

| Tarea | Dificultad | Qué aporta |
| :--- | :---: | :--- |
| **Separación `RegisterService` / `AuthService` $\leftrightarrow$ BD** | **Media** | Registro de cuenta (Usuario) y autenticación; `RegisterService` notifica a `PlayerService` (línea discontinua / evento) para activar al Jugador. |
| **`PlayerService` (Jugador activo en partida)** | **Baja-Media** | Modela al jugador activo/sesión de juego separado de la cuenta persistente de usuario. |
| **`MatchService` (enfrentamientos y salas) $\leftrightarrow$ BD** | **Media** | Emparejamiento, creación/unión a salas y registro de partidas entre dos jugadores. |
| **Balanceador / Gateway con `Nginx`** | **Baja-Media** | Enruta `/game`, `/match`, `/auth`, etc. hacia los microservicios y balancea carga entre réplicas. |
| **Sincronización entre 2 clientes (WebSocket o polling)** | **Media-Alta** | Permite jugar en tiempo real desde dos navegadores distintos. |
| **Manejo y tests de casos de error (5% rúbrica)** | **Baja-Media** | Validación estricta de entrada HTTP (400/404) y tests ante sala/partida inexistente o jugada inválida. |

---

### 4. Persistencia y Trazabilidad (15% rúbrica)

| Tarea | Dificultad | Qué aporta |
| :--- | :---: | :--- |
| **Repositorio de partidas/salas (ej. SQLite)** | **Media** | Puerto `out` + adaptador de BD para guardar partidas y su historial de movimientos. |
| **Consulta / reproductor de partidas guardadas** | **Baja-Media** | Permite revisar el histórico de partidas disputadas desde la web. |

---

### 5. Aportación propia (15% rúbrica)

| Tarea | Dificultad | Qué aporta |
| :--- | :---: | :--- |
| **Funcionalidad propia visible** *(elegir 1: reloj de ajedrez, lista de jugadas PGN/UCI, modo espectador o chat de sala)* | **Media** | Diferencia el proyecto y cubre el apartado de aportación propia justificada. |

---

### 6. Documentación y Cierre de entrega (15% capturas + README)

| Tarea | Dificultad | Qué aporta |
| :--- | :---: | :--- |
| **Actualizar diagrama UML `.jpg`** | **Baja** | Exportar desde Drive a `docs/diagrams/chess-backend-class-diagram.jpg` cuando se estabilicen los cambios. |
| **Capturas obligatorias y actualizar `README.md`** | **Baja** | Tablero funcionando, creación/unión a sala e histórico de partidas. |
| **Merge `develop` $\to$ `master` y Git Tag** | **Baja** | Publicar versión estable final según GitFlow. |

---

## 🎯 Plan de ataque: por dónde empezar y cómo afrontarlo

Regla de oro: **no abrir un frente nuevo sin dejar el anterior compilando y con tests en verde**. Este es el orden recomendado paso a paso:

### Fase 1 — Cerrar el núcleo de reglas (`backend/game`)
1. **Terminar el refactor de `PathGenerator`** *(tu punto actual)*:
   - Quitar `maxSteps` de `PathGenerator`, dejar las 6 reglas dependiendo solo de la interfaz `PathGenerator` y crear `Sliding` / `SingleStep` en `PseudoLegalMoveValidator.initialMap()`.
   - *Cierre:* los 45 tests actuales en verde.
2. **Envolver `Board` + `State` en `Position` y activar turnos (`w` / `b`)**:
   - Crear `CastlingRight`, actualizar `State(Color side, Set<CastlingRight>)`, crear `record Position(Board, State)` y combinar FEN de tablero + estado.
   - Validar el turno en `MovePieceUseCase` / `GetAvailableMovesUseCase` y mostrar *"Turno: Blancas/Negras"* en el frontend.
   - *Cierre:* no se pueden mover piezas del rival ni seleccionarlas fuera de turno + tests en verde.
3. **Jaque mate / ahogado + aviso visual en tablero**:
   - Añadir detección de sin jugadas legales (`isCheckmate` / `isStalemate`) aprovechando `ChessMoveValidator` + `CheckDetector`, y marcar al rey amenazado en el frontend.

### Fase 2 — Pasar de "tablero suelto" a API REST persistida (`game-service`)
4. **Introducir `GameStore` + `Operation` (`status` + `result`)**:
   - Definir puerto `GameStore` (con implementaciones en fichero o SQLite/MySQL) para guardar/cargar partidas por `id_game`.
   - Exponer endpoints REST reales: `POST` (crea partida y devuelve `id_game`), `GET ?id_game=` (recupera estado) y `PUT` (valida entrada HTTP estrictamente → `200`/`400`/`404` y persiste la jugada).
   - *Cierre:* pruebas unitarias con mocks de `GameStore` y pruebas de casos de error (`400`/`404`).
5. **Desacoplar `fetch` en `frontend/`**:
   - Extraer las llamadas HTTP de `ChessGameClient` a un adaptador/servicio propio para cumplir la arquitectura hexagonal ligera en cliente.
6. *(Opcional si da tiempo)* **Promoción de peón, enroque y *en passant*** usando `Position` y `CastlingRight`.

### Fase 3 — Salas, Usuarios y 2 jugadores (`MatchService`, `PlayerService`, `Auth`/`Register`)
7. **Servicios de usuario y jugador (`RegisterService`, `AuthService`, `PlayerService`)**:
   - Separar cuenta persistente en BD (`Register`/`Auth`) de jugador activo en partida (`PlayerService` notificado al registrarse).
8. **`MatchService` + tiempo real**:
   - Crear sala, unirse por código, asociar dos jugadores (blancas/negras) a un `id_game` y sincronizar jugadas (WebSocket o polling).
   - Cubrir tests de error (sala inexistente, sala llena, turno incorrecto).

### Fase 4 — Infraestructura y Aportación propia
9. **Balanceador / Gateway con `Nginx`**:
   - Colocar Nginx delante de los servicios (`/game`, `/match`, `/auth`, frontend estático) para enrutar y balancear carga.
10. **Aportación propia visible (15% rúbrica)**:
    - Añadir una mejora clara sobre lo ya montado (ej. historial de jugadas en pantalla, reloj de partida o modo espectador).

### Fase 5 — Cierre y entrega
11. **Diagrama UML y documentación**:
    - Exportar el `.jpg` final a `docs/diagrams/chess-backend-class-diagram.jpg`, añadir las 3 capturas obligatorias en `README.md` (tablero, salas, histórico) y documentar arquitectura y límites conocidos.
12. **GitFlow final**:
    - Merge `develop` → `master` y crear el tag de entrega (`v4.0.0` / final).
