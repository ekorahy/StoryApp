package com.example.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.Result
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.remote.response.DetailStoryResponse

class DetailStoryViewModel(private val repository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getDetailStory(token: String, id: String): LiveData<Result<DetailStoryResponse>> {
        return repository.getDetailStory(token, id)
    }
}