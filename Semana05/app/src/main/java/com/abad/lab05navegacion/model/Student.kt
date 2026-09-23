package com.abad.lab05navegacion.model

data class Student(
    val id: Int,
    val name: String,
    val carrera: String,
    val email: String,
    val facultad: String,
    val biografia: String,
    val avatarBgColorHex: Long = 0xFF6A4C93
)

object StudentRepository {
    val students = listOf(
        Student(
            id = 1,
            name = "Juan León",
            carrera = "Ingeniería de Sistemas",
            email = "juan.leon@tecsup.edu.pe",
            facultad = "Facultad de Ingeniería y Tecnología",
            biografia = "Estudiante destacado del 6to ciclo de Ingeniería de Sistemas. Apasionado por el desarrollo de software móvil, inteligencia artificial y arquitecturas de microservicios.",
            avatarBgColorHex = 0xFF6A4C93
        ),
        Student(
            id = 2,
            name = "María García",
            carrera = "Arquitectura",
            email = "maria.garcia@tecsup.edu.pe",
            facultad = "Facultad de Diseño y Arquitectura",
            biografia = "Estudiante de 8vo ciclo de Arquitectura con especialización en diseño urbano sostenible y modelado BIM. Participante activa en proyectos comunitarios.",
            avatarBgColorHex = 0xFF8B62C0
        ),
        Student(
            id = 3,
            name = "Carlos Pérez",
            carrera = "Medicina",
            email = "carlos.perez@tecsup.edu.pe",
            facultad = "Facultad de Ciencias de la Salud",
            biografia = "Estudiante de 10mo ciclo de Medicina Humana. Interesado en la investigación clínica, telemedicina y salud pública.",
            avatarBgColorHex = 0xFF3F51B5
        ),
        Student(
            id = 4,
            name = "Ana López",
            carrera = "Derecho",
            email = "ana.lopez@tecsup.edu.pe",
            facultad = "Facultad de Derecho y Ciencias Políticas",
            biografia = "Estudiante de 5to ciclo de Derecho. Enfocada en derecho corporativo y propiedad intelectual, delegada del club de debate universitario.",
            avatarBgColorHex = 0xFF009688
        ),
        Student(
            id = 5,
            name = "Luis Ramírez",
            carrera = "Administración",
            email = "luis.ramirez@tecsup.edu.pe",
            facultad = "Facultad de Ciencias Empresariales",
            biografia = "Estudiante de 7mo ciclo de Administración de Empresas. Apasionado por el emprendimiento digital, análisis financiero y gestión de proyectos.",
            avatarBgColorHex = 0xFFE91E63
        )
    )

    fun getStudentById(id: Int): Student {
        return students.find { it.id == id } ?: students.first()
    }
}
