package com.lawapp.android.data

import com.lawapp.android.data.model.ChatMessageDto
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor() : ChatRepository {

    private val client = NetworkClient.client
    private var webSocketSession: DefaultClientWebSocketSession? = null
    private val json = Json { ignoreUnknownKeys = true }

    override fun connectToChat(): Flow<ChatMessageDto> = flow {
        val token = TokenManager.token ?: return@flow

        try {
            // JWT token'ı URL'ye eklemek yerine Sec-WebSocket-Protocol header'ı ile gönderiyoruz.
            // Bu sayede token sunucu loglarında, reverse proxy kayıtlarında veya ağ izleyicilerinde görünmez.
            client.webSocket(
                method = HttpMethod.Get,
                host = "10.0.2.2",
                port = 8080,
                path = "/ws/chat",
                request = {
                    header(HttpHeaders.SecWebSocketProtocol, "bearer.$token")
                }
            ) {
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

    @kotlinx.serialization.Serializable
    private data class WebSocketMessagePayload(
        val sessionId: Long,
        val content: String,
        val fileUrl: String?
    )

    override suspend fun sendMessage(sessionId: Long, content: String, fileUrl: String?) {
        val session = webSocketSession ?: return
        if (session.isActive) {
            try {
                val payloadObj = WebSocketMessagePayload(sessionId, content, fileUrl)
                val payloadString = json.encodeToString(payloadObj)
                session.send(Frame.Text(payloadString))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    override suspend fun disconnect() {
        try {
            webSocketSession?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            webSocketSession = null
        }
    }
}
