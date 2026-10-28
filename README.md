# TeqRuta - Gestión de Traslados Técnicos (TEQMED SpA)

## Información del Proyecto
- **Asignatura:** Desarrollo de Aplicaciones Móviles (DSY1105)
- **Estudiante:** Cristóbal Millar
- **Evaluación:** Parcial 2 (Encargo)

## Descripción
Aplicación móvil desarrollada en Kotlin y Jetpack Compose para registrar traslados en terreno del personal técnico de TEQMED SpA. La aplicación funciona de manera 100% offline para adaptarse a zonas con conectividad intermitente.

## Funcionalidades Implementadas
1. **Persistencia Local (Room Database):** Almacenamiento local de kilometraje, fecha y coordenadas.
2. **Recursos Nativos:**
   - **GPS (Ubicación):** Captura de coordenadas geográficas en tiempo real.
   - **Cámara:** Captura de evidencia fotográfica del odómetro del vehículo.
3. **Gestión de Estado (MVVM):** Separación desacoplada entre la lógica de validación y los componentes de UI (`StateFlow`).
4. **Formularios Validados y Animaciones:** Retroalimentación visual inmediata ante errores y transiciones mediante `AnimatedVisibility`.

## Requisitos para Ejecución
- Android Studio Jellyfish / Koala o superior.
- Dispositivo o Emulador con Android 8.0 (API 26) o superior.
- Conexión a servicios de ubicación (GPS) y cámara habilitados.
