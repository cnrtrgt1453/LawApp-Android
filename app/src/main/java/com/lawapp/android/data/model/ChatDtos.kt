package com.lawapp.android.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatSessionDto(
    val id: Long,
    val leadId: Long,
    val leadTitle: String,
    val otherParticipantName: String,
    val otherParticipantRole: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Long
)

@Serializable
data class ChatMessageDto(
    val id: Long = 0,
    val sessionId: Long,
    val senderEmail: String,
    val senderName: String,
    val content: String,
    val createdAt: String? = null,
    val read: Boolean = false,
    val fileUrl: String? = null
)
