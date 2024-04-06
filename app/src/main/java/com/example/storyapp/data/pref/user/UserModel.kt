package com.example.storyapp.data.pref.user

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)