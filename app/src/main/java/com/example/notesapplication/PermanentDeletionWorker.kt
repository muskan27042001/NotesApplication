package com.example.notesapplication

import android.content.Context
import androidx.room.Room
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.notesapplication.db.NoteDatabase
import com.example.notesapplication.repository.NoteRepository
import java.util.concurrent.TimeUnit

class PermanentDeletionWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        try {

            // Initialize your database
            val db = Room.databaseBuilder(
                applicationContext,
                NoteDatabase::class.java, "note-database"
            ).build()

            // Initialize your repository
            val repository = NoteRepository(db)



            // Get the timestamp of 7 days ago
            val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)

            // Delete notes older than 7 days
            repository.permanentlyDeleteNotesOlderThan(sevenDaysAgo)

            return Result.success()
        } catch (e: Exception) {
            // Handle exceptions if any
            return Result.failure()
        }
    }
}






