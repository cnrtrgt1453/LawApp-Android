package com.lawapp.android.ui.client

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lawapp.android.data.model.ClientProfile
import com.lawapp.android.ui.theme.LawAppTheme
import coil.compose.AsyncImage
import com.lawapp.android.data.toFullUrl

@Composable
fun ClientProfileScreen(
    viewModel: ClientProfileViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}
) {
    val profile by viewModel.profile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    ClientProfileScreenContent(
        profile = profile,
        isLoading = isLoading,
        error = error,
        onUpdateProfile = { fullName, bio -> viewModel.updateProfile(fullName, bio) },
        onUploadPhoto = { uri -> viewModel.uploadProfileImage(uri) },
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProfileScreenContent(
    profile: ClientProfile?,
    isLoading: Boolean,
    error: String?,
    onUpdateProfile: (String, String) -> Unit,
    onUploadPhoto: (Uri) -> Unit,
    onLogout: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(profile) {
        profile?.let {
            fullName = it.fullName ?: ""
            bio = it.bio ?: ""
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> 
            uri?.let {
                selectedImageUri = it
                onUploadPhoto(it)
            }
        }
    )

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
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
                }

                // Profil Fotoğrafı Bölümü
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.size(140.dp)
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
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                modifier = Modifier.padding(40.dp),
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
                            modifier = Modifier.size(40.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Fotoğraf Değiştir", modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // İsim ve Soyisim
                Text(
                    text = profile?.fullName ?: "Müvekkil",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Müvekkil Hesabı",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (!isEditing) {
                    // --- GÖRÜNTÜLEME MODU (VIEW MODE) ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Hakkımda",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (bio.isNotEmpty()) bio else "Kendiniz hakkında henüz bilgi girmediniz.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

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
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("İsim Soyisim") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Hakkımda") },
                        placeholder = { Text("Kendinizden kısaca bahsedin...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 8
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { 
                                fullName = profile?.fullName ?: ""
                                bio = profile?.bio ?: ""
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
                                onUpdateProfile(fullName, bio)
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
                
                Text(
                    text = "Not: Profiliniz sadece teklif verdiğiniz veya size teklif veren avukatlar tarafından görüntülenebilir.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
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

@Preview(showBackground = true)
@Composable
fun ClientProfileScreenPreview() {
    LawAppTheme {
        ClientProfileScreenContent(
            profile = ClientProfile(
                id = 1,
                bio = "Merhaba, hukuki süreçlerim için profesyonel destek arıyorum.",
                profileImageUrl = null
            ),
            isLoading = false,
            error = null,
            onUpdateProfile = { _, _ -> },
            onUploadPhoto = {}
        )
    }
}
