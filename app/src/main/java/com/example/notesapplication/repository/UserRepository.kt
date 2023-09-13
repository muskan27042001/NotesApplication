package com.example.notesapplication.repository

import com.example.notesapplication.db.UserDao
import com.example.notesapplication.model.User


class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        userDao.insert(user)
    }
}