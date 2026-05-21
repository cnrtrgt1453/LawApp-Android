package com.lawapp.android.ui.lawyer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Lead(
    val id: Long,
    val title: String,
    val category: String,
    val city: String,
    val description: String
)

@Composable
fun LeadFeedScreen(
    leads: List<Lead>,
    onLeadClick: (Lead) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "İş Havuzu",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn {
            items(leads) { lead ->
                LeadItem(lead = lead, onClick = { onLeadClick(lead) })
            }
        }
    }
}

@Composable
fun LeadItem(lead: Lead, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = lead.category, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            Text(text = lead.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = lead.city, fontSize = 14.sp)
        }
    }
}
