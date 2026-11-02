# Arquitectura Hexagonal y Flujo del Backend — Chess Software

Este documento describe la arquitectura en capas adoptada en el proyecto de ajedrez (ULPGC), garantizando el desacoplamiento absoluto de las reglas del juego frente a frameworks web, notaciones y persistencia externa.

---

## 1. Diagrama de Capas (Ports & Adapters)

```text
┌────────────────────────────────────────────────────────────────────────┐
│                        INFRASTRUCTURE LAYER                            │
│  ┌───────────────────────┐  ┌─────────────────┐  ┌──────────────────┐  │
│  │ Javalin Web Endpoints │  │   Adapters FEN  │  │   Adapters UCI   │  │
│  │ (/initial, /move)     │  │ (Board, State)  │  │  (Move Serial.)  │  │
│  └───────────┬───────────┘  └────────┬────────┘  └────────┬─────────┘  │
│              │                       │                    │            │
│              ▼                       │                    │            │
│  ┌───────────────────────────────────┴────────────────────┴─────────┐  │
│  │                    APPLICATION LAYER                             │  │
│  │   - Casos de Uso: GetInitialBoardUseCase, MovePieceUseCase        │  │
│  │   - Puertos Entrada: BoardDeserializer, MoveDeserializer         │  │
│  │   - Puertos Salida: BoardSerializer, MoveSerializer              │  │
│  └───────────────────────────────────┬──────────────────────────────┘  │
│                                      │                                 │
│                                      ▼                                 │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                       DOMAIN LAYER (Core)                        │  │
│  │   - Entidades inmutables: Board, Move, Square, State             │  │
│  │   - Enumerados: Piece, Color, File, Rank                         │  │
│  │   - Cero dependencias externas (Java Puro)                       │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Esquema de Ejecución de una Petición HTTP

```text
[ Cliente Web / Navegador (fetch /api/board/move) ]
       │
       ▼
[ Javalin Handler (Main) ]
       │
       ▼ (Inyección de Context)
[ MovePieceEndPoint.execute() ] ──> Extrae queryParams: board (FEN) y move (UCI)
       │
       ▼ (Instancia MovePieceUseCase.Input)
[ MovePieceUseCase.execute(input) ]
       │
       ├──> [ BoardDeserializer (FEN) ] ──> Genera Board (Dominio)
       ├──> [ MoveDeserializer (UCI) ]  ──> Genera Move (Dominio)
       │
       ▼
[ Board.move(move) ] ──> Produce un nuevo Board inmutable con la pieza desplazada
       │
       ▼
[ BoardSerializer (FEN) ] ──> Transforma el nuevo Board a String FEN
       │
       ▼
[ context.result(newFen) ] ──> Respuesta HTTP 200 OK enviada al navegador
```

---

## 3. Responsabilidades por Capa

| Capa | Responsabilidad | Restricciones estrictas |
| --- | --- | --- |
| **Domain (`application.domain.model`)** | Entidades inmutables, geometría del tablero y representación pura del ajedrez. | Java puro estándar (`record`, `enum`, `class final`). Prohibidas librerías externas o formatos de transporte. |
| **Ports (`application.ports`)** | Contratos de interfaz segregados (ISP). Entrada (`deserializer`) y Salida (`serializer`). | Interfaces limpias con tipos del dominio o primitivos. Nunca acoplar a librerías concretas. |
| **UseCases (`application.usecases`)** | Orquestadores de la lógica de negocio. Directores de orquesta del sistema. | Solo dependen de Puertos y Dominio. No conocen HTTP ni Javalin. |
| **Adapters (`infrastructure.adapters`)** | Traductores de entrada/salida: notaciones FEN, UCI, algebraica y Endpoints web. | Implementan los puertos y adaptan el exterior hacia el Core. |
| **Composition Root (`Main.java`)** | Cableado de dependencias (Pure DI) y arranque del servidor Javalin. | Único lugar donde se instancian y enlazan las implementaciones concretas. |

---

## 4. Patrones de Diseño Aplicados

1. **Ports & Adapters (Arquitectura Hexagonal):** Desacopla completamente el núcleo de ajedrez de Javalin y de los formatos externos de intercambio.
2. **Pure Dependency Injection (Inversión de Control por Constructor):** Ninguna clase de negocio ni adaptador crea sus dependencias con `new`; todo colaborador se inyecta por constructor.
3. **Pipeline / Method Chaining en Casos de Uso:** Descomposición paso a paso mediante métodos privados en cascada (`process(...)`), garantizando claridad y trazabilidad de cada fase.
4. **Command / Request Object (`record Input`):** Agrupa los parámetros de entrada del caso de uso de forma inmutable y fuertemente tipada.
