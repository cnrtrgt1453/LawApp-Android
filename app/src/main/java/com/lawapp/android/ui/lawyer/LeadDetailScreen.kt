package com.lawapp.android.ui.lawyer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LeadDetailScreen(
    lead: Lead,
    templates: List<BidTemplateUI> = emptyList(),
    onBidSubmit: (String) -> Unit
) {
    var bidMessage by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = lead.category, fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
        Text(text = lead.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = lead.city, fontSize = 16.sp)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(text = "İlan Detayı", fontWeight = FontWeight.SemiBold)
        Text(text = lead.description, fontSize = 16.sp)
        
        Spacer(modifier = Modifier.weight(1f))

        // Şablon Seçici
        if (templates.isNotEmpty()) {
            Box {
                OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Şablon Seç")
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Şablon Seç")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    templates.forEach { template ->
                        DropdownMenuItem(
                            text = { Text(template.title) },
                            onClick = {
                                bidMessage = template.content
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        OutlinedTextField(
            value = bidMessage,
            onValueChange = { bidMessage = it },
            label = { Text("Müvekkile Mesajınız") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Teklif Ver", fontSize = 18.sp)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Teklif Onayı") },
            text = { Text("Bu ilana teklif vermek için kategorinin gerektirdiği kredi harcanacaktır. Onaylıyor musunuz?") },
            confirmButton = {
                Button(onClick = {
                    onBidSubmit(bidMessage)
                    showDialog = false
                }) {
                    Text("Onayla")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}
