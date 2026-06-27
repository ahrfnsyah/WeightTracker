package com.example.weighttracker.data

import kotlinx.coroutines.flow.Flow

class FoodRepository(
    private val foodDao: FoodDao
) {

    val allFoods: Flow<List<FoodEntity>> =
        foodDao.getAllFoods()

    suspend fun insertFood(food: FoodEntity) {
        foodDao.insertFood(food)
    }

    suspend fun updateFood(food: FoodEntity) {
        foodDao.updateFood(food)
    }

    suspend fun deleteFood(food: FoodEntity) {
        foodDao.deleteFood(food)
    }
}