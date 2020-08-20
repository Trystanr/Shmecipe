package com.trystan.shmecipe

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.trystan.shmecipe.databinding.FragmentLoginBinding
import com.trystan.shmecipe.databinding.FragmentRegisterBinding

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        // Sign user in if login clicked
        binding.loginSubmit.setOnClickListener {
            auth.signInWithEmailAndPassword(binding.loginEmail.text.toString().trim(), binding.loginPassword.text.toString().trim())
                .addOnSuccessListener {
                    Log.d("Authenticator", "User logged in successfully")
                    hideKeyboard()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Login failure: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.registerPrompt.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }


        return binding.root
    }



}