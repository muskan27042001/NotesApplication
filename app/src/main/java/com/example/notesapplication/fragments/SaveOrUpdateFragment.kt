package com.example.notesapplication.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.notesapplication.R
import com.example.notesapplication.activities.MainActivity
import com.example.notesapplication.databinding.BottomSheetLayoutBinding
import com.example.notesapplication.databinding.FragmentSaveOrUpdateBinding
import com.example.notesapplication.model.Note
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

    private lateinit var navController : NavController
    private lateinit var contentBinding : FragmentSaveOrUpdateBinding
    private var note : Note?=null
    private var color = -1
    private lateinit var result : String
    private val noteActivityViewModel : NoteActivityViewModel by activityViewModels()
    private val currentDate = SimpleDateFormat.getInstance().format(Date())  /// it will return current date and time
    private val job = CoroutineScope(Dispatchers.Main)
    private val args : SaveOrUpdateFragmentArgs by navArgs()
    private var selectedImagePath = ""

private var REQUEST_CODE_IMAGE = 789


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val animation = MaterialContainerTransform().apply{
            drawingViewId=R.id.fragment
            scrimColor= Color.TRANSPARENT
            duration=300L
        }
        sharedElementEnterTransition=animation
        sharedElementReturnTransition=animation



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("6","")
        super.onActivityResult(requestCode, resultCode, data)
           Log.d("7","")
           if (data != null){
               var selectedImageUrl = data.data
               Log.d("url", selectedImageUrl.toString())
               if (selectedImageUrl != null){
                   try {
                       Log.d("yay","")
                       var inputStream = requireActivity().contentResolver.openInputStream(selectedImageUrl)
                       var bitmap = BitmapFactory.decodeStream(inputStream)
                       contentBinding.imgNote.setImageBitmap(bitmap)
                       contentBinding.imgNote.visibility = View.VISIBLE


                       selectedImagePath = getPathFromUri(selectedImageUrl)!!
                   }catch (e:Exception){
                       Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
                   }

               }
           }


    }



    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath:String? = null
        var cursor = requireActivity().contentResolver.query(contentUri,null,null,null,null)
        if (cursor == null){
            filePath = contentUri.path
        }else{
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }




    private fun saveNote() {
        if(contentBinding.etNoteContent.text.toString().isEmpty() || contentBinding.etTitle.text.toString().isEmpty())
        {
            Toast.makeText(activity,"Title is Empty. Please fill",Toast.LENGTH_SHORT).show()
        }
        else
        {
            note=args.note
            when(note)
            {
                null-> {  // Fresh Note
                    noteActivityViewModel.saveNote(Note(0, contentBinding.etTitle.text.toString(), contentBinding.etNoteContent.text.toString(), currentDate, color,selectedImagePath))

                    // To update live data
                    result="Note Saved"
                    setFragmentResult(
                        "key",
                        bundleOf("bundleKey" to result)
                    )
                    navController.navigate(SaveOrUpdateFragmentDirections.actionSaveOrUpdateFragmentToNoteFragment())
                }
                else-> {   // update note
                    updateNote()
                    navController.popBackStack()
                }
            }


        }
    }

    private fun updateNote() {
        if(note!=null)
        {
            noteActivityViewModel.updateNote(
                Note(
                    note!!.id,
                    contentBinding.etTitle.text.toString(),
                    contentBinding.etNoteContent.getMD(),
                    currentDate,
                    color,selectedImagePath
                )
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentBinding=FragmentSaveOrUpdateBinding.bind(view)
        navController= Navigation.findNavController(view)
        val activity = activity as MainActivity

        ViewCompat.setTransitionName(
           contentBinding.noteContentFragmentParent,
           "recyclerView_${args.note?.id}"
       )

        // Image Delete
        contentBinding.imgDelete.setOnClickListener {
            selectedImagePath = ""
            contentBinding.layoutImage.visibility = View.GONE

        }

        // Menu Open
        contentBinding.menuIcon.setOnClickListener {
            showPopupMenu(it)
        }

        // Add Image
        contentBinding.addImage.setOnClickListener {
            Log.d("ADD IMAGE","")
            pickImageFromGallery()
        }

        // Share Note
        contentBinding.shareNote.setOnClickListener{
            Log.d("SHARED CLICKED","")
            val notee=args.note
            val pdfFile = createPdfFromNote(notee,requireContext())
            if (pdfFile != null) {
                val pdfUri = FileProvider.getUriForFile(requireContext(), "com.example.notesapplication.fileprovider", pdfFile)

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
            saveNote()
        }
        try
        {
            contentBinding.etNoteContent.setOnFocusChangeListener { _,hasFocus ->
                if(hasFocus)
                {
                    contentBinding.bottomBar.visibility=View.VISIBLE
                    contentBinding.etNoteContent.setStylesBar(contentBinding.styleBar)
                }
                else{
                    contentBinding.bottomBar.visibility=View.GONE
                }
            }
        }
        catch (e : Throwable)
        {
            Log.d("TAG",e.stackTrace.toString())
        }


        // On clicking "ColorPicker"
        contentBinding.fabColorPicker.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext(),R.style.BottomSheetDialog)
            val bottomSheetView : View =layoutInflater.inflate(R.layout.bottom_sheet_layout,null)

            with(bottomSheetDialog)
            {
                setContentView(bottomSheetView)
                show()
            }

            val bottomSheetBinding = BottomSheetLayoutBinding.bind(bottomSheetView)
            bottomSheetBinding.apply {
                colorPicker.apply {
                    setSelectedColor(color)
                    setOnColorSelectedListener { value->
                            color=value
                            contentBinding.apply {
                            noteContentFragmentParent.setBackgroundColor(color)
                                mainLayout.setBackgroundColor(color)
                                styleBar.setBackgroundColor(color)
                            toolbarFragmentNoteContent.setBackgroundColor(color)
                            bottomBar.setBackgroundColor(color)
                                etTitle.setBackgroundColor(color)
                                etNoteContent.setBackgroundColor(color)
                            activity.window.statusBarColor=color
                        }
                        bottomSheetBinding.bottomSheetParent.setCardBackgroundColor(color)
                    }
                }

                // background picker
                bottomSheetBinding.loveBgImg.setOnClickListener {
                    contentBinding.mainLayout.setBackgroundResource(R.drawable.love_bg)
                }
                bottomSheetParent.setCardBackgroundColor(color)
            }
            bottomSheetView.post{
                bottomSheetDialog.behavior.state= BottomSheetBehavior.STATE_EXPANDED
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

                    Log.d("ADD IMAGE","")
                    pickImageFromGallery()
                    true
                }
                R.id.share_note -> {
                    // Handle Item 2 click
                    Log.d("SHARED CLICKED","")
                    val notee=args.note
                    val pdfFile = createPdfFromNote(notee,requireContext())
                    if (pdfFile != null) {
                        val pdfUri = FileProvider.getUriForFile(requireContext(), "com.example.notesapplication.fileprovider", pdfFile)

                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "application/pdf"
                        shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri)
                        startActivity(Intent.createChooser(shareIntent, "Share PDF"))
                    } else {
                        Toast.makeText(requireContext(), "Error creating PDF", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.draw ->{
                    Log.d("Draw","")

                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }


    private fun pickImageFromGallery(){
        Log.d("PICK IMG","")
        var intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null){
            startActivityForResult(intent,REQUEST_CODE_IMAGE)
        }
    }

  /*  private fun pickImageFromGallery() {
        // Check if the permission is granted
        Log.d("1","")
        if (isReadPermissionGranted()) {
            // Permission granted, proceed with picking image
            Log.d("PICK IMG", "")
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(intent, REQUEST_CODE_IMAGE)
            }
        } else {
            // Permission not granted, request it
            Log.d("2","")
            requestPermission()
        }
    }*/

    private fun setUpNote() {
        val note=args.note
        val title=contentBinding.etTitle
        val content=contentBinding.etNoteContent
        val lastEdited=contentBinding.lastEdited
        if(note==null)
        {
            // Last Edited Dated
            contentBinding.lastEdited.text=getString(R.string.edited_on,SimpleDateFormat.getInstance().format(Date()))
        }
        if(note!=null)
        {
            title.setText(note.title)
            content.renderMD(note.content)
            color=note.color

            if (note.imgPath != ""){
                selectedImagePath = note.imgPath!!
                contentBinding.imgNote.setImageBitmap(BitmapFactory.decodeFile(note.imgPath))
                contentBinding.layoutImage.visibility = View.VISIBLE
                contentBinding.imgNote.visibility = View.VISIBLE
                contentBinding.imgDelete.visibility = View.VISIBLE
            }else{
                contentBinding.layoutImage.visibility = View.GONE
                contentBinding.imgNote.visibility = View.GONE
                contentBinding.imgDelete.visibility = View.GONE
            }


            lastEdited.text=getString(R.string.edited_on,note.date)

            contentBinding.apply {
                job.launch {
                    delay(10)
                    noteContentFragmentParent.setBackgroundColor(color)
                }
                toolbarFragmentNoteContent.setBackgroundColor(color)
                bottomBar.setBackgroundColor(color)
            }
            activity?.window?.statusBarColor=note.color
        }
    }


    fun createPdfFromNote(note: Note?, context: Context): File? {
        if (note == null) {
            Log.e("createPdfFromNote", "Note is null, cannot create PDF.")
            return null
        }
        Log.d("FUNC","")
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 page size in points
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        // Add text content to the PDF
        val paint = Paint()
        paint.textSize = 20f
        var yPosition = 50f
        val lineHeight = 30f

        if(note==null)
        {
            Log.d("NULL NOTE ","")
        }
        Log.d("CONTENT",note.content)
        note.content.split("\n").forEach {
            canvas.drawText(it, 50f, yPosition, paint)
            yPosition += lineHeight
        }

        // Add image to the PDF
       if (!note.imgPath.isNullOrEmpty()) {
            val imageUri = Uri.parse(note.imgPath)
            val imageFile = File(imageUri.path) // Create a File object from the Uri path
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)// Define a Matrix to scale the bitmap
                val matrix = Matrix()

                // Set the scale factors (adjust these values as needed)
                val scaleX = 0.3f  // 50% of the original width
                val scaleY = 0.3f  // 50% of the original height
                matrix.postScale(scaleX, scaleY)

                // Apply the Matrix to the bitmap
                val scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                // Draw the scaled bitmap on the canvas
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
            Log.d("SAVE PDF ","")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("PDF Creation", "Error creating PDF: ${e.message}")
            return null
        }

        document.close()

        return pdfFile
    }


}