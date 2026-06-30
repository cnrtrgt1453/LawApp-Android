package com.lawapp.android.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawapp.android.data.ApiService
import com.lawapp.android.data.TokenManager
import com.lawapp.android.data.model.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    fun login(email: String, password: String, role: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.login(email, password)
                TokenManager.saveToken(response.token, response.role ?: role)
                _loginSuccess.value = true
            } catch (e: Exception) {
                _error.value = "Giriş başarısız: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginWithGoogle(token: String, role: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.googleLogin(token, role)
                TokenManager.saveToken(response.token, response.role ?: role)
                _loginSuccess.value = true
            } catch (e: Exception) {
                _error.value = "Google Girişi başarısız: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginWithFacebook(token: String, role: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.facebookLogin(token, role)
                TokenManager.saveToken(response.token, response.role ?: role)
                _loginSuccess.value = true
            } catch (e: Exception) {
                _error.value = "Facebook Girişi başarısız: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(fullName: String, email: String, password: String, phone: String, role: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val request = RegisterRequest(fullName, email, password, phone, role)
                val response = apiService.register(request)
                TokenManager.saveToken(response.token, response.role ?: role)
                _loginSuccess.value = true
            } catch (e: Exception) {
                _error.value = "Kayıt başarısız: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _error.value = null }
}
