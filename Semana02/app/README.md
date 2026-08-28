## Fase 2: Refactorización con IA (rama con-ia)

### Objetivo
Aplicar los 4 pilares de POO (abstracción, herencia, encapsulamiento,
polimorfismo) al proyecto del carrito de compras, usando Gemini como
asistente de IA.

### Proceso de generación del prompt
Se consultó primero sobre cómo estructurar un buen prompt de
refactorización, dividiéndolo en: contexto, objetivo, restricciones
y formato de salida esperado.

### Estructura de un buen prompt (según lo consultado con la IA)

Para pedir ayuda de forma efectiva, un buen prompt debe tener:

1. **Contexto:** qué es el proyecto y en qué estado se encuentra
   actualmente (ej. "tengo un carrito de compras en Kotlin que ya
   calcula subtotal, IGV y descuento").

2. **Objetivo:** qué se quiere lograr específicamente (ej. "aplicar
   los 4 pilares de POO: herencia, polimorfismo, abstracción y
   encapsulamiento").

3. **Restricciones:** qué debe mantenerse sin cambios (ej. "sin
   perder la lógica de cálculo existente ni el formato de salida
   en consola").

4. **Formato de salida esperado:** cómo se quiere recibir la
   respuesta (ej. "código Kotlin comentado", o "explícame primero
   en texto antes de darme código").

Esta estructura se aplicó en cada uno de los prompts usados a lo
largo del proceso (ver sección siguiente).

### Prompts utilizados

**Prompt 1 (análisis, sin código):**
"Analiza el código de Carrito.kt (no hagas cambios todavía). Quiero refactorizarlo 
para aplicar los 4 pilares de POO: herencia, polimorfismo, abstracción y encapsulamiento, sin perder la lógica de cálculo de subtotal, IGV, descuento ni el formato de salida en consola.
Dame tu propuesta explicada en texto (no código todavía):
• ¿Cómo estructurarías la jerarquía de clases (qué subclases de Producto propones y por qué)?
• ¿Qué atributos encapsularías?
• ¿Dónde aplicarías polimorfismo?
Después de que confirme la propuesta, te pido el código."

**Prompt 2 (jerarquía de clases):**
"De acuerdo con la propuesta. Empecemos por partes:
Paso 1: Dame solo el código de la clase abstracta Producto y las subclases Electronico y 
Accesorio (con el método abstracto calcularImporte()). Todavía no toques main() ni las demás funciones."

**Prompt 3 (encapsulamiento):**
"Continuemos con el paso 2: aplica encapsulamiento. Crea una clase Carrito que encapsule la lista de 
productos (private), con métodos agregarProducto() y eliminarProducto(). También encapsula el atributo cantidad en Producto con validación (que nunca sea menor a 1). Todavía no toques main() del todo, solo dame las clases nuevas."

**Prompt 4 (polimorfismo/centralización):**
""Sí, procedamos con el Paso 3."
(esto continuó la conversación donde Gemini propuso mover
calcularSubtotal, calcularIGV, calcularDescuento y mostrarDetalle
dentro de la clase Carrito, usando calcularImporte() para activar
el polimorfismo)

**Prompt 5 (refactorizar main):**
"Sí, listo para el Paso 4"
(esto hizo que Gemini actualizara main() para usar los métodos de
Carrito y eliminara las funciones globales duplicadas)

**Prompt 6 (documentación):**
"Para terminar, agrega comentarios de documentación (KDoc) claros
en cada clase y método explicando qué pilar de POO representa y
por qué..."

### Resultado
- Abstracción: `Producto` como clase abstracta con método `calcularImporte()` sin implementar
- Herencia: `Electronico` y `Accesorio` heredan de `Producto`
- Polimorfismo: cada subclase implementa `calcularImporte()` de forma distinta
- Encapsulamiento: `Carrito` oculta la lista de productos (`private`), y `cantidad` valida que nunca sea menor a 1

