package es.uma.quiziosity

import android.app.Application
import android.content.Context
import androidx.room.Room
import es.uma.quiziosity.data.db.AppDatabase
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import es.uma.quiziosity.data.repository.UserRepository

class QuiziosityApp : Application() {
    // Define the database property at the class level
    lateinit var database: AppDatabase
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        // Save the instance of the application
        instance = this

        // Initialize MasterKey
        val masterKey = MasterKey.Builder(applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // Initialize the shared preferences here
        sharedPreferences = EncryptedSharedPreferences.create(
            applicationContext,
            "quiziosity_preferences",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        // Initialize the database here
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "quiziosity_database"
        )
            .fallbackToDestructiveMigration()  // Optional, handle migrations
            .build()
    }

    companion object {
        private lateinit var instance: QuiziosityApp

        fun getContext(): Context = instance.applicationContext

        fun getSharedPreferences(): SharedPreferences {
            return instance.sharedPreferences
        }

        fun getUserRepository(): UserRepository {
            return UserRepository(getDatabase().userDao())
        }

        // Access the database globally
        fun getDatabase(): AppDatabase {
            return instance.database
        }
    }
}
