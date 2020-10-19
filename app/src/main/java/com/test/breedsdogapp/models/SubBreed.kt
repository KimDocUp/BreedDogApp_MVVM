package com.test.breedsdogapp.models

data class SubBreed(
    var breedName: String,
    var images: ArrayList<TImage> = ArrayList()
)