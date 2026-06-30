package com.lawapp.android.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LawyerProfile(
    val id: Long? = null,
    val bio: String? = null,
    val profileImageUrl: String? = null,
    val youtubeUrl: String? = null,
    val linkedinUrl: String? = null,
    val instagramUrl: String? = null,
    val websiteUrl: String? = null,
    val fullName: String? = null,
    val specialties: List<String> = emptyList(),
    val city: String? = null
)

@Serializable
data class ProfileUpdateDto(
    val bio: String,
    val linkedinUrl: String,
    val instagramUrl: String,
    val websiteUrl: String,
    val youtubeUrl: String,
    val city: String,
    val specialties: List<String>
)

@Serializable
data class ClientProfile(
    val id: Long? = null,
    val bio: String? = null,
    val profileImageUrl: String? = null,
    val fullName: String? = null
)
