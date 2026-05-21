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
    val city: String
)

// --- Bid ---
@Serializable
data class BidDto(
    val id: Long = 0,
    val message: String,
    val status: String? = null,
    val createdAt: String? = null,
    val lawyer: UserSummary? = null
)

@Serializable
data class PlaceBidRequest(val leadId: Long, val message: String)

// --- User ---
@Serializable
data class UserSummary(
    val id: Long = 0,
    val fullName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val role: String? = null,
    val verified: Boolean = false
)

// --- Bid Template ---
@Serializable
data class BidTemplateDto(
    val id: Long = 0,
    val title: String,
    val content: String
)
