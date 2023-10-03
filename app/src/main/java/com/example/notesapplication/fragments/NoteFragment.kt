package com.example.notesapplication.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapplication.R
import com.example.notesapplication.activities.MainActivity
import com.example.notesapplication.adapter.RvNotesAdapter
import com.example.notesapplication.databinding.FragmentNoteBinding
import com.example.notesapplication.model.User
import com.example.notesapplication.utils.SwipeToDelete
import com.example.notesapplication.utils.hideKeyboard
import com.example.notesapplication.viewModel.NoteActivityViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.withContext

class NoteFragment : Fragment(R.layout.fragment_note) {

    private lateinit var noteBinding: FragmentNoteBinding
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private lateinit var rvAdapter: RvNotesAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var nav_view : NavigationView
    private var user: User? = null
    private val args: NoteFragmentArgs by navArgs()
    private var backPressedTime: Long = 0



    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        // Trannsition on exiting this fragment
        exitTransition= MaterialElevationScale(false).apply {
            duration=350
        }

        // Transition on entering this fragment
      /*  enterTransition= MaterialElevationScale(true).apply {
            duration=100
        }*/

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteBinding = FragmentNoteBinding.bind(view)
        val activity = activity as MainActivity
        val navController = Navigation.findNavController(view) // For moving from one fragment to other fragment
        requireView().hideKeyboard()

        ////////////////////////////////////
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + 1000 > System.currentTimeMillis()) {
                    activity?.finishAffinity()
                } else {
                    // Show a toast or perform any other action you want
                }
                backPressedTime = System.currentTimeMillis()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        ///////////////////////////////////
     //   user = arguments?.getParcelable("User")

        user = args.user
// Now you can use 'user' to access the logged-in user's information
        if (user != null) {
            // Use user.username, user.password, etc.
            Log.d("USER","")
            val username= user?.username
            Log.d("USER",username.toString())
        }
        else
        {
            Log.d("NULL","")
        }






        //   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            Log.d("THEME","light theme")
        } else {
            Log.d("THEME","dark theme")
        }

        // Open Menu
        noteBinding.menuDisplayNote.setOnClickListener {
            showPopupMenu(it)
        }

        CoroutineScope(Dispatchers.Main).launch {
            delay(10)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
           // activity.window.statusBarColor= Color.White

        }

        val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
       // drawerLayout?.openDrawer(GravityCompat.START) // Opens the drawer from the start (left) side

        nav_view=activity.findViewById<NavigationView>(R.id.nav_view)

        val nav_image=noteBinding.navImage
        nav_image.setOnClickListener {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        nav_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // Home Fragment
                R.id.home_fragment -> {

                }

                R.id.deleted_notes->{
                    Log.d("CLICKED","itemi 1")
                    navController.navigate(NoteFragmentDirections.actionNoteFragmentToDeletedNoteFragment(user))
                }
                R.id.labels->{
                    Log.d("CLICKED","label fragment")
                    navController.navigate(NoteFragmentDirections.actionNoteFragmentToLabelFragment(user))
                }
                R.id.logout_acount -> {
                    lifecycleScope.launch {
                        Log.d("user logout 1",user.toString())
                        user?.let {
                            withContext(Dispatchers.IO) {
                                Log.d("ho gya logout","")
                                noteActivityViewModel.setLoggedIn(it)
                            }
                        }
                        Log.d("user logout 2",user.toString())
                        val sharedPreferences = context?.getSharedPreferences("LOGGEDINUSERNAME", Context.MODE_PRIVATE)
                        val editor = sharedPreferences?.edit()
                        if (editor != null) {
                            editor.putString("username", null)
                            editor.apply()
                        }
                    }
                    noteActivityViewModel.clearUserData()
                    Log.d("user logout 3",user.toString())

                    navController.navigate(NoteFragmentDirections.actionNoteFragmentToLoginSignupFragment(user))


                }
                R.id.delete_account -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete your account?")
                        .setPositiveButton("Yes") { _, _ ->
                            // User clicked "Yes", proceed with account deletion
                                // User clicked "Yes", proceed with account deletion
                                user = args.user
                                user?.let { noteActivityViewModel.deleteUser(it) }

                                // Navigate to the login screen or perform any other necessary actions after deletion
                                navController.navigate(NoteFragmentDirections.actionNoteFragmentToLoginSignupFragment(user))
                        }
                        .setNegativeButton("No", null)
                        .show()
                }

                R.id.nav_theme_toggle->{
                    Log.d("CLICKED","switch")
                    false
                }

            }
            // Close the drawer after item selection
            if (drawerLayout != null) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            true
        }

val theme_switch = nav_view.menu.findItem(R.id.nav_theme_toggle).actionView as SwitchCompat

        theme_switch.setThumbResource(R.drawable.thumb) // Set custom thumb drawable
        theme_switch.setTrackResource(R.drawable.track)



        val sharedPreferences = context?.getSharedPreferences("MODE", Context.MODE_PRIVATE)
        val nightMode = sharedPreferences?.getBoolean("night", false)
        Log.d("MODE", nightMode.toString())

        if (nightMode == true) {
                theme_switch.isChecked
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        if (nightMode == false) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        if (theme_switch != null) {
            theme_switch.setOnCheckedChangeListener { buttonView, isChecked ->
                val editor = sharedPreferences?.edit()
                if (isChecked) {
                    Log.d("IS CHECKED","")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    editor?.putBoolean("night", true)
                } else {
                    Log.d("ELSE","")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    editor?.putBoolean("night", false)
                }
                editor?.apply()
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
        }


        // Sending user to SaveOrUpdateFragment on clicking "ADD Note"
        noteBinding.addNoteFab.setOnClickListener{
            noteBinding.appBarLayout.visibility=View.INVISIBLE
            if(user==null)
            {
                Log.d("inner fab","null")
            }
            if(user !=null)
            {
                Log.d("inner fab",user.toString())
            }
            val action = NoteFragmentDirections.actionNoteFragmentToSaveOrUpdateFragment(user)
            findNavController().navigate(action)
         //   navController.navigate(NoteFragmentDirections.actionNoteFragmentToSaveOrUpdateFragment())
        }

        // Sending user to SaveOrUpdateFragment on clicking "+"
        noteBinding.innerFab.setOnClickListener{
            noteBinding.appBarLayout.visibility=View.INVISIBLE
            if(user==null)
            {
                Log.d("inner fab","null")
            }
            if(user !=null)
            {
                Log.d("inner fab",user.toString())
            }
            val action = NoteFragmentDirections.actionNoteFragmentToSaveOrUpdateFragment(user)
            findNavController().navigate(action)
           // navController.navigate(NoteFragmentDirections.actionNoteFragmentToSaveOrUpdateFragment())
        }

        if(user==null)
        {
            Log.d("recy","null")
        }
        if(user !=null)
        {
            Log.d("recy",user.toString())
        }

        user?.let { recyclerViewDisplay(it) }
        swipeToDelete(noteBinding.rvNote)

        // Search
        noteBinding.search.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                noteBinding.noData.isVisible=false
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(s.toString().isNotEmpty())
                {
                    val text = s.toString()
                    val query = "%$text%"
                    if(query.isNotEmpty())
                    {
                        noteActivityViewModel.searchNote(query).observe(viewLifecycleOwner)
                        {
                            rvAdapter.submitList(it)
                        }
                    }
                    else
                    {
                        user?.let { observerDataChanges(it) }
                    }
                }
                else
                {
                    user?.let { observerDataChanges(it) }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        noteBinding.search.setOnEditorActionListener { v, actionId, _ ->
            if(actionId==EditorInfo.IME_ACTION_SEARCH)
            {
                v.clearFocus()
                requireView().hideKeyboard()
            }
            return@setOnEditorActionListener true

        }

        noteBinding.rvNote.setOnScrollChangeListener { _, scrollX, scrollY, _, oldSrollY ->
            when{
                scrollY>oldSrollY->{
                    noteBinding.chatFabText.isVisible=false
                }
                scrollX==scrollY->{ // little bit scrolling
                    noteBinding.chatFabText.isVisible=true
                }
                else ->  // swipping up again
                {
                    noteBinding.chatFabText.isVisible=true
                }
            }
        }
    }


    private fun swipeToDelete(rvNote: RecyclerView) {
        val swipeToDeleteCallback=object : SwipeToDelete()
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.absoluteAdapterPosition
                val note=rvAdapter.currentList[position]
                var actionBtnTapped=false

                // Keep a reference to the deleted note
                val deletedNote = note
                noteActivityViewModel.deleteNote(note)
                noteBinding.search.apply {
                    hideKeyboard()
                    clearFocus()
                }
                if(noteBinding.search.text.toString().isEmpty())
                {
                    user?.let { observerDataChanges(it) }
                }
                val snackbar = Snackbar.make(
                    requireView(),"Note Deleted", Snackbar.LENGTH_LONG
                ).addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>(){
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                    }

                    override fun onShown(transientBottomBar: Snackbar?) {
                        transientBottomBar?.setAction("UNDO"){
                            noteActivityViewModel.saveNote(note)
                            actionBtnTapped=true
                            noteBinding.noData.isVisible=false
                        }
                        super.onShown(transientBottomBar)
                    }
                }).apply {
                    animationMode=Snackbar.ANIMATION_MODE_FADE
                    setAnchorView(R.id.add_note_fab)
                }
                snackbar.setActionTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.yellowOrange
                    )
                )
                snackbar.show()
            }

        }
        val itemTouchHelper =ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(rvNote)
    }

    private fun observerDataChanges(user:User) {
        //val sortedNoteList = noteList.sortedBy { it.title }
        Log.d("idddd",user.id.toString())
        noteActivityViewModel.getNotesForUser(userId = user.id).observe(viewLifecycleOwner){list->
            noteBinding.noData.isVisible = list.isEmpty()
            rvAdapter.submitList(list)
        }
    }

    private fun observerDataChangesAtoZ(user: User) {
        //val sortedNoteList = noteList.sortedBy { it.title }
        noteActivityViewModel.getNotesForUser(userId = user.id).observe(viewLifecycleOwner){list->
            noteBinding.noData.isVisible=list.isEmpty()
            val sortedNoteList = list.sortedBy { it.title }
            rvAdapter.submitList(sortedNoteList)
        }
    }

    private fun observerDataChangesZtoA(user: User) {
        //val sortedNoteList = noteList.sortedBy { it.title }
        noteActivityViewModel.getNotesForUser(userId = user.id).observe(viewLifecycleOwner){list->
            noteBinding.noData.isVisible=list.isEmpty()
            val sortedNoteList = list.sortedByDescending { it.title }
            rvAdapter.submitList(sortedNoteList)
        }
    }

    private fun observerDataChangesNewEdit(user: User) {
        //val sortedNoteList = noteList.sortedBy { it.title }
        noteActivityViewModel.getNotesForUser(userId = user.id).observe(viewLifecycleOwner){list->
            noteBinding.noData.isVisible=list.isEmpty()
            val sortedNoteList = list.sortedBy { it.date }
            rvAdapter.submitList(sortedNoteList)
        }
    }

    private fun observerDataChangesLastEdit(user: User) {
        //val sortedNoteList = noteList.sortedBy { it.title }
        noteActivityViewModel.getNotesForUser(userId = user.id).observe(viewLifecycleOwner){list->
            noteBinding.noData.isVisible=list.isEmpty()
            val sortedNoteList = list.sortedByDescending { it.date }
            rvAdapter.submitList(sortedNoteList)
        }
    }

    private fun recyclerViewDisplay(user:User) {
        when(resources.configuration.orientation)
        {
            Configuration.ORIENTATION_PORTRAIT->setUpRecyclerView(2,user) // 2 columns in potrait mode
            Configuration.ORIENTATION_LANDSCAPE->setUpRecyclerView(3,user) // 3 columns in ladscape mode
        }
    }

    private fun setUpRecyclerView(spanCount: Int,user:User) {
        noteBinding.rvNote.apply {
        layoutManager=StaggeredGridLayoutManager(spanCount,StaggeredGridLayoutManager.VERTICAL)
        setHasFixedSize(true)
            if(user!=null)
            {
                rvAdapter= RvNotesAdapter(user!!)
                rvAdapter.stateRestorationPolicy=RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                adapter=rvAdapter
                postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
                viewTreeObserver.addOnPreDrawListener {
                    startPostponedEnterTransition()
                    true
                }
            }
            if(user==null)
            {
                Log.d("NULLLL","")
            }
      //  rvAdapter= RvNotesAdapter(user)
        }
        observerDataChanges(user)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.nav_theme_toggle->{
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.drawer_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun showPopupMenu(view: View?) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.display_notes_menu, popupMenu.menu)

        user=args.user
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.a_to_z -> {
                    user?.let { observerDataChangesAtoZ(it) }
                    true
                }
                R.id.z_to_a -> {
                    user?.let { observerDataChangesZtoA(it) }
                    true
                }
                R.id.new_edit -> {
                    user?.let { observerDataChangesNewEdit(it) }
                true
                }
                R.id.last_edit -> {
                    user?.let { observerDataChangesLastEdit(it) }
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

}