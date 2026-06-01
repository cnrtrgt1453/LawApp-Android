package com.lawapp.android.ui.lawyer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadFeedScreen(
    leads: List<Lead>,
    onLeadClick: (Lead) -> Unit
) {
    var selectedCategory by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("Tümü") }
    var selectedCity by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("Tümü") }

    val categories = listOf("Tümü", "Boşanma", "Ceza", "İş Hukuku", "Ticaret Hukuku")
    val cities = listOf("Tümü", "İstanbul", "Ankara", "İzmir")

    val filteredLeads = leads.filter { lead ->
        (selectedCategory == "Tümü" || lead.category.equals(selectedCategory, ignoreCase = true)) &&
        (selectedCity == "Tümü" || lead.city.equals(selectedCity, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Başvuru & Ön Görüşme Havuzu",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        // KVKK Bilgilendirme Notu
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Text(
                text = "🔒 KVKK Güvencesi: Müvekkillerin hassas kişisel verileri ilk temasta gizlenmektedir. Detaylı bilgiler sadece onaylanan sohbet odasında açılır.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Kategori Filtresi
        Text(text = "Kategoriye Göre Filtrele:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Şehir Filtresi
        Text(text = "Şehre Göre Filtrele:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            items(cities) { city ->
                FilterChip(
                    selected = selectedCity == city,
                    onClick = { selectedCity = city },
                    label = { Text(city) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredLeads) { lead ->
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
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = lead.category, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                Text(text = lead.city, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = lead.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            // KVKK Maskelenmiş Önizleme
            Text(
                text = if (lead.description.length > 80) lead.description.take(80) + "..." else lead.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}
