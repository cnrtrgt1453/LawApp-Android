package com.lawapp.android.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lawapp.android.data.model.AppointmentDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsListScreen(
    role: String, // "CLIENT" or "LAWYER"
    appointments: List<AppointmentDto>,
    isLoading: Boolean,
    onAcceptClick: (Long) -> Unit = {},
    onRejectClick: (Long) -> Unit = {},
    onStartCall: (AppointmentDto) -> Unit = {},
    onRefresh: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Bekleyenler", "Onaylananlar", "Geçmiş")

    LaunchedEffect(Unit) {
        onRefresh()
    }

    val filteredList = remember(appointments, selectedTab) {
        appointments.filter { app ->
            when (selectedTab) {
                0 -> app.status == "PENDING"
                1 -> app.status == "ACCEPTED"
                else -> app.status == "COMPLETED" || app.status == "REJECTED" || app.status == "CANCELLED"
            }
        }.sortedBy { it.appointmentTime }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Randevularım", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab Satırı
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, label ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(label, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (filteredList.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Bu kategoride randevunuz bulunmamaktadır.",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredList) { appointment ->
                            AppointmentItem(
                                role = role,
                                appointment = appointment,
                                onAccept = { onAcceptClick(appointment.id) },
                                onReject = { onRejectClick(appointment.id) },
                                onStartCall = { onStartCall(appointment) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentItem(
    role: String,
    appointment: AppointmentDto,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onStartCall: () -> Unit
) {
    val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val displayFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy - HH:mm")
    
    val time = try {
        LocalDateTime.parse(appointment.appointmentTime, inputFormatter).format(displayFormatter)
    } catch (e: Exception) {
        appointment.appointmentTime
    }

    // Arama butonu aktifliği kontrolü: Randevu saatine 10 dk kala açılır ve randevudan 40 dk sonrasına kadar aktif kalır
    val isCallActive = remember(appointment) {
        try {
            val appTime = LocalDateTime.parse(appointment.appointmentTime, inputFormatter)
            val now = LocalDateTime.now()
            now.isAfter(appTime.minusMinutes(10)) && now.isBefore(appTime.plusMinutes(40))
        } catch (e: Exception) {
            true // Hata durumunda test kolaylığı için true dön
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Tarih",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = time,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                
                Surface(
                    color = when (appointment.status) {
                        "PENDING" -> MaterialTheme.colorScheme.secondaryContainer
                        "ACCEPTED" -> Color(0xFFE8F5E9)
                        "REJECTED" -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (appointment.status) {
                            "PENDING" -> "Onay Bekliyor"
                            "ACCEPTED" -> "Onaylandı"
                            "REJECTED" -> "Reddedildi"
                            "COMPLETED" -> "Tamamlandı"
                            else -> appointment.status
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (appointment.status) {
                            "PENDING" -> MaterialTheme.colorScheme.onSecondaryContainer
                            "ACCEPTED" -> Color(0xFF2E7D32)
                            "REJECTED" -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (role == "CLIENT") "Avukat: Av. ${appointment.lawyerName}" else "Vatandaş: ${appointment.clientName}",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "Hukuki Konu: ${appointment.leadTitle ?: "Genel Danışmanlık"}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Kategori: ${appointment.leadCategory ?: "Genel"}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Platform İşlem Bedeli: ${appointment.platformFee} TL (${appointment.paymentStatus})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            // Aksiyon Butonları
            if (appointment.status == "PENDING" && role == "LAWYER") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Onayla")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Onayla", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Reddet")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reddet", fontWeight = FontWeight.Bold)
                    }
                }
            } else if (appointment.status == "ACCEPTED") {
                Spacer(modifier = Modifier.height(16.dp))
                if (role == "CLIENT") {
                    Button(
                        onClick = onStartCall,
                        enabled = isCallActive,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE1306C), // Instagram rengi havası
                            disabledContainerColor = Color.LightGray
                        )
                    ) {
                        Icon(Icons.Default.VideoCall, contentDescription = "Arama", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isCallActive) "Görüntülü Aramayı Başlat" else "Arama Saatini Bekleyin (Son 10 Dk)",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    // Avukat tarafı uyarısı
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Telefon", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isCallActive) "Vatandaşın Araması Bekleniyor..." else "Randevu saatinde vatandaş arayacaktır.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
