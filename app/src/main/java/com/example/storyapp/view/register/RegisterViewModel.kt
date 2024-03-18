package com.example.storyapp.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.Result
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.remote.response.RegisterResponse

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> {
        return userRepository.register(name, email, password)
    }
}