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
    val status: String // "PENDING", "ACCEPTED", "REJECTED"
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
            text = "Gelen Teklifler",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (bids.isEmpty()) {
            Text("Henüz bir teklif gelmedi. Beklemede kalın.", modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn {
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
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Avukat: ${bid.lawyerName}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                if (bid.status == "ACCEPTED") {
                    Text(text = "Kabul Edildi", color = androidx.compose.ui.graphics.Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                } else if (bid.status == "REJECTED") {
                    Text(text = "Reddedildi", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = bid.message, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            if (bid.status == "ACCEPTED") {
                Text(
                    text = "İletişim: ${bid.phoneNumber}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onContact,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Hemen Ara")
                }
            } else if (bid.status == "PENDING") {
                Button(
                    onClick = onAccept,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Teklifi Kabul Et")
                }
            }
        }
    }
}
