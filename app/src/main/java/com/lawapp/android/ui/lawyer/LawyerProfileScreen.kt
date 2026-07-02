package com.lawapp.android.ui.lawyer

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.lawapp.android.data.toFullUrl
import com.lawapp.android.ui.common.CityPickerDialog
import com.lawapp.android.ui.common.TurkishCities
import com.lawapp.android.ui.leads.LawCategories

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LawyerProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}
) {
    val profile by viewModel.profile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var bio by remember { mutableStateOf("") }
    var linkedinUrl by remember { mutableStateOf("") }
    var instagramUrl by remember { mutableStateOf("") }
    var websiteUrl by remember { mutableStateOf("") }
    var youtubeUrl by remember { mutableStateOf("") }
    var selectedSpecialties by remember { mutableStateOf<List<String>>(emptyList()) }
    var city by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var cityExpanded by remember { mutableStateOf(false) }
    
    // Profil yüklendiğinde alanları doldur
    LaunchedEffect(profile) {
        profile?.let {
            bio = it.bio ?: ""
            linkedinUrl = it.linkedinUrl ?: ""
            instagramUrl = it.instagramUrl ?: ""
            websiteUrl = it.websiteUrl ?: ""
            youtubeUrl = it.youtubeUrl ?: ""
            selectedSpecialties = it.specialties
            city = it.city ?: ""
        }
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> 
            uri?.let {
                selectedImageUri = it
                viewModel.uploadProfileImage(it)
            }
        }
    )

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
            // Silently ignore
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Profili Düzenle" else "Profilim") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        if (isLoading && profile == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
                }

                // Profil Fotoğrafı Bölümü
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.size(120.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        val imageModel = selectedImageUri ?: profile?.profileImageUrl.toFullUrl()
                        if (imageModel != null) {
                            AsyncImage(
                                model = imageModel,
                                contentDescription = "Profil Fotoğrafı",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                modifier = Modifier.padding(32.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    if (isEditing) {
                        IconButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.size(36.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Fotoğraf Değiştir", modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // İsim ve Soyisim
                Text(
                    text = profile?.fullName ?: "Avukat",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Avukat Hesabı" + (if (city.isNotEmpty()) " • $city" else ""),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (!isEditing) {
                    // --- GÖRÜNTÜLEME MODU (VIEW MODE) ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Hakkımda",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (bio.isNotEmpty()) bio else "Henüz biyografi eklenmedi.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Uzmanlık Alanları
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Uzmanlık Alanlarım",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            if (selectedSpecialties.isNotEmpty()) {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    selectedSpecialties.forEach { specialty ->
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text(specialty) }
                                        )
                                    }
                                }
                            } else {
                                Text("Henüz uzmanlık alanı eklenmedi.", color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sosyal Medya ve Web Bağlantıları
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "İletişim ve Sosyal Medya",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Bağlantı Satırları
                            SocialLinkRow("LinkedIn", linkedinUrl, Icons.Default.Share) { openLink(linkedinUrl) }
                            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                            
                            SocialLinkRow("Instagram", instagramUrl, Icons.Default.Share) { openLink(instagramUrl) }
                            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                            
                            SocialLinkRow("YouTube", youtubeUrl, Icons.Default.Share) { openLink(youtubeUrl) }
                            Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                            
                            SocialLinkRow("Kişisel Web Sitesi", websiteUrl, Icons.Default.Language) { openLink(websiteUrl) }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { isEditing = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Profili Düzenle", fontSize = 16.sp, modifier = Modifier.padding(8.dp))
                    }

                } else {
                    // --- DÜZENLEME MODU (EDIT MODE) ---
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Hakkımda (Biyografi)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Uzmanlık Alanları Seçimi
                    Text(
                        "Uzmanlık Alanları Seçin",
                        modifier = Modifier.align(Alignment.Start),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LawCategories.list.forEach { category ->
                            val isSelected = selectedSpecialties.contains(category)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedSpecialties = if (isSelected) {
                                        selectedSpecialties - category
                                    } else {
                                        selectedSpecialties + category
                                    }
                                },
                                label = { Text(category) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Şehir Seçimi
                    Text(
                        "Bulunduğunuz Şehir",
                        modifier = Modifier.align(Alignment.Start),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = if (city.isNotEmpty()) city else "Şehir Seçin",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Şehir") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, "dropdown")
                            }
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { cityExpanded = true }
                        )
                    }

                    if (cityExpanded) {
                        CityPickerDialog(
                            onDismissRequest = { cityExpanded = false },
                            onCitySelected = { selectedCity ->
                                city = selectedCity
                                cityExpanded = false
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Sosyal Medya ve Web Bağlantıları",
                        modifier = Modifier.align(Alignment.Start),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = linkedinUrl,
                        onValueChange = { linkedinUrl = it },
                        label = { Text("LinkedIn URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = instagramUrl,
                        onValueChange = { instagramUrl = it },
                        label = { Text("Instagram URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = youtubeUrl,
                        onValueChange = { youtubeUrl = it },
                        label = { Text("YouTube URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = websiteUrl,
                        onValueChange = { websiteUrl = it },
                        label = { Text("Kişisel Web Sitesi") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { 
                                // Reset fields to current profile
                                bio = profile?.bio ?: ""
                                linkedinUrl = profile?.linkedinUrl ?: ""
                                instagramUrl = profile?.instagramUrl ?: ""
                                websiteUrl = profile?.websiteUrl ?: ""
                                youtubeUrl = profile?.youtubeUrl ?: ""
                                selectedSpecialties = profile?.specialties ?: emptyList()
                                city = profile?.city ?: ""
                                selectedImageUri = null
                                isEditing = false 
                            },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("İptal", fontSize = 16.sp, modifier = Modifier.padding(8.dp))
                        }

                        Button(
                            onClick = { 
                                viewModel.updateProfile(bio, linkedinUrl, instagramUrl, websiteUrl, youtubeUrl, city, selectedSpecialties)
                                isEditing = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = MaterialTheme.shapes.medium,
                            enabled = !isLoading
                        ) {
                            Text("Kaydet", fontSize = 16.sp, modifier = Modifier.padding(8.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Çıkış Yap")
                }
            }
        }
    }
}

@Composable
fun SocialLinkRow(
    label: String,
    url: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = url.isNotBlank()) { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (url.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = if (url.isNotBlank()) url else "Eklenmedi",
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = if (url.isNotBlank()) TextDecoration.Underline else TextDecoration.None
                ),
                color = if (url.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        }
    }
}
