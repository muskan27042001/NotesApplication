package com.example.notesapplication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.notesapplication.R
import com.example.notesapplication.databinding.FragmentSignupBinding
import com.example.notesapplication.model.User
import com.example.notesapplication.viewModel.NoteActivityViewModel


class SignupFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var signupBinding: FragmentSignupBinding
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        signupBinding=FragmentSignupBinding.bind(view)

        signupBinding.signupButton.setOnClickListener {
            val username = signupBinding.signupEmail.text.toString()
            val password = signupBinding.signupPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Check if username is already taken
                    val existingUser = noteActivityViewModel.getUserByUsername(username)
                existingUser.observe(viewLifecycleOwner) { user ->
                    if (user == null || user.username.isEmpty()) {
                        // User doesn't exist
                        Log.d("ExistingUser", "User doesn't exist")
                        val user = User(username = username, password = password, isLoggedIn = false)
                        noteActivityViewModel.insertUser(user)
                        // Notify user that sign up was successful
                        Toast.makeText(requireContext(), "Sign Up Successful", Toast.LENGTH_SHORT).show()
                    } else {
                        // User exists, you can access user.username
                        Toast.makeText(requireContext(), "Username is already taken", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                // Fields are empty, display an error message
                Toast.makeText(requireContext(), "Please enter both username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}