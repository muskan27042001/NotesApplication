package com.example.notesapplication.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.notesapplication.model.User

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?
}