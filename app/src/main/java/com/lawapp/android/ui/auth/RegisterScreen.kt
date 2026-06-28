package com.lawapp.android.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lawapp.android.ui.theme.LawAppTheme
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    selectedRole: String,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val registerSuccess by viewModel.loginSuccess.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            onRegisterSuccess()
        }
    }

    LaunchedEffect(error) {
        error?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { padding ->
        RegisterScreenContent(
            selectedRole = selectedRole,
            fullName = fullName,
            email = email,
            password = password,
            phone = phone,
            onFullNameChange = { fullName = it },
            onEmailChange = { email = it },
            onPasswordChange = { password = it },
            onPhoneChange = { phone = it },
            onRegisterClick = {
                if (fullName.isBlank() || email.isBlank() || password.isBlank() || phone.isBlank()) {
                    scope.launch { snackbarHostState.showSnackbar("Lütfen tüm alanları doldurun.") }
                } else {
                    viewModel.register(fullName, email, password, phone, selectedRole)
                }
            },
            onNavigateToLogin = onNavigateToLogin,
            isLoading = isLoading,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun RegisterScreenContent(
    selectedRole: String,
    fullName: String,
    email: String,
    password: String,
    phone: String,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val roleText = if (selectedRole == "LAWYER") "Avukat Kaydı" else "Vatandaş Kaydı"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = roleText,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = { Text("Ad Soyad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("E-posta") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Şifre") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Telefon Numarası") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Kayıt Ol", fontSize = 18.sp)
            }
        }
        
        TextButton(onClick = onNavigateToLogin, enabled = !isLoading) {
            Text("Zaten hesabınız var mı? Giriş Yapın")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    LawAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RegisterScreenContent(
                selectedRole = "CLIENT",
                fullName = "",
                email = "",
                password = "",
                phone = "",
                onFullNameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onPhoneChange = {},
                onRegisterClick = {},
                onNavigateToLogin = {},
                isLoading = false
            )
        }
    }
}
