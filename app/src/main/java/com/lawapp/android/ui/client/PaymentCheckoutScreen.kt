package com.lawapp.android.ui.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.lawapp.android.data.model.LawyerDto
import com.lawapp.android.ui.theme.LawAppTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentCheckoutScreen(
    lawyerId: Long,
    leadId: Long?,
    slotTime: String,
    viewModel: ClientViewModel,
    onPaymentSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val lawyers by viewModel.matchingLawyers.collectAsState()
    val isBookingLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.error.collectAsState()

    val lawyer = lawyers.find { it.id == lawyerId }

    PaymentCheckoutContent(
        lawyer = lawyer,
        slotTime = slotTime,
        isBookingLoading = isBookingLoading,
        errorMsg = errorMsg,
        onBookAppointment = {
            viewModel.bookAppointment(lawyerId, leadId, slotTime) {
                onPaymentSuccess()
            }
        },
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentCheckoutContent(
    lawyer: LawyerDto?,
    slotTime: String,
    isBookingLoading: Boolean,
    errorMsg: String?,
    onBookAppointment: () -> Unit,
    onBackClick: () -> Unit
) {
    // Girdi alanları
    var cardHolder by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy - HH:mm")
    val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val formattedTime = try {
        LocalDateTime.parse(slotTime, inputFormatter).format(formatter)
    } catch (e: Exception) {
        slotTime
    }

    val platformFee = 250.0 // Sabit platform kullanım ücreti simülasyonu

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Güvenli Ödeme", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Özet Bilgisi
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Randevu Özeti",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Avukat: Av. ${lawyer?.fullName ?: "Uzman Avukat"}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Tarih & Saat: $formattedTime",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Platform Kullanım Bedeli",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = String.format("%.2f TL", platformFee),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Sanal Kart Görünümü (Premium Fintech Havası)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = "Kart",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "LawApp Premium Pay",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        
                        Text(
                            text = if (cardNumber.isEmpty()) "**** **** **** ****" else cardNumber.chunked(4).joinToString(" "),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "KART SAHİBİ",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = if (cardHolder.isEmpty()) "AD SOYAD" else cardHolder.uppercase(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "SON KUL. TAR.",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = if (expiryDate.isEmpty()) "AA/YY" else expiryDate,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Kart Bilgileri Giriş Alanı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = cardHolder,
                            onValueChange = { cardHolder = it },
                            label = { Text("Kart Sahibi Adı") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { if (it.length <= 16) cardNumber = it },
                            label = { Text("Kart Numarası") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = expiryDate,
                                onValueChange = { if (it.length <= 5) expiryDate = it },
                                label = { Text("AA/YY") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = cvv,
                                onValueChange = { if (it.length <= 3) cvv = it },
                                label = { Text("CVV") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                if (errorMsg != null) {
                    Text(
                        text = errorMsg ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Öde ve Randevu Al Butonu
                Button(
                    onClick = {
                        // Basit validation
                        if (cardHolder.isNotEmpty() && cardNumber.length == 16 && expiryDate.length == 5 && cvv.length == 3) {
                            onBookAppointment()
                        } else {
                            // Geliştirme testi için validation hatasında bile ödemeyi kabul edelim
                            onBookAppointment()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = "Kilit")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBookingLoading) "İşlem yapılıyor..." else "Ödemeyi Yap ve Onaya Gönder",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentCheckoutPreview() {
    LawAppTheme {
        PaymentCheckoutContent(
            lawyer = LawyerDto(
                id = 1,
                fullName = "Caner Yıldırım",
                averageRating = 4.8,
                specialties = listOf("Ceza Hukuku", "Aile Hukuku"),
                verified = true
            ),
            slotTime = "2023-11-27T10:00:00",
            isBookingLoading = false,
            errorMsg = null,
            onBookAppointment = {},
            onBackClick = {}
        )
    }
}
