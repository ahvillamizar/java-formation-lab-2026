# Estrategia de testing — Catálogo de productos

> Completa esta plantilla. Es el entregable que diferencia el nivel **Experto**: no basta con que los
> tests pasen, hay que justificar **qué se prueba dónde y por qué**.

## 1. Pirámide propuesta

| Nivel | Herramienta | Qué valida | Qué NO valida | Nº de tests | Tiempo aprox. |
|-------|-------------|------------|---------------|-------------|---------------|
| Unitario (dominio) | JUnit 5 | Invariantes de `Product` y precisión de `BigDecimal` | Serialización HTTP y wiring de Spring | 2 | Milisegundos; por medir |
| Unitario (aplicación) | JUnit 5 + Mockito | Generación de id, persistencia y producto inexistente | Configuración de Spring y HTTP | 2 | Milisegundos; por medir |
| Slice web | `@WebMvcTest` + `MockMvc` | Contrato HTTP, validación, respuestas y autorización | Persistencia y wiring de toda la aplicación | 7 | Segundos; por medir |
| Integración | `@SpringBootTest` | Contexto y recorrido servicio/repositorio en memoria | Persistencia durable y despliegue real | 1 | Segundos; por medir |

## 2. Matriz de decisión

Para cada regla, marca en qué nivel se prueba y justifica.

| Regla / comportamiento | Dominio | Aplicación | Slice web | Integración | Justificación |
|------------------------|:-------:|:----------:|:---------:|:-----------:|---------------|
| El precio debe ser > 0 | X | | X | | Dominio protege la invariante; el slice verifica el contrato de entrada. |
| El nombre vacío devuelve 400 | | | X | | Validación Bean Validation y formato de error son responsabilidad del borde HTTP. |
| Producto inexistente devuelve 404 | | X | X | | Servicio expresa el caso de ausencia; slice valida su traducción HTTP. |
| Se genera un id al crear | | X | | | El caso de uso asigna el id antes de invocar el puerto de persistencia. |
| `Location` apunta al recurso creado | | | X | | Es parte del contrato HTTP de creación. |
| `DELETE` exige rol `ADMIN` | | | X | | El slice importa la configuración real de seguridad y verifica autorización. |
| Las capas quedan cableadas | | | | X | El smoke test arranca el contexto y recorre servicio y repositorio. |

## 3. Decisiones y trade-offs

- `@MockBean` reemplaza el puerto de aplicación dentro del contexto MVC para aislar el controller sin cargar persistencia. `@Mock` crea un colaborador Mockito sin arrancar Spring, manteniendo unitarios los tests del servicio.
- Usar `@SpringBootTest` por cada comportamiento HTTP elevaría el coste de arranque del contexto y alargaría el pipeline. En este ejercicio el tiempo real queda pendiente de medir; se separa en 7 tests de slice y un solo smoke test completo.
- Spring reutiliza contextos cuando las clases comparten configuración, perfiles, propiedades y mocks. Cambios en propiedades, perfiles, configuración importada o definición de mocks pueden crear otro contexto y reducir el acierto de caché.
- La persistencia en memoria no prueba durabilidad ni concurrencia de una base real. Añadiría tests de integración con la base elegida, contract tests para consumidores y pruebas de carga para dimensionar latencia y throughput.

## 4. Mediciones

| Comando | Tests | Tiempo reportado por Surefire |
|---------|-------|-------------------------------|
| `mvn -pl ejercicio-2-construccion test -Dtest='ProductControllerTest*'` | 7 (pendiente de ejecutar) | No disponible: el runner no ejecutó el comando |
| `mvn -pl ejercicio-2-construccion verify` | 12 (pendiente de ejecutar) | No disponible: el runner no ejecutó el comando |
