package com.example.notesapplication.fragments

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import android.widget.Switch
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.compose.material3.Snackbar
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapplication.R
import com.example.notesapplication.activities.MainActivity
import com.example.notesapplication.adapter.RvNotesAdapter
import com.example.notesapplication.databinding.FragmentNoteBinding
import com.example.notesapplication.utils.SwipeToDelete
import com.example.notesapplication.utils.hideKeyboard
import com.example.notesapplication.viewModel.NoteActivityViewModel
import com.google.android.material.color.MaterialColors.getColor
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NoteFragment : Fragment(R.layout.fragment_note) {

    private lateinit var noteBinding: FragmentNoteBinding
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private lateinit var rvAdapter: RvNotesAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var nav_view : NavigationView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        // Trannsition on exiting this fragment
        exitTransition= MaterialElevationScale(false).apply {
            duration=350
        }

        // Transition on entering this fragment
        enterTransition= MaterialElevationScale(true).apply {
            duration=350
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteBinding = FragmentNoteBinding.bind(view)
        val activity = activity as MainActivity
        val navController = Navigation.findNavController(view) // For moving from one fragment to other fragment
        requireView().hideKeyboard()


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
           // activity.window.statusBarColor=Color.White

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
                R.id.nav_theme -> {

                }
                R.id.nav_item_1->{
                    Log.d("CLICKED","itemi 1")
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
            navController.navigate(NoteFragmentDirections.actionNoteFragmentToSaveOrUpdateFragment())
        }

        // Sending user to SaveOrUpdateFragment on clicking "+"
        noteBinding.innerFab.setOnClickListener{
            noteBinding.appBarLayout.visibility=View.INVISIBLE
            navController.navigate(NoteFragmentDirections.actionNoteFragmentToSaveOrUpdateFragment())
        }

        recyclerViewDisplay()
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
                        observerDataChanges()
                    }
                }
                else
                {
                    observerDataChanges()
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
                noteActivityViewModel.deleteNote(note)
                noteBinding.search.apply {
                    hideKeyboard()
                    clearFocus()
                }
                if(noteBinding.search.text.toString().isEmpty())
                {
                    observerDataChanges()
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

    private fun observerDataChanges() {
        //val sortedNoteList = noteList.sortedBy { it.title }
        noteActivityViewModel.getAllNotes().observe(viewLifecycleOwner){list->
            noteBinding.noData.isVisible=list.isEmpty()
            rvAdapter.submitList(list)
        }
    }

    private fun observerDataChangesAtoZ() {
        //val sortedNoteList = noteList.sortedBy { it.title }
        noteActivityViewModel.getAllNotes().observe(viewLifecycleOwner){list->
            noteBinding.noData.isVisible=list.isEmpty()
            val sortedNoteList = list.sortedBy { it.title }
            rvAdapter.submitList(sortedNoteList)
        }
    }

    private fun observerDataChangesZtoA() {
        //val sortedNoteList = noteList.sortedBy { it.title }
        noteActivityViewModel.getAllNotes().observe(viewLifecycleOwner){list->
            noteBinding.noData.isVisible=list.isEmpty()
            val sortedNoteList = list.sortedByDescending { it.title }
            rvAdapter.submitList(sortedNoteList)
        }
    }

    private fun observerDataChangesNewEdit() {
        //val sortedNoteList = noteList.sortedBy { it.title }
        noteActivityViewModel.getAllNotes().observe(viewLifecycleOwner){list->
            noteBinding.noData.isVisible=list.isEmpty()
            val sortedNoteList = list.sortedBy { it.date }
            rvAdapter.submitList(sortedNoteList)
        }
    }

    private fun observerDataChangesLastEdit() {
        //val sortedNoteList = noteList.sortedBy { it.title }
        noteActivityViewModel.getAllNotes().observe(viewLifecycleOwner){list->
            noteBinding.noData.isVisible=list.isEmpty()
            val sortedNoteList = list.sortedByDescending { it.date }
            rvAdapter.submitList(sortedNoteList)
        }
    }

    private fun recyclerViewDisplay() {
        when(resources.configuration.orientation)
        {
            Configuration.ORIENTATION_PORTRAIT->setUpRecyclerView(2) // 2 columns in potrait mode
            Configuration.ORIENTATION_LANDSCAPE->setUpRecyclerView(3) // 3 columns in ladscape mode
        }
    }

    private fun setUpRecyclerView(spanCount: Int) {
        noteBinding.rvNote.apply {
        layoutManager=StaggeredGridLayoutManager(spanCount,StaggeredGridLayoutManager.VERTICAL)
        setHasFixedSize(true)
        rvAdapter= RvNotesAdapter()
        rvAdapter.stateRestorationPolicy=RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        adapter=rvAdapter
        postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
        viewTreeObserver.addOnPreDrawListener {
            startPostponedEnterTransition()
            true
            }
        }
        observerDataChanges()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_theme -> {
                return true
            }
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

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.a_to_z -> {
                observerDataChangesAtoZ()
                    true
                }
                R.id.z_to_a -> {
                    observerDataChangesZtoA()
                    true
                }
                R.id.new_edit -> {
                    observerDataChangesNewEdit()
                true
                }
                R.id.last_edit -> {
                    observerDataChangesLastEdit()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

}