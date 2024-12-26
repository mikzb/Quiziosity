package es.uma.quiziosity.utils

import at.favre.lib.crypto.bcrypt.BCrypt

object SecurityUtils {

    /**
     * Hashes a plain text password using BCrypt.
     * @param password the plain text password
     * @return the hashed password
     */
    fun hashString(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    /**
     * Verifies a plain text password against a hashed password.
     * @param password the plain text password
     * @param hashedPassword the stored hashed password
     * @return true if the password matches, false otherwise
     */
    fun verifyString(password: String, hashedPassword: String): Boolean {
        val result = BCrypt.verifyer().verify(password.toCharArray(), hashedPassword)
        return result.verified
    }
}
