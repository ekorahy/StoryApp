package com.example.storyapp.view.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.Result
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.remote.response.AddStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {

    fun addNewStory(
        photo: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<AddStoryResponse>> {
        return repository.addNewStory(photo, description)
    }
}