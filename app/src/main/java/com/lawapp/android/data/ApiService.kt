package com.lawapp.android.data

import com.lawapp.android.data.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Tüm backend API çağrılarını merkezi olarak yöneten servis.
 */
object ApiService {

    private val client = NetworkClient.client
    private val baseUrl = NetworkClient.BASE_URL

    private fun HttpRequestBuilder.auth() {
        TokenManager.token?.let {
            header(HttpHeaders.Authorization, "Bearer $it")
        }
    }

    // ==================== AUTH ====================

    suspend fun login(email: String, password: String): AuthResponse {
        val response = client.post("$baseUrl/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }
        return response.body()
    }

    suspend fun register(request: RegisterRequest): AuthResponse {
        val response = client.post("$baseUrl/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }

    // ==================== LEADS ====================

    suspend fun getAllLeads(): List<LeadDto> {
        val response = client.get("$baseUrl/leads/all") { auth() }
        return response.body()
    }

    suspend fun getMyLeads(): List<LeadDto> {
        val response = client.get("$baseUrl/leads/my-leads") { auth() }
        return response.body()
    }

    suspend fun createLead(request: CreateLeadRequest): LeadDto {
        val response = client.post("$baseUrl/leads/create") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.body()
    }

    // ==================== BIDS ====================

    suspend fun placeBid(leadId: Long, message: String): BidDto {
        val response = client.post("$baseUrl/bids/place") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(PlaceBidRequest(leadId, message))
        }
        return response.body()
    }

    suspend fun getBidsForLead(leadId: Long): List<BidDto> {
        val response = client.get("$baseUrl/bids/lead/$leadId") { auth() }
        return response.body()
    }

    suspend fun acceptBid(bidId: Long): BidDto {
        val response = client.post("$baseUrl/bids/$bidId/accept") { auth() }
        return response.body()
    }

    // ==================== TEMPLATES ====================

    suspend fun getTemplates(): List<BidTemplateDto> {
        val response = client.get("$baseUrl/templates") { auth() }
        return response.body()
    }

    suspend fun createTemplate(title: String, content: String): BidTemplateDto {
        val response = client.post("$baseUrl/templates") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(BidTemplateDto(title = title, content = content))
        }
        return response.body()
    }

    suspend fun deleteTemplate(id: Long) {
        client.delete("$baseUrl/templates/$id") { auth() }
    }

    // ==================== PROFILE ====================

    suspend fun getLawyerProfile(): LawyerProfile {
        val response = client.get("$baseUrl/profile") { auth() }
        return response.body()
    }

    suspend fun updateLawyerProfile(dto: ProfileUpdateDto): LawyerProfile {
        val response = client.put("$baseUrl/profile") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(dto)
        }
        return response.body()
    }

    suspend fun getClientProfile(): ClientProfile {
        val response = client.get("$baseUrl/profile/client") { auth() }
        return response.body()
    }

    suspend fun updateClientProfile(bio: String): ClientProfile {
        val response = client.put("$baseUrl/profile/client") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(bio)
        }
        return response.body()
    }

    // ==================== CHAT ====================

    suspend fun getChatSessions(): List<ChatSessionDto> {
        val response = client.get("$baseUrl/chats") { auth() }
        return response.body()
    }

    suspend fun getChatMessages(sessionId: Long): List<ChatMessageDto> {
        val response = client.get("$baseUrl/chats/$sessionId/messages") { auth() }
        return response.body()
    }

    suspend fun markMessagesAsRead(sessionId: Long) {
        client.post("$baseUrl/chats/$sessionId/read") { auth() }
    }
}
