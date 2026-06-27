package com.example.weighttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_table")
data class StepEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val steps: Int,
    val date: String
)