package com.test.breedsdogapp.models

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("message")
    var messageData: List<String>
)