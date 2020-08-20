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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.trystan.shmecipe.databinding.FragmentRegisterBinding
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = Firebase.auth
        db = Firebase.firestore
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)

        binding.registerSubmit.setOnClickListener {
            auth.createUserWithEmailAndPassword(binding.registerEmail.text.toString().trim(), binding.registerPassword.text.toString().trim())
                .addOnCompleteListener { it: Task<AuthResult> ->
                    if(!it.isSuccessful) return@addOnCompleteListener

                    Log.d("Authentication", "Authentication Successful")
                    val uid = FirebaseAuth.getInstance().uid ?: ""
                    val user = hashMapOf(
                        "username" to registerUsername.text.toString(),
                        "email" to registerEmail.text.toString()
                    )
                    db.collection("users")
                        .document(uid)
                        .set(user)
                        .addOnSuccessListener {
                            Log.d("Authentication", "Registered Successfully")
                            hideKeyboard()
                            findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                        }
                        .addOnFailureListener {
                            Log.d("Authentication", "Database Error: ${it.message}")
                        }

                }
                .addOnFailureListener {
                    Log.d("Authentication", "The registration failed ${it.message}")
                    Toast.makeText(context, "The registration failed ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return binding.root
    }

}