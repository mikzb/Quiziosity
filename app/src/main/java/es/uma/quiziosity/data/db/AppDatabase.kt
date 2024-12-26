package es.uma.quiziosity.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import es.uma.quiziosity.data.dao.UserDao
import es.uma.quiziosity.data.entity.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
