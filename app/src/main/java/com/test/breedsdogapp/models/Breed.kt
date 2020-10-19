package com.test.breedsdogapp.models

import java.io.Serializable

data class Breed(
    var breedName: String,
    var subBreed : ArrayList<SubBreed> = ArrayList(),
    var images : ArrayList<TImage> = ArrayList()
): Serializable