package com.example.weighttracker.data

class StepRepository(
    private val stepDao: StepDao
) {
    val allSteps = stepDao.getAllSteps()

    suspend fun insert(step: StepEntity) {
        stepDao.insertStep(step)
    }

    suspend fun update(step: StepEntity) {
        stepDao.updateStep(step)
    }

    suspend fun delete(step: StepEntity) {
        stepDao.deleteStep(step)
    }
}