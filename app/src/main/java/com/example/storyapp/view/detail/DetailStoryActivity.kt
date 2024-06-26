package com.example.storyapp.view.detail

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.storyapp.R
import com.example.storyapp.data.Result
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.utils.convertDate
import com.example.storyapp.view.ViewModelFactoryStory
import com.google.android.material.snackbar.Snackbar

class DetailStoryActivity : AppCompatActivity() {

    private val viewModel by viewModels<DetailStoryViewModel> {
        ViewModelFactoryStory.getInstance(this)
    }

    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val id = intent.getStringExtra(ID)

        viewModel.getDetailStory(id.toString()).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.pbDetail.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.pbDetail.visibility = View.GONE
                        val story = result.data.story
                        setDetailStory(story as Story)
                    }

                    is Result.Error -> {
                        binding.pbDetail.visibility = View.GONE
                        val errorMessage = result.error
                        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setDetailStory(story: Story) {
        with(binding) {
            Glide.with(root.context)
                .load(story.photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivStory)
            tvName.text = story.name
            tvDate.text = convertDate(story.createdAt.toString())
            tvDesc.text = story.description
        }
    }

    companion object {
        const val ID = "id"
    }
}