# Arquitectura del Frontend y Comunicación Web — Chess Software

Este documento describe la arquitectura técnica del cliente web ligero y su ciclo de vida con el servidor Javalin.

---

## 1. Diagrama de Secuencia: Ciclo de Vida del Frontend

```mermaid
sequenceDiagram
    autonumber
    actor Usuario
    participant UI as Navegador Web (DOM)
    participant Client as ChessGameClient (app.js)
    participant Server as Servidor Javalin (Puerto 7070)
    participant Core as Backend Hexagonal

    Usuario->>UI: Accede a http://localhost:7070
    Server-->>UI: Sirve index.html, style.css y app.js
    Client->>Server: fetch('/api/board/initial')
    Server->>Core: GetInitialBoardUseCase.execute()
    Core-->>Server: Retorna FEN inicial
    Server-->>Client: FEN String (HTTP 200)
    Client->>UI: Renderiza cuadrícula 8x8 con piezas Unicode

    Usuario->>UI: Clic en 'e2' (selección origen)
    UI->>Client: onSquareClicked('e2', 'P')
    Client->>UI: Resalta casilla seleccionada

    Usuario->>UI: Clic en 'e4' (destino)
    UI->>Client: onSquareClicked('e4', null)
    Client->>Server: fetch('/api/board/move?board={fen}&move=e2e4')
    Server->>Core: MovePieceUseCase.execute(Input)
    Core-->>Server: Retorna nuevo FEN con peón en e4
    Server-->>Client: FEN String actualizado (HTTP 200)
    Client->>UI: Re-renderiza tablero con la pieza desplazada
```

---

## 2. Componentes del Cliente Web

Ubicados en `game/src/main/resources/public/`:

1. **`index.html`:**
   - Estructura semántica sin frameworks.
   - Contenedor del tablero `#board` y panel lateral `#controls` con estado y campo FEN en tiempo real.
2. **`style.css`:**
   - Grid CSS puro de $8 \times 64\text{px}$ para el tablero.
   - Colores clásicos de madera para casillas (`#f0d9b5` / `#b58863`) y resaltado de selección.
3. **`app.js` (`ChessGameClient`):**
   - **Parser FEN:** `parseFenToMatrix(fen)` convierte la notación FEN en una matriz de $8 \times 8$.
   - **Mapeador Unicode:** Traduce identificadores de pieza (`K, Q, R, B, N, P, k, q, r...`) a glifos tipográficos oficiales de ajedrez (♔, ♕, ♖, ♗, ♘, ♙...).
   - **Rutas Relativas:** Utiliza URLs relativas (`/api/board/...`) para garantizar portabilidad inmediata a cualquier dominio o entorno sin tocar código.
