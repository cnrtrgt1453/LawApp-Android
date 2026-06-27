package com.lawapp.android.data

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkClient {
    // Localhost testi için: 10.0.2.2 (Android Emulator'ün bilgisayarınıza erişim adresi)
    // Canlı için: "api.lawapp.io"
    const val HOST = "api.lawapp.io" 
    const val IS_SECURE = true   // Canlı için true, yerel için false
    val PORT: Int? = if (IS_SECURE) null else 8080        // Yerel backend portunuz (Örn: 8080, 3000), canlıda null

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
