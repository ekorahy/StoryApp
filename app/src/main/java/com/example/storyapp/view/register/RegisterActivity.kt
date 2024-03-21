package com.example.storyapp.view.register

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
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.login.LoginActivity
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val viewModel: RegisterViewModel by viewModels {
            factory
        }

        playAnimation()

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            viewModel.register(name, email, password).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.pbRegister.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            binding.pbRegister.visibility = View.GONE
                            val message = result.data.message
                            alertResponse(message.toString())
                        }

                        is Result.Error -> {
                            binding.pbRegister.visibility = View.GONE
                            val errorMessage = result.error
                            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.btnToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.tvDesc, View.ALPHA, 1f).setDuration(500)
        val name =
            ObjectAnimator.ofFloat(binding.edRegisterNameLayout, View.ALPHA, 1f).setDuration(500)
        val email =
            ObjectAnimator.ofFloat(binding.edRegisterEmailLayout, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.edRegisterPasswordLayout, View.ALPHA, 1f)
            .setDuration(500)
        val button = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)
        val toLogin = ObjectAnimator.ofFloat(binding.tvToLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, desc, name, email, password, button, toLogin)
            start()
        }
    }

    private fun alertResponse(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Success")
            setMessage(message)
            setPositiveButton("Ok") { _, _ ->
                finish()
            }
            create()
            show()
        }
    }
}