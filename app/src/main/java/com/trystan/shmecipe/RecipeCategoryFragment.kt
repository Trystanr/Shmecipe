package com.trystan.shmecipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
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
            Log.d("ItemClicked", "Item ID:")
        }

        db.collection("recipes")
            .whereEqualTo("category", "Main Dishes")
            .get()
            .addOnSuccessListener {
                for (recipe in it) {
                    val resultRecipeItem = recipe.toObject<RecipePost>()

                    resultRecipeItem.id = recipe.id

                    Log.d("RecipeItem", "${resultRecipeItem}")
                    adapter.add(RecipeItemTwo(resultRecipeItem))
                }
            }
            .addOnFailureListener {
                Log.d("RecipeItem", "Did not work")
            }

        return binding.root
    }

}

class RecipeItemTwo(val recipeItem: RecipePost): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.mainHeading.text = recipeItem.title
        viewHolder.subHeading.text = recipeItem.subheading
        viewHolder.timeStamp.text = recipeItem.timestamp.toDate().toString()
    }

    override fun getLayout(): Int = R.layout.fragment_all_recipe_item
}