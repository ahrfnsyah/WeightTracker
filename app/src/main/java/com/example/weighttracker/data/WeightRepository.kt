package com.example.weighttracker.data

class WeightRepository(
    private val weightDao: WeightDao
) {

    val allWeights = weightDao.getAllWeights()

    suspend fun insert(weight: WeightEntity) {
        weightDao.insertWeight(weight)
    }

    suspend fun delete(weight: WeightEntity) {
        weightDao.deleteWeight(weight)
    }

    suspend fun update(weight: WeightEntity) {
        weightDao.updateWeight(weight)
    }
}