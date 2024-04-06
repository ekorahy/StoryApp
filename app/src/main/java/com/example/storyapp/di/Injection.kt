package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.local.StoryDatabase
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.repository.UserRepository
import com.example.storyapp.data.pref.user.UserPreference
import com.example.storyapp.data.pref.user.dataStore
import com.example.storyapp.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepositoryAuthentication(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiServiceAuthentication()
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideRepositoryStory(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiServiceStory(user.token)
        val storyDatabase = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(pref, apiService, storyDatabase)
    }
}