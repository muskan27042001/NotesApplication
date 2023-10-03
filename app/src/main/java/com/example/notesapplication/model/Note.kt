package com.example.notesapplication.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Note(
    @PrimaryKey(autoGenerate = true)

    var id: Int = 0,
    val title: String,
    val content: String,
    val date: String,
    val color: Int = -1,
    var imgPath:String? = null,
    var deletedTimestamp: Long? = null,
    var isPinned: Int = 0,
    val userId: Int, // Foreign key to associate a note with a user
    val bg: Int = 0,
    var label: String? = null
) : Serializable

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String,
    var isLoggedIn: Boolean,
    var labels: List<String>
) : Serializable
