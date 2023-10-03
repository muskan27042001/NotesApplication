package com.example.notesapplication.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.notesapplication.R
import com.example.notesapplication.adapter.ViewPagerAdapter
import com.example.notesapplication.databinding.FragmentLoginSignupBinding
import com.example.notesapplication.databinding.FragmentNoteBinding
import com.example.notesapplication.model.User
import com.example.notesapplication.viewModel.NoteActivityViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginSignupFragment : Fragment(R.layout.fragment_login_signup) {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var loginSignupFragmentNoteBinding: FragmentLoginSignupBinding
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private var user : User?=null

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

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // view.visibility = View.GONE
       /* lifecycleScope.launch {

            val sharedPreferences = requireContext().getSharedPreferences("LOGGEDINUSERNAME", Context.MODE_PRIVATE)
            val value = sharedPreferences.getString("username", "defaultValue")
            if (value != null) {
                Log.d("value",value)
                val user = noteActivityViewModel.getUserByUsername(value).observe(viewLifecycleOwner){user->
                    if(user!=null)
                    {
                        val username= user?.username
                        Log.d("USER",username.toString())
                        Log.d("LOGINSIGNUP", user.toString())
                    }
                    if(user==null)
                    {
                        Log.d("LOGINSIGNUP","user is null")


                    }
                }

            }
        }*/


      /*  lifecycleScope.launch {
            val isLoggedIn = noteActivityViewModel.isLoggedIn()
            if (isLoggedIn) {
                val user = noteActivityViewModel.getLoggedInUser()
                Log.d("Logged in user ", user.toString())
                if (user != null) {
                    // User is logged in, you can now use the 'user' object
                    withContext(Dispatchers.Main) {
                        val action = LoginSignupFragmentDirections.actionLoginSignupFragmentToNoteFragment(user)
                        findNavController().navigate(action)
                    }
                } else {
                    Log.d("User in not logged in","")
                    view.visibility = View.VISIBLE
                    // Handle case where user is somehow logged in but user object is null
                }
            }
        }*/

        loginSignupFragmentNoteBinding = FragmentLoginSignupBinding.bind(view)

        tabLayout=loginSignupFragmentNoteBinding.tabLayout
        viewPager2=loginSignupFragmentNoteBinding.viewPager

        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("Signup"));

        val adapter = ViewPagerAdapter(this) // 'this' refers to the Fragment
        viewPager2.adapter = adapter

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.getTabAt(position)?.select()
            }
        })

        // Set custom tab view
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            val customView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null)
            val tabTextView = customView.findViewById<TextView>(R.id.tab_text)
            tabTextView.text = tab!!.text
            tab.customView = customView
        }


        // Set a custom OnTabSelectedListener to handle visibility
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager2.currentItem = tab.position

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // No action needed when a tab is unselected

            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // No action needed when a tab is reselected
            }
        })
    }


}