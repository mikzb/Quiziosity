// UserRepository.kt
package es.uma.quiziosity.data.repository

import es.uma.quiziosity.data.dao.UserDao
import es.uma.quiziosity.data.entity.User

class UserRepository(private val userDao: UserDao) {

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    suspend fun insertUser(user: User) : Long{
        return userDao.insertUser(user)
    }

    suspend fun getUsersByScorePaged(offset: Int, limit: Int): List<User> {
        return userDao.getUsersByScorePaged(offset, limit)
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }

    suspend fun deleteUserByUsername(username: String) {
        userDao.deleteUserByUsername(username)
    }

    suspend fun updateScore(username: String, score: Int) {
        val user = getUserByUsername(username)
        if (user != null) {
            user.score = score
            updateUser(user)
        }
    }
}