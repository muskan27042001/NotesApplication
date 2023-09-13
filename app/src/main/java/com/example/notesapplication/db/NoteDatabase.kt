package com.example.notesapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notesapplication.model.Note

@Database(entities = [Note::class],
    version = 2,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getNoteDao(): DAO

    companion object {
        @Volatile  // Singelton Pattern
        private var instance: NoteDatabase? = null  // Default value is null
        private val Lock = Any()

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
               database.execSQL("ALTER TABLE note ADD COLUMN imgPath TEXT")
            }
        }

        operator fun invoke(context: Context) = instance ?: synchronized(Lock) {
            instance ?: createDatabase(context).also {
                instance = it
            }
        }

        // Creating Database
        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            NoteDatabase::class.java,
            "note_database"
        ).addMigrations(MIGRATION_1_2)
            .build()
    }
}
