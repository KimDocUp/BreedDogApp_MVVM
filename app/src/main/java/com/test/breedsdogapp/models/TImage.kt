package com.test.breedsdogapp.models

import androidx.room.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = TBreed::class,
        parentColumns = ["id"],
        childColumns = ["breed_id"]
    )], indices = [Index("breed_id"), Index(value = ["url"], unique = true)]
)
data class TImage (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var url: String="",
    var isLike: Boolean=false,
    @ColumnInfo(name = "breed_id")
    var breed_id: Long = 0
)