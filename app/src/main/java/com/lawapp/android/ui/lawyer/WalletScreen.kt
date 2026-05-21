package com.lawapp.android.ui.lawyer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CreditPackage(
    val name: String,
    val amount: Int,
    val price: String,
    val isPopular: Boolean = false
)

val creditPackages = listOf(
    CreditPackage("Başlangıç", 100, "499 TL"),
    CreditPackage("Profesyonel", 500, "1.999 TL", isPopular = true),
    CreditPackage("Sınırsız Güç", 1200, "3.999 TL")
)

@Composable
fun WalletScreen(
    currentBalance: Int,
    onPackageClick: (CreditPackage) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Cüzdanım",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Mevcut Bakiye Kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Mevcut Kredi Bakiyesi", color = Color.White.copy(alpha = 0.8f))
                Text(
                    text = "$currentBalance 🪙",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(text = "Kredi Paketi Satın Al", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(creditPackages) { pkg ->
                PackageItem(pkg = pkg, onClick = { onPackageClick(pkg) })
            }
        }
    }
}

@Composable
fun PackageItem(pkg: CreditPackage, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() },
        border = if (pkg.isPopular) ButtonDefaults.outlinedButtonBorder else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (pkg.isPopular) {
                Text(
                    text = "EN POPÜLER",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer).padding(horizontal = 4.dp)
                )
            }
            Text(text = pkg.name, fontSize = 14.sp)
            Text(text = "${pkg.amount} Kredi", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = pkg.price, fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
        }
    }
}
