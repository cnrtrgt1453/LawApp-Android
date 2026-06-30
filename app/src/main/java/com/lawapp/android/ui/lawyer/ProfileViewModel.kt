package com.lawapp.android.ui.lawyer

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawapp.android.data.ApiService
import com.lawapp.android.data.model.LawyerProfile
import com.lawapp.android.data.model.ProfileUpdateDto
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

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
                _profile.value = apiService.getLawyerProfile()
            } catch (e: Exception) {
                _error.value = "Profil yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(bio: String, linkedin: String, instagram: String, website: String, youtube: String, city: String, specialties: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val dto = ProfileUpdateDto(bio, linkedin, instagram, website, youtube, city, specialties)
                _profile.value = apiService.updateLawyerProfile(dto)
            } catch (e: Exception) {
                _error.value = "Güncelleme başarısız: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                if (bytes != null) {
                    val fileName = "profile_${System.currentTimeMillis()}.jpg"
                    val imageUrl = apiService.uploadProfileImage(bytes, fileName)
                    _profile.value = _profile.value?.copy(profileImageUrl = imageUrl)
                } else {
                    _error.value = "Fotoğraf okunamadı."
                }
            } catch (e: Exception) {
                _error.value = "Fotoğraf yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

