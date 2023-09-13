package com.example.notesapplication.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

// In Extension class we create functions so that we can use them anywhere multiple times

// HIDE KEYBOARD
fun View.hideKeyboard()=(context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
    .hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
