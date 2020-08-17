package com.trystan.shmecipe.data

import com.google.firebase.Timestamp

data class RecipePost (
    var id: String = "",
    val userId: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val title: String = "",
    val subheading: String = "",
    val body: String =""
)