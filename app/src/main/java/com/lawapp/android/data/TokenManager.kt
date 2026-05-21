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
