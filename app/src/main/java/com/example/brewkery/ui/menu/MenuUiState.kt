package com.example.brewkery.ui.menu

import com.example.brewkery.data.model.MenuResponse

sealed class MenuUiState {
    object Loading : MenuUiState()
    data class Success(val menu: MenuResponse) : MenuUiState()
    data class Error(val message: String) : MenuUiState()
}
