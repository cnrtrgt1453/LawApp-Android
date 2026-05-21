package com.lawapp.android.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawapp.android.data.ApiService
import com.lawapp.android.data.model.BidDto
import com.lawapp.android.data.model.CreateLeadRequest
import com.lawapp.android.data.model.LeadDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClientViewModel : ViewModel() {

    // --- Leads ---
    private val _myLeads = MutableStateFlow<List<LeadDto>>(emptyList())
    val myLeads: StateFlow<List<LeadDto>> = _myLeads

    // --- Bids for a specific lead ---
    private val _bids = MutableStateFlow<List<BidDto>>(emptyList())
    val bids: StateFlow<List<BidDto>> = _bids

    // --- State ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    init {
        fetchMyLeads()
    }

    fun fetchMyLeads() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _myLeads.value = ApiService.getMyLeads()
            } catch (e: Exception) {
                _error.value = "İlanlar yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createLead(title: String, description: String, category: String, city: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                ApiService.createLead(CreateLeadRequest(title, description, category, city))
                _successMessage.value = "İlan başarıyla oluşturuldu!"
                fetchMyLeads() // Listeyi yenile
            } catch (e: Exception) {
                _error.value = "İlan oluşturulamadı: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchBidsForLead(leadId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _bids.value = ApiService.getBidsForLead(leadId)
            } catch (e: Exception) {
                _error.value = "Teklifler yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun acceptBid(bidId: Long, leadId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                ApiService.acceptBid(bidId)
                _successMessage.value = "Teklif kabul edildi! İletişim bilgileri açıldı."
                fetchBidsForLead(leadId) // Listeyi yenile
            } catch (e: Exception) {
                _error.value = "Teklif kabul edilemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _error.value = null }
    fun clearSuccess() { _successMessage.value = null }
}
