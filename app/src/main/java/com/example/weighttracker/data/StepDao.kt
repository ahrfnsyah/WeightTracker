package com.example.weighttracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {

    @Insert
    suspend fun insertStep(step: StepEntity)

    @Update
    suspend fun updateStep(step: StepEntity)

    @Delete
    suspend fun deleteStep(step: StepEntity)

    @Query("SELECT * FROM step_table ORDER BY id DESC")
    fun getAllSteps(): Flow<List<StepEntity>>
}