package es.uma.quiziosity

import android.app.Application
import android.content.Context
import androidx.room.Room
import es.uma.quiziosity.data.db.AppDatabase

class QuiziosityApp : Application() {
    // Define the database property at the class level
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
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

        // Access the database globally
        fun getDatabase(): AppDatabase {
            return instance.database
        }
    }
}
