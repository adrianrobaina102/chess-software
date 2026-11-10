# 📚 Documentación Técnica — Chess Software (ULPGC)

Bienvenido al centro de documentación técnica del sistema de **Salas para Jugar al Ajedrez**, proyecto de la asignatura **Ingeniería del Software 2** en la Escuela de Ingeniería Informática de la ULPGC.

---

## 🗂️ Índice de Secciones

| Documento | Descripción |
| :--- | :--- |
| **[`diagrams/`](diagrams/README.md)** | **Diagrama de Clases UML** detallado del backend (`backend/game`), modelando dominio, casos de uso, puertos (`in`/`out`) y adaptadores (`in`/`out`). |
| **[`architecture.md`](architecture.md)** | **Arquitectura y Flujos del Sistema**: Diagramas de secuencia y flujos paso a paso (carga de tablero inicial y aplicación de movimientos). |
| **[`frontend-cycle.md`](frontend-cycle.md)** | **Ciclo de Frontend y Concurrencia**: Diagrama de secuencia del cliente SPA interactivo en JavaScript. |
| **[`agent.md`](agent.md)** | **Reglas del Agente y Protocolo de Vigilancia Proactiva**: Filosofía de trabajo, GitFlow, estándares de calidad, Pure DI y rubric checks. |

---

## 🏛️ Principios Arquitectónicos Fundamentales

1. **Arquitectura Hexagonal (Puertos y Adaptadores):**  
   El Core (`architecture`) está completamente aislado del exterior. No contiene dependencias de Javalin (`Context`), frameworks HTTP, sockets ni persistencia directa.
2. **Puertos Segregados en Entrada y Salida:**  
   - `ports.in`: Contratos de entrada y lectura (deserializadores FEN, UCI, Algebraic, State).
   - `ports.out`: Contratos de salida y escritura (serializadores FEN, UCI, Algebraic y repositorios).
3. **Inversión de Dependencias Pura (Pure DI):**  
   Los adaptadores reciben todas sus dependencias por constructor (`private final`). El `Main.java` (Composition Root) es el único responsable de instanciar y ensamblar el grafo.
4. **Dominio Inmutable:**  
   Todas las entidades y objetos de valor (`Board`, `Square`, `Move`, `Piece`, `State`) son inmutables, usando `record` y `enum` de Java 27.
