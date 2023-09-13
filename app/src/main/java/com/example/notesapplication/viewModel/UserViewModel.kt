package com.example.notesapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapplication.model.Note
import com.example.notesapplication.model.User
import com.example.notesapplication.repository.NoteRepository
import com.example.notesapplication.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    fun insertUser(user: User) { viewModelScope.launch {
            repository.insertUser(user)
        }
    }
}
