package com.example.weighttracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update

import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {

    @Insert
    suspend fun insertWeight(weight: WeightEntity)

    @Query("SELECT * FROM weights ORDER BY id DESC")
    fun getAllWeights(): Flow<List<WeightEntity>>

    @Delete
    suspend fun deleteWeight(weight: WeightEntity)

    @Update
    suspend fun updateWeight(weight: WeightEntity)

}