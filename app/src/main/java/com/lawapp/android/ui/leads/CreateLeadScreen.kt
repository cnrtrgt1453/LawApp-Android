package com.lawapp.android.ui.leads

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.lawapp.android.ui.client.ClientViewModel
import com.lawapp.android.ui.common.TurkishCities

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLeadScreen(
    viewModel: ClientViewModel,
    onLeadCreated: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(LawCategories.list[0]) }
    var city by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var cityExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Yeni İlan Oluştur",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Hukuki sorununuzu detaylıca anlatın, avukatlar size ulaşsın.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Kategori Seçimi
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Hukuk Kategorisi") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, "dropdown")
                }
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.8f).heightIn(max = 280.dp)
            ) {
                LawCategories.list.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("İlan Başlığı") },
            placeholder = { Text("Örn: Boşanma davası için danışmanlık") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Şehir Seçimi
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = if (city.isNotEmpty()) city else "Şehir Seçin",
                onValueChange = {},
                readOnly = true,
                label = { Text("Şehir") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (city.isEmpty()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, "dropdown")
                }
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { cityExpanded = true }
            )
            DropdownMenu(
                expanded = cityExpanded,
                onDismissRequest = { cityExpanded = false },
                modifier = Modifier.fillMaxWidth(0.8f).heightIn(max = 280.dp)
            ) {
                TurkishCities.list.forEach { TurkishCity ->
                    DropdownMenuItem(
                        text = { Text(TurkishCity) },
                        onClick = {
                            city = TurkishCity
                            cityExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Detaylı Açıklama") },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            maxLines = 5
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { 
                if (city.isNotEmpty()) {
                    viewModel.createLead(title, description, selectedCategory, city) {
                        onLeadCreated()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = title.isNotBlank() && description.isNotBlank() && city.isNotEmpty()
        ) {
            Text("İlanı Yayınla", fontSize = 18.sp)
        }
    }
}
