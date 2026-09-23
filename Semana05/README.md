Prompt : Rediseño visual "Portal Académico"

Tengo una app Android en Jetpack Compose con Navigation Compose. Actualmente
tiene 4 pantallas conectadas por un NavHost (rutas: home, list, detail/{itemId},
profile), usando un sealed class Screen y un NavController. La lógica de
navegación y el paso de itemId:Int deben quedar exactamente igual — SOLO quiero
que rediseñes la presentación visual de cada pantalla, manteniendo los mismos
onClick/navigate() que ya existen.

Transforma el tema a un "Portal Académico" con esta paleta: morado (#6A4C93
aprox.) como color primario, fondos con degradado de morado claro a blanco,
tarjetas blancas con esquinas redondeadas y sombra suave.

1. NUEVA pantalla LoginScreen (antes de Home): tarjeta blanca centrada sobre
   fondo degradado morado→blanco, título "Portal Académico", subtítulo "Accede
   a tu cuenta", campo "Correo institucional" con ícono de sobre, campo
   "Contraseña" con ícono de ojo para mostrar/ocultar, botón morado sólido
   "INICIAR SESIÓN", y texto "¿Olvidaste tu contraseña?" debajo.

2. HomeScreen → pantalla de bienvenida: fondo degradado morado a blanco, texto
   grande "Bienvenido, [nombre]" en blanco, subtítulo "¿Qué deseas gestionar
   hoy?", dos tarjetas clicables: "Directorio de Alumnos" (navega a List) y
   "Mi Perfil Académico" (navega a Profile). Abajo, texto rojo "Cerrar Sesión
   Segura".

3. ListScreen → "Directorio de Alumnos": TopAppBar con flecha de volver, lista
   de tarjetas con avatar circular, nombre en negrita, carrera en gris, flecha
   ">". Al tocar, navega a Detail pasando el id.

4. DetailScreen → "Expediente Académico": encabezado con fondo degradado
   morado, foto circular grande centrada, nombre y carrera. Card blanca con
   ID Estudiante, Correo, Facultad y sección Biografía.

5. ProfileScreen → "Configuración de Perfil": encabezado con foto y nombre,
   secciones "INFORMACIÓN PERSONAL" y "ACADÉMICO", botón rojo claro "Cerrar
   Sesión" que navega a Home limpiando el back stack.

Además de todo el rediseño anterior, necesito que la autenticación sea
funcional de verdad, no solo visual:

6. Un objeto UserRepository en memoria con una MutableList de usuarios
   (nombre, correo, teléfono, contraseña), y funciones para registrar
   usuarios y validar login.

7. LoginScreen debe validar de verdad: si el correo o contraseña están
   vacíos, muestra "Completa todos los campos". Si no coinciden con ningún
   usuario del repositorio, muestra "Correo o contraseña incorrectos". Solo
   navega a Home si las credenciales son correctas.

8. Una nueva RegisterScreen.kt: campos de nombre, correo, teléfono,
   contraseña, confirmar contraseña. Valida que las contraseñas coincidan,
   que ningún campo esté vacío, y que el correo no esté ya registrado. Al
   registrar, agrega el usuario al repositorio y regresa a Login.

9. Una nueva ForgotPasswordScreen.kt: campo de correo, botón "Enviar enlace
   de recuperación" que muestra un mensaje de confirmación simulado.

10. Conecta "¿No tienes cuenta? Regístrate" hacia Register, y "¿Olvidaste tu
    contraseña?" hacia ForgotPassword. Actualiza Screen.kt y AppNavigation.kt
    con las 2 rutas nuevas.

11. El nombre en "Bienvenido, [nombre]" (Home) y "Nombre Completo" (Profile)
    debe ser el del usuario que realmente inició sesión, no uno fijo. El
    teléfono en Profile también debe ser el real del usuario registrado.

12. El Directorio de Alumnos debe sincronizarse con los usuarios registrados:
    al registrarse, el usuario también debe aparecer como estudiante en el
    directorio, con datos razonables por defecto para los campos que el
    registro no pide.

No uses imágenes de internet, solo Icons.Default.Person dentro de un círculo
de color. Mantén los mismos nombres de función (HomeScreen, ListScreen,
DetailScreen, ProfileScreen).

Dame el código completo de todos los archivos nuevos y modificados.

Requerimientos funcionales
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
6. Repositorio de usuarios en memoria con registro y validación de login
7. Login con validación real: mensajes distintos para "campos vacíos"
   y "credenciales incorrectas"
8. Pantalla de Registro (nombre, correo, teléfono, contraseñas)
9. Pantalla de Recuperar Contraseña (simulada)
10. Navegación entre Login ↔ Registro ↔ Recuperar contraseña
11. Nombre y teléfono del usuario logueado reflejados en Home y Perfil
12. Sincronización automática: usuario registrado también aparece en
    el Directorio de Alumnos