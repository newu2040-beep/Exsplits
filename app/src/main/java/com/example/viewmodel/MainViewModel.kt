package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BillRepository
import com.example.data.DomainBill
import com.example.data.Participant
import com.example.data.SplitType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(private val repository: BillRepository) : ViewModel() {

    val allBills = repository.allBills.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteBills = repository.favoriteBills.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    private val _currentAppTheme = MutableStateFlow(0) // 0-4 for 5 themes
    val currentAppTheme: StateFlow<Int> = _currentAppTheme
    
    fun setTheme(themeIndex: Int) {
        _currentAppTheme.value = themeIndex
    }

    fun addBill(bill: DomainBill) {
        viewModelScope.launch {
            repository.insertBill(bill)
        }
    }

    fun toggleFavorite(id: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, isFavorite)
        }
    }

    fun deleteBill(id: String) {
        viewModelScope.launch {
            repository.deleteBill(id)
        }
    }
}

class MainViewModelFactory(private val repository: BillRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
