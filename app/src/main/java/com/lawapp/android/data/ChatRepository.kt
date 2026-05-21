package com.lawapp.android.data

import com.lawapp.android.data.model.ChatMessageDto
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

object ChatRepository {

    private val client = NetworkClient.client
    private var webSocketSession: DefaultClientWebSocketSession? = null
    private val json = Json { ignoreUnknownKeys = true }

    fun connectToChat(): Flow<ChatMessageDto> = flow {
        val token = TokenManager.token ?: return@flow

        try {
            client.webSocket(method = HttpMethod.Get, host = "10.0.2.2", port = 8080, path = "/ws/chat?token=$token") {
                webSocketSession = this
                
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        try {
                            val message = json.decodeFromString<ChatMessageDto>(text)
                            emit(message)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            webSocketSession = null
        }
    }.flowOn(Dispatchers.IO)

    suspend fun sendMessage(sessionId: Long, content: String, fileUrl: String? = null) {
        val session = webSocketSession ?: return
        if (session.isActive) {
            // Basit JSON payload oluşturma (escape karakterleri dahil)
            val escapedContent = content.replace("\"", "\\\"").replace("\n", "\\n")
            val payload = """
                {
                    "sessionId": $sessionId,
                    "content": "$escapedContent",
                    "fileUrl": ${if (fileUrl != null) "\"$fileUrl\"" else "null"}
                }
            """.trimIndent()
            session.send(Frame.Text(payload))
        }
    }
    
    suspend fun disconnect() {
        try {
            webSocketSession?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            webSocketSession = null
        }
    }
}
