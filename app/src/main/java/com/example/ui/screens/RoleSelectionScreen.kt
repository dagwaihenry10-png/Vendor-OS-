package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.nigeria.NigeriaData
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    currentMode: String,
    currentState: String,
    currentLga: String,
    currentArea: String,
    currentBusinessName: String,
    onConfirmed: (mode: String, state: String, lga: String, area: String, businessName: String) -> Unit
) {
    var selectedMode by remember { mutableStateOf(currentMode.ifBlank { "both" }) }
    var selectedState by remember { mutableStateOf(currentState.ifBlank { "Lagos" }) }
    var selectedLGA by remember { mutableStateOf(currentLga.ifBlank { "Ikeja" }) }
    var areaInput by remember { mutableStateOf(currentArea.ifBlank { "Computer Village" }) }
    var businessNameInput by remember { mutableStateOf(currentBusinessName.ifBlank { "My Business Hub" }) }

    var stateExpanded by remember { mutableStateOf(false) }
    var lgaExpanded by remember { mutableStateOf(false) }

    val lgaList = remember(selectedState) {
        NigeriaData.getLGAs(selectedState)
    }

    LaunchedEffect(selectedState) {
        if (!lgaList.contains(selectedLGA)) {
            selectedLGA = lgaList.firstOrNull() ?: "Central"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(VendorGreen, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("V", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Welcome to VendorOS",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            "Choose how you want to use the super app",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            item {
                Text(
                    "SELECT YOUR ACTIVE MODE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
            }

            // Mode 1: Vendor Mode
            item {
                ModeCard(
                    title = "Vendor Mode",
                    subtitle = "I sell on WhatsApp & Instagram. I need Auto-Reply + Trust Badge & Verified Proofs.",
                    badge = "AUTO REPLY + TRUST",
                    icon = Icons.Default.Chat,
                    accentColor = VendorGreen,
                    isSelected = selectedMode == "vendor",
                    onClick = { selectedMode = "vendor" }
                )
            }

            // Mode 2: Skills Mode
            item {
                ModeCard(
                    title = "Skills Mode (HandworkNG)",
                    subtitle = "I want to learn or teach handwork. Find verified craft masters near me.",
                    badge = "FIND / TEACH MASTERS",
                    icon = Icons.Default.Build,
                    accentColor = SkillsOrange,
                    isSelected = selectedMode == "skills",
                    onClick = { selectedMode = "skills" }
                )
            }

            // Mode 3: Super Mode
            item {
                ModeCard(
                    title = "Super Mode (3-in-1)",
                    subtitle = "I do both! I run an online business and I also teach/practice skilled craft.",
                    badge = "RECOMMENDED COMBO",
                    icon = Icons.Default.Star,
                    accentColor = SuperBlueLight,
                    isSelected = selectedMode == "both",
                    onClick = { selectedMode = "both" }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "BUSINESS & LOCATION SETUP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
            }

            item {
                OutlinedTextField(
                    value = businessNameInput,
                    onValueChange = { businessNameInput = it },
                    label = { Text("Business or Workshop Name") },
                    placeholder = { Text("e.g. Lagos Prime Couture") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VendorGreen,
                        unfocusedBorderColor = NavyBorder,
                        focusedContainerColor = NavyCard,
                        unfocusedContainerColor = NavyCard
                    )
                )
            }

            // State Selector
            item {
                ExposedDropdownMenuBox(
                    expanded = stateExpanded,
                    onExpandedChange = { stateExpanded = !stateExpanded }
                ) {
                    OutlinedTextField(
                        value = "$selectedState State",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("State (36 States + FCT)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VendorGreen,
                            unfocusedBorderColor = NavyBorder,
                            focusedContainerColor = NavyCard,
                            unfocusedContainerColor = NavyCard
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = stateExpanded,
                        onDismissRequest = { stateExpanded = false },
                        modifier = Modifier.background(NavyCard)
                    ) {
                        NigeriaData.getStates().forEach { state ->
                            DropdownMenuItem(
                                text = { Text(state, color = Color.White) },
                                onClick = {
                                    selectedState = state
                                    stateExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // LGA Selector
            item {
                ExposedDropdownMenuBox(
                    expanded = lgaExpanded,
                    onExpandedChange = { lgaExpanded = !lgaExpanded }
                ) {
                    OutlinedTextField(
                        value = "$selectedLGA LGA",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Local Government Area (LGA)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lgaExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VendorGreen,
                            unfocusedBorderColor = NavyBorder,
                            focusedContainerColor = NavyCard,
                            unfocusedContainerColor = NavyCard
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = lgaExpanded,
                        onDismissRequest = { lgaExpanded = false },
                        modifier = Modifier.background(NavyCard)
                    ) {
                        lgaList.forEach { lga ->
                            DropdownMenuItem(
                                text = { Text(lga, color = Color.White) },
                                onClick = {
                                    selectedLGA = lga
                                    lgaExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Area / Landmark
            item {
                OutlinedTextField(
                    value = areaInput,
                    onValueChange = { areaInput = it },
                    label = { Text("Area / Landmark / Street") },
                    placeholder = { Text("e.g. Toyin St, Computer Village, Lekki Phase 1") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VendorGreen,
                        unfocusedBorderColor = NavyBorder,
                        focusedContainerColor = NavyCard,
                        unfocusedContainerColor = NavyCard
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        onConfirmed(
                            selectedMode,
                            selectedState,
                            selectedLGA,
                            areaInput.ifBlank { "Central" },
                            businessNameInput.ifBlank { "VendorOS Business" }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (selectedMode) {
                            "vendor" -> VendorGreen
                            "skills" -> SkillsOrange
                            else -> SuperBlueLight
                        },
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        "Save & Enter VendorOS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(
                if (isSelected) Modifier.border(2.dp, accentColor, RoundedCornerShape(20.dp))
                else Modifier.border(1.dp, NavyBorder, RoundedCornerShape(20.dp))
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(accentColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            badge,
                            color = accentColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    subtitle,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = accentColor,
                    unselectedColor = TextMuted
                )
            )
        }
    }
}
