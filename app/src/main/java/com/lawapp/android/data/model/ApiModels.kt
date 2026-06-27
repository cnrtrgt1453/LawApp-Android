package com.lawapp.android.data.model

import kotlinx.serialization.Serializable

// --- Auth ---
@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val role: String
)

@Serializable
data class AuthResponse(val token: String, val role: String? = null)

// --- Lead ---
@Serializable
data class LeadDto(
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val city: String,
    val status: String? = null,
    val createdAt: String? = null
)

@Serializable
data class CreateLeadRequest(
    val title: String,
    val description: String,
    val category: String,
    val city: String,
    val wizardAnswersJson: String? = null // Akıllı sihirbaz sorularının JSON verileri
)

// --- Calendar Slot ---
@Serializable
data class CalendarSlotDto(
    val id: Long = 0,
    val lawyerId: Long,
    val slotTime: String,
    val available: Boolean
)

@Serializable
data class CreateCalendarSlotRequest(
    val slotTime: String
)

// --- Appointment ---
@Serializable
data class AppointmentDto(
    val id: Long = 0,
    val clientId: Long,
    val clientName: String,
    val lawyerId: Long,
    val lawyerName: String,
    val appointmentTime: String,
    val status: String,
    val platformFee: Double,
    val paymentStatus: String,
    val roomId: String? = null,
    val leadId: Long? = null,
    val leadTitle: String? = null,
    val leadCategory: String? = null
)

@Serializable
data class BookAppointmentRequest(
    val lawyerId: Long,
    val leadId: Long? = null,
    val appointmentTime: String
)

// --- User ---
@Serializable
data class UserSummary(
    val id: Long = 0,
    val fullName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val role: String? = null,
    val verified: Boolean = false,
    val barLicenseImageUrl: String? = null,
    val averageRating: Double? = 0.0,
    val specialties: List<String>? = emptyList()
)

// --- Lawyer Details ---
@Serializable
data class LawyerDto(
    val id: Long,
    val fullName: String,
    val averageRating: Double = 5.0,
    val specialties: List<String> = emptyList(),
    val phoneNumber: String? = null,
    val barNumber: String? = null,
    val verified: Boolean = false,
    val bio: String? = null,
    val profileImageUrl: String? = null,
    val introVideoUrl: String? = null
)
