package com.trystan.shmecipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.trystan.shmecipe.databinding.FragmentWriteRecipeBinding


class WriteRecipeFragment : Fragment() {

    private lateinit var binding: FragmentWriteRecipeBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_write_recipe, container, false)
        db = Firebase.firestore
        auth = Firebase.auth

        binding.cancelRecipePost.setOnClickListener {
            findNavController().navigate(R.id.action_writeRecipeFragment_to_homeFragment)
        }

        binding.PublishRecipe.setOnClickListener {
            val recipe = hashMapOf(
                "userID" to auth.currentUser?.uid,
                "timestamp" to Timestamp.now(),
                "title" to binding.recipeMainHeading.text.toString(),
                "subheading" to binding.recipeSubHeading.text.toString(),
                "body" to binding.recipeBodyCopy.text.toString()
            )

            db.collection("recipes")
                .add(recipe)
                .addOnFailureListener {
                    Log.d("recipe","Failed to post recipe")
                    Toast.makeText(context, "Failed to post recipe", Toast.LENGTH_SHORT).show()
                }
                .addOnSuccessListener {
                    Log.d("recipe","Successfully posted recipe")
                    Toast.makeText(context, "Successfully posted recipe", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_writeRecipeFragment_to_homeFragment)
                }
        }

        return binding.root
    }

}