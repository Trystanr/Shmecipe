package com.trystan.shmecipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.trystan.shmecipe.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

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

        // Inflate the layout for this fragment
        return binding.root
    }

}