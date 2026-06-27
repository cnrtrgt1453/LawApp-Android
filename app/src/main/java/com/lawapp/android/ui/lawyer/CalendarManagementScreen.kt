package com.lawapp.android.ui.lawyer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lawapp.android.data.model.CalendarSlotDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarManagementScreen(
    viewModel: LawyerViewModel
) {
    val slots by viewModel.slots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val successMsg by viewModel.successMessage.collectAsState()
    val errorMsg by viewModel.error.collectAsState()

    var selectedDate by remember { mutableStateOf(LocalDate.now().plusDays(1)) } // Varsayılan yarın
    var selectedHour by remember { mutableStateOf("10:00") }

    val dateOptions = remember {
        listOf(
            LocalDate.now().plusDays(1) to "Yarın (${LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM"))})",
            LocalDate.now().plusDays(2) to "Öbür Gün (${LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("dd.MM"))})",
            LocalDate.now().plusDays(3) to "3 Gün Sonra (${LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM"))})"
        )
    }

    val hourOptions = listOf("09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00", "17:00")

    LaunchedEffect(Unit) {
        viewModel.fetchCalendarSlots()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Çalışma Takvimim", fontWeight = FontWeight.Bold) }
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Bilgi Kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Bilgi",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Vatandaşların sizden görüntülü görüşme randevusu alabilmesi için aşağıdan uygun olduğunuz gün ve saat dilimlerini takviminize ekleyin.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Slot Ekleme Arayüzü
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Müsait Saat Dilimi Ekle",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Tarih Seçimi
                        Text("Tarih Seçin", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            dateOptions.forEach { (date, label) ->
                                FilterChip(
                                    selected = selectedDate == date,
                                    onClick = { selectedDate = date },
                                    label = { Text(label, fontSize = 11.sp) }
                                )
                            }
                        }

                        // Saat Seçimi
                        Text("Saat Seçin", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        // Saatleri horizontal scrollable veya grid olarak yerleştirebiliriz
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Basit bir Dropdown Menu veya kaydırılabilir satır
                            Box(modifier = Modifier.fillMaxWidth()) {
                                var expanded by remember { mutableStateOf(false) }
                                OutlinedButton(
                                    onClick = { expanded = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Seçilen Saat: $selectedHour")
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    hourOptions.forEach { hour ->
                                        DropdownMenuItem(
                                            text = { Text(hour) },
                                            onClick = {
                                                selectedHour = hour
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val time = LocalTime.parse(selectedHour)
                                val dateTime = LocalDateTime.of(selectedDate, time)
                                val isoStr = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                viewModel.addCalendarSlot(isoStr)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Ekle")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Takvime Ekle", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Geri bildirim mesajları
                if (successMsg != null) {
                    Text(successMsg ?: "", color = Color(0xFF4CAF50), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                if (errorMsg != null) {
                    Text(errorMsg ?: "", color = MaterialTheme.colorScheme.error, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }

                Divider()

                Text(
                    text = "Aktif Müsait Saatleriniz",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (slots.isEmpty()) {
                    Text("Henüz takviminize müsait bir slot eklemediniz.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    val displayFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy - HH:mm")
                    val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(slots) { slot ->
                            val time = try {
                                LocalDateTime.parse(slot.slotTime, inputFormatter).format(displayFormatter)
                            } catch (e: Exception) {
                                slot.slotTime
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = "Saat",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Column {
                                            Text(text = time, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                            Text(
                                                text = if (slot.available) "Müsait (Rezervasyon Bekliyor)" else "Rezerve Edildi / Dolu",
                                                fontSize = 12.sp,
                                                color = if (slot.available) Color(0xFF4CAF50) else Color.Red
                                            )
                                        }
                                    }
                                    
                                    if (slot.available) {
                                        IconButton(onClick = { viewModel.deleteCalendarSlot(slot.id) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Sil",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
