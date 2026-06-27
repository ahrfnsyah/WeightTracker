package com.example.weighttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weights")
data class WeightEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val weight: Float,
    val date: String
)