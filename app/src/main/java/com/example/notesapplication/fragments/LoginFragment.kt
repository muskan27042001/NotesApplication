package com.example.notesapplication.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.notesapplication.R
import com.example.notesapplication.databinding.FragmentLoginBinding
import com.example.notesapplication.viewModel.NoteActivityViewModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var loginBinding: FragmentLoginBinding
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private lateinit var result : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginBinding=FragmentLoginBinding.bind(view)
       // navController = Navigation.findNavController(view) // For moving from one fragment to other fragment


        loginBinding.loginButton.setOnClickListener {
            val username = loginBinding.loginEmail.text.toString()
            val password = loginBinding.loginPassword.text.toString()


                // Check if username and password are not empty
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    //   val user = noteActivityViewModel.signIn(username, password)
                    noteActivityViewModel.signIn(username, password)
                        .observe(viewLifecycleOwner) { user ->
                            if (user != null) {
                                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                            //    val navController = findNavController(view)
                                // navController.navigate(R.id.action_loginSignupFragment_to_noteFragment)

                                //  val action = LoginSignupFragmentDirections.actionLoginSignupFragmentToNoteFragment(user)
                                //  navController.navigate(action)
                                val sharedPreferences = context?.getSharedPreferences("LOGGEDINUSERNAME", Context.MODE_PRIVATE)
                                val editor = sharedPreferences?.edit()
                                if (editor != null) {
                                    editor.putString("username", username)
                                    editor.apply()
                                }


                                lifecycleScope.launch {
                                    noteActivityViewModel.setLoggedIn(user)
                                }

                                val action = LoginSignupFragmentDirections.actionLoginSignupFragmentToNoteFragment(user)
                                findNavController().navigate(action)
                                //  navController.navigate(LoginFragmentDirections.actionLoginFragmentToNoteFragment(user))

                            }
                            else
                            {
                                Toast.makeText(requireContext(), "Invalid username or password", Toast.LENGTH_SHORT).show()
                            }
                        }
                }else {
                    Toast.makeText(requireContext(), "Please enter both username and password", Toast.LENGTH_SHORT).show()
                }

        }
        }


}

