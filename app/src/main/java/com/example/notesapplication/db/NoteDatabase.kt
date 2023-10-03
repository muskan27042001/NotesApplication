package com.example.notesapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notesapplication.Converters
import com.example.notesapplication.model.Note
import com.example.notesapplication.model.User

@Database(entities = [Note::class, User::class],
    version = 10,
    exportSchema = false
)
@TypeConverters(Converters::class) // Add this line
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getNoteDao(): DAO

    companion object {
       // @Volatile  // Singelton Pattern
        private var instance: NoteDatabase? = null  // Default value is null
        private val Lock = Any()

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
               database.execSQL("ALTER TABLE note ADD COLUMN imgPath TEXT")
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2,3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE note ADD COLUMN deletedTimestamp INTEGER")
            }
        }
        val MIGRATION_3_4: Migration = object : Migration(3,4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE note ADD COLUMN isPinned INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_4_5: Migration = object : Migration(4,5){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL, password TEXT NOT NULL)")
            }
        }

        val MIGRATION_5_6: Migration = object : Migration(4,5){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE note ADD COLUMN userId INTEGER")
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
        ).fallbackToDestructiveMigration()
            .build()
    }
}
