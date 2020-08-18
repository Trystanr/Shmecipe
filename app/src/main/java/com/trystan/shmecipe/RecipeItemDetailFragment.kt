package com.trystan.shmecipe

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.trystan.shmecipe.data.RecipePost
import com.trystan.shmecipe.data.UserPost
import com.trystan.shmecipe.databinding.FragmentRecipeItemDetailBinding
import kotlinx.android.synthetic.main.fragment_all_recipe_item.*
import java.lang.Exception
import java.time.format.DateTimeFormatter

class RecipeItemDetailFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: FragmentRecipeItemDetailBinding
    private lateinit var db: FirebaseFirestore

    private val args: RecipeItemDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_item_detail, container, false)

        auth = Firebase.auth
        db = Firebase.firestore

        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser

            db.collection("recipes").document(args.recipeid)
                .get()
                .addOnSuccessListener {
                    val item = it.toObject<RecipePost>()

                    binding.recipeDetailTitle.text = item?.title
                    binding.recipeDetailIngredients.text = item?.subheading
                    binding.recipeDetailTimestamp.text = item?.timestamp?.toDate().toString()
                    binding.recipeDetailBody.text = item?.body

                    if (firebaseUser != null) {
                        // User is signed in
                        Log.d("Authenticator", "User is signed in: ${firebaseUser.getIdToken(true)}")
                        Log.d("Authenticator", "User is signed in: ${firebaseUser.uid}")
                        Log.d("Authenticator", "User id: ${item.toString()}")
                    } else {
                        // No user is signed in
                        Log.d("Authenticator", "User not signed in")
                    }

                    // Get user name by uid
                    db.collection("users")
                        .whereEqualTo(FieldPath.documentId(), item?.userID)
                        .get()
                        .addOnSuccessListener {
                            for (user in it) {
                                val resultRecipeItem = user.toObject<UserPost>()

                                binding.recipeDetailUser.text = resultRecipeItem.username

                                if (item?.userID == firebaseUser?.uid) {
                                    binding.recipeDetailDelete.animate().alpha(1f)
                                }
                            }
                        }
                        .addOnFailureListener {
                            Log.d("Authenticator", "Could not find user")
                        }

                    binding.recipeDetailLoading.visibility = View.GONE

                    if (item?.headerImageURL != "" && item?.headerImageURL!!.isNotEmpty()) {
                        Picasso.get().load(item?.headerImageURL).fit().centerCrop().into(binding.recipeDetailImage, object: com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                binding.recipeDetailImageLoading.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                binding.recipeDetailImage.visibility = View.GONE
                                binding.recipeDetailImageLoading.visibility = View.GONE
                            }
                        })
                    } else {
                        binding.recipeDetailImage.visibility = View.GONE
                        binding.recipeDetailImageLoading.visibility = View.GONE
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error, recipe not found", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_recipeItemDetailFragment_to_homeFragment)
                }


        }

        binding.recipeDetailDelete.setOnClickListener {
            db.collection("recipes").document(args.recipeid)
                .get()
                .addOnSuccessListener {
                    it.reference.delete().addOnSuccessListener {
                        Toast.makeText(context, "Successfully deleted recipe", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_recipeItemDetailFragment_to_homeFragment)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error, recipe not deleted", Toast.LENGTH_SHORT).show()
                }
        }

        return binding.root
    }
}