package com.example.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.storyapp.R
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.main.MainActivity
import com.example.storyapp.data.Result
import com.example.storyapp.view.register.RegisterActivity
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: LoginViewModel by viewModels {
            factory
        }

        playAnimation()

        binding.btnToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            viewModel.login(email, password).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.pbLogin.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            binding.pbLogin.visibility = View.GONE
                            val message = result.data.message
                            val loginResult = result.data.loginResult
                            viewModel.saveSession(
                                UserModel(
                                    email,
                                    loginResult?.token.toString()
                                )
                            )
                            alertResponse(message.toString())
                        }

                        is Result.Error -> {
                            binding.pbLogin.visibility = View.GONE
                            val errorMessage = result.error
                            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        val label = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.tvDesc, View.ALPHA, 1f).setDuration(500)
        val email =
            ObjectAnimator.ofFloat(binding.edLoginEmailLayout, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.edLoginPasswordLayout, View.ALPHA, 1f).setDuration(500)
        val button = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val toRegister =
            ObjectAnimator.ofFloat(binding.tvToRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(label, desc, email, password, button, toRegister)
            start()
        }
    }

    private fun alertResponse(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(message)
            setMessage("You successfully login")
            setPositiveButton("Ok") { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }
}