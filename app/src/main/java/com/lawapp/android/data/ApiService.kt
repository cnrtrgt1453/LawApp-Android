package com.lawapp.android.data

import com.lawapp.android.data.model.*

interface ApiService {
    suspend fun login(email: String, password: String): AuthResponse
    suspend fun register(request: RegisterRequest): AuthResponse
    suspend fun getAllLeads(): List<LeadDto>
    suspend fun getMyLeads(): List<LeadDto>
    suspend fun createLead(request: CreateLeadRequest): LeadDto
    suspend fun getCalendarSlots(lawyerId: Long): List<CalendarSlotDto>
    suspend fun getAvailableCalendarSlots(lawyerId: Long): List<CalendarSlotDto>
    suspend fun addCalendarSlot(slotTime: String): CalendarSlotDto
    suspend fun deleteCalendarSlot(slotId: Long)
    suspend fun bookAppointment(lawyerId: Long, leadId: Long?, appointmentTime: String): AppointmentDto
    suspend fun acceptAppointment(id: Long): AppointmentDto
    suspend fun rejectAppointment(id: Long): AppointmentDto
    suspend fun getMyAppointments(): List<AppointmentDto>
    suspend fun getMatchingLawyers(leadId: Long): List<LawyerDto>
    suspend fun getLawyerProfile(): LawyerProfile
    suspend fun updateLawyerProfile(dto: ProfileUpdateDto): LawyerProfile
    suspend fun getClientProfile(): ClientProfile
    suspend fun updateClientProfile(dto: ClientProfileUpdateDto): ClientProfile
    suspend fun uploadProfileImage(bytes: ByteArray, fileName: String): String
    suspend fun getChatSessions(): List<ChatSessionDto>
    suspend fun getChatMessages(sessionId: Long): List<ChatMessageDto>
    suspend fun markMessagesAsRead(sessionId: Long)
    suspend fun googleLogin(token: String, role: String): AuthResponse
    suspend fun facebookLogin(token: String, role: String): AuthResponse
}
