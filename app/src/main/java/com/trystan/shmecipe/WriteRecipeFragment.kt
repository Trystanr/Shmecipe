package com.trystan.shmecipe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.trystan.shmecipe.databinding.FragmentWriteRecipeBinding
import kotlinx.android.synthetic.*
import java.util.*


class WriteRecipeFragment : Fragment() {

    private lateinit var binding: FragmentWriteRecipeBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    private lateinit var uri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_write_recipe, container, false)
        db = Firebase.firestore
        storage = Firebase.storage
        auth = Firebase.auth

        binding.cancelRecipePost.setOnClickListener {
            findNavController().navigate(R.id.action_writeRecipeFragment_to_homeFragment)
        }

        binding.recipeUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            startActivityForResult(intent, 0)
        }

        binding.PublishRecipe.setOnClickListener {

            if (binding.recipeSelect.selectedItemPosition !== 0) {
                val recipe = hashMapOf(
                    "userID" to auth.currentUser?.uid,
                    "timestamp" to Timestamp.now(),
                    "category" to binding.recipeSelect.selectedItem.toString(),
                    "title" to binding.recipeMainHeading.text.toString(),
                    "subheading" to binding.recipeSubHeading.text.toString(),
                    "body" to binding.recipeBodyCopy.text.toString()
                )

                db.collection("recipes")
                    .add(recipe)
                    .addOnFailureListener {
                        Log.d("recipe", "Failed to post recipe")
                        Toast.makeText(context, "Failed to post recipe", Toast.LENGTH_SHORT).show()
                    }
                    .addOnSuccessListener {
                        Log.d("recipe", "Successfully posted recipe")
                        hideKeyboard()

                        // Upload image
                        uploadImageToStorage(it.id)

                        Toast.makeText(context, "Successfully posted recipe", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().navigate(R.id.action_writeRecipeFragment_to_homeFragment)
                    }
            } else {
                Toast.makeText(context, "Select a category", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            uri = data.data!!

            Log.d("Recipe", "Photo uri: ${uri}")

            binding.recipeUploadImage.setImageURI(uri)
            binding.recipeUploadImage.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun uploadImageToStorage(recipeID: String) {
        val fileName = UUID.randomUUID().toString()
        Log.d("recipe", "UUID: ${fileName}")

        val ref = storage.getReference("/images/$fileName")

        if (Uri.EMPTY != uri) {
            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl
                        .addOnSuccessListener {
                            Log.d("recipe", "Image URL: ${it}")

                            // Upload image to recipe object
                            saveImageToRecipe(it.toString(), recipeID)
                        }
                        .addOnFailureListener {
                            Log.d("recipe", "Failed to upload URL: ${it}")
                            Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Log.d("recipe", "Failed to upload URL: ${it}")
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        }

    }

    private fun saveImageToRecipe(imageUrl: String, recipeID: String) {
        db.collection("recipes").document(recipeID).update("headerImageURL", imageUrl)
            .addOnSuccessListener {
                Log.d("recipe", "Added image to recipe item")
            }
            .addOnFailureListener {
                Log.d("recipe", "Did not add image to recipe item")
                Toast.makeText(context, "Failed to assign image to recipe", Toast.LENGTH_SHORT).show()
            }
    }


}