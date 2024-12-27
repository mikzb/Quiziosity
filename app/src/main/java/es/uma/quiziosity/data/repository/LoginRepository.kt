package es.uma.quiziosity.data.repository

import es.uma.quiziosity.QuiziosityApp
import es.uma.quiziosity.data.model.Result
import es.uma.quiziosity.data.model.LoggedInUser

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        // handle login
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    suspend fun register(username: String, password: String): Result<LoggedInUser> {
        // handle register
        val result = dataSource.register(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser

        // Get the encrypted SharedPreferences instance
        val sharedPreferences = QuiziosityApp.getSharedPreferences()

        // Store the user credentials securely
        with(sharedPreferences.edit()) {
            putString("user_id", loggedInUser.userId)
            putString("username", loggedInUser.displayName)
            apply()
        }
    }
}