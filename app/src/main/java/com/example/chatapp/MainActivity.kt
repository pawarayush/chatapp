package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.UUID

class MainActivity :  AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Regester.setOnClickListener{
            performregester()

        }
        Already_have_an_account.setOnClickListener{
            Log.d("MainActivity", "Try to show login activity: ")
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        selectphoto.setOnClickListener {
            Log.d("MainActivity", "Try to Show Photo selected: ")
            val intent=Intent(Intent.ACTION_PICK)
            intent.type=("image/*")
            startActivityForResult(intent, 0)


        }
    }
    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int , resultCode: Int , data: Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        if (requestCode ==0 && resultCode == Activity.RESULT_OK && data !== null){
            Log.d("MainActivity" , "Photo was selected : ")

            selectedPhotoUri= data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            selectimageround.setImageBitmap(bitmap)
            selectphoto.alpha =0f


//            val bitmapDrawable = BitmapDrawable(bitmap)
//            selectphoto.setBackgroundDrawable( bitmapDrawable)
        }
    }

    private fun performregester() {
        val email =  Email_edittext.text.toString()
        val password =Password_edittext.text.toString()
        if (email.isEmpty() || password.isEmpty()){
            Log.e("MainActivity", "email is: " + email )
            Log.e("MainActivity", "password is: $password" )
            Toast.makeText(this ,"Please enter the id",Toast.LENGTH_SHORT).show()
            return

        }



        ////////////////////


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                ////else
                Log.d("MainActivity" , "successfully created user with uid:${it.result.user !!.uid} ")
                uploadImageToFirebaseStorage()

            }

            .addOnFailureListener{
                Log.d("MainActivity","Failed to create user :${it.message}")
                Toast.makeText(this ,"Failed to create user :${it.message}",Toast.LENGTH_SHORT).show()

            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null)return
        val filename = UUID.randomUUID().toString()

        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("MainActivity" , "Sucessfully uploaded ImageToFirebaseStorage:${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("MainActivity" , "FileLocation: $it")
                    saveuserTofirebaseDatabase(it.toString())
                }
            }

    }

    private fun saveuserTofirebaseDatabase(profileimageurl:String) {
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        val user= User(uid, Username_edittext.text.toString(),profileimageurl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("MainActivity" , "saveuserTofirebaseDatabase")


            }
    }

}
class User(val uid: String, val username: String ,val profileimage:String)



