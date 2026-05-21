package com.lawapp.android.data

/**
 * Basit bir in-memory token yöneticisi.
 * Gerçek uygulamada DataStore veya EncryptedSharedPreferences kullanılmalıdır.
 */
object TokenManager {
    private var _token: String? = null
    private var _role: String? = null

    val token: String? get() = _token
    val role: String? get() = _role

    val email: String? get() {
        val t = _token ?: return null
        return try {
            val parts = t.split(".")
            if (parts.size >= 2) {
                val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.DEFAULT), Charsets.UTF_8)
                val subField = "\"sub\":\""
                if (payload.contains(subField)) {
                    payload.substringAfter(subField).substringBefore("\"")
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun saveToken(token: String, role: String) {
        _token = token
        _role = role
    }

    fun clear() {
        _token = null
        _role = null
    }

    fun isLoggedIn(): Boolean = _token != null
}
