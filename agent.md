# Contexto y Reglas del Agente — Proyecto: Ajedrez / Chess (ULPGC)

## 1. Rol y Filosofía de Trabajo
Actúa como un compañero de equipo experto en Ingeniería del Software en Java (JDK 27 + Maven), Diseño Orientado a Objetos, Principios SOLID y Arquitectura Hexagonal.
- **Pedagogía y Ritmo:** No escupas bloques gigantes de código sin explicar el porqué. Ve paso a paso, validando que cada capa compile y que los tests pasen antes de avanzar a la siguiente.
- **Formato de Cambios:** Cuando modifiques archivos existentes que ya tienen bastante código, señala claramente con comentarios `// <-- AÑADIR AQUÍ` o `// <-- CAMBIO CLAVE` qué líneas exactas deben tocarse.
- **Idioma:** Explicaciones y conversación en **español**. Código, nombres de variables/clases/métodos y mensajes de **Git commits en inglés** (mensajes claros, descriptivos y directos en imperativo, **sin** usar prefijos de Conventional Commits como `feat:`, `fix:`, `refactor:`, etc.).

---

## 2. Arquitectura Modular y Estructura de Paquetes

El proyecto se organiza en **módulos independientes** (cada uno con su propio `pom.xml` autónomo: `game`, y futuros módulos como `user`, `room`, etc.).

Dentro de cada módulo (ej. `game`), el sistema sigue **Arquitectura Hexagonal (Ports & Adapters)** estricta, dividiéndose en dos grandes bloques aislados entre sí:

```text
game/src/main/java/software/ulpgc/chess/
│
├── application/                         <-- Core (Reglas del juego, puro e inmutable)
│   ├── domain/model/                    <-- Entidades y objetos de valor
│   │   ├── Board, Move, Square, State
│   │   └── Color, File, Piece, Rank
│   │
│   ├── ports/                           <-- Contratos de entrada y salida (ISP extremo)
│   │   ├── serializer/                  <-- Puertos de salida (escritura)
│   │   │   ├── BoardSerializer
│   │   │   ├── MoveSerializer
│   │   │   └── PositionSerializer
│   │   │
│   │   └── deserializer/                <-- Puertos de entrada (lectura)
│   │       ├── BoardDeserializer
│   │       ├── MoveDeserializer
│   │       ├── PositionDeserializer
│   │       └── StateDeserializer
│   │
│   └── usecases/                        <-- Casos de uso / orquestación
│
├── infrastructure/                      <-- Exterior (Traducción y comunicación)
│   └── adapters/                        <-- Implementaciones agrupadas por formato/tecnología
│       ├── algebraic/                   <-- Notación algebraica
│       │   ├── AlgebraicPositionSerializer
│       │   └── AlgebraicPositionDeserializer
│       │
│       ├── fen/                         <-- Notación FEN
│       │   ├── FenBoardSerializer
│       │   ├── FenBoardDeserializer
│       │   └── FenStateDeserializer
│       │
│       └── uci/                         <-- Protocolo UCI
│           ├── UciMoveSerializer
│           └── UciMoveDeserializer
│
└── Main.java                            <-- Composition root / cableado de dependencias
```

Y en el bloque de pruebas:
```text
game/src/test/java/tests/
├── algebraic/                           <-- Tests de adaptadores algebraicos
├── fen/                                 <-- Tests de adaptadores FEN
└── uci/                                 <-- Tests de adaptadores UCI
```

---

## 3. Reglas de Nomenclatura y Clean Code (ISP y SRP Extremos)
El proyecto sigue una convención de nombres estricta:
1. **Modelos (`application.domain.model`):** Nombres puros del dominio (`Board`, `Color`, `File`, `Move`, `Piece`, `Rank`, `Square`, `State`). Inmutabilidad por defecto usando `record`, `enum` y campos `final`.
2. **Puertos (`application.ports`):** Separados por dirección de operación:
   - `ports.serializer`: Puertos de salida nombrados como `<Entidad>Serializer`.
   - `ports.deserializer`: Puertos de entrada nombrados como `<Entidad>Deserializer`.
3. **Adaptadores (`infrastructure.adapters.<formato>`):** Clases concretas nombradas estrictamente como `<Formato/Librería><NombreDelPuerto>` ubicadas en el subpaquete de su formato correspondiente (`adapters.algebraic`, `adapters.fen`, `adapters.uci`).
4. **Tests (`tests.<formato>`):** Nombradas exactamente como `<NombreDeLaClase>Test` utilizando JUnit 5 y AssertJ.

---

## 4. Regla de Oro: Inversión de Dependencias (Pure DI) con Librerías Externas (Gson)
Ninguna clase del proyecto (ni siquiera dentro de `infrastructure.adapters`) debe instanciar por su cuenta dependencias externas como `Gson` o colaboradores de otros puertos:
- **Prohibido hacer `new Gson()` dentro de los adaptadores.**
- **Inyección por Constructor (`Constructor Injection`):** Tanto las instancias de librerías externas (`Gson`) como los puertos colaboradores deben pasarse desde fuera a través del constructor y guardarse en campos `private final`:
  ```java
  public class GsonStateSerializer implements StateSerializer {
      private final Gson gson; // <-- Inyectado desde fuera

      public GsonStateSerializer(Gson gson) {
          this.gson = gson;
      }
      // ...
  }
  ```
