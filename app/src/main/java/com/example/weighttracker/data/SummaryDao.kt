package com.example.weighttracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface SummaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(summary: SummaryEntity)

    @Update
    suspend fun update(summary: SummaryEntity)

    @Delete
    suspend fun delete(summary: SummaryEntity)

    @Query(
        """
        SELECT * FROM summary_table
        ORDER BY id DESC
        """
    )
    fun getAllSummary(): Flow<List<SummaryEntity>>

    @Query(
        "SELECT * FROM summary_table WHERE date = :date LIMIT 1"
    )
    suspend fun getSummaryByDate(
        date: String
    ): SummaryEntity?
}