package com.lawapp.android.data

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkClient {
    // Canlıya çıkarken HOST ve IS_SECURE değerlerini değiştirmeniz yeterli olacaktır
    const val HOST = "10.0.2.2" // Canlıda örn: "api.lawapp.com"
    const val PORT = 8080        // Canlıda standart HTTPS/WSS için 443 veya portsuz kullanımda null
    const val IS_SECURE = false  // Canlıda güvenli HTTPS ve WSS için true yapın

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
