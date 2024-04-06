package com.example.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.repository.StoryRepository
import com.example.storyapp.di.Injection
import com.example.storyapp.view.addstory.AddStoryViewModel
import com.example.storyapp.view.detail.DetailStoryViewModel
import com.example.storyapp.view.main.MainViewModel
import com.example.storyapp.view.storymaps.StoryMapsViewModel

class ViewModelFactoryStory(private val storyRepository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(DetailStoryViewModel::class.java) -> {
                DetailStoryViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(StoryMapsViewModel::class.java) -> {
                StoryMapsViewModel(storyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactoryStory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactoryStory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactoryStory::class.java) {
                    INSTANCE = ViewModelFactoryStory(Injection.provideRepositoryStory(context))
                }
            }
            return INSTANCE as ViewModelFactoryStory
        }
    }
}