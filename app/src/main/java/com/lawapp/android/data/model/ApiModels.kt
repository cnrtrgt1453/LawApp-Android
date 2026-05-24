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
data class AuthResponse(val token: String)

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

// --- Bid (Consultation Request) ---
@Serializable
data class BidDto(
    val id: Long = 0,
    val message: String,
    val status: String? = null,
    val createdAt: String? = null,
    val lawyer: UserSummary? = null
)

typealias ConsultationRequestDto = BidDto

@Serializable
data class PlaceBidRequest(val leadId: Long, val message: String)

typealias PlaceConsultationRequest = PlaceBidRequest

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

// --- Bid Template ---
@Serializable
data class BidTemplateDto(
    val id: Long = 0,
    val title: String,
    val content: String
)
