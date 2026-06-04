package com.lawapp.android.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawapp.android.data.ApiService
import com.lawapp.android.data.ChatRepository
import com.lawapp.android.data.model.ChatMessageDto
import com.lawapp.android.data.model.ChatSessionDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val apiService: ApiService,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _chatSessions = MutableStateFlow<List<ChatSessionDto>>(emptyList())
    val chatSessions: StateFlow<List<ChatSessionDto>> = _chatSessions.asStateFlow()

    private val _activeMessages = MutableStateFlow<List<ChatMessageDto>>(emptyList())
    val activeMessages: StateFlow<List<ChatMessageDto>> = _activeMessages.asStateFlow()

    private val _activeSessionId = MutableStateFlow<Long?>(null)
    val activeSessionId: StateFlow<Long?> = _activeSessionId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchChatSessions()
        startWebSocketListener()
    }

    fun fetchChatSessions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _chatSessions.value = apiService.getChatSessions()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun startWebSocketListener() {
        viewModelScope.launch {
            try {
                chatRepository.connectToChat().collect { incoming ->
                    // Eğer gelen mesaj şu an açık olan odanın mesajıysa, listeye ekle
                    if (incoming.sessionId == _activeSessionId.value) {
                        // Aynı mesajın iki kere eklenmesini önlemek için ID kontrolü yapalım (eko)
                        val exists = _activeMessages.value.any { it.id == incoming.id && incoming.id != 0L }
                        if (!exists) {
                            _activeMessages.value = _activeMessages.value + incoming
                            markAsRead(incoming.sessionId)
                        }
                    }
                    // Mesaj listesi değiştiği için odaların son mesaj/okunmamış sayılarını güncelle
                    _chatSessions.value = apiService.getChatSessions()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadMessages(sessionId: Long) {
        viewModelScope.launch {
            _activeSessionId.value = sessionId
            _isLoading.value = true
            try {
                _activeMessages.value = apiService.getChatMessages(sessionId)
                markAsRead(sessionId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendMessage(content: String) {
        val sessionId = _activeSessionId.value ?: return
        if (content.isBlank()) return
        
        viewModelScope.launch {
            try {
                chatRepository.sendMessage(sessionId, content)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun markAsRead(sessionId: Long) {
        viewModelScope.launch {
            try {
                apiService.markMessagesAsRead(sessionId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun closeChat() {
        _activeSessionId.value = null
        _activeMessages.value = emptyList()
        fetchChatSessions() // Okunmamışları güncellemek için yenile
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            chatRepository.disconnect()
        }
    }
}
