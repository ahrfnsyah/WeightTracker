package com.example.weighttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weighttracker.data.SummaryEntity
import com.example.weighttracker.data.SummaryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SummaryViewModel(
    private val repository: SummaryRepository
) : ViewModel() {

    val allSummary =
        repository.allSummary
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun insertSummary(
        summary: SummaryEntity
    ) {
        viewModelScope.launch {
            repository.insert(summary)
        }
    }

    fun updateSummary(
        summary: SummaryEntity
    ) {
        viewModelScope.launch {
            repository.update(summary)
        }
    }

    fun deleteSummary(
        summary: SummaryEntity
    ) {
        viewModelScope.launch {
            repository.delete(summary)
        }
    }

    fun saveSummary(
        summary: SummaryEntity
    ) {
        viewModelScope.launch {
            repository.insert(summary)
        }
    }

    suspend fun getSummaryByDate(
        date: String
    ): SummaryEntity? {
        return repository.getSummaryByDate(date)
    }
}

class SummaryViewModelFactory(
    private val repository: SummaryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                SummaryViewModel::class.java
            )
        ) {
            return SummaryViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}