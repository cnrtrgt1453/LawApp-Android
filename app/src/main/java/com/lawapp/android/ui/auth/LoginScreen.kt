package com.lawapp.android.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lawapp.android.ui.theme.LawAppTheme
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

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
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onClearError: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    // Karar-5: Login başarılıysa navigasyonu tetikle
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) onLoginSuccess()
    }

    // Karar-6: Hata mesajını Snackbar ile göster
    LaunchedEffect(error) {
        error?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            onClearError()
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
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Giriş", fontSize = 18.sp)
                }
            }

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
            onLoginSuccess = {},
            onNavigateToRegister = {},
            onClearError = {}
        )
    }
}
