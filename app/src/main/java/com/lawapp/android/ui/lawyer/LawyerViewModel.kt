package com.lawapp.android.ui.lawyer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawapp.android.data.ApiService
import com.lawapp.android.data.model.AppointmentDto
import com.lawapp.android.data.model.CalendarSlotDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LawyerViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    // --- Calendar Slots ---
    private val _slots = MutableStateFlow<List<CalendarSlotDto>>(emptyList())
    val slots: StateFlow<List<CalendarSlotDto>> = _slots

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
        // Avukat detaylarını yükle
        fetchCalendarSlots()
        fetchAppointments()
    }

    fun fetchCalendarSlots() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Avukatın kendi ID'sine ihtiyaç duymadan token'dan alan yapısı veya
                // profile endpoint'i üzerinden id alabiliriz. Ancak backend'de
                // calendar/add ve delete token üzerinden çalışmaktadır.
                // calendar/lawyer/{id} için kendi profil ID'mizi almalıyız.
                val profile = apiService.getLawyerProfile()
                _slots.value = apiService.getCalendarSlots(profile.id ?: 0L)
            } catch (e: Exception) {
                _error.value = "Takvim slotları yüklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addCalendarSlot(slotTime: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                apiService.addCalendarSlot(slotTime)
                _successMessage.value = "Yeni saat dilimi takviminize eklendi."
                fetchCalendarSlots()
            } catch (e: Exception) {
                _error.value = "Slot eklenemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCalendarSlot(slotId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                apiService.deleteCalendarSlot(slotId)
                _successMessage.value = "Saat dilimi silindi."
                fetchCalendarSlots()
            } catch (e: Exception) {
                _error.value = "Slot silinemedi: ${e.localizedMessage}"
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

    fun acceptAppointment(appointmentId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                apiService.acceptAppointment(appointmentId)
                _successMessage.value = "Randevu onaylandı!"
                fetchAppointments()
                fetchCalendarSlots() // Slot müsaitlik durumunu güncelle
            } catch (e: Exception) {
                _error.value = "Randevu onaylanamadı: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun rejectAppointment(appointmentId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                apiService.rejectAppointment(appointmentId)
                _successMessage.value = "Randevu reddedildi ve ücret müvekkile iade edildi."
                fetchAppointments()
                fetchCalendarSlots() // Slotu tekrar boşa çıkar
            } catch (e: Exception) {
                _error.value = "Randevu reddedilemedi: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _error.value = null }
    fun clearSuccess() { _successMessage.value = null }
}
