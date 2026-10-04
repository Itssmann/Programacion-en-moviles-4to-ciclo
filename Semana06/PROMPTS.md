Prompt 1 - Badge y contador de favoritos

Analiza primero el proyecto actual Semana06 antes de realizar cambios.

El proyecto es una aplicación TECSUP Store desarrollada con Kotlin y Jetpack Compose. Ya cuenta con:

- TarjetaProducto.kt con un DropdownMenu por producto.
- El DropdownMenu tiene las opciones Favoritos, Compartir y Reportar.
- AppDrawer.kt con un NavigationDrawer.
- El drawer tiene Inicio, Mis pedidos, Favoritos y Perfil.
- AppNavigation.kt controla la navegación.
- HomeScreen.kt muestra la lista de productos.
- La navegación actual ya funciona correctamente.

Quiero implementar únicamente la siguiente mejora, sin modificar innecesariamente la estructura ni eliminar funcionalidades existentes:

Cuando el usuario seleccione "Favoritos" desde el DropdownMenu de un producto, ese producto debe quedar marcado como favorito.

El estado de favoritos debe manejarse en un nivel superior para que pueda ser compartido entre las tarjetas de productos y el NavigationDrawer.

En el ítem "Favoritos" del NavigationDrawer agrega un Badge de Material 3 que muestre la cantidad actual de productos marcados como favoritos.

Requisitos:
1. El contador debe iniciar en 0.
2. Al marcar un producto como favorito, el contador debe aumentar.
3. Un mismo producto no debe contarse dos veces.
4. El cambio debe reflejarse inmediatamente en el badge del NavigationDrawer.
5. Mantén funcionando Inicio, Mis pedidos, Favoritos y Perfil.
6. Mantén funcionando el DropdownMenu con Favoritos, Compartir y Reportar.
7. Usa estado de Jetpack Compose correctamente.
8. Usa Material 3 Badge o BadgedBox.
9. No cambies los nombres de los packages actuales.
10. No cambies MainActivity innecesariamente.
11. No elimines código funcional existente.
12. Mantén la estructura actual del proyecto y realiza solamente los cambios necesarios.

Antes de modificar archivos, revisa el código existente y explícame brevemente qué archivos necesitas modificar y por qué.

Después realiza los cambios necesarios y al finalizar indícame exactamente qué archivos modificaste.

Prompt 2 - Pantalla de favoritos

La mejora del badge funciona, pero detecté un problema de experiencia de usuario:

Cuando ingreso a la sección "Favoritos" desde el NavigationDrawer, la pantalla FavoritosScreen únicamente muestra el texto "Favoritos" y no muestra los productos que el usuario marcó como favoritos.

Quiero completar esta funcionalidad aprovechando el estado de favoritos que ya existe en AppNavigation.kt.

Realiza únicamente los cambios necesarios para que:

1. FavoritosScreen reciba la lista actual de productos favoritos.
2. Muestre el título "Favoritos".
3. Muestre los productos favoritos utilizando TarjetaProducto o una presentación coherente con HomeScreen.
4. Cuando no existan favoritos, muestre un mensaje como "Aún no tienes productos favoritos".
5. La lista se actualice automáticamente cuando se agreguen productos desde el DropdownMenu.
6. No rompas el badge ya implementado.
7. No dupliques el estado de favoritos dentro de FavoritosScreen.
8. Mantén AppNavigation como fuente principal del estado.
9. No modifiques archivos que no sean necesarios.
10. Mantén funcionando toda la navegación existente.

Primero indícame qué archivos necesitas modificar y luego realiza los cambios.