package es.uma.quiziosity.data.dao

import androidx.room.*
import es.uma.quiziosity.data.entity.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Delete
    suspend fun deleteUser(user: User)
}