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
│       ├── uci/                         <-- Protocolo UCI
│       │   ├── UciMoveSerializer
│       │   └── UciMoveDeserializer
│       │
│       └── endpoints/                   <-- Adaptadores de entrada Web (Javalin)
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
3. **Adaptadores (`infrastructure.adapters.<formato>`):** Clases concretas nombradas estrictamente como `<Formato/Librería><NombreDelPuerto>` ubicadas en el subpaquete de su formato correspondiente (`adapters.algebraic`, `adapters.fen`, `adapters.uci`), o `<Recurso>EndPoint` en `adapters.endpoints`.
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

---

## 5. Reglas de Integración HTTP (Servidor Web con Javalin)
El sistema expone su lógica de negocio al exterior mediante una API HTTP utilizando **Javalin**. La integración debe respetar estrictamente la Arquitectura Hexagonal y la Inversión de Control:

### A. Aislamiento del Core (Application)
- **Cero dependencias web:** El directorio `application` (Dominio, Casos de Uso y Puertos) **jamás** debe importar clases de Javalin (como `Context`), ni saber que existe un servidor web.
- Los Casos de Uso orquestan la lógica y devuelven tipos primitivos, objetos del dominio, o `String` (usando un puerto de serialización) de vuelta al adaptador que los llamó.

### B. Adaptadores de Entrada Web (Endpoints)
- Se alojan en `infrastructure.adapters.endpoints`.
- **Misión de Traducción:** Un `EndPoint` actúa como traductor bilingüe entre Javalin y el Core.
- **Regla de Inyección:** Deben recibir por constructor el/los Casos de Uso necesarios y el objeto `Context` de Javalin.
- **Flujo estricto del método `execute()`:**
  1. Extraer los parámetros de la petición HTTP (`context.queryParam(...)`, `context.pathParam(...)`, o `context.body()`).
  2. Ejecutar el Caso de Uso pasándole **solo** tipos de Java estándar (`String`, `int`, etc.).
  3. Recoger el resultado devuelto por el Caso de Uso.
  4. Enviar la respuesta al cliente obligatoriamente mediante `context.result(resultado)`.

### C. Ensamblaje en el Main (Composition Root)
- La clase `Main` es el **único** lugar autorizado para hacer `Javalin.create().start(...)`.
- **Enrutamiento dinámico:** La instanciación del `EndPoint` y la inyección del `Context` se realiza dentro de la definición de la ruta HTTP. Ejemplo estricto a seguir:
  ```java
  app.get("/api/recurso", context -> {
      new RecursoEndPoint(miCasoDeUso, context).execute();
  });
  ```
