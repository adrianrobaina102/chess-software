# Arquitectura y Flujos del Sistema — Chess Software

Este documento describe de forma visual e inmediata cómo fluyen las acciones en el sistema, separando cada ciclo en su propio diagrama para que sea rápido y fácil de entender.

---

## 1. Flujo 1: Carga y Renderizado del Tablero Inicial

Muestra cómo el navegador solicita el estado inicial del ajedrez y el backend lo construye desde el dominio puro.

```text
[ Pantalla del Tablero (Navegador: http://localhost:7070) ]
       │
       ▼ (Al cargar la página: window.DOMContentLoaded)
[ ChessGameClient.fetchInitialBoard() ]
       │
       ▼ (fetch GET /api/board/initial)
[ Javalin Router (Main: puerto 7070) ]
       │
       ▼
[ GetInitialBoardEndPoint.execute() ]
       │
       ▼ (Llamada limpia sin parámetros)
[ GetInitialBoardUseCase.execute() ]
       │
       ├──> [ Board.initial() ] ──────────> Construye piezas en posición inicial (Dominio)
       │
       ▼ (Pasa el Board al puerto)
[ FenBoardSerializer.serialize(board) ] ──> Genera "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"
       │
       ▼
[ context.result(fen) ] ──────────────────> Respuesta HTTP 200 (text/plain)
       │
       ▼
[ app.js: parseFenToMatrix() ] ───────────> Genera matriz 8x8 y dibuja piezas ♔ ♕ ♖ en el DOM
```

---

## 2. Flujo 2: Ejecución de un Movimiento de Pieza

Muestra el ciclo completo desde que el usuario hace clic en dos casillas hasta que el backend valida y devuelve el nuevo estado.

```text
[ Interacción del Usuario en el Tablero ]
       │
       ├─ Clic 1: Casilla origen (ej: 'e2' con peón ♙) ──> Resalta casilla seleccionada
       └─ Clic 2: Casilla destino (ej: 'e4')          ──> Forma movimiento UCI: "e2e4"
       │
       ▼
[ ChessGameClient.makeMove("e2", "e4") ]
       │
       ▼ (fetch GET /api/board/move?board={fen}&move=e2e4)
[ Javalin Router (Main: puerto 7070) ]
       │
       ▼
[ MovePieceEndPoint.execute() ]
       │
       ├──> Extrae queryParam("board")  (String FEN)
       ├──> Extrae queryParam("move")   (String UCI)
       │
       ▼ (Encapsula en Command inmutable)
[ MovePieceUseCase.Input(board, move) ]
       │
       ▼
[ MovePieceUseCase.execute(input) ] ── (Tubería de transformación limpia: process)
       │
       ├──> [ FenBoardDeserializer ] ──> Convierte FEN a Board (Dominio)
       ├──> [ UciMoveDeserializer ]  ──> Convierte "e2e4" a Move (from: e2, to: e4)
       │
       ▼
[ Board.move(move) ] ──────────────────> Produce un nuevo Board inmutable con la pieza desplazada
       │
       ▼
[ FenBoardSerializer.serialize(newBoard) ] ──> Convierte a "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR"
       │
       ▼
[ context.result(newFen) ] ────────────> Respuesta HTTP 200 con el nuevo FEN
       │
       ▼
[ app.js: actualiza currentFen ] ──────> Re-renderiza el tablero: el peón blanco aparece en e4
```

---

## 3. Mapa de Componentes: ¿Dónde vive cada pieza?

Para ubicar cualquier archivo en 2 segundos según la Arquitectura Hexagonal:

```text
backend/game/src/main/java/software/ulpgc/chess/
│
├── application/                         <-- EL CORE (Negocio puro, no sabe qué es Javalin ni HTML)
│   ├── domain/model/                    <-- Entidades inmutables: Board, Move, Square, State, Piece, Color...
│   ├── ports/                           <-- Contratos de interfaz (ISP):
│   │   ├── serializer/                  <-- Salida: BoardSerializer, MoveSerializer...
│   │   └── deserializer/                <-- Entrada: BoardDeserializer, MoveDeserializer...
│   └── usecases/                        <-- Acciones del sistema: GetInitialBoardUseCase, MovePieceUseCase
│
├── infrastructure/                      <-- EL EXTERIOR (Traducción y librerías)
│   └── adapters/                        <-- Implementaciones concretas:
│       ├── algebraic/                   <-- Notación de casillas 'e4' (AlgebraicPosition...)
│       ├── fen/                         <-- Notación FEN (FenBoardSerializer/Deserializer...)
│       ├── uci/                         <-- Notación UCI 'e2e4' (UciMoveSerializer/Deserializer)
│       └── endpoints/                   <-- Adaptadores Web Javalin (GetInitialBoardEndPoint, MovePieceEndPoint)
│
└── Main.java                            <-- COMPOSITION ROOT (Une todo con Pure DI y arranca el servidor)
```

