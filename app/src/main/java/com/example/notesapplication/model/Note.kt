package com.example.notesapplication.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)

    var id : Int = 0,
    val title : String,
    val content : String,
    val date : String,
    val color : Int = -1,
    var imgPath:String? = null
) : Serializable
