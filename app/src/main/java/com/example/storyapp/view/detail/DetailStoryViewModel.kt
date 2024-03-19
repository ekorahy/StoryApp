package com.example.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.Result
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.remote.response.DetailStoryResponse

class DetailStoryViewModel(private val repository: UserRepository) : ViewModel() {
    fun getDetailStory(id: String): LiveData<Result<DetailStoryResponse>> {
        return repository.getDetailStory(id)
    }
}