package com.lawapp.android.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawapp.android.data.ApiService
import com.lawapp.android.data.model.AppointmentDto
import com.lawapp.android.data.model.CalendarSlotDto
import com.lawapp.android.data.model.CreateLeadRequest
import com.lawapp.android.data.model.LeadDto
import com.lawapp.android.data.model.LawyerDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    // --- Leads ---
    private val _myLeads = MutableStateFlow<List<LeadDto>>(emptyList())
    val myLeads: StateFlow<List<LeadDto>> = _myLeads

    // --- Matching Lawyers ---
    private val _matchingLawyers = MutableStateFlow<List<LawyerDto>>(emptyList())
    val matchingLawyers: StateFlow<List<LawyerDto>> = _matchingLawyers

    // --- Available Slots ---
    private val _availableSlots = MutableStateFlow<List<CalendarSlotDto>>(emptyList())
    val availableSlots: StateFlow<List<CalendarSlotDto>> = _availableSlots

    // --- Appointments ---
    private val _appointments = MutableStateFlow<List<AppointmentDto>>(emptyList())
    val appointments: StateFlow<List<AppointmentDto>> = _appointments

    // --- State ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    init {
        fetchMyLeads()
        fetchAppointments()
    }

    fun fetchMyLeads() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _myLeads.value = apiService.getMyLeads()
            } catch (e: Exception) {
                _error.value = "İlanlar yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createLead(title: String, description: String, category: String, city: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val lead = apiService.createLead(CreateLeadRequest(title, description, category, city))
                _successMessage.value = "İlan başarıyla oluşturuldu!"
                fetchMyLeads() // Listeyi yenile
                onCreated(lead.id)
            } catch (e: Exception) {
                _error.value = "İlan oluşturulamadı: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchMatchingLawyers(leadId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _matchingLawyers.value = apiService.getMatchingLawyers(leadId)
            } catch (e: Exception) {
                _error.value = "Eşleşen avukatlar yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchAvailableSlots(lawyerId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _availableSlots.value = apiService.getAvailableCalendarSlots(lawyerId)
            } catch (e: Exception) {
                _error.value = "Müsait zamanlar yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun bookAppointment(lawyerId: Long, leadId: Long?, appointmentTime: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                apiService.bookAppointment(lawyerId, leadId, appointmentTime)
                _successMessage.value = "Randevu talebi oluşturuldu ve platform ücreti ödemesi yapıldı!"
                fetchAppointments()
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Randevu alınamadı: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchAppointments() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _appointments.value = apiService.getMyAppointments()
            } catch (e: Exception) {
                _error.value = "Randevular yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _error.value = null }
    fun clearSuccess() { _successMessage.value = null }
}
