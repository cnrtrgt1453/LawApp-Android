package com.lawapp.android.data

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkClient {
    // Yeni Sunucu Ayarları
    const val HOST = "89.167.75.213" 
    const val IS_SECURE = false   // IP üzerinden direkt bağlantıda genellikle SSL yoktur, HTTP kullanılır
    val PORT: Int? = 8080         // Sunucudaki Spring Boot portunuz (varsayılan 8080)

    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            // Canlıda güvenlik için logları kapatıyoruz
            level = LogLevel.NONE 
        }
        install(WebSockets)
    }
    
    val BASE_URL = if (IS_SECURE) "https://$HOST/api" else "http://$HOST:$PORT/api"
}

fun String?.toFullUrl(): String? {
    if (this == null) return null
    if (this.startsWith("http://") || this.startsWith("https://")) return this
    val base = NetworkClient.BASE_URL.substringBefore("/api")
    return "$base$this"
}
