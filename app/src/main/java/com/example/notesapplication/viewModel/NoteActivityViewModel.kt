package com.example.notesapplication.viewModel

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapplication.model.Note
import com.example.notesapplication.repository.NoteRepository
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class NoteActivityViewModel(private val repository: NoteRepository) : ViewModel() {

    fun saveNote(newNote : Note) = viewModelScope.launch {
        repository.addNote(newNote)
    }

    fun updateNote(existingNote : Note) = viewModelScope.launch {
        repository.updateNote(existingNote)
    }

    fun deleteNote(existingNote : Note) = viewModelScope.launch {
        repository.deleteNote(existingNote)
    }

    fun searchNote(query : String) : LiveData<List<Note>> {
        return repository.searchNote(query)
    }

    fun getAllNotes() : LiveData<List<Note>> = repository.getNote()


}

