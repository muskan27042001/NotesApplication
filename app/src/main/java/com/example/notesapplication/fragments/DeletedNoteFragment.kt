package com.example.notesapplication.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapplication.R
import com.example.notesapplication.adapter.RvNotesAdapter
import com.example.notesapplication.databinding.FragmentDeletedNoteBinding
import com.example.notesapplication.databinding.FragmentNoteBinding
import com.example.notesapplication.model.User
import com.example.notesapplication.utils.SwipeToDelete
import com.example.notesapplication.utils.hideKeyboard
import com.example.notesapplication.viewModel.NoteActivityViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import java.util.concurrent.TimeUnit

class DeletedNoteFragment : Fragment(R.layout.fragment_deleted_note) {

    private lateinit var deletedNoteBinding: FragmentDeletedNoteBinding
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private lateinit var rvAdapter: RvNotesAdapter
    private var user : User?=null
    private val userargs : DeletedNoteFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
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

        deletedNoteBinding = FragmentDeletedNoteBinding.bind(view)

        user=userargs.user
        user?.let { recyclerViewDisplay(it) }
        swipeToDelete(deletedNoteBinding.rvNote)

    }

    private fun recyclerViewDisplay(user:User) {
        when(resources.configuration.orientation)
        {
            Configuration.ORIENTATION_PORTRAIT->setUpRecyclerView(2,user) // 2 columns in potrait mode
            Configuration.ORIENTATION_LANDSCAPE->setUpRecyclerView(3,user) // 3 columns in ladscape mode
        }
    }

    private fun setUpRecyclerView(spanCount: Int,user:User) {
        deletedNoteBinding.rvNote.apply {
            layoutManager= StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)

            if(user!=null){
                rvAdapter= user?.let { RvNotesAdapter(it) }!!
                rvAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
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

        }
        observerDataChanges(user)
    }

    private fun observerDataChanges(user:User) {
            // Retrieve deleted notes since 7 days ago
           // val sevenDaysAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000

            noteActivityViewModel.getDeletedNotesSince(user.id).observe(viewLifecycleOwner) { deletedNotes ->
                if(deletedNotes.isNotEmpty())
                {
                    for (deletedNote in deletedNotes) {
                        // Access properties of each deletedNote
                        val deletedTimestamp = deletedNote.deletedTimestamp

                        if (deletedTimestamp?.let { isSevenDaysOld(it) } == true) {
                            // The timestamp is 7 days old or older
                            noteActivityViewModel.delete(deletedNote)
                        } else {
                            // The timestamp is less than 7 days old
                            // Your code here...
                        }


                    }
                    rvAdapter.submitList(deletedNotes)
                }
                else
                {
                    deletedNoteBinding.noData.visibility=View.VISIBLE
                }

            }
    }

    fun isSevenDaysOld(timestamp: Long): Boolean {
        val currentTimestamp = System.currentTimeMillis()
        val sevenDaysAgoTimestamp = currentTimestamp - (7 * 24 * 60 * 60 * 1000)
        return timestamp <= sevenDaysAgoTimestamp
    }

    // 7 * 24 * 60 * 60 * 1000

    private fun swipeToDelete(rvNote: RecyclerView) {
        val swipeToDeleteCallback=object : SwipeToDelete()
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.absoluteAdapterPosition
                val note=rvAdapter.currentList[position]
                var actionBtnTapped=false

                // Keep a reference to the deleted note
                val deletedNote = note
                Log.d("Restored note",deletedNote.toString())
                deletedNote.deletedTimestamp=0
                Log.d("Restored note after",deletedNote.toString())
              //  noteActivityViewModel.saveNote(deletedNote)
noteActivityViewModel.updateNote(deletedNote)
                val snackbar = Snackbar.make(
                    requireView(),"Note Restored", Snackbar.LENGTH_LONG
                ).addCallback(object: BaseTransientBottomBar.BaseCallback<Snackbar>(){
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                    }

                }).apply {
                    animationMode= Snackbar.ANIMATION_MODE_FADE

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
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(rvNote)
    }

}