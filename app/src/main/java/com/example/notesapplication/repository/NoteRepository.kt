package com.example.notesapplication.repository

import com.example.notesapplication.db.NoteDatabase
import com.example.notesapplication.model.Note
import com.example.notesapplication.model.User

class NoteRepository(private val db : NoteDatabase) {

    fun getNote()=db.getNoteDao().getAllNote()

    fun searchNote(query  :String)=db.getNoteDao().searchNote(query)

    suspend fun addNote(note : Note) = db.getNoteDao().addNote(note)

    suspend fun updateNote(note : Note) = db.getNoteDao().updateNote(note)

    suspend fun delete(note : Note) = db.getNoteDao().delete(note)
    suspend fun deleteNote(note: Note, deletedTimestamp: Long) = db.getNoteDao().deleteNote(note)

    fun getDeletedNotesSince() = db.getNoteDao().getDeletedNotesSince()

    fun getUndeletedNotes() = db.getNoteDao().getUndeletedNotes()

    fun permanentlyDeleteNotesOlderThan(timestamp: Long) = db.getNoteDao().permanentlyDeleteNotesOlderThan(timestamp)
    suspend fun updateNotePinnedStatus(note: Note) = db.getNoteDao().updateNotePinnedStatus(note)

    suspend fun insert(user : User) = db.getNoteDao().insert(user)

    fun signIn(username : String , password : String ) = db.getNoteDao().signIn(username,password)

    fun getUserByUsername(username: String) = db.getNoteDao().getUserByUsername(username)

    suspend fun deleteUser(user: User) = db.getNoteDao().deleteUser(user)

    suspend fun updateUser(user: User) = db.getNoteDao().updateUser(user)

    fun getNotesForUser(userId : Int) = db.getNoteDao().getNotesForUser(userId)


    suspend fun isLoggedIn(): Boolean {
        return db.getNoteDao().getLoggedInUser() != null
    }
    suspend fun getLoggedInUser(): User? {
        // Assuming you have a function in your UserDao to get the logged-in user
        return db.getNoteDao().getLoggedInUser()
    }

    suspend fun setLoggedIn(user: User) {
        user.isLoggedIn = !user.isLoggedIn
        db.getNoteDao().updateUser(user) // Assuming you have an update method in your repository
    }

}