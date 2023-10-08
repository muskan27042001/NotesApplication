package com.example.notesapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapplication.model.Note
import com.example.notesapplication.model.User
import com.example.notesapplication.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteActivityViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _userLiveData = MutableLiveData<User?>()
    val userLiveData: MutableLiveData<User?> get() = _userLiveData

    fun saveNote(newNote : Note) = viewModelScope.launch {
        repository.addNote(newNote)
    }

    fun updateNote(existingNote : Note) = viewModelScope.launch {
        repository.updateNote(existingNote)
    }

    fun deleteNote(existingNote: Note, deletedTimestamp: Long? = null) = viewModelScope.launch {
        existingNote.deletedTimestamp = deletedTimestamp
        repository.updateNote(existingNote)
    }

    fun delete(existingNote: Note) = viewModelScope.launch {
        repository.delete(existingNote)
    }

    fun deleteNote(existingNote: Note) = viewModelScope.launch {
      //  val deletedTimestamp = System.currentTimeMillis() // Current time when note was deleted
       // repository.deleteNote(existingNote, deletedTimestamp)
        val deletedTimestamp = System.currentTimeMillis() // Current time when note was deleted
        existingNote.deletedTimestamp = deletedTimestamp
        repository.updateNote(existingNote)
    }


    fun searchNote(query : String) : LiveData<List<Note>> {
        return repository.searchNote(query)
    }

    fun getAllNotes() : LiveData<List<Note>> = repository.getNote()


    fun getDeletedNotesSince(userId: Int) : LiveData<List<Note>> = repository.getDeletedNotesSince(userId)

    fun getNotesOfLabel(userId: Int,labelname : String) : LiveData<List<Note>> = repository.getNotesOfLabel(userId,labelname)

    fun getUndeletedNotes() : LiveData<List<Note>> = repository.getUndeletedNotes()

    fun toggleNotePinnedStatus(note: Note) = viewModelScope.launch {
        note.isPinned = if (note.isPinned == 0) 1 else 0
        repository.updateNotePinnedStatus(note)
    }



    fun insertUser(user: User) { viewModelScope.launch {
        repository.insert(user)
    }
    }

    fun signIn(username: String, password: String): LiveData<User> {
        return repository.signIn(username, password)
        // Handle the signed-in user here
    }


    fun getUserByUsername(username: String): LiveData<User> {
        return repository.getUserByUsername(username)
        // Handle the retrieved user here
    }

    fun deleteUser(user: User) { viewModelScope.launch {
        repository.deleteUser(user)
    }
    }

    fun getNotesForUser(userId : Int) :LiveData<List<Note>> = repository.getNotesForUser(userId)

    fun getAllPinnedNotesForUser(userId: Int) : LiveData<List<Note>> = repository.getAllPinnedNotesForUser(userId)

    fun clearUserData() {
        _userLiveData.value = null
    }

    suspend fun isLoggedIn(): Boolean {
        return repository.isLoggedIn()
    }

    suspend fun getLoggedInUser() : User?{
        return repository.getLoggedInUser()
    }

    suspend fun setLoggedIn(user : User)  = repository.setLoggedIn(user)

    suspend fun updateUserLabels(user: User, labels: List<String>) = repository.updateUserLabels(user,labels)

    fun getLabelsForUser(username : String) :LiveData<List<String>> = repository.getLabelsForUser(username)

    suspend fun addLabelsToUser(user: User, newLabel : String) = repository.addLabelsToUser(user,newLabel)

    suspend fun removeLabelFromUser(user: User,labelToRemove: String) = repository.removeLabelFromUser(user,labelToRemove)

    suspend fun removeeLabelFromNote(user: User,note: Note) = repository.removeLabelFromNote(user,note)

     suspend fun addLabelToNote(note : Note, selectedlabel : String) = repository.addLabelToNote(note,selectedlabel)

    suspend fun getLabelOfNote(userId : Int, id : Int): String? {
        return repository.getLabelOfNote(userId,id)
    }

    suspend fun pinnedornot(noteId : Int) : Int{
        return repository.pinnedornot(noteId)
    }
}

