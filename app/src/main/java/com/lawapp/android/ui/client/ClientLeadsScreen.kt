package com.lawapp.android.ui.client

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
import com.lawapp.android.ui.common.Lead

@Composable
fun ClientLeadsScreen(
    myLeads: List<Lead>,
    onLeadClick: (Lead) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "İlanlarım",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (myLeads.isEmpty()) {
            Text("Henüz bir ilan açmadınız.", modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn {
                items(myLeads) { lead ->
                    MyLeadItem(lead = lead, onClick = { onLeadClick(lead) })
                }
            }
        }
    }
}

@Composable
fun MyLeadItem(lead: Lead, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = lead.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(text = lead.category, fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
            }
            // Teklif sayısını gösteren bir rozet (Badge) - Örnek statik veri
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "3 Teklif",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
