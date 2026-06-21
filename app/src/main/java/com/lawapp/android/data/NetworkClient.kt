package com.lawapp.android.data

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkClient {
    // Canlıya çıkarken HOST ve IS_SECURE değerlerini değiştirmeniz yeterli olacaktır
    const val HOST = "api.lawapp.io" // Canlı domain adresiniz
    const val PORT = 443              // HTTPS standart portu
    const val IS_SECURE = true        // Canlıda güvenli HTTPS/WSS için true

    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            level = if (IS_SECURE) LogLevel.NONE else LogLevel.HEADERS // Canlıda logları kapatıyoruz
        }
        install(WebSockets)
    }
    
    val BASE_URL = if (IS_SECURE) "https://$HOST/api" else "http://$HOST:$PORT/api"
}
