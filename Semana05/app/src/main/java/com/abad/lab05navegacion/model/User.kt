package com.abad.lab05navegacion.model

data class User(
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)

object UserRepository {
    val users = mutableListOf(
        User(
            name = "Luis Pablo Abad",
            email = "luis.abad@tecsup.edu.pe",
            phone = "+51 987 654 321",
            password = "123"
        ),
        User(
            name = "Juan León",
            email = "juan.leon@tecsup.edu.pe",
            phone = "+51 912 345 678",
            password = "123"
        ),
        User(
            name = "María García",
            email = "maria.garcia@tecsup.edu.pe",
            phone = "+51 998 765 432",
            password = "123"
        )
    )

    var currentUser: User? = null

    fun registerUser(user: User): Boolean {
        if (users.any { it.email.equals(user.email, ignoreCase = true) }) {
            return false
        }
        users.add(user)
        // Agrega automáticamente al estudiante en el directorio de alumnos
        StudentRepository.addStudentFromUser(user)
        return true
    }

    fun validateLogin(email: String, password: String): User? {
        val found = users.find {
            it.email.trim().equals(email.trim(), ignoreCase = true) && it.password == password
        }
        if (found != null) {
            currentUser = found
        }
        return found
    }

    fun logout() {
        currentUser = null
    }
}
