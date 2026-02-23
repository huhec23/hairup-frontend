package com.example.hairup.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hairup.data.SessionManager

class AdminServiceViewModelFactory(private val sessionManager: SessionManager) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminServiceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return AdminServiceViewModel(sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}