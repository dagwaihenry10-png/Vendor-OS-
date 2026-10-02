package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.Master
import com.example.ui.screens.skills.*
import com.example.ui.screens.vendor.*
import com.example.ui.theme.*
import com.example.viewmodel.VendorOSViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: VendorOSViewModel,
    onOpenRoleSelection: () -> Unit
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val rules by viewModel.rules.collectAsStateWithLifecycle()
    val proofs by viewModel.proofs.collectAsStateWithLifecycle()
    val masters by viewModel.masters.collectAsStateWithLifecycle()
    val inquiries by viewModel.inquiries.collectAsStateWithLifecycle()
    val chatLogs by viewModel.chatLogs.collectAsStateWithLifecycle()
    val payments by viewModel.payments.collectAsStateWithLifecycle()

    val selectedSkillCat by viewModel.selectedSkillCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedProofTab by viewModel.selectedProofTab.collectAsStateWithLifecycle()
    val selectedInquiryTab by viewModel.selectedInquiryTab.collectAsStateWithLifecycle()
    val selectedPlan by viewModel.selectedPaymentPlan.collectAsStateWithLifecycle()
    val currentPayment by viewModel.currentPayment.collectAsStateWithLifecycle()
    val paymentMsg by viewModel.paymentVerificationState.collectAsStateWithLifecycle()
    val testReplyResult by viewModel.testReplyResult.collectAsStateWithLifecycle()
    val selectedMaster by viewModel.selectedMaster.collectAsStateWithLifecycle()
    val mapRadius by viewModel.mapRadiusKm.collectAsStateWithLifecycle()

    // Navigation Tab state
    var selectedBottomNavIndex by remember { mutableIntStateOf(0) }
    var activeSubMode by remember { mutableStateOf(user.activeMode) }
    var showCreateProofFromFab by remember { mutableStateOf(false) }

    LaunchedEffect(user.activeMode) {
        activeSubMode = user.activeMode
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    when (activeSubMode) {
                                        "vendor" -> VendorGreen
                                        "skills" -> SkillsOrange
                                        else -> SuperBlueLight
                                    },
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("V", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "VendorOS",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            when (activeSubMode) {
                                                "vendor" -> VendorGreen.copy(alpha = 0.2f)
                                                "skills" -> SkillsOrange.copy(alpha = 0.2f)
                                                else -> SuperBlueLight.copy(alpha = 0.2f)
                                            },
                                            RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        when (activeSubMode) {
                                            "vendor" -> "VENDOR MODE"
                                            "skills" -> "SKILLS MODE"
                                            else -> "SUPER 3-IN-1"
                                        },
                                        color = when (activeSubMode) {
                                            "vendor" -> VendorGreen
                                            "skills" -> SkillsOrange
                                            else -> Color(0xFF60A5FA)
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            Text(
                                "${user.selectedState} · ${user.selectedLGA}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                },
                actions = {
                    // Quick Mode Switcher Dropdown / Chip
                    FilledTonalButton(
                        onClick = onOpenRoleSelection,
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = NavyCard, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = "Switch Mode", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Modes", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = { viewModel.toggleAutoReply(!user.autoReplyOn) }) {
                        Icon(
                            imageVector = if (user.autoReplyOn) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                            contentDescription = "Auto Reply Toggle",
                            tint = if (user.autoReplyOn) VendorGreen else TextMuted
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyBg
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavyCard,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                if (activeSubMode == "vendor") {
                    // 4 Vendor Tabs
                    NavigationBarItem(
                        selected = selectedBottomNavIndex == 0,
                        onClick = { selectedBottomNavIndex = 0 },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = VendorGreen,
                            indicatorColor = VendorGreen,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = selectedBottomNavIndex == 1,
                        onClick = { selectedBottomNavIndex = 1 },
                        icon = { Icon(Icons.Default.SmartToy, contentDescription = "Rules") },
                        label = { Text("Auto Rules") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = VendorGreen,
                            indicatorColor = VendorGreen,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = selectedBottomNavIndex == 2,
                        onClick = { selectedBottomNavIndex = 2 },
                        icon = { Icon(Icons.Default.Security, contentDescription = "Trust & Proofs") },
                        label = { Text("Proofs") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = VendorGreen,
                            indicatorColor = VendorGreen,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = selectedBottomNavIndex == 3,
                        onClick = { selectedBottomNavIndex = 3 },
                        icon = { Icon(Icons.Default.CreditCard, contentDescription = "Secure OPay") },
                        label = { Text("Secure Pay") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = VendorGreen,
                            indicatorColor = VendorGreen,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                } else if (activeSubMode == "skills") {
                    // 4 Skills Tabs
                    NavigationBarItem(
                        selected = selectedBottomNavIndex == 0,
                        onClick = { selectedBottomNavIndex = 0 },
                        icon = { Icon(Icons.Default.Explore, contentDescription = "Directory") },
                        label = { Text("Handwork") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = SkillsOrange,
                            indicatorColor = SkillsOrange,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = selectedBottomNavIndex == 1,
                        onClick = { selectedBottomNavIndex = 1 },
                        icon = { Icon(Icons.Default.Map, contentDescription = "Radar Map") },
                        label = { Text("Radar Map") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = SkillsOrange,
                            indicatorColor = SkillsOrange,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = selectedBottomNavIndex == 2,
                        onClick = { selectedBottomNavIndex = 2 },
                        icon = { Icon(Icons.Default.ForwardToInbox, contentDescription = "Inquiries") },
                        label = { Text("Inquiries") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = SkillsOrange,
                            indicatorColor = SkillsOrange,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = selectedBottomNavIndex == 3,
                        onClick = { selectedBottomNavIndex = 3 },
                        icon = { Icon(Icons.Default.Storefront, contentDescription = "Master Hub") },
                        label = { Text("Master Hub") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = SkillsOrange,
                            indicatorColor = SkillsOrange,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                } else {
                    // Super Mode (3-in-1 combo with 5 tabs)
                    NavigationBarItem(
                        selected = selectedBottomNavIndex == 0,
                        onClick = { selectedBottomNavIndex = 0 },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Vendor Dashboard") },
                        label = { Text("Vendor") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = VendorGreen,
                            indicatorColor = VendorGreen,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = selectedBottomNavIndex == 1,
                        onClick = { selectedBottomNavIndex = 1 },
                        icon = { Icon(Icons.Default.SmartToy, contentDescription = "Auto Rules") },
                        label = { Text("Rules") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = VendorGreen,
                            indicatorColor = VendorGreen,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = selectedBottomNavIndex == 2,
                        onClick = { selectedBottomNavIndex = 2 },
                        icon = { Icon(Icons.Default.VerifiedUser, contentDescription = "Proofs") },
                        label = { Text("Proofs") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = VendorGreen,
                            indicatorColor = VendorGreen,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = selectedBottomNavIndex == 3,
                        onClick = { selectedBottomNavIndex = 3 },
                        icon = { Icon(Icons.Default.Handyman, contentDescription = "Handwork") },
                        label = { Text("Handwork") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = SkillsOrange,
                            indicatorColor = SkillsOrange,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )

                    NavigationBarItem(
                        selected = selectedBottomNavIndex == 4,
                        onClick = { selectedBottomNavIndex = 4 },
                        icon = { Icon(Icons.Default.Lock, contentDescription = "OPay Pay") },
                        label = { Text("Secure Pay") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = SuperBlueLight,
                            indicatorColor = SuperBlueLight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        },
        containerColor = NavyBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeSubMode) {
                "vendor" -> {
                    when (selectedBottomNavIndex) {
                        0 -> VendorDashboardTab(
                            user = user,
                            proofs = proofs,
                            logs = chatLogs,
                            onToggleAutoReply = { viewModel.toggleAutoReply(it) },
                            onCreateProofClick = { selectedBottomNavIndex = 2 },
                            onViewPublicTrustPage = { selectedBottomNavIndex = 2 },
                            onTestMessageSubmit = { viewModel.testMessageSimulator(user.name, it) },
                            testReplyResult = testReplyResult
                        )
                        1 -> VendorRulesTab(
                            user = user,
                            rules = rules,
                            chatLogs = chatLogs,
                            onAddRule = { kw, reply, type -> viewModel.addRule(kw, reply, type) },
                            onToggleRule = { viewModel.toggleRuleActive(it) },
                            onDeleteRule = { viewModel.deleteRule(it) },
                            onUpdateWorkingHours = { s, e -> viewModel.updateWorkingHours(s, e) }
                        )
                        2 -> VendorProofsTab(
                            proofs = proofs,
                            selectedTab = selectedProofTab,
                            onTabSelected = { viewModel.setProofTab(it) },
                            onCreateProof = { name, phone, photo, comm -> viewModel.createProof(name, phone, photo, comm) },
                            onConfirmProof = { viewModel.confirmProof(it) },
                            onDeleteProof = { viewModel.deleteProof(it) }
                        )
                        else -> VendorSettingsTab(
                            user = user,
                            currentPayment = currentPayment,
                            payments = payments,
                            selectedPlan = selectedPlan,
                            verificationMessage = paymentMsg,
                            onPlanSelected = { viewModel.setPaymentPlan(it) },
                            onPaySecurelyNow = { viewModel.launchPayNow() },
                            onVerifyPaymentNow = { viewModel.verifyPaymentNow() },
                            onWhatsAppSupport = { viewModel.launchWhatsAppHelp() },
                            onChangeLocation = onOpenRoleSelection
                        )
                    }
                }
                "skills" -> {
                    when (selectedBottomNavIndex) {
                        0 -> SkillsHomeTab(
                            user = user,
                            masters = masters,
                            selectedCategory = selectedSkillCat,
                            searchQuery = searchQuery,
                            onCategorySelected = { viewModel.setSkillCategory(it) },
                            onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                            onMasterClicked = { viewModel.selectMaster(it) },
                            onSwitchToMap = { selectedBottomNavIndex = 1 },
                            onBecomeMasterClick = { selectedBottomNavIndex = 3 }
                        )
                        1 -> SkillsMapTab(
                            user = user,
                            masters = masters,
                            radiusKm = mapRadius,
                            onRadiusChange = { viewModel.setMapRadius(it) },
                            onMasterSelected = { viewModel.selectMaster(it) }
                        )
                        2 -> SkillsInquiriesTab(
                            user = user,
                            inquiries = inquiries,
                            selectedTab = selectedInquiryTab,
                            onTabSelected = { viewModel.setInquiryTab(it) },
                            onUpdateInquiryStatus = { id, st -> viewModel.updateInquiryStatus(id, st) }
                        )
                        else -> MasterDashboardTab(
                            user = user,
                            masters = masters,
                            onBecomeMasterSubmit = { b, o, c, s, y, d, st, l, ar, ad, m, w, ha, af, p, wa ->
                                viewModel.becomeMaster(b, o, c, s, y, d, st, l, ar, ad, m, w, ha, af, p, wa)
                            },
                            onPromoteShop = { viewModel.promoteMasterShop(it) }
                        )
                    }
                }
                else -> {
                    // Super Mode (3-in-1 combo)
                    when (selectedBottomNavIndex) {
                        0 -> VendorDashboardTab(
                            user = user,
                            proofs = proofs,
                            logs = chatLogs,
                            onToggleAutoReply = { viewModel.toggleAutoReply(it) },
                            onCreateProofClick = { selectedBottomNavIndex = 2 },
                            onViewPublicTrustPage = { selectedBottomNavIndex = 2 },
                            onTestMessageSubmit = { viewModel.testMessageSimulator(user.name, it) },
                            testReplyResult = testReplyResult
                        )
                        1 -> VendorRulesTab(
                            user = user,
                            rules = rules,
                            chatLogs = chatLogs,
                            onAddRule = { kw, reply, type -> viewModel.addRule(kw, reply, type) },
                            onToggleRule = { viewModel.toggleRuleActive(it) },
                            onDeleteRule = { viewModel.deleteRule(it) },
                            onUpdateWorkingHours = { s, e -> viewModel.updateWorkingHours(s, e) }
                        )
                        2 -> VendorProofsTab(
                            proofs = proofs,
                            selectedTab = selectedProofTab,
                            onTabSelected = { viewModel.setProofTab(it) },
                            onCreateProof = { name, phone, photo, comm -> viewModel.createProof(name, phone, photo, comm) },
                            onConfirmProof = { viewModel.confirmProof(it) },
                            onDeleteProof = { viewModel.deleteProof(it) }
                        )
                        3 -> SkillsHomeTab(
                            user = user,
                            masters = masters,
                            selectedCategory = selectedSkillCat,
                            searchQuery = searchQuery,
                            onCategorySelected = { viewModel.setSkillCategory(it) },
                            onSearchQueryChanged = { viewModel.setSearchQuery(it) },
                            onMasterClicked = { viewModel.selectMaster(it) },
                            onSwitchToMap = {},
                            onBecomeMasterClick = {}
                        )
                        else -> VendorSettingsTab(
                            user = user,
                            currentPayment = currentPayment,
                            payments = payments,
                            selectedPlan = selectedPlan,
                            verificationMessage = paymentMsg,
                            onPlanSelected = { viewModel.setPaymentPlan(it) },
                            onPaySecurelyNow = { viewModel.launchPayNow() },
                            onVerifyPaymentNow = { viewModel.verifyPaymentNow() },
                            onWhatsAppSupport = { viewModel.launchWhatsAppHelp() },
                            onChangeLocation = onOpenRoleSelection
                        )
                    }
                }
            }

            // Master Detail Sheet Modal (if selected)
            selectedMaster?.let { master ->
                MasterDetailSheet(
                    master = master,
                    onDismiss = { viewModel.selectMaster(null) },
                    onRequestToLearn = { msg -> viewModel.sendInquiry(master, msg) }
                )
            }
        }
    }
}
