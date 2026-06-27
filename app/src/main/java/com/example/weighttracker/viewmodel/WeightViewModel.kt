package com.example.weighttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weighttracker.data.WeightEntity
import com.example.weighttracker.data.WeightRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeightViewModel(
    private val repository: WeightRepository
) : ViewModel() {

    val allWeights = repository.allWeights
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertWeight(
        weight: Float,
        date: String
    ) {
        viewModelScope.launch {
            repository.insert(
                WeightEntity(
                    weight = weight,
                    date = date
                )
            )
        }
    }

    fun updateWeight(weight: WeightEntity) {
        viewModelScope.launch {
            repository.update(weight)
        }
    }

    fun deleteWeight(weight: WeightEntity) {
        viewModelScope.launch {
            repository.delete(weight)
        }
    }
}

class WeightViewModelFactory(
    private val repository: WeightRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(WeightViewModel::class.java)) {
            return WeightViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}