package es.uma.quiziosity.data.repository

import es.uma.quiziosity.QuiziosityApp
import es.uma.quiziosity.data.model.Result
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
            val user = userDao.getUserByUsername(username) ?: return Result.Error(Exception("User not found"))

            // Validate password
            if (!SecurityUtils.verifyString(password, user.hashedPassword)) {
                return Result.Error(Exception("Invalid credentials"))
            }

            // Login success
            Result.Success(LoggedInUser(user.id.toString(), user.username, user.score))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun register(username: String, password: String): Result<LoggedInUser> {
        return try {
            // Fetch user from database
            val userDao = QuiziosityApp.getDatabase().userDao()

            // If user already exists, return error
            if (userDao.getUserByUsername(username) != null) {
                return Result.Error(Exception("User already exists"))
            }

            // Create user
            val user = es.uma.quiziosity.data.entity.User(0, username, SecurityUtils.hashString(password))

            // Insert user into database
            val userId = userDao.insertUser(user)

            // Login success
            Result.Success(LoggedInUser(userId.toString(), username))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
