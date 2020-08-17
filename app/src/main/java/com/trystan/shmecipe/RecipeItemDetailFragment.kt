package com.trystan.shmecipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.trystan.shmecipe.data.RecipePost
import com.trystan.shmecipe.databinding.FragmentRecipeItemDetailBinding

class RecipeItemDetailFragment : Fragment() {

    private lateinit var binding: FragmentRecipeItemDetailBinding
    private lateinit var db: FirebaseFirestore

    private val args: RecipeItemDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_item_detail, container, false)

        binding.recipeDetailTitle.text = args.recipeid

        db = Firebase.firestore

        db.collection("recipes").document(args.recipeid)
            .get()
            .addOnSuccessListener {
                val item = it.toObject<RecipePost>()

                binding.recipeDetailTitle.text = item?.title
                binding.recipeDetailTitle2.text = item?.subheading
                binding.recipeDetailTitle3.text = item?.timestamp?.toDate().toString()
                binding.recipeDetailTitle4.text = item?.body
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error, recipe not found", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_recipeItemDetailFragment_to_homeFragment)
            }

        return binding.root
    }
}