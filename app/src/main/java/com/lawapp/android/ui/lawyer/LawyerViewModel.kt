package com.lawapp.android.ui.lawyer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawapp.android.data.ApiService
import com.lawapp.android.data.model.BidTemplateDto
import com.lawapp.android.data.model.LeadDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LawyerViewModel : ViewModel() {

    // --- Leads ---
    private val _leads = MutableStateFlow<List<LeadDto>>(emptyList())
    val leads: StateFlow<List<LeadDto>> = _leads

    // --- Templates ---
    private val _templates = MutableStateFlow<List<BidTemplateDto>>(emptyList())
    val templates: StateFlow<List<BidTemplateDto>> = _templates

    // --- State ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    init {
        fetchLeads()
        fetchTemplates()
    }

    fun fetchLeads() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _leads.value = ApiService.getAllLeads()
            } catch (e: Exception) {
                _error.value = "İlanlar yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchTemplates() {
        viewModelScope.launch {
            try {
                _templates.value = ApiService.getTemplates()
            } catch (e: Exception) {
                _error.value = "Şablonlar yüklenemedi: ${e.localizedMessage}"
            }
        }
    }

    fun placeBid(leadId: Long, message: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                ApiService.placeBid(leadId, message)
                _successMessage.value = "Teklif başarıyla gönderildi!"
            } catch (e: Exception) {
                _error.value = "Teklif gönderilemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createTemplate(title: String, content: String) {
        viewModelScope.launch {
            try {
                ApiService.createTemplate(title, content)
                fetchTemplates() // Listeyi yenile
            } catch (e: Exception) {
                _error.value = "Şablon oluşturulamadı: ${e.localizedMessage}"
            }
        }
    }

    fun deleteTemplate(id: Long) {
        viewModelScope.launch {
            try {
                ApiService.deleteTemplate(id)
                fetchTemplates()
            } catch (e: Exception) {
                _error.value = "Şablon silinemedi: ${e.localizedMessage}"
            }
        }
    }

    fun clearError() { _error.value = null }
    fun clearSuccess() { _successMessage.value = null }
}
