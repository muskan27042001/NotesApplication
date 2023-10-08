package com.example.notesapplication.activities

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.notesapplication.databinding.ActivityMainBinding
import com.example.notesapplication.db.NoteDatabase
import com.example.notesapplication.repository.NoteRepository
import com.example.notesapplication.viewModel.NoteActivityViewModel
import com.example.notesapplication.viewModel.NoteActivityViewModelFactory

class MainActivity : AppCompatActivity() {

    lateinit var noteActivityViewModel : NoteActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private var REQUEST_CODE_PERMISSION = 123
    private var REQUEST_CODE_IMAGE = 789


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()  // Hide action bar
        Log.d("TAG","main activity created")
        binding=ActivityMainBinding.inflate(layoutInflater)
if(ContextCompat.checkSelfPermission(this@MainActivity,Manifest.permission.READ_EXTERNAL_STORAGE)!==PackageManager.PERMISSION_GRANTED)
{
    Log.d("1","")
    if(ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,Manifest.permission.READ_EXTERNAL_STORAGE)){
        Log.d("2","")
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),REQUEST_CODE_PERMISSION)
    }
    else{
        Log.d("3","")
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),REQUEST_CODE_PERMISSION)
    }
}

        try{
            setContentView(binding.root)

            val noteRepository= NoteRepository(NoteDatabase(this))
            val noteActivityViewModelFactory= NoteActivityViewModelFactory(noteRepository)
            noteActivityViewModel= ViewModelProvider(this,
                noteActivityViewModelFactory)[NoteActivityViewModel::class.java]

            NoteDatabase.invoke(this)



        }
        catch (e : Exception)
        {
            Log.d("EXE",e.toString())
        }

/*
// Initialize WorkManager
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

// Step 2: Schedule the Work (e.g., in your main application class)
        val periodicWorkRequest = PeriodicWorkRequest.Builder<PermanentDeletionWorker>(
            1, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
*/


    }

    fun changeTheme() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val newNightMode = if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(newNightMode)
        recreate() // Recreate the activity to apply the new theme
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("4",requestCode.toString())
       when(requestCode){
           REQUEST_CODE_PERMISSION->{
               Log.d("5","")

               if(grantResults.isNotEmpty() ){
Log.d("1.1","")
                   if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                   {
                       Log.d("6","")
                       if((ContextCompat.checkSelfPermission(this@MainActivity,Manifest.permission.READ_EXTERNAL_STORAGE)===PackageManager.PERMISSION_GRANTED)){
                         //  Toast.makeText(this,"granted",Toast.LENGTH_SHORT).show()

                       }

                   }
                   else{
                       Log.d("7","")
                     //  Toast.makeText(this,"denied",Toast.LENGTH_SHORT).show()
                   }

                   return
               }
           }
       }

    }
}
