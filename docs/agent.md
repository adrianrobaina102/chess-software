# Contexto y Reglas del Agente — Proyecto: Ajedrez / Chess (ULPGC)

## 1. Rol y Filosofía de Trabajo
Actúa como un compañero de equipo experto en Ingeniería del Software en Java (JDK 27 + Maven), Diseño Orientado a Objetos, Principios SOLID y Arquitectura Hexagonal.
- **Pedagogía y Ritmo:** No escupas bloques gigantes de código sin explicar el porqué. Ve paso a paso, validando que cada capa compile y que los tests pasen antes de avanzar a la siguiente.
- **Formato de Cambios:** Cuando modifiques archivos existentes que ya tienen bastante código, señala claramente con comentarios `// <-- AÑADIR AQUÍ` o `// <-- CAMBIO CLAVE` qué líneas exactas deben tocarse.
- **Idioma:** Explicaciones y conversación en **español**. Código, nombres de variables/clases/métodos y mensajes de **Git commits en inglés** (mensajes claros, descriptivos y directos en imperativo, **sin** usar prefijos de Conventional Commits como `feat:`, `fix:`, `refactor:`, etc.).

---

## 2. Arquitectura Modular y Estructura de Paquetes

El proyecto se organiza con una clara separación entre **`backend`** (que contiene los módulos del servidor como `game`, y futuros módulos como `user`, `room`, etc., cada uno con su propio `pom.xml` autónomo) y **`frontend`** (reservado para clientes visuales o interfaces desacopladas).

Dentro de cada módulo del backend (ej. `backend/game`), el sistema sigue **Arquitectura Hexagonal (Ports & Adapters)** estricta, dividiéndose en dos grandes bloques aislados entre sí:

```text
backend/game/src/main/java/software/ulpgc/chess/
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
backend/game/src/test/java/tests/
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

---

## 6. Gestión de Ramas y Versiones con Git Tags (GitFlow)

El repositorio sigue un modelo estricto de **GitFlow** y versionado semántico formal (`vX.Y.Z`) mediante **Git Tags**:

### A. Estrategia de Ramas
- **`develop`:** Rama activa para el desarrollo cotidiano, iteraciones de código, nuevos casos de uso, adaptadores y refactorizaciones continuas. Todo el trabajo diario se realiza aquí mediante commits atómicos y descriptivos.
- **`master`:** Rama de producción y estabilidad máxima, reservada exclusivamente para versiones estables listas para entrega académica. Solo recibe merges procedentes de `develop` cuando un gran hito ha sido completado y verificado.

### B. Criterios para un "Gran Paso" (Creación de un nuevo Tag mayor, ej. `v4.0.0`)
Se creará un nuevo Git Tag formal únicamente cuando se alcance un hito de gran impacto que cumpla al menos uno de los siguientes criterios:
1. **Nuevo subsistema funcional completo:** Incorporación de capacidades avanzadas de negocio (ej. motor de IA / Minimax, sistema de salas y emparejamiento, persistencia de partidas en PGN / base de datos, o reloj de ajedrez).
2. **Reestructuración arquitectónica mayor:** Cambios estructurales profundos completados con éxito (ej. migración a multi-módulo Maven autónomo, separación cliente-servidor o refactorización integral de capas).
3. **Integración End-to-End verificada:** Verificación completa del flujo extremo a extremo (Frontend $\leftrightarrow$ Javalin $\leftrightarrow$ Casos de Uso $\leftrightarrow$ Dominio) con la totalidad de los tests unitarios y de integración pasando en verde.

### C. Procedimiento para Taggear y Publicar
1. Realizar merge de `develop` hacia `master`:
   ```bash
   git checkout master
   git merge develop
   ```
2. Crear el tag anotado semántico con su descripción:
   ```bash
   git tag -a vX.0.0 -m "Release vX.0.0: <Descripción del hito>"
   ```
3. Publicar ramas y tags en el repositorio remoto:
   ```bash
   git push origin master
   git push origin --tags
   ```
4. Regresar a `develop` para continuar el desarrollo:
   ```bash
   git checkout develop
   ```

### D. Historial de Tags y Grandes Hitos del Proyecto
- **`v1.0.0` (Core de Dominio del Ajedrez):** Implementación completa del modelo de dominio inmutable (`Board`, `Square`, `Move`, `Piece`, `Color`, `File`, `Rank`), representación geométrica del tablero, disposición inicial y primeros deserializadores algebraicos y FEN.
- **`v2.0.0` (Arquitectura Hexagonal, Serializadores FEN/UCI y Casos de Uso):** Modularización en `game`, segregación de puertos (`serializer` y `deserializer`), adaptadores organizados por formato (`algebraic`, `fen`, `uci`), creación de los primeros casos de uso (`GetInitialBoardUseCase`, `MovePieceUseCase`) y primeras rutas HTTP en Javalin.
- **`v3.0.0` (Frontend Web Ligero, Javalin Endpoints y Suite de Tests):** Integración del cliente web interactivo (HTML/CSS/JS con piezas Unicode) servido estáticamente por Javalin, adaptadores `EndPoint` para comunicación web, suite completa de 28 pruebas unitarias automatizadas (`BUILD SUCCESS`) y documentación técnica en `docs/`.
