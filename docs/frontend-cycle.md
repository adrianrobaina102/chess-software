# Ciclo de Vida del Frontend — Chess Software

Este documento describe de forma simple y visual cómo el cliente web interactúa con el backend.

---

## 1. Esquema Simple del Ciclo Web

```text
[ Usuario ] ── Clic en el navegador (http://localhost:7070)
    │
    ├─> Al entrar a la web:
    │   [ app.js ] ── GET /api/board/initial ──> [ Backend ] ──> Devuelve FEN inicial
    │       │
    │       └─> Pinta el tablero con las piezas iniciales ♔ ♕ ♖
    │
    └─> Al mover una pieza (ej: 'e2' a 'e4'):
        [ Clic 1: Casilla 'e2' ] ──> Resalta la pieza
        [ Clic 2: Casilla 'e4' ] ──> Forma la jugada "e2e4"
            │
            ▼
        [ app.js ] ── GET /api/board/move?board={fen}&move=e2e4 ──> [ Backend ]
            │                                                              │
            │                                                              ▼
            │                                              Calcula nuevo tablero
            │                                                              │
            ▼                                                              ▼
        [ app.js ] <─────────────────── Devuelve nuevo FEN <───────────────┘
            │
            ▼
        Mueve el peón a 'e4' en la pantalla
```

---

## 2. Componentes Básicos (`game/src/main/resources/public/`)

* **`index.html`:** La estructura de la página (el tablero y el indicador de turno).
* **`style.css`:** El diseño visual (colores de las casillas y tamaño).
* **`app.js`:** 
  1. Pide el tablero inicial al servidor (`fetch('/api/board/initial')`).
  2. Detecta los dos clics del ratón (origen y destino).
  3. Le pide al servidor mover la pieza (`fetch('/api/board/move?board=...&move=...')`).
  4. Actualiza la pantalla con la respuesta.
