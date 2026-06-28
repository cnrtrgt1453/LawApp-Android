package com.lawapp.android.data

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkClient {
    // Sunucu (Production) Ayarları
    const val HOST = "api.lawapp.io" 
    const val IS_SECURE = true   
    val PORT: Int? = null 

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
    
    val BASE_URL = "https://$HOST/api"
}
