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

    // ==================== CALENDAR ====================

    suspend fun getCalendarSlots(lawyerId: Long): List<CalendarSlotDto> {
        val response = client.get("$baseUrl/calendar/lawyer/$lawyerId") { auth() }
        return response.body()
    }

    suspend fun getAvailableCalendarSlots(lawyerId: Long): List<CalendarSlotDto> {
        val response = client.get("$baseUrl/calendar/lawyer/$lawyerId/available") { auth() }
        return response.body()
    }

    suspend fun addCalendarSlot(slotTime: String): CalendarSlotDto {
        val response = client.post("$baseUrl/calendar/add") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(CreateCalendarSlotRequest(slotTime))
        }
        return response.body()
    }

    suspend fun deleteCalendarSlot(slotId: Long) {
        client.delete("$baseUrl/calendar/delete/$slotId") { auth() }
    }

    // ==================== APPOINTMENTS ====================

    suspend fun bookAppointment(lawyerId: Long, leadId: Long?, appointmentTime: String): AppointmentDto {
        val response = client.post("$baseUrl/appointments/book") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(BookAppointmentRequest(lawyerId, leadId, appointmentTime))
        }
        return response.body()
    }

    suspend fun acceptAppointment(id: Long): AppointmentDto {
        val response = client.post("$baseUrl/appointments/$id/accept") { auth() }
        return response.body()
    }

    suspend fun rejectAppointment(id: Long): AppointmentDto {
        val response = client.post("$baseUrl/appointments/$id/reject") { auth() }
        return response.body()
    }

    suspend fun getMyAppointments(): List<AppointmentDto> {
        val response = client.get("$baseUrl/appointments/my") { auth() }
        return response.body()
    }

    // ==================== MATCHING LAWYERS ====================

    suspend fun getMatchingLawyers(leadId: Long): List<LawyerDto> {
        val response = client.get("$baseUrl/leads/$leadId/matching-lawyers") { auth() }
        return response.body()
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
