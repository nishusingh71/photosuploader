@file:Suppress("DEPRECATION")

package com.example.photosuploader

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var buttonChoose:Button
    private lateinit var buttonUpload:Button
    private lateinit var imageView: ImageView
    private  var filePath: Uri?=null
    internal var storageReference: StorageReference? =null
    private var storage: FirebaseStorage? =null
    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonChoose=findViewById(R.id.button_chooser)
        buttonUpload=findViewById(R.id.button_uploader)
        imageView=findViewById(R.id.imageView)

        storage= FirebaseStorage.getInstance()
        storageReference=storage!!.reference


// for choose image from your device
        buttonChoose.setOnClickListener {

                val intentChooser = Intent()
                intentChooser.type = "image/*"
                intentChooser.action=Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intentChooser, "Select Photo's"), 71)

        }
        //for upload photos on FirebaseStorage
        buttonUpload.setOnClickListener {
            @Suppress("DEPRECATION")
            val progressDialog=ProgressDialog(this)
            progressDialog.setTitle("Uploading....")
            progressDialog.show()
            val imagesRef = storageReference!!.child("image/"+UUID.randomUUID().toString())
            imagesRef.putFile(filePath!!)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(this@MainActivity, "Uploaded", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { p0 ->
                        progressDialog.dismiss()
                        Toast.makeText(this@MainActivity, "Failed" + p0.message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnProgressListener {  taskSnapShot->
                        val progress=100.0*taskSnapShot.bytesTransferred/taskSnapShot.totalByteCount
                        @Suppress("DEPRECATION")
                        progressDialog.setMessage("Uploaded"+progress.toInt()+"%...")
                    }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==71&&resultCode== Activity.RESULT_OK&&data!=null&&data.data!=null){
             filePath=data.data

            try {
               @Suppress("DEPRECATION")
               val bitmap=MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imageView.setImageBitmap(bitmap)
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }

}