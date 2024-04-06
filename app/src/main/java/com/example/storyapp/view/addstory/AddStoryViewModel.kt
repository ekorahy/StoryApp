package com.example.storyapp.view.addstory

import android.location.Location
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.Result
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.data.pref.user.UserModel
import com.example.storyapp.data.remote.response.AddStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _imageUriCamera = MutableLiveData<Uri?>(null)
    val imageUriCamera: LiveData<Uri?> = _imageUriCamera

    private val _currentImageUri = MutableLiveData<Uri?>(null)
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    private val _isUseLocation = MutableLiveData(false)
    val isUseLocation: LiveData<Boolean> = _isUseLocation

    private val _location = MutableLiveData<Location?>(null)
    val location: LiveData<Location?> = _location

    fun getSession(): LiveData<UserModel> {
        return storyRepository.getSession().asLiveData()
    }

    fun addNewStory(
        photo: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<AddStoryResponse>> {
        return storyRepository.addNewStory(photo, description)
    }

    fun addNewStoryWithLocation(
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: Double,
        lon: Double
    ): LiveData<Result<AddStoryResponse>> {
        return storyRepository.addNewStoryWithLocation(photo, description, lat, lon)
    }

    fun setImageUriCamera(uri: Uri) {
        _imageUriCamera.postValue(uri)
    }

    fun setCurrentImageUri(uri: Uri) {
        _currentImageUri.postValue(uri)
    }

    fun setIsUseLocation(location: Boolean) {
        _isUseLocation.postValue(location)
    }

    fun setLocation(location: Location) {
        _location.postValue(location)
    }
}