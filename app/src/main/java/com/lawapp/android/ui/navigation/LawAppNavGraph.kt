package com.lawapp.android.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.lawapp.android.ui.auth.AuthViewModel
import com.lawapp.android.ui.auth.LoginScreen
import com.lawapp.android.ui.auth.RegisterScreen
import com.lawapp.android.ui.auth.RoleSelectionScreen
import com.lawapp.android.ui.client.*
import com.lawapp.android.ui.lawyer.*
import com.lawapp.android.ui.leads.CreateLeadScreen
import com.lawapp.android.ui.chat.*
import com.lawapp.android.ui.common.*

// --- Route Tanımları ---
object Routes {
    const val ROLE_SELECTION = "role_selection"
    const val LOGIN = "login/{role}"
    const val REGISTER = "register/{role}"
    const val LAWYER_HOME = "lawyer_home"
    const val CLIENT_HOME = "client_home"
    const val CALENDAR = "calendar_management"
    const val SPECIALIZED_LAWYERS = "specialized_lawyers/{leadId}/{leadTitle}"
    const val LAWYER_DETAILS = "lawyer_details/{lawyerId}/{leadId}"
    const val PAYMENT_CHECKOUT = "payment_checkout/{lawyerId}/{leadId}/{slotTime}"
    const val CLIENT_APPOINTMENTS = "client_appointments"
    const val LAWYER_PROFILE = "lawyer_profile"
    const val CLIENT_PROFILE = "client_profile"
    const val WALLET = "wallet"
    const val CREATE_LEAD = "create_lead"
    const val CHAT_LIST = "chat_list"
    const val CHAT_DETAIL = "chat_detail/{sessionId}/{partnerName}/{partnerRole}/{leadTitle}"
    const val VIDEO_CALL = "video_call/{partnerName}"
}

// --- Bottom Navigation Item ---
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val lawyerBottomNavItems = listOf(
    BottomNavItem("Randevu", Icons.Default.DateRange, Routes.LAWYER_HOME),
    BottomNavItem("Takvimim", Icons.Default.CalendarMonth, Routes.CALENDAR),
    BottomNavItem("Mesajlar", Icons.Default.Chat, Routes.CHAT_LIST),
    BottomNavItem("Cüzdan", Icons.Default.AccountBalanceWallet, Routes.WALLET),
    BottomNavItem("Profil", Icons.Default.Person, Routes.LAWYER_PROFILE)
)

val clientBottomNavItems = listOf(
    BottomNavItem("İlanlarım", Icons.Default.List, Routes.CLIENT_HOME),
    BottomNavItem("Yeni İlan", Icons.Default.Add, Routes.CREATE_LEAD),
    BottomNavItem("Randevu", Icons.Default.DateRange, Routes.CLIENT_APPOINTMENTS),
    BottomNavItem("Mesajlar", Icons.Default.Chat, Routes.CHAT_LIST),
    BottomNavItem("Profil", Icons.Default.Person, Routes.CLIENT_PROFILE)
)

// --- Lawyer Home with Bottom Nav ---
@Composable
fun LawyerScaffold(
    navController: NavHostController,
    onStartCall: (String) -> Unit,
    onLogout: () -> Unit = {}
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val viewModel: LawyerViewModel = hiltViewModel()
    val appointments by viewModel.appointments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                lawyerBottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Routes.LAWYER_HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LAWYER_HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.LAWYER_HOME) {
                AppointmentsListScreen(
                    role = "LAWYER",
                    appointments = appointments,
                    isLoading = isLoading,
                    onAcceptClick = { id -> viewModel.acceptAppointment(id) },
                    onRejectClick = { id -> viewModel.rejectAppointment(id) },
                    onRefresh = { viewModel.fetchAppointments() }
                )
            }
            composable(Routes.CALENDAR) {
                CalendarManagementScreen(viewModel = viewModel)
            }
            composable(Routes.CHAT_LIST) {
                val chatViewModel: ChatViewModel = hiltViewModel()
                val sessions by chatViewModel.chatSessions.collectAsState()
                val chatLoading by chatViewModel.isLoading.collectAsState()

                LaunchedEffect(Unit) {
                    chatViewModel.fetchChatSessions()
                }

                ChatListScreen(
                    sessions = sessions,
                    isLoading = chatLoading,
                    onSessionClick = { session ->
                        navController.navigate("chat_detail/${session.id}/${session.otherParticipantName}/${session.otherParticipantRole}/${session.leadTitle}")
                    }
                )
            }
            composable(
                Routes.CHAT_DETAIL,
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.LongType },
                    navArgument("partnerName") { type = NavType.StringType },
                    navArgument("partnerRole") { type = NavType.StringType },
                    navArgument("leadTitle") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
                val partnerName = backStackEntry.arguments?.getString("partnerName") ?: ""
                val partnerRole = backStackEntry.arguments?.getString("partnerRole") ?: ""
                val leadTitle = backStackEntry.arguments?.getString("leadTitle") ?: ""

                val chatViewModel: ChatViewModel = hiltViewModel()
                val messages by chatViewModel.activeMessages.collectAsState()

                LaunchedEffect(sessionId) {
                    chatViewModel.loadMessages(sessionId)
                }

                ChatDetailScreen(
                    partnerName = partnerName,
                    partnerRole = partnerRole,
                    leadTitle = leadTitle,
                    messages = messages,
                    onSendMessage = { chatViewModel.sendMessage(it) },
                    onBackClick = {
                        chatViewModel.closeChat()
                        navController.popBackStack()
                    }
                )
            }
            composable(Routes.WALLET) {
                WalletScreen(
                    currentBalance = 250,
                    onPackageClick = { pkg -> /* Ödeme akışı */ }
                )
            }
            composable(Routes.LAWYER_PROFILE) {
                LawyerProfileScreen(onLogout = onLogout)
            }
        }
    }
}

// --- Client Home with Bottom Nav ---
@Composable
fun ClientScaffold(
    navController: NavHostController,
    onStartCall: (String) -> Unit,
    onLogout: () -> Unit = {}
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val viewModel: ClientViewModel = hiltViewModel()
    val myLeads by viewModel.myLeads.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                clientBottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Routes.CLIENT_HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.CLIENT_HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.CLIENT_HOME) {
                val uiLeads = myLeads.map { dto ->
                    Lead(dto.id, dto.title, dto.category, dto.city, dto.description)
                }
                ClientLeadsScreen(
                    myLeads = uiLeads,
                    onLeadClick = { lead ->
                        navController.navigate("specialized_lawyers/${lead.id}/${lead.title}")
                    }
                )
            }
            composable(
                Routes.SPECIALIZED_LAWYERS,
                arguments = listOf(
                    navArgument("leadId") { type = NavType.LongType },
                    navArgument("leadTitle") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val leadId = backStackEntry.arguments?.getLong("leadId") ?: 0L
                val leadTitle = backStackEntry.arguments?.getString("leadTitle") ?: ""
                SpecializedLawyersScreen(
                    leadId = leadId,
                    leadTitle = leadTitle,
                    viewModel = viewModel,
                    onLawyerClick = { lawyerId ->
                        navController.navigate("lawyer_details/$lawyerId/$leadId")
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(
                Routes.LAWYER_DETAILS,
                arguments = listOf(
                    navArgument("lawyerId") { type = NavType.LongType },
                    navArgument("leadId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val lawyerId = backStackEntry.arguments?.getLong("lawyerId") ?: 0L
                val leadId = backStackEntry.arguments?.getLong("leadId") ?: 0L
                LawyerDetailsScreen(
                    lawyerId = lawyerId,
                    leadId = leadId,
                    viewModel = viewModel,
                    onSlotSelected = { slotTime ->
                        navController.navigate("payment_checkout/$lawyerId/$leadId/$slotTime")
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(
                Routes.PAYMENT_CHECKOUT,
                arguments = listOf(
                    navArgument("lawyerId") { type = NavType.LongType },
                    navArgument("leadId") { type = NavType.LongType },
                    navArgument("slotTime") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val lawyerId = backStackEntry.arguments?.getLong("lawyerId") ?: 0L
                val leadId = backStackEntry.arguments?.getLong("leadId") ?: 0L
                val slotTime = backStackEntry.arguments?.getString("slotTime") ?: ""
                PaymentCheckoutScreen(
                    lawyerId = lawyerId,
                    leadId = leadId,
                    slotTime = slotTime,
                    viewModel = viewModel,
                    onPaymentSuccess = {
                        navController.navigate(Routes.CLIENT_APPOINTMENTS) {
                            popUpTo(Routes.CLIENT_HOME) { saveState = false }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Routes.CLIENT_APPOINTMENTS) {
                AppointmentsListScreen(
                    role = "CLIENT",
                    appointments = appointments,
                    isLoading = isLoading,
                    onStartCall = { app -> onStartCall(app.lawyerName) },
                    onRefresh = { viewModel.fetchAppointments() }
                )
            }
            composable(Routes.CREATE_LEAD) {
                CreateLeadScreen(
                    viewModel = viewModel,
                    onLeadCreated = {
                        navController.navigate(Routes.CLIENT_HOME) {
                            popUpTo(Routes.CLIENT_HOME) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.CHAT_LIST) {
                val chatViewModel: ChatViewModel = hiltViewModel()
                val sessions by chatViewModel.chatSessions.collectAsState()
                val chatLoading by chatViewModel.isLoading.collectAsState()

                LaunchedEffect(Unit) {
                    chatViewModel.fetchChatSessions()
                }

                ChatListScreen(
                    sessions = sessions,
                    isLoading = chatLoading,
                    onSessionClick = { session ->
                        navController.navigate("chat_detail/${session.id}/${session.otherParticipantName}/${session.otherParticipantRole}/${session.leadTitle}")
                    }
                )
            }
            composable(
                Routes.CHAT_DETAIL,
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.LongType },
                    navArgument("partnerName") { type = NavType.StringType },
                    navArgument("partnerRole") { type = NavType.StringType },
                    navArgument("leadTitle") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
                val partnerName = backStackEntry.arguments?.getString("partnerName") ?: ""
                val partnerRole = backStackEntry.arguments?.getString("partnerRole") ?: ""
                val leadTitle = backStackEntry.arguments?.getString("leadTitle") ?: ""

                val chatViewModel: ChatViewModel = hiltViewModel()
                val messages by chatViewModel.activeMessages.collectAsState()

                LaunchedEffect(sessionId) {
                    chatViewModel.loadMessages(sessionId)
                }

                ChatDetailScreen(
                    partnerName = partnerName,
                    partnerRole = partnerRole,
                    leadTitle = leadTitle,
                    messages = messages,
                    onSendMessage = { chatViewModel.sendMessage(it) },
                    onBackClick = {
                        chatViewModel.closeChat()
                        navController.popBackStack()
                    }
                )
            }
            composable(Routes.CLIENT_PROFILE) {
                ClientProfileScreen(onLogout = onLogout)
            }
        }
    }
}

// --- Ana Navigation Graph ---
@Composable
fun LawAppNavGraph() {
    val rootNavController = rememberNavController()

    NavHost(navController = rootNavController, startDestination = Routes.ROLE_SELECTION) {
        composable(Routes.ROLE_SELECTION) {
            RoleSelectionScreen(onRoleSelected = { role ->
                rootNavController.navigate("login/$role")
            })
        }
        composable(
            Routes.LOGIN,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "CLIENT"
            LoginScreen(
                onLoginSuccess = {
                    val actualRole = com.lawapp.android.data.TokenManager.role ?: role
                    val destination = if (actualRole == "LAWYER" || actualRole == "ROLE_LAWYER") "lawyer_main" else "client_main"
                    rootNavController.navigate(destination) {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    rootNavController.navigate("register/$role")
                }
            )
        }
        composable(
            Routes.REGISTER,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "CLIENT"
            RegisterScreen(
                selectedRole = role,
                onRegisterSuccess = {
                    val actualRole = com.lawapp.android.data.TokenManager.role ?: role
                    val destination = if (actualRole == "LAWYER" || actualRole == "ROLE_LAWYER") "lawyer_main" else "client_main"
                    rootNavController.navigate(destination) {
                        popUpTo(Routes.ROLE_SELECTION) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    rootNavController.popBackStack()
                }
            )
        }
        composable("lawyer_main") {
            LawyerScaffold(
                navController = rememberNavController(),
                onStartCall = { partnerName ->
                    rootNavController.navigate("video_call/$partnerName")
                },
                onLogout = {
                    com.lawapp.android.data.TokenManager.clear()
                    rootNavController.navigate(Routes.ROLE_SELECTION) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("client_main") {
            ClientScaffold(
                navController = rememberNavController(),
                onStartCall = { partnerName ->
                    rootNavController.navigate("video_call/$partnerName")
                },
                onLogout = {
                    com.lawapp.android.data.TokenManager.clear()
                    rootNavController.navigate(Routes.ROLE_SELECTION) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(
            Routes.VIDEO_CALL,
            arguments = listOf(navArgument("partnerName") { type = NavType.StringType })
        ) { backStackEntry ->
            val partnerName = backStackEntry.arguments?.getString("partnerName") ?: "Görüşme"
            VideoCallScreen(
                partnerName = partnerName,
                onEndCall = { rootNavController.popBackStack() }
            )
        }
    }
}
