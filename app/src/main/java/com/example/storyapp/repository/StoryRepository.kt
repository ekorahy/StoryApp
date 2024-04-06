package com.example.storyapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.Result
import com.example.storyapp.data.local.StoryDatabase
import com.example.storyapp.data.paging.StoryRemoteMediator
import com.example.storyapp.data.pref.user.UserModel
import com.example.storyapp.data.pref.user.UserPreference
import com.example.storyapp.data.remote.response.AddStoryResponse
import com.example.storyapp.data.remote.response.DetailStoryResponse
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.StoryResponse
import com.example.storyapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getDetailStory(id: String): LiveData<Result<DetailStoryResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getDetailStory(id)
                val story = response.story
                val error = response.error
                val message = response.message
                emit(Result.Success(DetailStoryResponse(error, message, story)))
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, DetailStoryResponse::class.java)
                val errorMessage = errorBody.message
                emit(Result.Error(errorMessage.toString()))
            } catch (e: Exception) {
                val message = e.message
                emit(Result.Error(message.toString()))
            }
        }

    fun addNewStory(
        photo: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.addNewStory(photo, description)
            val error = response.error
            val message = response.message
            emit(Result.Success(AddStoryResponse(error, message)))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, AddStoryResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
        } catch (e: Exception) {
            val message = e.message
            emit(Result.Error(message.toString()))
        }
    }

    fun addNewStoryWithLocation(
        photo: MultipartBody.Part,
        description: RequestBody,
        lat: Double,
        lon: Double
    ): LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.addNewStoryWithLocation(photo, description, lat, lon)
            val error = response.error
            val message = response.message
            emit(Result.Success(AddStoryResponse(error, message)))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, AddStoryResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
        } catch (e: Exception) {
            val message = e.message
            emit(Result.Error(message.toString()))
        }
    }

    fun getStoriesWithLocation(): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesWithLocation()
            val listStory = response.listStory
            val error = response.error
            val message = response.message
            emit(Result.Success(StoryResponse(listStory, error, message)))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, StoryResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage.toString()))
        } catch (e: Exception) {
            val message = e.message
            emit(Result.Error(message.toString()))
        }
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            storyDatabase: StoryDatabase
        ): StoryRepository = instance ?: synchronized(this) {
            instance ?: StoryRepository(userPreference, apiService, storyDatabase)
        }.also { instance = it }
    }
}