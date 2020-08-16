package com.trystan.shmecipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth

        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser

            if (firebaseUser == null) {
                Log.d("Authenticator", "User not authenticated")

                findNavController().navigate(R.id.loginFragment)
            } else {
                Log.d("Authenticator", "User is authenticated!")
            }
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

}