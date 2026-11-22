# Roadmap del Proyecto — Chess Software (ULPGC)

Resumen del estado actual y de las tareas pendientes, ordenadas por área con su **dificultad estimada** y su **peso en la rúbrica de IS2**.

---

## ✅ Ya completado (`v5.0.0` — `backend/game` + `frontend/`)

- Modelo de dominio inmutable (`GamePosition`, `Board`, `Square`, `Move`, `Piece`, `Color`, `File`, `Rank`, `State`, `CastlingRight`, `GameStatus`).
- Serializadores y deserializadores FEN, UCI y notación algebraica.
- Reglas de movimiento por pieza (`RookRule`, `BishopRule`, `QueenRule`, `KingRule`, `KnightRule`, `PawnRule`) con `SlidingPathGenerator` y `SingleStepPathGenerator`.
- Validación de jugadas legales con detección de jaque, jaque mate y ahogado (`PseudoLegalMoveValidator`, `CheckDetector`, `ChessMoveValidator`, `GameStatusEvaluator`).
- Persistencia de partidas en disco (`GameStore` + `FileGameStore`).
- Casos de uso organizados en `usecases/board/` y `usecases/game/`, y adaptadores de entrada en `adapters/in/rest/` y `adapters/in/deserializers/`.
- API REST completa de partidas (`POST /api/games`, `GET /api/games`, `PUT /api/games`) con manejo de errores `400`/`404`, junto a los endpoints `/api/board/*`.
- Frontend SPA conectado a `/api/games` con `localStorage` (recupera partida tras F5), indicador de turno, resaltado de jugadas válidas/capturas y aviso visual de jaque, mate y ahogado.
- Suite de **67 tests unitarios** (JUnit 5 + AssertJ) en verde y diagrama `.drawio` actualizado.

---

## 📋 Tareas pendientes

### 1. Reglas de ajedrez avanzadas (`backend/game` + `frontend/`)

| Tarea | Dificultad | Qué aporta |
| :--- | :---: | :--- |
| **Promoción de peón** | **Media** | Permite coronar al llegar a la última fila (UCI tipo `e7e8q` + selector visual). |
| **Enroque y captura al paso (*en passant*)** | **Media-Alta** | Completa las reglas oficiales del ajedrez usando `State` (`castlingRights` y casilla *en passant*). |

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

### Fase 1 — Completar núcleo y cliente (`v5.0.0` ✅ / siguientes pasos)
1. **Reglas básicas, `GameStore`, API REST `/api/games` y conexión en frontend** ✅ *(completado en `v5.0.0`)*.
2. **Desacoplar `fetch` en `frontend/`** *(siguiente paso recomendado)*:
   - Extraer las llamadas HTTP de `ChessGameClient` a un adaptador/servicio propio para cumplir la arquitectura hexagonal ligera en cliente.
3. *(Opcional si da tiempo)* **Promoción de peón, enroque y *en passant*** usando `GamePosition` y `CastlingRight`.

### Fase 2 — Salas, Usuarios y 2 jugadores (`MatchService`, `PlayerService`, `Auth`/`Register`)
4. **Servicios de usuario y jugador (`RegisterService`, `AuthService`, `PlayerService`)**:
   - Separar cuenta persistente en BD (`Register`/`Auth`) de jugador activo en partida (`PlayerService` notificado al registrarse).
5. **`MatchService` + tiempo real**:
   - Crear sala, unirse por código, asociar dos jugadores (blancas/negras) a un `id_game` y sincronizar jugadas (WebSocket o polling).
   - Cubrir tests de error (sala inexistente, sala llena, turno incorrecto).

### Fase 3 — Infraestructura y Aportación propia
6. **Balanceador / Gateway con `Nginx`**:
   - Colocar Nginx delante de los servicios (`/game`, `/match`, `/auth`, frontend estático) para enrutar y balancear carga.
7. **Aportación propia visible (15% rúbrica)**:
   - Añadir una mejora clara sobre lo ya montado (ej. historial de jugadas en pantalla, reloj de partida o modo espectador).

### Fase 4 — Cierre y entrega
8. **Diagrama UML y documentación**:
   - Exportar el `.jpg` final a `docs/diagrams/chess-backend-class-diagram.jpg`, añadir las 3 capturas obligatorias en `README.md` (tablero, salas, histórico) y documentar arquitectura y límites conocidos.
9. **GitFlow final**:
   - Merge `develop` → `master` y crear el tag de entrega final.
