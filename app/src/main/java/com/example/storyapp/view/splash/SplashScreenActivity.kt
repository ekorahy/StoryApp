package com.example.storyapp.view.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivitySplashScreenBinding
import com.example.storyapp.view.ViewModelFactoryAuthentication
import com.example.storyapp.view.login.LoginActivity
import com.example.storyapp.view.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val viewModel by viewModels<SplashScreenViewModel> {
        ViewModelFactoryAuthentication.getInstance(this)
    }

    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playAnimation()
    }

    private fun playAnimation() {
        val ivLogo = ObjectAnimator.ofFloat(binding.ivLogo, View.ALPHA, 1f).setDuration(2000)
        val tvLabelBy = ObjectAnimator.ofFloat(binding.tvLabelBy, View.ALPHA, 1f).setDuration(2000)
        val tvAppDeveloper =
            ObjectAnimator.ofFloat(binding.tvAppDeveloper, View.ALPHA, 1f).setDuration(2000)

        val together = AnimatorSet().apply {
            playTogether(tvLabelBy, tvAppDeveloper)
        }

        AnimatorSet().apply {
            playSequentially(ivLogo, together)
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onAnimationFinished()
                }
            })
            start()
        }
    }

    private fun onAnimationFinished() {
        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        finish()
    }
}