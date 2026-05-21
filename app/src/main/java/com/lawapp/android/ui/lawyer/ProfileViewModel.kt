package com.lawapp.android.ui.lawyer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawapp.android.data.ApiService
import com.lawapp.android.data.model.LawyerProfile
import com.lawapp.android.data.model.ProfileUpdateDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _profile = MutableStateFlow<LawyerProfile?>(null)
    val profile: StateFlow<LawyerProfile?> = _profile

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
                _profile.value = ApiService.getLawyerProfile()
            } catch (e: Exception) {
                _error.value = "Profil yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(bio: String, linkedin: String, instagram: String, website: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val dto = ProfileUpdateDto(bio, linkedin, instagram, website)
                _profile.value = ApiService.updateLawyerProfile(dto)
            } catch (e: Exception) {
                _error.value = "Güncelleme başarısız: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

