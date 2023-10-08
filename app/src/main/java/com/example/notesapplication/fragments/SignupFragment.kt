package com.example.notesapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.notesapplication.R
import com.example.notesapplication.databinding.CustomToastLayoutBinding
import com.example.notesapplication.databinding.FragmentSignupBinding
import com.example.notesapplication.model.User
import com.example.notesapplication.viewModel.NoteActivityViewModel
import com.google.android.material.transition.MaterialElevationScale


class SignupFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var signupBinding: FragmentSignupBinding
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    val labellistfordb = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Trannsition on exiting this fragment
        exitTransition= MaterialElevationScale(false).apply {
            duration=350
        }

        // Transition on entering this fragment
        enterTransition= MaterialElevationScale(true).apply {
            duration=100
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        signupBinding=FragmentSignupBinding.bind(view)

        signupBinding.signupButton.setOnClickListener {
            val username = signupBinding.signupEmail.text.toString()
            val password = signupBinding.signupPassword.text.toString()

            val layout = layoutInflater.inflate(R.layout.custom_toast_layout,null)
            // Validate email format
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            if (!username.matches(emailPattern.toRegex())) {

                val customToastLayoutBinding = CustomToastLayoutBinding.bind(layout)
                customToastLayoutBinding.toastid.setText("Invalid email format")

                val toast = Toast(requireContext())
                toast.duration = Toast.LENGTH_SHORT
                toast.view = layout
                toast.setGravity(Gravity.BOTTOM, 0, 0)
                toast.show()

              //  Toast.makeText(requireContext(),"Invalid email format",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Check if username is already taken
                var userAlreadyExists = false
                    val existingUser = noteActivityViewModel.getUserByUsername(username)
                existingUser.observe(viewLifecycleOwner) { user ->
                        if (user == null && !userAlreadyExists) {
                            // User doesn't exist
                            Log.d("ExistingUser", "User doesn't exist")
                            val newuser = User(username = username, password = password, isLoggedIn = false, labels = labellistfordb)
                            noteActivityViewModel.insertUser(newuser)
                            // Notify user that sign up was successful
                            val customToastLayoutBinding = CustomToastLayoutBinding.bind(layout)
                            customToastLayoutBinding.toastid.setText("Sign Up Successful")

                            val toast = Toast(requireContext())
                            toast.duration = Toast.LENGTH_SHORT
                            toast.view = layout
                            toast.setGravity(Gravity.BOTTOM, 0, 0)
                            toast.show()

                       //     Toast.makeText(requireContext(), "Sign Up Successful", Toast.LENGTH_SHORT).show()
                            userAlreadyExists = true // Set flag to true after successful sign up
                            existingUser.removeObservers(viewLifecycleOwner) // Remove observer to prevent further callbacks
                        }
                    else
                        {
                            val customToastLayoutBinding = CustomToastLayoutBinding.bind(layout)
                            customToastLayoutBinding.toastid.setText("Already")

                            val toast = Toast(requireContext())
                            toast.duration = Toast.LENGTH_SHORT
                            toast.view = layout
                            toast.setGravity(Gravity.BOTTOM, 0, 0)
                            toast.show()

                         //   Toast.makeText(requireContext(), "Already", Toast.LENGTH_SHORT).show()
                            userAlreadyExists = true
                        }
                    }

            } else {
                // Fields are empty, display an error message
                val customToastLayoutBinding = CustomToastLayoutBinding.bind(layout)
                customToastLayoutBinding.toastid.setText("Please enter both username and password")

                val toast = Toast(requireContext())
                toast.duration = Toast.LENGTH_SHORT
                toast.view = layout
                toast.setGravity(Gravity.BOTTOM, 0, 0)
                toast.show()
               // Toast.makeText(requireContext(), "Please enter both username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}