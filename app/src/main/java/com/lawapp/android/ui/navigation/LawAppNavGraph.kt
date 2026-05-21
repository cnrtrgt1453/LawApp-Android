package com.lawapp.android.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
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

// --- Route Tanımları ---
object Routes {
    const val ROLE_SELECTION = "role_selection"
    const val LOGIN = "login/{role}"
    const val REGISTER = "register/{role}"
    const val LAWYER_HOME = "lawyer_home"
    const val CLIENT_HOME = "client_home"
    const val LEAD_DETAIL = "lead_detail/{leadId}"
    const val LEAD_BIDS = "lead_bids/{leadId}"
    const val LAWYER_PROFILE = "lawyer_profile"
    const val CLIENT_PROFILE = "client_profile"
    const val WALLET = "wallet"
    const val TEMPLATES = "templates"
    const val CREATE_LEAD = "create_lead"
}

// --- Bottom Navigation Item ---
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val lawyerBottomNavItems = listOf(
    BottomNavItem("İş Havuzu", Icons.Default.Search, Routes.LAWYER_HOME),
    BottomNavItem("Şablonlar", Icons.Default.Description, Routes.TEMPLATES),
    BottomNavItem("Cüzdan", Icons.Default.AccountBalanceWallet, Routes.WALLET),
    BottomNavItem("Profil", Icons.Default.Person, Routes.LAWYER_PROFILE)
)

val clientBottomNavItems = listOf(
    BottomNavItem("İlanlarım", Icons.Default.List, Routes.CLIENT_HOME),
    BottomNavItem("Yeni İlan", Icons.Default.Add, Routes.CREATE_LEAD),
    BottomNavItem("Profil", Icons.Default.Person, Routes.CLIENT_PROFILE)
)

// --- Lawyer Home with Bottom Nav ---
@Composable
fun LawyerScaffold(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val viewModel: LawyerViewModel = viewModel()
    val leads by viewModel.leads.collectAsState()
    val templates by viewModel.templates.collectAsState()

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
                // API'den gelen Lead DTO'larını UI modeline çevir
                val uiLeads = leads.map { dto ->
                    Lead(dto.id, dto.title, dto.category, dto.city, dto.description)
                }
                LeadFeedScreen(
                    leads = uiLeads,
                    onLeadClick = { lead ->
                        navController.navigate("lead_detail/${lead.id}")
                    }
                )
            }
            composable(
                Routes.LEAD_DETAIL,
                arguments = listOf(navArgument("leadId") { type = NavType.LongType })
            ) { backStackEntry ->
                val leadId = backStackEntry.arguments?.getLong("leadId") ?: 0L
                // leads listesinden ilgili lead'i bul
                val leadDto = leads.find { it.id == leadId }
                val lead = leadDto?.let {
                    Lead(it.id, it.title, it.category, it.city, it.description)
                } ?: Lead(leadId, "", "", "", "")

                val uiTemplates = templates.map { BidTemplateUI(it.id, it.title, it.content) }

                LeadDetailScreen(
                    lead = lead,
                    templates = uiTemplates,
                    onBidSubmit = { message ->
                        viewModel.placeBid(leadId, message)
                        navController.popBackStack()
                    }
                )
            }
            composable(Routes.TEMPLATES) {
                val uiTemplates = templates.map { BidTemplateUI(it.id, it.title, it.content) }
                TemplatesScreen(
                    templates = uiTemplates,
                    onCreateTemplate = { title, content -> viewModel.createTemplate(title, content) },
                    onDeleteTemplate = { id -> viewModel.deleteTemplate(id) }
                )
            }
            composable(Routes.WALLET) {
                WalletScreen(
                    currentBalance = 250, // TODO: API'den kredi bakiyesi çekilecek
                    onPackageClick = { pkg -> /* Ödeme akışı */ }
                )
            }
            composable(Routes.LAWYER_PROFILE) {
                LawyerProfileScreen()
            }
        }
    }
}

// --- Client Home with Bottom Nav ---
@Composable
fun ClientScaffold(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val viewModel: ClientViewModel = viewModel()
    val myLeads by viewModel.myLeads.collectAsState()
    val bids by viewModel.bids.collectAsState()

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
                        viewModel.fetchBidsForLead(lead.id)
                        navController.navigate("lead_bids/${lead.id}")
                    }
                )
            }
            composable(
                Routes.LEAD_BIDS,
                arguments = listOf(navArgument("leadId") { type = NavType.LongType })
            ) { backStackEntry ->
                val leadId = backStackEntry.arguments?.getLong("leadId") ?: 0L
                val leadTitle = myLeads.find { it.id == leadId }?.title ?: "İlan #$leadId"

                val uiBids = bids.map { dto ->
                    BidUI(
                        id = dto.id,
                        lawyerName = dto.lawyer?.fullName ?: "Avukat",
                        message = dto.message,
                        date = dto.createdAt ?: "",
                        phoneNumber = dto.lawyer?.phoneNumber ?: "Bilgi yok",
                        status = dto.status ?: "PENDING"
                    )
                }
                LeadBidsScreen(
                    leadTitle = leadTitle,
                    bids = uiBids,
                    onAcceptBid = { bid -> viewModel.acceptBid(bid.id, leadId) },
                    onContactLawyer = { bid -> /* Telefon araması Intent'i */ }
                )
            }
            composable(Routes.CREATE_LEAD) {
                CreateLeadScreen(
                    onLeadCreated = {
                        viewModel.fetchMyLeads()
                        navController.navigate(Routes.CLIENT_HOME) {
                            popUpTo(Routes.CLIENT_HOME) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.CLIENT_PROFILE) {
                ClientProfileScreen()
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
                    val destination = if (role == "LAWYER") "lawyer_main" else "client_main"
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
                    val destination = if (role == "LAWYER") "lawyer_main" else "client_main"
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
            val lawyerNavController = rememberNavController()
            LawyerScaffold(navController = lawyerNavController)
        }
        composable("client_main") {
            val clientNavController = rememberNavController()
            ClientScaffold(navController = clientNavController)
        }
    }
}
