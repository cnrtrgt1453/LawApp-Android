package com.lawapp.android.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lawapp.android.R
import com.lawapp.android.ui.theme.LawAppTheme
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

@Composable
fun LoginScreen(
    role: String = "CLIENT",
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()

    LoginScreenContent(
        isLoading = isLoading,
        error = error,
        loginSuccess = loginSuccess,
        onLogin = { email, password -> viewModel.login(email, password, role) },
        onLoginWithGoogle = { token -> viewModel.loginWithGoogle(token, role) },
        onLoginWithFacebook = { token -> viewModel.loginWithFacebook(token, role) },
        onLoginSuccess = onLoginSuccess,
        onNavigateToRegister = onNavigateToRegister,
        onClearError = { viewModel.clearError() }
    )
}

@Composable
fun LoginScreenContent(
    isLoading: Boolean,
    error: String?,
    loginSuccess: Boolean,
    onLogin: (String, String) -> Unit,
    onLoginWithGoogle: (String) -> Unit,
    onLoginWithFacebook: (String) -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onClearError: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    // --- GOOGLE SIGN IN ---
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (!idToken.isNullOrEmpty()) {
                    onLoginWithGoogle(idToken)
                } else {
                    scope.launch { snackbarHostState.showSnackbar("Google kimlik doğrulaması başarısız.") }
                }
            } catch (e: ApiException) {
                scope.launch { snackbarHostState.showSnackbar("Google Giriş Hatası: ${e.localizedMessage}") }
            }
        }
    )

    fun startGoogleSignIn() {
        val webClientId = context.getString(R.string.google_web_client_id)
        if (webClientId == "YOUR_GOOGLE_WEB_CLIENT_ID.apps.googleusercontent.com") {
            scope.launch { snackbarHostState.showSnackbar("Lütfen strings.xml dosyasındaki google_web_client_id değerini güncelleyin.") }
            return
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(webClientId)
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            googleSignInLauncher.launch(googleSignInClient.signInIntent)
        }
    }

    // --- FACEBOOK LOGIN ---
    val callbackManager = remember { CallbackManager.Factory.create() }
    val loginManager = remember { LoginManager.getInstance() }
    
    val facebookLoginLauncher = rememberLauncherForActivityResult(
        contract = loginManager.createActivityResultContract(callbackManager),
        onResult = { /* Managed by CallbackManager */ }
    )

    DisposableEffect(Unit) {
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val token = result.accessToken.token
                onLoginWithFacebook(token)
            }
            override fun onCancel() {
                scope.launch { snackbarHostState.showSnackbar("Facebook girişi iptal edildi.") }
            }
            override fun onError(error: FacebookException) {
                scope.launch { snackbarHostState.showSnackbar("Facebook Giriş Hatası: ${error.localizedMessage}") }
            }
        })
        onDispose {
            loginManager.unregisterCallback(callbackManager)
        }
    }

    fun startFacebookSignIn() {
        val facebookAppId = context.getString(R.string.facebook_app_id)
        if (facebookAppId == "YOUR_FACEBOOK_APP_ID") {
            scope.launch { snackbarHostState.showSnackbar("Lütfen strings.xml dosyasındaki facebook_app_id değerini güncelleyin.") }
            return
        }
        facebookLoginLauncher.launch(listOf("public_profile", "email"))
    }

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) onLoginSuccess()
    }

    LaunchedEffect(error) {
        error?.let {
            if (it.contains("Giriş başarısız")) {
                showErrorDialog = true
            } else {
                scope.launch { snackbarHostState.showSnackbar(it) }
            }
            onClearError()
        }
    }

    if (showErrorDialog) {
        Dialog(onDismissRequest = { showErrorDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Başlık Bandı (Lacivert zemin)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Hata",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    // Hata Mesajı
                    Text(
                        text = "Kullanıcı adı ve şifre hatalıdır.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                        textAlign = TextAlign.Center
                    )

                    // Tamam Butonu (Ortalanmış ve lacivert)
                    Button(
                        onClick = { showErrorDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(bottom = 20.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = "Tamam",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Giriş Yap",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-posta") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Şifre") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onLogin(email, password) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading,
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("E-posta ile Giriş", fontSize = 16.sp)
                }
            }

            // --- VEYA AYIRACI ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                Text("veya", modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
                Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
            }

            // --- SOSYAL MEDYA GİRİŞ BUTONLARI ---
            Button(
                onClick = { startGoogleSignIn() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F1F1), contentColor = Color.Black),
                shape = MaterialTheme.shapes.medium,
                enabled = !isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Google ile Devam Et", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { startFacebookSignIn() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2), contentColor = Color.White),
                shape = MaterialTheme.shapes.medium,
                enabled = !isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = "Facebook",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Facebook ile Devam Et", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text("Hesabınız yok mu? Kayıt Olun")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LawAppTheme {
        LoginScreenContent(
            isLoading = false,
            error = null,
            loginSuccess = false,
            onLogin = { _, _ -> },
            onLoginWithGoogle = {},
            onLoginWithFacebook = {},
            onLoginSuccess = {},
            onNavigateToRegister = {},
            onClearError = {}
        )
    }
}
