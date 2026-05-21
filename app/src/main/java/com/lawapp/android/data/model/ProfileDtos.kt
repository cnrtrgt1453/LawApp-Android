package com.lawapp.android.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LawyerProfile(
    val id: Long? = null,
    val bio: String? = null,
    val profileImageUrl: String? = null,
    val introVideoUrl: String? = null,
    val linkedinUrl: String? = null,
    val instagramUrl: String? = null,
    val websiteUrl: String? = null
)

@Serializable
data class ProfileUpdateDto(
    val bio: String,
    val linkedinUrl: String,
    val instagramUrl: String,
    val websiteUrl: String
)

@Serializable
data class ClientProfile(
    val id: Long? = null,
    val bio: String? = null,
    val profileImageUrl: String? = null
)
