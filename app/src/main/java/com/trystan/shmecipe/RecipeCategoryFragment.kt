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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.trystan.shmecipe.data.RecipePost
import com.trystan.shmecipe.databinding.FragmentRecipeCategoryBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_all_recipe_item.*


class RecipeCategoryFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentRecipeCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_category, container, false)

        db = Firebase.firestore

        var adapter = GroupAdapter<GroupieViewHolder>()

        binding.categoryRecipeRecyclerView.adapter = adapter

        adapter.setOnItemClickListener { item, view ->
            Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
        }

        db.collection("recipes")
            .whereEqualTo("category", "Main Dishes")
            .get()
            .addOnSuccessListener {
                for (recipe in it) {
                    val resultRecipeItem = recipe.toObject<RecipePost>()

                    resultRecipeItem.id = recipe.id

                    Log.d("RecipeItem", "${resultRecipeItem}")
                    adapter.add(RecipeItem(resultRecipeItem))
                }
            }
            .addOnFailureListener {
                Log.d("RecipeItem", "Did not work")
            }

        return binding.root
    }

}