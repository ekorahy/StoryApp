package com.example.storyapp.view.addstory

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.Result
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.remote.response.AddStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {

    private val _imageUriCamera = MutableLiveData<Uri?>(null)
    val imageUriCamera: LiveData<Uri?> = _imageUriCamera

    private val _currentImageUri = MutableLiveData<Uri?>(null)
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun addNewStory(
        token: String,
        photo: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<AddStoryResponse>> {
        return repository.addNewStory(token, photo, description)
    }

    fun setImageUriCamera(uri: Uri) {
        _imageUriCamera.postValue(uri)
    }

    fun setCurrentImageUri(uri: Uri) {
        _currentImageUri.postValue(uri)
    }
}