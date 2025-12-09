package com.example.smart.sportlive.data.model

import com.google.gson.annotations.SerializedName

data class SportDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("sportIconUrl")
    val sportIconUrl: String?
)
