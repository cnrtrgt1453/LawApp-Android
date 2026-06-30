package com.lawapp.android.ui.client

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lawapp.android.data.model.LawyerDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.Preview
import com.lawapp.android.data.model.CalendarSlotDto
import com.lawapp.android.ui.theme.LawAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerDetailsScreen(
    lawyerId: Long,
    leadId: Long?,
    viewModel: ClientViewModel,
    onSlotSelected: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val lawyers by viewModel.matchingLawyers.collectAsState()
    val slots by viewModel.availableSlots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val lawyer = lawyers.find { it.id == lawyerId } ?: LawyerDto(
        id = lawyerId,
        fullName = "Avukat Detayı",
        averageRating = 5.0
    )

    LaunchedEffect(lawyerId) {
        viewModel.fetchAvailableSlots(lawyerId)
    }

    LawyerDetailsContent(
        lawyer = lawyer,
        slots = slots,
        isLoading = isLoading,
        onSlotSelected = onSlotSelected,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerDetailsContent(
    lawyer: LawyerDto,
    slots: List<CalendarSlotDto>,
    isLoading: Boolean,
    onSlotSelected: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Avukat Profili", fontWeight = FontWeight.Bold) },
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
                // Profil Kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lawyer.fullName.take(2).uppercase(),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Av. ${lawyer.fullName}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (lawyer.verified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Doğrulanmış",
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                
                                Text(
                                    text = "Baro Sicil No: ${lawyer.barNumber ?: "Belirtilmemiş"}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Puan",
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = String.format("%.1f", lawyer.averageRating),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Uzmanlık Alanları",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            lawyer.specialties.forEach { specialty ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(specialty, fontSize = 12.sp) }
                                )
                            }
                        }
                    }
                }

                // Sosyal Medya ve Web Bağlantıları Kartı
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    val uriHandler = LocalUriHandler.current
                    fun openLink(url: String) {
                        if (url.isBlank()) return
                        val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            "https://$url"
                        } else {
                            url
                        }
                        try {
                            uriHandler.openUri(formattedUrl)
                        } catch (e: Exception) {
                            // ignore
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "İletişim ve Sosyal Medya",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        SocialDetailLinkRow("LinkedIn", lawyer.linkedinUrl, Icons.Default.Share) { openLink(lawyer.linkedinUrl ?: "") }
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        
                        SocialDetailLinkRow("Instagram", lawyer.instagramUrl, Icons.Default.Share) { openLink(lawyer.instagramUrl ?: "") }
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        
                        SocialDetailLinkRow("YouTube", lawyer.youtubeUrl, Icons.Default.Share) { openLink(lawyer.youtubeUrl ?: "") }
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        
                        SocialDetailLinkRow("Kişisel Web Sitesi", lawyer.websiteUrl, Icons.Default.Language) { openLink(lawyer.websiteUrl ?: "") }
                    }
                }

                // Biyografi
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Hakkımda",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = lawyer.bio ?: "Bu avukat henüz biyografisini eklememiştir.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }

                // Çalışma Takvimi / Müsait Randevu Saatleri
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Takvim")
                            Text(
                                text = "Çalışma Takvimi",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        
                        Text(
                            text = "Lütfen görüşmek istediğiniz uygun zaman dilimini seçin. Görüşme randevusu için platform kullanım ücreti tahsil edilecektir.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        } else if (slots.isEmpty()) {
                            Text(
                                text = "Avukatın şu anda müsait bir randevu slotu bulunmamaktadır.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                            val uniqueDates = remember(slots) {
                                slots.map { LocalDateTime.parse(it.slotTime, inputFormatter).toLocalDate() }.distinct().sorted()
                            }

                            LaunchedEffect(uniqueDates) {
                                if (selectedDate == null && uniqueDates.isNotEmpty()) {
                                    selectedDate = uniqueDates.first()
                                }
                            }

                            // Tarih Seçici Yatay Liste
                            val dateChipFormatter = DateTimeFormatter.ofPattern("dd MMM EEE")
                            Text(
                                text = "Tarih Seçin",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            ) {
                                items(uniqueDates) { date ->
                                    val isSelected = selectedDate == date
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedDate = date },
                                        label = { Text(date.format(dateChipFormatter)) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Filtrelenmiş Slotları listeyelim
                            val filteredSlots = slots.filter {
                                LocalDateTime.parse(it.slotTime, inputFormatter).toLocalDate() == selectedDate
                            }

                            if (filteredSlots.isEmpty()) {
                                Text(
                                    text = "Seçilen tarihte müsait saat bulunmamaktadır.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                // Slotları grid halinde gösterelim
                                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    filteredSlots.chunked(2).forEach { rowSlots ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            rowSlots.forEach { slot ->
                                                val parsedTime = LocalDateTime.parse(slot.slotTime, inputFormatter)
                                                val formatted = parsedTime.format(formatter)
                                                
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                                        .border(
                                                            width = 1.dp,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            shape = RoundedCornerShape(8.dp)
                                                        )
                                                        .clickable { onSlotSelected(slot.slotTime) }
                                                        .padding(12.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = formatted,
                                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                }
                                            }
                                            if (rowSlots.size == 1) {
                                                Spacer(modifier = Modifier.weight(1f))
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
}

@Preview(showBackground = true)
@Composable
fun LawyerDetailsPreview() {
    LawAppTheme {
        LawyerDetailsContent(
            lawyer = LawyerDto(
                id = 1,
                fullName = "Caner Yıldırım",
                barNumber = "12345",
                averageRating = 4.8,
                specialties = listOf("Ceza Hukuku", "Aile Hukuku"),
                bio = "10 yıllık tecrübesiyle her türlü hukuki sorununuzda yanınızdayız.",
                verified = true
            ),
            slots = listOf(
                CalendarSlotDto(1, 1, "2023-10-27T10:00:00", true),
                CalendarSlotDto(2, 1, "2023-10-27T11:00:00", true),
                CalendarSlotDto(3, 1, "2023-10-28T14:00:00", true)
            ),
            isLoading = false,
            onSlotSelected = {},
            onBackClick = {}
        )
    }
}

@Composable
fun SocialDetailLinkRow(
    label: String,
    url: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !url.isNullOrBlank()) { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (!url.isNullOrBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = if (!url.isNullOrBlank()) url else "Eklenmedi",
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = if (!url.isNullOrBlank()) TextDecoration.Underline else TextDecoration.None
                ),
                color = if (!url.isNullOrBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        }
    }
}
