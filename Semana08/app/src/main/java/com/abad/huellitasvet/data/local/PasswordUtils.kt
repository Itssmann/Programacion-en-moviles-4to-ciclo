package com.abad.huellitasvet.data.local

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PasswordUtils {
    fun hashPassword(password: String): String {
        val saltBytes = ByteArray(16)
        SecureRandom().nextBytes(saltBytes)
        val md = MessageDigest.getInstance("SHA-256")
        md.update(saltBytes)
        val hashed = md.digest(password.toByteArray(Charsets.UTF_8))
        val saltString = Base64.getEncoder().encodeToString(saltBytes)
        val hashString = Base64.getEncoder().encodeToString(hashed)
        return "$saltString:$hashString"
    }

    fun verifyPassword(password: String, stored: String): Boolean {
        if (!stored.contains(":")) {
            return password == stored
        }
        val parts = stored.split(":")
        if (parts.size != 2) return false
        val saltBytes = Base64.getDecoder().decode(parts[0])
        val expectedHash = parts[1]

        val md = MessageDigest.getInstance("SHA-256")
        md.update(saltBytes)
        val hashed = md.digest(password.toByteArray(Charsets.UTF_8))
        val hashString = Base64.getEncoder().encodeToString(hashed)
        return hashString == expectedHash
    }
}
