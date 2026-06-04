package com.lawapp.android.data

import com.lawapp.android.data.model.ChatMessageDto
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun connectToChat(): Flow<ChatMessageDto>
    suspend fun sendMessage(sessionId: Long, content: String, fileUrl: String? = null)
    suspend fun disconnect()
}
