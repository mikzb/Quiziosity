package es.uma.quiziosity.data

import es.uma.quiziosity.QuiziosityApp
import es.uma.quiziosity.data.model.LoggedInUser
import es.uma.quiziosity.utils.SecurityUtils

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            // Fetch user from database
            val userDao = QuiziosityApp.getDatabase().userDao()

            // If user doesn't exist, return error
            val user = userDao.getUserByEmail(username) ?: return Result.Error(Exception("User not found"))

            // Validate password
            if (!SecurityUtils.verifyPassword(password, user.hashedPassword)) {
                return Result.Error(Exception("Invalid credentials"))
            }

            // Login success
            Result.Success(LoggedInUser(user.id.toString(), user.name))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}
