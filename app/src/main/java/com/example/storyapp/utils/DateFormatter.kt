package com.example.storyapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertDate(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    return try {
        val date = inputFormat.parse(inputDate)
        outputFormat.format(date as Date)
    } catch (e: Exception) {
        "Invalid Date Format"
    }
}