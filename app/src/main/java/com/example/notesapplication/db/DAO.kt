package com.example.notesapplication.db

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notesapplication.model.Note
import com.example.notesapplication.model.User

@Dao
interface DAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNote(note : Note)

    @Update
    suspend fun updateNote(note : Note)

    @Query("SELECT * FROM Note ORDER BY isPinned DESC , id DESC")
    fun getAllNote() : LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE title LIKE :query Or content LIKE :query OR date LIKE :query ORDER BY id DESC")
    fun searchNote(query :String ) : LiveData<List<Note>>

    @Delete
    suspend fun deleteNote(note : Note)

    @Delete
    suspend fun delete(note : Note)

   // @Query("SELECT * FROM note WHERE deletedTimestamp IS NOT NULL AND deletedTimestamp > :timestamp")
   // fun getDeletedNotesSince(timestamp: Long): LiveData<List<Note>>

    @Query("SELECT * FROM note WHERE deletedTimestamp > 0 ")
    fun getDeletedNotesSince(): LiveData<List<Note>>

    @Query("DELETE FROM Note WHERE deletedTimestamp < :timestamp")
    fun permanentlyDeleteNotesOlderThan(timestamp: Long)

    @Query("SELECT * FROM Note WHERE deletedTimestamp = 0 ORDER BY isPinned DESC , id DESC")
    fun getUndeletedNotes(): LiveData<List<Note>>

    @Update
    suspend fun updateNotePinnedStatus(note: Note)

    @Query("SELECT * FROM Note WHERE userId = :userId")
    fun getNotesForUser(userId: Int): LiveData<List<Note>>

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Int): LiveData<User>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    fun signIn(username: String, password: String): LiveData<User>

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): LiveData<User>

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE isLoggedIn = 1")
    suspend fun getLoggedInUser(): User?

    @Update
    fun updateLoggedInStatus(user: User)


}
