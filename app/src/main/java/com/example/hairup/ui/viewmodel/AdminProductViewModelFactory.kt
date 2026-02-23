package com.example.hairup.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hairup.data.SessionManager

class AdminProductViewModelFactory(private val sessionManager: SessionManager) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return AdminProductViewModel(sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}