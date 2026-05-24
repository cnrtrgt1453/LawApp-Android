package com.lawapp.android.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BidUI(
    val id: Long,
    val lawyerName: String,
    val message: String,
    val date: String,
    val phoneNumber: String,
    val status: String, // "PENDING", "ACCEPTED", "REJECTED"
    val rating: Double = 5.0,
    val specialties: List<String> = listOf("Genel Hukuk"),
    val verified: Boolean = true
)

@Composable
fun LeadBidsScreen(
    leadTitle: String,
    bids: List<BidUI>,
    onAcceptBid: (BidUI) -> Unit,
    onContactLawyer: (BidUI) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = leadTitle,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Ön Görüşme Başvuruları",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (bids.isEmpty()) {
            Text("Henüz bir ön görüşme başvurusu gelmedi. Beklemede kalın.", modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bids) { bid ->
                    BidItem(
                        bid = bid, 
                        onAccept = { onAcceptBid(bid) },
                        onContact = { onContactLawyer(bid) }
                    )
                }
            }
        }
    }
}

@Composable
fun BidItem(bid: BidUI, onAccept: () -> Unit, onContact: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Text(text = "Av. ${bid.lawyerName}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        if (bid.verified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "✓ Doğrulanmış",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    // Yıldız Puanı Gösterimi
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Text(text = "⭐ ${bid.rating}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                
                if (bid.status == "ACCEPTED") {
                    Text(text = "Kabul Edildi", color = androidx.compose.ui.graphics.Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                } else if (bid.status == "REJECTED") {
                    Text(text = "Reddedildi", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Uzmanlık Alanları
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                bid.specialties.forEach { spec ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(spec, fontSize = 10.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = bid.message, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (bid.status == "ACCEPTED") {
                Text(
                    text = "🔒 Güvenli İletişim Hattı Aktif",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onContact,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Güvenli Sohbet / Ön Görüşmeyi Başlat")
                }
            } else if (bid.status == "PENDING") {
                Button(
                    onClick = onAccept,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Ön Görüşme Talebini Kabul Et")
                }
            }
        }
    }
}
