# CampusGPS – Semana 13

## Descripción
CampusGPS es una aplicación móvil desarrollada en Kotlin y Jetpack Compose que permite visualizar lugares de interés en un mapa, obtener la ubicación actual del usuario, calcular distancias y encontrar el lugar más cercano.

## Tecnologías utilizadas
- Kotlin
- Android Studio
- Jetpack Compose
- Material Design 3
- OpenStreetMap / MapLibre
- Google Play Services – Location
- Kotlin Coroutines y StateFlow
- Arquitectura MVVM

## Funcionalidades implementadas

### 1. Visualización del mapa
- Integración de OpenStreetMap.
- Visualización de los lugares registrados.
- Marcadores diferenciados por colores.
- Cámara interactiva con animación.

### 2. Ubicación del usuario
- Solicitud de permisos de ubicación.
- Obtención de coordenadas mediante Fused Location Provider.
- Visualización de latitud y longitud.
- Marcador de ubicación actual.

### 3. Cálculo de distancias
- Cálculo de distancia entre el usuario y los lugares.
- Conversión de metros a kilómetros.
- Ordenamiento de lugares desde el más cercano al más lejano.

### 4. Lista de lugares y selección
- Visualización de los lugares disponibles.
- Nombre, categoría y distancia.
- Selección de lugares mediante tarjetas.
- Resaltado visual del lugar seleccionado.
- Movimiento de la cámara hacia el lugar seleccionado.

### 5. Lugar más cercano
- Identificación automática del lugar más cercano.
- Marcador verde para destacar el lugar.
- Visualización de su nombre y distancia.
- Botón para centrar el mapa en dicho lugar.

### 6. Manejo de errores
- Identificación de permisos de ubicación denegados.
- Detección de problemas relacionados con el GPS.
- Comprobación de disponibilidad de Google Play Services.
- Mensajes informativos y opciones para reintentar o abrir ajustes.

## Arquitectura del proyecto

Se utiliza el patrón arquitectónico MVVM para separar las responsabilidades:

- **Model:** contiene los modelos de datos y estados de la interfaz.
- **View:** presenta la interfaz mediante Jetpack Compose.
- **ViewModel:** administra los estados y la lógica de presentación.
- **Repository:** gestiona la obtención de ubicación y los datos de los lugares.
- **Util:** contiene funciones auxiliares para calcular y formatear distancias.

## Pruebas realizadas
- Verificación de permisos de ubicación.
- Obtención de coordenadas actuales.
- Cálculo y ordenamiento de distancias.
- Selección de lugares en la lista.
- Identificación del lugar más cercano.
- Movimiento de cámara hacia los lugares.
- Pruebas de permisos denegados y manejo de errores.


