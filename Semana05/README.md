Prompt 1: Rediseño visual "Portal Académico"

Tengo una app Android en Jetpack Compose con Navigation Compose. Actualmente
tiene 4 pantallas conectadas por un NavHost (rutas: home, list, detail/{itemId},
profile), usando un sealed class Screen y un NavController. La lógica de
navegación y el paso de itemId:Int deben quedar exactamente igual — SOLO quiero
que rediseñes la presentación visual de cada pantalla, manteniendo los mismos
onClick/navigate() que ya existen.

Transforma el tema a un "Portal Académico" con esta paleta: morado (
#6A4C93
aprox.) como color primario, fondos con degradado de morado claro a blanco,
tarjetas blancas con esquinas redondeadas y sombra suave.

NUEVA pantalla LoginScreen (antes de Home): tarjeta blanca centrada sobre
fondo degradado morado→blanco, título "Portal Académico", subtítulo "Accede
a tu cuenta", campo "Correo institucional" con ícono de sobre, campo
"Contraseña" con ícono de ojo para mostrar/ocultar, botón morado sólido
"INICIAR SESIÓN" que navega a Home, y texto "¿Olvidaste tu contraseña?"
debajo (sin funcionalidad real, solo visual).

HomeScreen → pantalla de bienvenida: fondo degradado morado (arriba) a
blanco (abajo), texto grande "Bienvenido, [nombre]" en blanco, subtítulo
"¿Qué deseas gestionar hoy?", dos tarjetas clicables con ícono + título +
descripción: "Directorio de Alumnos" (ícono de grupo de personas, navega a
List) y "Mi Perfil Académico" (ícono de persona, navega a Profile). Abajo,
texto pequeño en rojo "Cerrar Sesión Segura" con ícono de salida.

ListScreen → "Directorio de Alumnos": TopAppBar con flecha de volver y
título "Directorio de Alumnos". Lista de tarjetas (usa datos de ejemplo:
Juan León - Ingeniería de Sistemas, María García - Arquitectura, Carlos
Pérez - Medicina, Ana López - Derecho, Luis Ramírez - Administración), cada
una con avatar circular (usa un ícono de persona con fondo de color si no
hay imágenes reales), nombre en negrita, carrera debajo en gris, y flecha
">" a la derecha. Al tocar una tarjeta, navega a Detail pasando su id
(mantén exactamente la misma lógica de navegación que ya tengo).

DetailScreen → "Expediente Académico": TopAppBar con flecha de volver y
título "Expediente Académico". Encabezado con fondo degradado morado, foto
circular grande centrada, nombre en negrita y carrera debajo. Card blanca
con filas (ícono + etiqueta + valor): "ID Estudiante", "Correo Electrónico",
"Facultad", y una sección "Biografía" con un párrafo de texto de ejemplo.
Usa el itemId recibido para mostrar cuál alumno es (puedes mapearlo a los
datos de ejemplo de la lista).

ProfileScreen → "Configuración de Perfil": mismo estilo de encabezado con
foto y nombre sobre fondo degradado. Debajo, dos secciones con título en
mayúsculas: "INFORMACIÓN PERSONAL" (Nombre Completo, Correo, Teléfono, cada
uno con su ícono) y "ACADÉMICO" (Carrera, Ciclo Actual). Al final, una fila
o botón en rojo claro "Cerrar Sesión" que navega de vuelta a Home limpiando
el back stack (igual que ya lo tengo con popUpTo).

No uses imágenes de internet ni URLs externas para las fotos de perfil — usa
un ícono por defecto (Icons.Default.Person) dentro de un círculo de color, ya
que el proyecto no tiene acceso a assets ni internet.

Dame el código Kotlin completo de cada archivo modificado, manteniendo los
mismos nombres de función (HomeScreen, ListScreen, DetailScreen, ProfileScreen)
y agregando LoginScreen como nuevo archivo.

Restricción clave
Mantener exactamente la misma lógica de navegación (NavController,
sealed class Screen, paso de itemId: Int) — solo cambiar la
presentación visual.

Requerimientos solicitados a la IA
1. Nueva pantalla LoginScreen (antes de Home): tarjeta blanca centrada
   sobre fondo degradado morado→blanco, campos de correo y contraseña
   con íconos, botón "INICIAR SESIÓN".
2. Rediseño de HomeScreen: fondo degradado morado a blanco, saludo
   personalizado, tarjetas clicables hacia Directorio de Alumnos y
   Mi Perfil Académico, opción de Cerrar Sesión.
3. Rediseño de ListScreen ("Directorio de Alumnos"): TopAppBar morada,
   lista de tarjetas con avatar circular, nombre, carrera y flecha de
   navegación, manteniendo la lógica de paso de itemId a Detail.
4. Rediseño de DetailScreen ("Expediente Académico"): encabezado con
   foto circular y datos personales, Card con ID, correo, facultad y
   biografía del estudiante.
5. Rediseño de ProfileScreen ("Configuración de Perfil"): secciones de
   Información Personal y Académico, botón de Cerrar Sesión que limpia
   el back stack.