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
| **Control de turno activo (`w` / `b`)** | **Baja** | Evita mover piezas del rival en backend y muestra de quién es el turno en la UI. |
| **Aviso visual de Jaque + Jaque Mate / Ahogado** | **Media** | Marca al rey amenazado y detecta el fin de partida automático. |
| **Promoción de peón** | **Media** | Permite coronar al llegar a la última fila (UCI tipo `e7e8q` + selector visual). |
| **Enroque y captura al paso (*en passant*)** | **Media-Alta** | Completa las reglas oficiales del ajedrez usando `State` (derechos de enroque y casilla *en passant*). |

---

### 2. Arquitectura del Frontend (`frontend/`)

| Tarea | Dificultad | Qué aporta |
| :--- | :---: | :--- |
| **Desacoplar `fetch` de la UI** | **Baja** | Separa la comunicación HTTP de `ChessGameClient` en un adaptador/servicio sustituible por mocks (requisito de rúbrica). |

---

### 3. Salas, Usuarios y Partida entre 2 jugadores (`backend/room` / `backend/user`)

| Tarea | Dificultad | Qué aporta |
| :--- | :---: | :--- |
| **Identificación básica de usuarios** | **Baja-Media** | Asociar jugadores (blancas / negras) a cada partida y sala. |
| **Gestión de salas (crear, listar, unirse por código)** | **Media** | Núcleo funcional de la entrega (*Sistema de salas de ajedrez*). |
| **Sincronización entre 2 clientes (WebSocket o polling)** | **Media-Alta** | Permite jugar en tiempo real desde dos navegadores distintos. |
| **Manejo y tests de casos de error (5% rúbrica)** | **Baja-Media** | Respuestas claras y tests ante sala inexistente, sala llena o jugada inválida. |

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

## 🎯 Orden recomendado para avanzar sin agobios

1. **Corto plazo (victoria rápida):** Control de turno (`w` / `b`) + desacoplar `fetch` en el frontend.
2. **Siguiente salto grande:** Jaque mate / ahogado + aviso visual en el tablero.
3. **Bloque de rúbrica clave:** Módulo de salas (`backend/room`) + sincronización de 2 jugadores + persistencia en SQLite.
4. **Remate final:** Aportación propia (ej. reloj o historial de jugadas en pantalla), capturas y entrega en `master`.
