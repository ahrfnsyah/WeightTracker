package com.example.weighttracker.data

class SummaryRepository(
    private val dao: SummaryDao
) {

    val allSummary =
        dao.getAllSummary()

    suspend fun insert(
        summary: SummaryEntity
    ) {
        dao.insert(summary)
    }

    suspend fun update(
        summary: SummaryEntity
    ) {
        dao.update(summary)
    }

    suspend fun delete(
        summary: SummaryEntity
    ) {
        dao.delete(summary)
    }

    suspend fun getSummaryByDate(
        date: String
    ): SummaryEntity? {
        return dao.getSummaryByDate(date)
    }
}