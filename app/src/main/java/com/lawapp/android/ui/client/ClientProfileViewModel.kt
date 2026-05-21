package com.lawapp.android.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawapp.android.data.ApiService
import com.lawapp.android.data.model.ClientProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientProfileViewModel : ViewModel() {

    private val _profile = MutableStateFlow<ClientProfile?>(null)
    val profile: StateFlow<ClientProfile?> = _profile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _profile.value = ApiService.getClientProfile()
            } catch (e: Exception) {
                _error.value = "Profil yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(bio: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _profile.value = ApiService.updateClientProfile(bio)
            } catch (e: Exception) {
                _error.value = "Güncelleme başarısız: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
