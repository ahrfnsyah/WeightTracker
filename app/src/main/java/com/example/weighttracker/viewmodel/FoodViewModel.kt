package com.example.weighttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weighttracker.data.FoodEntity
import com.example.weighttracker.data.FoodRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FoodViewModel(
    private val repository: FoodRepository
) : ViewModel() {

    val allFoods =
        repository.allFoods.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertFood(
        name: String,
        calories: Int,
        date: String
    ) {
        viewModelScope.launch {
            repository.insertFood(
                FoodEntity(
                    name = name,
                    calories = calories,
                    date = date
                )
            )
        }
    }

    fun updateFood(food: FoodEntity) {
        viewModelScope.launch {
            repository.updateFood(food)
        }
    }

    fun deleteFood(food: FoodEntity) {
        viewModelScope.launch {
            repository.deleteFood(food)
        }
    }
}