package com.example.storyapp

import com.example.storyapp.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "story-fILx-baIkPM9kbA9 $i",
                "https://story-api.dicoding.dev/images/stories/photos-1712297717204_9eb89731e0525521ec30.jpg",
                "2024-04-05T06:15:17.209Z",
                "ekorahy",
                "test $i",
                -6.897427877949661,
                107.63428341597319
            )
            items.add(story)
        }
        return items
    }
}