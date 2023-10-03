package com.example.notesapplication.repository

import androidx.lifecycle.LiveData
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

    fun getDeletedNotesSince(userId : Int) = db.getNoteDao().getDeletedNotesSince(userId)

    fun getNotesOfLabel(userId: Int,labelname : String) = db.getNoteDao().getNotesOfLabel(userId,labelname)

    fun getUndeletedNotes() = db.getNoteDao().getUndeletedNotes()

    fun permanentlyDeleteNotesOlderThan(timestamp: Long) = db.getNoteDao().permanentlyDeleteNotesOlderThan(timestamp)
    suspend fun updateNotePinnedStatus(note: Note) = db.getNoteDao().updateNotePinnedStatus(note)

    suspend fun insert(user : User) = db.getNoteDao().insert(user)

    fun signIn(username : String , password : String ) = db.getNoteDao().signIn(username,password)

    fun getUserByUsername(username: String) = db.getNoteDao().getUserByUsername(username)

    suspend fun deleteUser(user: User) = db.getNoteDao().deleteUser(user)

    suspend fun updateUser(user: User) = db.getNoteDao().updateUser(user)

    fun getNotesForUser(userId : Int) = db.getNoteDao().getNotesForUser(userId)

    fun getLabelsForUser(username: String) = db.getNoteDao().getLabelsForUser(username)

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

    suspend fun addLabelToNote(note : Note,selectedlabel : String) {
        note.label=selectedlabel
        db.getNoteDao().updateNote(note)
    }

    suspend fun updateUserLabels(user: User, labels: List<String>) {
        user.labels = labels
        db.getNoteDao().updateUser(user)
    }

    suspend fun addLabelsToUser(user: User, newLabel:String) {
        val currentLabels = user.labels.toMutableList() // Convert to mutable list
        currentLabels.add(newLabel) // Add new labels to the list
        user.labels = currentLabels // Update the labels property

        db.getNoteDao().updateUser(user) // Update the user in the database
    }

    suspend fun removeLabelFromUser(user: User, labelToRemove: String) {
        val currentLabels = user.labels.toMutableList() // Convert to mutable list
        currentLabels.remove(labelToRemove) // Remove the specified label
        user.labels = currentLabels // Update the labels property

        db.getNoteDao().updateUser(user) // Update the user in the database
    }

    fun getUserById(userId: Int): LiveData<User> {
        return db.getNoteDao().getUserById(userId)
    }

    suspend fun getLabelOfNote(userId : Int, id : Int) : String? {
        return db.getNoteDao().getLabelOfNote(userId,id)
    }

    suspend fun pinnedornot(noteId : Int) : Boolean{
        return db.getNoteDao().pinnedornot(noteId)
    }

}