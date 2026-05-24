package com.lawapp.android.ui.client

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreateLeadWizardScreen(
    onLeadSubmit: (title: String, description: String, category: String, city: String, wizardJson: String) -> Unit,
    onCancel: () -> Unit
) {
    var step by remember { mutableStateOf(1) }
    
    // Form States
    var selectedCategory by remember { mutableStateOf("") }
    var q1Answer by remember { mutableStateOf("") }
    var q2Answer by remember { mutableStateOf("") }
    var q3Answer by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("İstanbul") }
    var additionalNotes by remember { mutableStateOf("") }
    var isKvkkAccepted by remember { mutableStateOf(false) }

    val categories = listOf("Boşanma", "Ceza", "İş Hukuku", "Ticaret Hukuku")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Üst İlerleme Barı (Stepper indicator)
        Text(
            text = "Yeni Ön Görüşme Başvurusu (${step}/4)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LinearProgressIndicator(
            progress = { step / 4f },
            modifier = Modifier.fillMaxWidth().height(6.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (step) {
                1 -> {
                    // Adım 1: Kategori Seçimi
                    Column {
                        Text(text = "Hukuki Yardım Almak İstediğiniz Alanı Seçin:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        categories.forEach { category ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedCategory == category) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable { selectedCategory = category }
                            ) {
                                Text(
                                    text = category,
                                    modifier = Modifier.padding(16.dp),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Adım 2: Akıllı Sihirbaz Soru Seti (Kategori Özel)
                    Column {
                        Text(text = "Detayları Belirleyin:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        when (selectedCategory) {
                            "Boşanma" -> {
                                Text("1. Boşanma türünü belirtin:", fontWeight = FontWeight.SemiBold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { q1Answer = "Anlaşmalı" }, colors = ButtonDefaults.buttonColors(containerColor = if (q1Answer == "Anlaşmalı") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)) { Text("Anlaşmalı") }
                                    Button(onClick = { q1Answer = "Çekişmeli" }, colors = ButtonDefaults.buttonColors(containerColor = if (q1Answer == "Çekişmeli") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)) { Text("Çekişmeli") }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("2. Ortak velayeti olan çocuk var mı?", fontWeight = FontWeight.SemiBold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { q2Answer = "Evet" }, colors = ButtonDefaults.buttonColors(containerColor = if (q2Answer == "Evet") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)) { Text("Evet") }
                                    Button(onClick = { q2Answer = "Hayır" }, colors = ButtonDefaults.buttonColors(containerColor = if (q2Answer == "Hayır") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)) { Text("Hayır") }
                                }
                            }
                            "İş Hukuku" -> {
                                Text("1. Hukuki uyuşmazlığın konusu nedir?", fontWeight = FontWeight.SemiBold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { q1Answer = "Kıdem/İhbar Tazminatı" }, colors = ButtonDefaults.buttonColors(containerColor = if (q1Answer == "Kıdem/İhbar Tazminatı") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)) { Text("Tazminat Alacağı") }
                                    Button(onClick = { q1Answer = "İşe İade" }, colors = ButtonDefaults.buttonColors(containerColor = if (q1Answer == "İşe İade") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)) { Text("İşe İade") }
                                }
                            }
                            else -> {
                                Text("1. Sorununuzun aciliyet durumu:", fontWeight = FontWeight.SemiBold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { q1Answer = "Çok Acil" }, colors = ButtonDefaults.buttonColors(containerColor = if (q1Answer == "Çok Acil") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)) { Text("Çok Acil") }
                                    Button(onClick = { q1Answer = "Genel Sorun" }, colors = ButtonDefaults.buttonColors(containerColor = if (q1Answer == "Genel Sorun") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)) { Text("Genel Sorun") }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Adım 3: Başlık ve Konum
                    Column {
                        Text(text = "Talep Özeti ve Konum:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Talebinizin Kısa Başlığı (Örn: Boşanma Ön Danışma)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("Şehir (Örn: İstanbul)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = additionalNotes,
                            onValueChange = { additionalNotes = it },
                            label = { Text("Hukuki Sorunuzun Detayı (KVKK Kapsamında İsim ve Telefon Yazmayınız!)") },
                            modifier = Modifier.fillMaxWidth().height(120.dp)
                        )
                    }
                }
                4 -> {
                    // Adım 4: KVKK Açık Rıza ve Onay
                    Column {
                        Text(text = "KVKK Güvencesi ve Gönderim:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "🔒 Kişisel Verilerin Korunması Taahhüdü",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "LawApp, müvekkil gizliliğine ve sır saklama yükümlülüklerine saygı duyar. Girdiğiniz bilgiler kesinlikle kamuya açık paylaşılmaz. Sadece onaylayacağınız doğrulanmış avukata kademeli olarak gösterilir.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable { isKvkkAccepted = !isKvkkAccepted }
                        ) {
                            Checkbox(
                                checked = isKvkkAccepted,
                                onCheckedChange = { isKvkkAccepted = it }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Girdiğim verilerde kişisel veri (İsim, Telefon, TCKN vb.) bulunmadığını taahhüt ediyor ve Aydınlatma Metnini kabul ediyorum.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Alt Navigasyon Butonları
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (step > 1) {
                OutlinedButton(onClick = { step-- }) {
                    Text("Geri")
                }
            } else {
                OutlinedButton(onClick = onCancel) {
                    Text("İptal")
                }
            }

            if (step < 4) {
                Button(
                    onClick = { step++ },
                    enabled = (step == 1 && selectedCategory.isNotEmpty()) || step > 1
                ) {
                    Text("İleri")
                }
            } else {
                Button(
                    onClick = {
                        val wizardJson = "{\"category\":\"$selectedCategory\",\"q1\":\"$q1Answer\",\"q2\":\"$q2Answer\",\"q3\":\"$q3Answer\"}"
                        val finalDesc = "Detaylar: $additionalNotes"
                        onLeadSubmit(title, finalDesc, selectedCategory, city, wizardJson)
                    },
                    enabled = isKvkkAccepted && title.isNotEmpty() && city.isNotEmpty()
                ) {
                    Text("Talebi Havuza Gönder")
                }
            }
        }
    }
}
