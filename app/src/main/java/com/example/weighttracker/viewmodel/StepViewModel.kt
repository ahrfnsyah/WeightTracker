package com.example.weighttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weighttracker.data.StepEntity
import com.example.weighttracker.data.StepRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StepViewModel(
    private val repository: StepRepository
) : ViewModel() {

    val allSteps = repository.allSteps
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertStep(steps: Int, date: String) {
        viewModelScope.launch {
            repository.insert(
                StepEntity(
                    steps = steps,
                    date = date
                )
            )
        }
    }

    fun updateStep(step: StepEntity) {
        viewModelScope.launch {
            repository.update(step)
        }
    }

    fun deleteStep(step: StepEntity) {
        viewModelScope.launch {
            repository.delete(step)
        }
    }
}