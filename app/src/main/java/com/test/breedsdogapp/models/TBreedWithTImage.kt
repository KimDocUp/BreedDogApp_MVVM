package com.test.breedsdogapp.models

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

data class TBreedWithTImage(
    @Embedded
    val breed: TBreed = TBreed(),
    @Relation(
        parentColumn = "id",
        entityColumn = "breed_id"
    )
    val images : MutableList<TImage> = mutableListOf()
) : Serializable