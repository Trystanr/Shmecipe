package com.trystan.shmecipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.trystan.shmecipe.data.RecipePost
import com.trystan.shmecipe.databinding.FragmentHomeBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_all_recipe_item.*
import java.lang.Exception


class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Set up binding before anything else
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        auth = Firebase.auth
        db = Firebase.firestore

        // Groupie adapter
        var adapter = GroupAdapter<GroupieViewHolder>()

        binding.allRecipeRecyclerView.adapter = adapter

        (activity as AppCompatActivity).supportActionBar?.title = "Shmecipe"

        // Go to detail view
        adapter.setOnItemClickListener { item, view ->
            val recipeItem = item as RecipeItem
            Log.d("ItemClick", "ID: ${recipeItem.recipeItem.id}")

            val action = HomeFragmentDirections.actionHomeFragmentToRecipeItemDetailFragment(recipeItem.recipeItem.id)

            findNavController().navigate(action)
        }

        // Spinner to select category to view
        binding.recipeSelect.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            var userSelect = -1

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Spinner onItemSelected gets called on init and on view pop
                Log.d("recipes", "Clicked item ${position}")

                userSelect++

                if (userSelect < 1) {
                    userSelect++
                } else {
                    userSelect = -2

                    val cat = binding.recipeSelect.selectedItem.toString()
                    Log.d("recipes", "${cat}")

                    binding.recipeSelect.setSelection(0,false)


                    val action = HomeFragmentDirections.actionHomeFragmentToRecipeCategoryFragment(resources.getStringArray(R.array.categories)[position])
                    findNavController().navigate(action)
                }
            }

        }

        // Get list of recipes and add to adapter
        db.collection("recipes")
            .get()
            .addOnSuccessListener {
                for (recipe in it) {
                    val resultRecipeItem = recipe.toObject<RecipePost>()

                    resultRecipeItem.id = recipe.id

                    Log.d("recipe", "${resultRecipeItem}")
                    adapter.add(RecipeItem(resultRecipeItem))

                    binding.recipeLoading.visibility = View.GONE
                }
            }

        // Check if user authenticated
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser

            if (firebaseUser == null) {
                Log.d("Authenticator", "User not authenticated")

                findNavController().navigate(R.id.loginFragment)
            } else {
                Log.d("Authenticator", "User is authenticated!")
            }
        }

        binding.createRecipe.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_writeRecipeFragment)
        }

        return binding.root
    }

}


class RecipeItem(val recipeItem: RecipePost): Item() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.mainHeading.text = recipeItem.title
        viewHolder.subHeading.text = recipeItem.category
        viewHolder.timeStamp.text = recipeItem.timestamp.toDate().toString()
        if (recipeItem.headerImageURL != "" && recipeItem.headerImageURL.isNotEmpty()) {
            Picasso
                .get()
                .load(recipeItem.headerImageURL)
                .fit().centerCrop()
                .into(viewHolder.recipeImage,
                    object: com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            viewHolder.loadingCircle.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            viewHolder.recipeImage.visibility = View.GONE
                            viewHolder.loadingCircle.visibility = View.GONE
                        }
                    }
                )
        } else {
            viewHolder.loadingCircle.visibility = View.GONE
            viewHolder.recipeImage.visibility = View.GONE
        }

    }

    override fun getLayout(): Int = R.layout.fragment_all_recipe_item
}