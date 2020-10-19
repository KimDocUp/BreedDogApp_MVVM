package com.test.breedsdogapp.models

import java.io.Serializable

data class Images (val name:String, val images:MutableList<TImage> = mutableListOf()) : Serializable