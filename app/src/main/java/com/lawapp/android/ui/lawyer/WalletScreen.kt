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
    val isPopular: Boolean = false,
    val description: String = ""
)

val creditPackages = listOf(
    CreditPackage("Başlangıç", 100, "499 TL"),
    CreditPackage("Profesyonel", 500, "1.999 TL", isPopular = true),
    CreditPackage("Sınırsız Güç", 1200, "3.999 TL")
)

val subscriptionPackages = listOf(
    CreditPackage("Gümüş Avukat", 150, "1.499 TL/Ay", description = "Uzmanlık Rozeti, 150 Kredi"),
    CreditPackage("Altın Ortak", 400, "2.999 TL/Ay", isPopular = true, description = "Arama Önceliği, 400 Kredi, Öne Çıkarılan Profil"),
    CreditPackage("Platin Partner", 1000, "5.999 TL/Ay", description = "Sınırsız Kredi, Sohbet Önceliği, VIP Destek")
)

@Composable
fun WalletScreen(
    currentBalance: Int,
    onPackageClick: (CreditPackage) -> Unit
) {
    var selectedTab by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Cüzdanım & Üyelik",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Mevcut Bakiye Kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Mevcut Kredi Bakiyesi", color = Color.White.copy(alpha = 0.8f))
                Text(
                    text = "$currentBalance 🪙",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        // TabRow - Tek Seferlik vs Abonelik
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Kredi Yükle", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Abonelik Paketleri 👑", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            Text(text = "Tek Seferlik Kredi Yükleme", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(creditPackages) { pkg ->
                    PackageItem(pkg = pkg, onClick = { onPackageClick(pkg) })
                }
            }
        } else {
            Text(text = "Aylık SaaS Abonelikleri ile Avantaj Kazanın", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(subscriptionPackages) { pkg ->
                    SubscriptionItem(pkg = pkg, onClick = { onPackageClick(pkg) })
                }
            }
        }
    }
}

@Composable
fun PackageItem(pkg: CreditPackage, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable { onClick() },
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
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 4.dp)
                )
            }
            Text(text = pkg.name, fontSize = 14.sp)
            Text(text = "${pkg.amount} Kredi", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = pkg.price, fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun SubscriptionItem(pkg: CreditPackage, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = if (pkg.isPopular) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)) else CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = pkg.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    if (pkg.isPopular) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AVANTAJLI",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = pkg.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = pkg.price, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Hemen Katıl", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
