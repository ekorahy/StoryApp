package com.example.storyapp.view.storymaps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.Result
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.data.pref.user.UserModel
import com.example.storyapp.data.remote.response.StoryResponse

class StoryMapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return storyRepository.getSession().asLiveData()
    }

    fun getStoriesWithLocation(): LiveData<Result<StoryResponse>> {
        return storyRepository.getStoriesWithLocation()
    }
}