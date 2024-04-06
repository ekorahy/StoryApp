package com.example.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.repository.UserRepository
import com.example.storyapp.di.Injection
import com.example.storyapp.view.login.LoginViewModel
import com.example.storyapp.view.register.RegisterViewModel
import com.example.storyapp.view.splash.SplashScreenViewModel

class ViewModelFactoryAuthentication(private val userRepository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(SplashScreenViewModel::class.java) -> {
                SplashScreenViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactoryAuthentication? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactoryAuthentication {
            if (INSTANCE == null) {
                synchronized(ViewModelFactoryAuthentication::class.java) {
                    INSTANCE = ViewModelFactoryAuthentication(
                        Injection.provideRepositoryAuthentication(context)
                    )
                }
            }
            return INSTANCE as ViewModelFactoryAuthentication
        }
    }
}