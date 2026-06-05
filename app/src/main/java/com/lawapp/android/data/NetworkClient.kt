package com.lawapp.android.data

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkClient {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            level = LogLevel.HEADERS // BODY seviyesi hassas verileri loglar, üretimde NONE kullanılmalıdır
        }
        install(WebSockets)
    }
    
    const val BASE_URL = "http://10.0.2.2:8080/api" // Android emulator localhost
}
