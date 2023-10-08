package com.example.notesapplication.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notesapplication.R
import com.example.notesapplication.activities.MainActivity
import com.example.notesapplication.databinding.BottomSheetLayoutBinding
import com.example.notesapplication.databinding.FragmentSaveOrUpdateBinding
import com.example.notesapplication.model.Note
import com.example.notesapplication.model.User
import com.example.notesapplication.utils.hideKeyboard
import com.example.notesapplication.viewModel.NoteActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class SaveOrUpdateFragment : Fragment(R.layout.fragment_save_or_update) {

    private lateinit var navController: NavController
    private lateinit var contentBinding: FragmentSaveOrUpdateBinding
    private var note: Note? = null
    private var idOfUser: Int = -1
    private var idOfNote: Int = -1
    private var labelName: String? = null
    private var pinnedornotint : Int = 0
    private var bg = 0
    private var labelclearedclicked : Int = 0
    private var user: User? = null
    private var color = -1
    private lateinit var result: String
    private val noteActivityViewModel: NoteActivityViewModel by activityViewModels()
    private val currentDate =
        SimpleDateFormat.getInstance().format(Date())  /// it will return current date and time
    private val job = CoroutineScope(Dispatchers.Main)
    private val args: SaveOrUpdateFragmentArgs by navArgs()
    private val userargs: SaveOrUpdateFragmentArgs by navArgs()
    private var selectedImagePath = ""
    private var pinned: Int = 0

    private var REQUEST_CODE_IMAGE = 789


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            scrimColor = Color.TRANSPARENT
            duration = 300L
        }
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("6", "")
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("7", "")
        if (data != null) {
            var selectedImageUrl = data.data
            Log.d("url", selectedImageUrl.toString())
            if (selectedImageUrl != null) {
                try {
                    Log.d("yay", "")
                    var inputStream =
                        requireActivity().contentResolver.openInputStream(selectedImageUrl)
                    var bitmap = BitmapFactory.decodeStream(inputStream)
                    contentBinding.layoutImage.visibility = View.VISIBLE
                    contentBinding.imgNote.visibility = View.VISIBLE
                    contentBinding.imgDelete.visibility = View.VISIBLE
                    contentBinding.imgNote.setImageBitmap(bitmap)


                    selectedImagePath = getPathFromUri(selectedImageUrl)!!
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }

            }
        }


    }


    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath: String? = null
        var cursor = requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }


    private fun saveNote(user: User) {
        Log.d("save note", user.id.toString())
        if (contentBinding.etNoteContent.text.toString()
                .isEmpty() || contentBinding.etTitle.text.toString().isEmpty()
        ) {
            Toast.makeText(activity, "Title is Empty. Please fill", Toast.LENGTH_SHORT).show()
        } else {
            note = args.note
            when (note) {

                null -> {  // Fresh Note
                    Log.d("pin", pinned.toString())

                    if (user != null) {
                        // Use user.username, user.password, etc.
                        val sharedPreferences =
                            context?.getSharedPreferences("Label", Context.MODE_PRIVATE)
                        val labelName = sharedPreferences?.getString("labelname", null)

                        val sharedPreferencesmode = context?.getSharedPreferences("MODE", Context.MODE_PRIVATE)
                        val nightMode = sharedPreferencesmode?.getBoolean("night", false)

                        if(nightMode==true && color==-1)
                        {
                            color=Color.GRAY
                        }

                        if(nightMode==false && color==-1)
                        {
                            color=Color.WHITE
                        }


                        if (labelName != null) {
                            Log.d("label name save", labelName)
                        } else {
                            Log.d("label name save", "null")
                        }

                        Log.d("USER", "")
                        val id = user?.id
                        Log.d("USER", id.toString())
                        Log.d("idd", id.toString())
                        id?.let {
                            Note(
                                0,
                                contentBinding.etTitle.text.toString(),
                                contentBinding.etNoteContent.text.toString(),
                                currentDate,
                                color,
                                selectedImagePath,
                                0,
                                0,
                                it,
                                bg,
                                labelName
                            )
                        }?.let { noteActivityViewModel.saveNote(it) }

                        // To update live data
                        result = "Note Saved"
                        setFragmentResult(
                            "key",
                            bundleOf("bundleKey" to result)
                        )
                        navController.navigate(
                            SaveOrUpdateFragmentDirections.actionSaveOrUpdateFragmentToNoteFragment(
                                user
                            )
                        )

                        val editor = sharedPreferences?.edit()
                        editor?.putString("labelname", null)
                        editor?.apply() // Apply the changes
                    }

                    else {
                        Log.d("NULL save", "")
                    }

                }

                else -> {   // update note

                    updateNote(user)
                  //  navController.popBackStack()
                    val action = SaveOrUpdateFragmentDirections.actionSaveOrUpdateFragmentToNoteFragment(user)
                    findNavController().navigate(action)
                }
            }


        }
    }

    private fun updateNote(user: User) {
        //if(note!=null)
        // {

        if (user != null) {

           // val sharedPreferences = context?.getSharedPreferences("Label", Context.MODE_PRIVATE)
           // val labelName = sharedPreferences?.getString("labelname", null)
           // if (labelName != null) {
           //     Log.d("label name save", labelName)
           // }

            lifecycleScope.launch {

                val sharedPreferences =
                    context?.getSharedPreferences("Label", Context.MODE_PRIVATE)
                val sharedpreflabelName = sharedPreferences?.getString("labelname", null)


                if(sharedpreflabelName!=null && idOfNote!=-1)
                {
                    Log.d("sharedpref update",sharedpreflabelName)
                    contentBinding.labelText.visibility = View.VISIBLE
                    contentBinding.labelText.setText(sharedpreflabelName)
                    contentBinding.labelImg.visibility = View.VISIBLE
                    contentBinding.labelClearImg.visibility=View.VISIBLE

                }

                Log.d("DEBUG update", "idOfUser: $idOfUser, idOfNote: $idOfNote")
                val label = noteActivityViewModel.getLabelOfNote(idOfNote, idOfUser)
                Log.d("labelllllll update", label.toString())
                labelName = label ?: null
                if (labelName != null && idOfNote!=-1) {
                    Log.d("null ni h ", "update")
                    contentBinding.labelText.visibility = View.VISIBLE
                    contentBinding.labelText.setText(labelName)
                    contentBinding.labelImg.visibility = View.VISIBLE
                    contentBinding.labelClearImg.visibility=View.VISIBLE
                }
                else{
                    contentBinding.labelText.visibility = View.GONE
                    contentBinding.labelText.setText(labelName)
                    contentBinding.labelImg.visibility = View.GONE
                    contentBinding.labelClearImg.visibility=View.GONE
                }

                if(sharedpreflabelName==null && labelName==null)
                {
                    labelName=null
                }
                if(sharedpreflabelName!=null && labelName==null)
                {
                    labelName=sharedpreflabelName
                }

                if(sharedpreflabelName==null && labelName!=null)
                {
                    labelName=labelName
                }


                note = args.note
                if (note != null) {
                    // Use user.username, user.password, etc.
                    Log.d("note", "")
                    idOfNote = note!!.id
                    Log.d("IDOFNOTE", idOfNote.toString())
                    Log.d("note", note.toString())
                } else {
                    Log.d("NULL create view", "note")
                }


                lifecycleScope.launch {
                    if (note != null) {
                        pinnedornotint = noteActivityViewModel.pinnedornot(idOfNote)
                        Log.d("pinned h ya ni ", pinnedornotint.toString())
                        if (pinnedornotint == 0) {
                            Log.d("pinned ni h ", "update")
                            contentBinding.pinNote.setBackgroundResource(R.drawable.baseline_favorite_border_24)
                        } else {
                            Log.d("pinned hai", "update")
                            contentBinding.pinNote.setBackgroundResource(R.drawable.baseline_favorite_24)
                        }
                    }
                }


                if (pinned == 0) {
                    Log.d("pinned ni h ", "hhhhhh")
                } else {
                    Log.d("pinned hai", "hhhhhh")
                }
            }
            // Use user.username, user.password, etc.
            Log.d("USER", "")
            val id = user?.id
            Log.d("USER", id.toString())

            if(labelclearedclicked==1)
            {
                labelName= null
            }
            id?.let {
                Note(
                    note!!.id,
                    contentBinding.etTitle.text.toString(),
                    contentBinding.etNoteContent.getMD(),
                    currentDate,
                    color, selectedImagePath, 0, pinnedornotint, it, bg, labelName
                )
            }?.let {
                noteActivityViewModel.updateNote(
                    it
                )
                val sharedPreferences =
                    context?.getSharedPreferences("Label", Context.MODE_PRIVATE)
                val editor = sharedPreferences?.edit()
                editor?.putString("labelname", null)
                editor?.apply() // Apply the changes
            }
        } else {
            Log.d("NULL", "")
        }

        //}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
Log.d("view created","")
        contentBinding = FragmentSaveOrUpdateBinding.bind(view)
        navController = Navigation.findNavController(view)
        val activity = activity as MainActivity

        //    val sharedPreferences = context?.getSharedPreferences("Label", Context.MODE_PRIVATE)
        //     val labelName = sharedPreferences?.getString("labelname", null)


        //   contentBinding.labelText.setText(labelName)
        //   contentBinding.labelText.visibility=View.VISIBLE
        //   contentBinding.labelImg.visibility=View.VISIBLE

        user = userargs.user
        if (user != null) {
            // Use user.username, user.password, etc.
            Log.d("USER", "")
            val username = user?.username
            idOfUser = user!!.id
            Log.d("IDOFUSER", idOfUser.toString())
            Log.d("USER", username.toString())
        } else {
            Log.d("NULL create view", "")
        }

        note = args.note
        if (note != null) {
            // Use user.username, user.password, etc.
            Log.d("note", "")
            idOfNote = note!!.id
            Log.d("IDOFNOTE", idOfNote.toString())
            Log.d("note", note.toString())
        } else {
            Log.d("NULL create view", "note")
        }


        lifecycleScope.launch {

            val sharedPreferences =
                context?.getSharedPreferences("Label", Context.MODE_PRIVATE)
            val sharedpreflabelName = sharedPreferences?.getString("labelname", null)


            if(sharedpreflabelName!=null && idOfNote==-1)
            {
                Log.d("sharedpref",sharedpreflabelName)
                contentBinding.labelText.visibility = View.VISIBLE
                contentBinding.labelText.setText(sharedpreflabelName)
                contentBinding.labelImg.visibility = View.VISIBLE
                contentBinding.labelClearImg.visibility=View.VISIBLE

            }


            Log.d("DEBUG", "idOfUser: $idOfUser, idOfNote: $idOfNote")
            val label = noteActivityViewModel.getLabelOfNote(idOfNote, idOfUser)
            Log.d("labelllllll",label.toString())
            labelName = label ?: null

            if (labelName != null && idOfNote!=-1) {
                Log.d("null ni h ", "")
                contentBinding.labelText.visibility = View.VISIBLE
                contentBinding.labelText.setText(labelName)
                contentBinding.labelImg.visibility = View.VISIBLE
                contentBinding.labelClearImg.visibility=View.VISIBLE
            }
            /*else
            {
                contentBinding.labelText.visibility = View.GONE
                contentBinding.labelText.setText(labelName)
                contentBinding.labelImg.visibility = View.GONE
                contentBinding.labelClearImg.visibility=View.GONE
            }
*/
            if (note != null) {
                pinnedornotint = noteActivityViewModel.pinnedornot(idOfNote)
                Log.d("pinned h ya ni ", pinnedornotint.toString())
                if (pinnedornotint == 0) {
                    Log.d("pinned ni h ", "view created")
                    contentBinding.pinNote.setBackgroundResource(R.drawable.baseline_favorite_border_24)
                } else {
                    Log.d("pinned hai", " view created")
                    contentBinding.pinNote.setBackgroundResource(R.drawable.baseline_favorite_24)
                }
            }

        }





        ViewCompat.setTransitionName(
            contentBinding.noteContentFragmentParent,
            "recyclerView_${args.note?.id}"
        )

        // Image Delete
        contentBinding.imgDelete.setOnClickListener {
            selectedImagePath = ""
            contentBinding.layoutImage.visibility = View.GONE

        }

        // Label Delete
        contentBinding.labelClearImg.setOnClickListener {

            if(labelclearedclicked == 1)
            {
                labelclearedclicked=0
            }
            if(labelclearedclicked==0)
            {
                labelclearedclicked=1
            }
            if (user != null) {
                // Use user.username, user.password, etc.
                Log.d("USER", "")
                val username = user?.username
                idOfUser = user!!.id
                Log.d("IDOFUSER", idOfUser.toString())
                Log.d("USER", username.toString())
            } else {
                Log.d("NULL create view", "")
            }

            note = args.note
            if (note != null) {
                // Use user.username, user.password, etc.
                Log.d("note", "")
                idOfNote = note!!.id
                Log.d("IDOFNOTE", idOfNote.toString())
                Log.d("note", note.toString())
            } else {
                Log.d("NULL create view", "note")
            }
            lifecycleScope.launch {
                user?.let { it1 -> note?.let { it2 ->
                    noteActivityViewModel.removeeLabelFromNote(it1,it2)
                }
                }

                contentBinding.labelImg.visibility=View.GONE
                contentBinding.labelText.visibility=View.GONE
                contentBinding.labelClearImg.visibility=View.GONE
            }

        }

        // Add Label
        contentBinding.labelNote.setOnClickListener {

            contentBinding.labelImg.visibility = View.VISIBLE
            contentBinding.labelText.visibility = View.VISIBLE
            val user = userargs.user as? User
            note = args.note as? Note
            if (user != null) {
                Log.d("add label user before", note.toString())
                val action = SaveOrUpdateFragmentDirections.actionSaveOrUpdateFragmentToAddLabelFragment(user,note)
                findNavController().navigate(action)
            } else {
                // Handle the case where user is null, maybe show an error message
                Log.d("hhhhaawww", "")
            }
        }
        // Menu Open
        contentBinding.menuIcon.setOnClickListener {
            showPopupMenu(it)
        }

        // Pin note
        contentBinding.pinNote.setOnClickListener {
            Log.d("pin clicked", "")
            val notee = args.note

            lifecycleScope.launch {
                if (note != null) {
                    pinnedornotint = noteActivityViewModel.pinnedornot(idOfNote)
                    Log.d("pinned h ya ni ", pinnedornotint.toString())
                    if (pinnedornotint == 0) {
                        Log.d("pinned ni h ", "on clicked 1")
                      //  contentBinding.pinNote.setBackgroundResource(R.drawable.baseline_favorite_border_24)
                    } else {
                        Log.d("pinned hai", " on clicked 1")
                      //  contentBinding.pinNote.setBackgroundResource(R.drawable.baseline_favorite_24)
                    }
                }
            }
            if(notee!=null)
            {
                noteActivityViewModel.toggleNotePinnedStatus(notee)
            }

           /* if (notee != null) {
                pinned = if (pinned == 0) 1 else 0
                if (pinned == 0) {
                    contentBinding.pinNote.setBackgroundResource(R.drawable.baseline_favorite_border_24)
                }
                if (pinned == 1) {
                    contentBinding.pinNote.setBackgroundResource(R.drawable.baseline_favorite_24)
                }
                Log.d("status", pinned.toString())
                noteActivityViewModel.toggleNotePinnedStatus(notee)
                Log.d("status", pinned.toString())
            }*/

            lifecycleScope.launch {
                if (note != null) {
                    pinnedornotint = noteActivityViewModel.pinnedornot(idOfNote)
                    Log.d("pinned h ya ni ", pinnedornotint.toString())
                    if (pinnedornotint == 0) {
                        Log.d("pinned ni h ", "on clicked 2")
                        contentBinding.pinNote.setBackgroundResource(R.drawable.baseline_favorite_border_24)
                    } else {
                        Log.d("pinned hai", " on clicked 2")
                        contentBinding.pinNote.setBackgroundResource(R.drawable.baseline_favorite_24)
                    }
                }
            }

        }


        // Add Image
        contentBinding.addImage.setOnClickListener {
            Log.d("ADD IMAGE", "")
            pickImageFromGallery()
        }

        // Share Note
        contentBinding.sendNote.setOnClickListener {
            Log.d("SHARED CLICKED", "")
            val notee = args.note
            val pdfFile = createPdfFromNote(notee, requireContext())
            if (pdfFile != null) {
                val pdfUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.notesapplication.fileprovider",
                    pdfFile
                )

                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "application/pdf"
                shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)
                startActivity(Intent.createChooser(shareIntent, "Share PDF"))
            } else {
                Toast.makeText(requireContext(), "Error creating PDF", Toast.LENGTH_SHORT).show()
            }
        }

        // On clicking "Back Arrow"
        contentBinding.backButton.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }


        // On clicking "Save Note"
        contentBinding.saveNote.setOnClickListener {
            user?.let { it1 -> saveNote(it1) }
        }
        try {
            contentBinding.etNoteContent.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    contentBinding.bottomBar.visibility = View.VISIBLE
                    contentBinding.etNoteContent.setStylesBar(contentBinding.styleBar)
                } else {
                    contentBinding.bottomBar.visibility = View.GONE
                }
            }
        } catch (e: Throwable) {
            Log.d("TAG", e.stackTrace.toString())
        }


        // On clicking "ColorPicker"
        contentBinding.fabColorPicker.setOnClickListener {
            Log.d("color picker clickeed", "")
            val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
            val bottomSheetView: View = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
            with(bottomSheetDialog)
            {
                setContentView(bottomSheetView)
                Log.d("color picker clickeed 2", "")
                show()
            }

            val bottomSheetBinding = BottomSheetLayoutBinding.bind(bottomSheetView)
            bottomSheetBinding.apply {
                colorPicker.apply {
                    setSelectedColor(color)
                    setOnColorSelectedListener { value ->
                        color = value
                        contentBinding.apply {
                            noteContentFragmentParent.setBackgroundColor(color)
                            mainLayout.setBackgroundColor(color)
                            styleBar.setBackgroundColor(color)
                            optionsLayout.setBackgroundColor(color)
                            toolbarFragmentNoteContent.setBackgroundColor(color)
                            bottomBar.setBackgroundColor(color)
                            etTitle.setBackgroundColor(color)
                            etNoteContent.setBackgroundColor(color)
                            //activity.window.statusBarColor=color
                        }
                        bottomSheetBinding.bottomSheetParent.setCardBackgroundColor(color)
                    }
                }

                // background picker
                bottomSheetBinding.floralBgImg.setOnClickListener {
                    contentBinding.mainLayout.setBackgroundResource(R.drawable.flower_bg)
                    bg = 1
                }
                bottomSheetBinding.leafBgImg.setOnClickListener {
                    contentBinding.mainLayout.setBackgroundResource(R.drawable.leaf_pdf)
                    bg = 2
                }
                bottomSheetBinding.foodBgImg.setOnClickListener {
                    contentBinding.mainLayout.setBackgroundResource(R.drawable.food_pdf)
                    bg = 3
                }
                bottomSheetBinding.dogBgImg.setOnClickListener {
                    contentBinding.noteContentFragmentParent.setBackgroundResource(R.drawable.dog)
                    bg = 4
                }
                bottomSheetBinding.beachBgImg.setOnClickListener {
                    contentBinding.mainLayout.setBackgroundResource(R.drawable.beach)
                    bg = 5
                }
                bottomSheetBinding.whiteBgImg.setOnClickListener {
                    contentBinding.mainLayout.setBackgroundResource(R.drawable.white)
                    bg = 6
                }
                bottomSheetBinding.buildingBgImg.setOnClickListener {
                    contentBinding.mainLayout.setBackgroundResource(R.drawable.building)
                    bg = 7
                }


                bottomSheetParent.setCardBackgroundColor(color)
            }
            bottomSheetView.post {
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        // opens with existing note item
        setUpNote()
    }

    private fun showPopupMenu(view: View?) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.note_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_item1 -> {
                    // Handle Item 1 click

                    Log.d("ADD IMAGE", "")
                    pickImageFromGallery()
                    true
                }

                R.id.share_note -> {
                    // Handle Item 2 click
                    Log.d("SHARED CLICKED", "")
                    val notee = args.note
                    val pdfFile = createPdfFromNote(notee, requireContext())
                    if (pdfFile != null) {
                        val pdfUri = FileProvider.getUriForFile(
                            requireContext(),
                            "com.example.notesapplication.fileprovider",
                            pdfFile
                        )

                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "application/pdf"
                        shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)
                        startActivity(Intent.createChooser(shareIntent, "Share PDF"))
                    } else {
                        Toast.makeText(requireContext(), "Error creating PDF", Toast.LENGTH_SHORT)
                            .show()
                    }
                    true
                }

                R.id.draw -> {
                    Log.d("Draw", "")

                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }


    private fun pickImageFromGallery() {
        Log.d("PICK IMG", "")
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_IMAGE)
        }
    }


    private fun setUpNote() {
        Log.d("setupnote", "")
        val note = args.note
        val title = contentBinding.etTitle
        val content = contentBinding.etNoteContent
        val lastEdited = contentBinding.lastEdited
        if (note == null) {
            // Last Edited Dated
            contentBinding.lastEdited.text =
                getString(R.string.edited_on, SimpleDateFormat.getInstance().format(Date()))
        }
        if (note != null) {
            title.setText(note.title)
            content.renderMD(note.content)
            color = note.color
            bg = note.bg

            if (note.imgPath != "") {
                selectedImagePath = note.imgPath!!
                contentBinding.imgNote.setImageBitmap(BitmapFactory.decodeFile(note.imgPath))
                contentBinding.layoutImage.visibility = View.VISIBLE
                contentBinding.imgNote.visibility = View.VISIBLE
                contentBinding.imgDelete.visibility = View.VISIBLE
            } else {
                contentBinding.layoutImage.visibility = View.GONE
                contentBinding.imgNote.visibility = View.GONE
                contentBinding.imgDelete.visibility = View.GONE
            }


            lastEdited.text = getString(R.string.edited_on, note.date)

            contentBinding.apply {
                job.launch {
                    delay(10)
                    noteContentFragmentParent.setBackgroundColor(color)
                    //  toolbarFragmentNoteContent.setBackgroundColor(color)
                    //  bottomBar.setBackgroundColor(color)
                    if (bg == 1) {
                        noteContentFragmentParent.setBackgroundResource(R.drawable.flower_pdf)
                    }
                    if (bg == 2) {
                        noteContentFragmentParent.setBackgroundResource(R.drawable.dog_pdf)
                    }
                    if (bg == 3) {
                        noteContentFragmentParent.setBackgroundResource(R.drawable.food_pdf)
                    }
                    if (bg == 4) {
                        noteContentFragmentParent.setBackgroundResource(R.drawable.dog)
                    }
                    if (bg == 5) {
                        noteContentFragmentParent.setBackgroundResource(R.drawable.beach)
                    }
                    if (bg == 6) {
                        noteContentFragmentParent.setBackgroundResource(R.drawable.white)
                    }
                    if (bg == 7) {
                        noteContentFragmentParent.setBackgroundResource(R.drawable.building)
                    }
                }
                //  toolbarFragmentNoteContent.setBackgroundColor(color)
                //  bottomBar.setBackgroundColor(color)
            }
          //  activity?.window?.statusBarColor = note.color
        }
    }

    fun createPdfFromNote(note: Note?, context: Context): File? {
        if (note == null) {
            Log.e("createPdfFromNote", "Note is null, cannot create PDF.")
            return null
        }

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 page size in points
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        // Calculate scaling factors
        val pageWidth = pageInfo.pageWidth
        val pageHeight = pageInfo.pageHeight


        // Draw background
        val paint = Paint()
        paint.color = note.color // Adjust the color as needed
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)


        // Set text color
        paint.textSize = 20f
        paint.color = Color.BLACK // Set the desired text color

        var yPosition = 50f
        val lineHeight = 30f

        val linesPerPage = (pageHeight - yPosition) / lineHeight

        note.content?.split("\n")?.forEach {
            if (yPosition + lineHeight > pageHeight) {
                // If the content exceeds the current page, start a new page
                document.finishPage(page)
                val newPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, document.pages.size + 1).create()
                page = document.startPage(newPageInfo)
                canvas = page.canvas
                yPosition = 50f

                // Set background color on new page
                paint.color = note.color
                canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

                // Set text color on new page
                paint.color = Color.BLACK
            }
            canvas.drawText(it, 50f, yPosition, paint)
            yPosition += lineHeight
        }

        // Add image to the PDF (code for adding image)
        if (!note.imgPath.isNullOrEmpty()) {
            val imageUri = Uri.parse(note.imgPath)
            val imageFile = File(imageUri.path) // Create a File object from the Uri path
            if (imageFile.exists()) {

                val bitmap =
                    BitmapFactory.decodeFile(imageFile.absolutePath)// Define a Matrix to scale the bitmap
                val matrix = Matrix()

                // Set the scale factors (adjust these values as needed)
                val scaleX = 0.3f  // 50% of the original width
                val scaleY = 0.3f  // 50% of the original height
                matrix.postScale(scaleX, scaleY)

                // Apply the Matrix to the bitmap
                val scaledBitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                // Draw the scaled bitmap on the canvas
                paint.color = Color.BLACK // Set the desired text color here as well (if needed)
                canvas.drawBitmap(scaledBitmap, 50f, yPosition, paint)
            } else {
                Log.e("PDF Creation", "Image file does not exist: ${imageFile.absolutePath}")
            }
        }
        document.finishPage(page)

        // Save the PDF
        val pdfFile = File(context.getExternalFilesDir(null), "${note.title}.pdf")
        try {
            val outputStream = FileOutputStream(pdfFile)
            document.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            Log.d("SAVE PDF ", "")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("PDF Creation", "Error creating PDF: ${e.message}")
            return null
        }

        document.close()

        return pdfFile
    }

}