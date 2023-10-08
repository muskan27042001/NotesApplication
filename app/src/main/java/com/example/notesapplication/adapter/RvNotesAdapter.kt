package com.example.notesapplication.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapplication.R
import com.example.notesapplication.databinding.NoteItemLayoutBinding
import com.example.notesapplication.fragments.NoteFragmentDirections
import com.example.notesapplication.model.Note
import com.example.notesapplication.model.User
import com.example.notesapplication.utils.hideKeyboard
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import org.commonmark.node.SoftLineBreak
import kotlinx.coroutines.withContext



class RvNotesAdapter(private val user: User):ListAdapter<Note,RvNotesAdapter.NotesViewHolder> (DiffUtilCallback()){
        inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentBinding = NoteItemLayoutBinding.bind(itemView)
        val title: MaterialTextView = contentBinding.noteItemTitle
        val content: TextView = contentBinding.noteContentItem
        val date: MaterialTextView = contentBinding.noteDate
        val parent: MaterialCardView = contentBinding.noteItemLayoutParent
            val pinNote : ImageView = contentBinding.pinNote
            val parentimage : ImageView = contentBinding.noteBackgroundImage
            val notebackgroundlayout: ConstraintLayout = contentBinding.notebackgroundlayout
        val markwon = Markwon.builder(itemView.context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(itemView.context))
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    super.configureVisitor(builder)
                    builder.on(SoftLineBreak::class.java) {
                            visitor, _ -> visitor.forceNewLine()
                    }
                }
            })
            .build()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvNotesAdapter.NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item_layout,parent,false))
    }

    override fun onBindViewHolder(holder: RvNotesAdapter.NotesViewHolder, position: Int) {
        getItem(position).let { note ->
            holder.apply {

                parent.transitionName="recyclerView_${note.id}"
                title .text=note.title
                markwon.setMarkdown(content,note.content)
                date.text=note.date

                val sharedPreferencesmode = itemView.context.getSharedPreferences("MODE", Context.MODE_PRIVATE)
                val nightMode = sharedPreferencesmode?.getBoolean("night", false)

                if(nightMode==true && note!!.color==-1)
                {
                    note.color= Color.GRAY
                   // note.color = Color.rgb(255, 165, 0)
                }

                if(nightMode==false && note!!.color==-1)
                {
                    note.color= Color.WHITE
                }

                if(note.isPinned==1)
                {
                    pinNote.visibility=View.VISIBLE
                }

                if(note.isPinned==0)
                {
                    pinNote.visibility=View.INVISIBLE
                }

                parent.setCardBackgroundColor(note.color)


                // Adjust the size of the background
               val layoutParams = parent.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                parent.layoutParams = layoutParams

                itemView.setOnClickListener{
                    val action = NoteFragmentDirections.actionNoteFragmentToSaveOrUpdateFragment(user)
                        .setNote(note)
                    val extras= FragmentNavigatorExtras(parent to "recyclerView_${note.id}")
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action,extras)
                }

                content.setOnClickListener{
                    val action = NoteFragmentDirections.actionNoteFragmentToSaveOrUpdateFragment(user)
                        .setNote(note)
                    val extras= FragmentNavigatorExtras(parent to "recyclerView_${note.id}")
                    it.hideKeyboard()
                    Navigation.findNavController(it).navigate(action,extras)
                }

            }
            }
        }
    }
