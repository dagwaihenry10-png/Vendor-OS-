package com.example.ui.screens.skills

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Master
import com.example.data.models.User
import com.example.data.nigeria.NigeriaData
import com.example.utils.SecureConfig
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterDashboardTab(
    user: User,
    masters: List<Master>,
    onBecomeMasterSubmit: (
        bizName: String,
        owner: String,
        category: String,
        skillName: String,
        years: Int,
        desc: String,
        state: String,
        lga: String,
        area: String,
        address: String,
        monthly: Int,
        weekly: Int?,
        hasAccom: Boolean,
        accomFee: Int,
        phone: String,
        whatsapp: String
    ) -> Unit,
    onPromoteShop: (String) -> Unit
) {
    val context = LocalContext.current
    val currentMasterProfile = remember(masters, user.uid) {
        masters.find { it.userId == user.uid }
    }

    var currentStep by remember { mutableIntStateOf(1) }

    // Form states
    var bizNameInput by remember { mutableStateOf(user.businessName) }
    var ownerNameInput by remember { mutableStateOf(user.name) }
    var skillCategoryInput by remember { mutableStateOf("Barber") }
    var skillNameInput by remember { mutableStateOf("") }
    var yearsExpInput by remember { mutableStateOf("5") }
    var descriptionInput by remember { mutableStateOf("") }

    var stateInput by remember { mutableStateOf(user.selectedState) }
    var lgaInput by remember { mutableStateOf(user.selectedLGA) }
    var areaInput by remember { mutableStateOf(user.selectedArea) }
    var addressInput by remember { mutableStateOf("") }

    var monthlyInput by remember { mutableStateOf("5000") }
    var weeklyInput by remember { mutableStateOf("1500") }
    var hasAccomInput by remember { mutableStateOf(false) }
    var accomFeeInput by remember { mutableStateOf("5000") }

    var phoneInput by remember { mutableStateOf(user.phone) }
    var whatsappInput by remember { mutableStateOf("2348123456789") }

    var stateDropdownExpanded by remember { mutableStateOf(false) }
    var lgaDropdownExpanded by remember { mutableStateOf(false) }
    var skillDropdownExpanded by remember { mutableStateOf(false) }

    val lgas = remember(stateInput) {
        NigeriaData.getLGAs(stateInput)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        if (currentMasterProfile != null) {
            // ==========================================
            // ALREADY A MASTER: DASHBOARD STATS & PROMOTION
            // ==========================================
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, SkillsOrange.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    currentMasterProfile.businessName,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                                Text(
                                    "${currentMasterProfile.skillCategory} · ${currentMasterProfile.area}, ${currentMasterProfile.state}",
                                    fontSize = 12.sp,
                                    color = SkillsOrange
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(if (currentMasterProfile.isPromoted) SkillsOrange else Color(0xFF1E3A8A), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    if (currentMasterProfile.isPromoted) "BOOST ACTIVE" else "VERIFIED MASTER",
                                    color = if (currentMasterProfile.isPromoted) Color.Black else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Grid 2x2
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MasterStatBox("Profile Views", "${currentMasterProfile.views}", Icons.Default.Visibility, Color(0xFF38BDF8), Modifier.weight(1f))
                            MasterStatBox("Total Students", "${currentMasterProfile.totalStudents}", Icons.Default.School, VendorEmerald, Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MasterStatBox("Shop Rating", "%.1f ⭐".format(currentMasterProfile.rating), Icons.Default.Star, Color(0xFFFBBF24), Modifier.weight(1f))
                            MasterStatBox("Monthly Rate", "₦%,d".format(currentMasterProfile.pricePerMonth), Icons.Default.PriceCheck, SkillsOrange, Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onPromoteShop(currentMasterProfile.masterId)
                                Toast.makeText(context, "Shop Boost 7 Days activated via secure OPay gateway!", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SkillsOrange, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Promote Shop (7 Days Boost - ₦1,000 via Secure OPay)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        } else {
            // ==========================================
            // NOT A MASTER YET: 4-STEP REGISTRATION WIZARD
            // ==========================================
            item {
                Text(
                    "Become a Handwork Master Artisan",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Register your workshop to train apprentices and receive inquiries.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // Step Progress Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..4).forEach { step ->
                        val isDone = step < currentStep
                        val isCurrent = step == currentStep
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        when {
                                            isDone -> VendorEmerald
                                            isCurrent -> SkillsOrange
                                            else -> NavyCardLight
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$step",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isDone || isCurrent) Color.Black else TextSecondary
                                )
                            }
                            if (step < 4) {
                                Box(
                                    modifier = Modifier
                                        .width(36.dp)
                                        .height(2.dp)
                                        .background(if (isDone) VendorEmerald else NavyBorder)
                                )
                            }
                        }
                    }
                }
            }

            // STEP 1: BUSINESS INFO
            if (currentStep == 1) {
                item {
                    Text("STEP 1: BUSINESS & CRAFT INFO", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                }

                item {
                    OutlinedTextField(
                        value = bizNameInput,
                        onValueChange = { bizNameInput = it },
                        label = { Text("Workshop / Salon / Business Name *") },
                        placeholder = { Text("e.g. Supreme Clipperz Barbershop") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyCard, unfocusedContainerColor = NavyCard)
                    )
                }

                item {
                    OutlinedTextField(
                        value = ownerNameInput,
                        onValueChange = { ownerNameInput = it },
                        label = { Text("Master Artisan Owner Name *") },
                        placeholder = { Text("e.g. Master Emeka Okafor") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyCard, unfocusedContainerColor = NavyCard)
                    )
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = skillDropdownExpanded,
                        onExpandedChange = { skillDropdownExpanded = !skillDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = skillCategoryInput,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Primary Skill Category *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = skillDropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyCard, unfocusedContainerColor = NavyCard)
                        )

                        ExposedDropdownMenu(
                            expanded = skillDropdownExpanded,
                            onDismissRequest = { skillDropdownExpanded = false },
                            modifier = Modifier.background(NavyCard)
                        ) {
                            NigeriaData.allSkills.forEach { skill ->
                                DropdownMenuItem(
                                    text = { Text(skill, color = Color.White) },
                                    onClick = {
                                        skillCategoryInput = skill
                                        skillDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = skillNameInput,
                        onValueChange = { skillNameInput = it },
                        label = { Text("Craft Specialty Details") },
                        placeholder = { Text("e.g. Pro Barbering, Fade & Dreadlocks") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyCard, unfocusedContainerColor = NavyCard)
                    )
                }

                item {
                    OutlinedTextField(
                        value = yearsExpInput,
                        onValueChange = { yearsExpInput = it },
                        label = { Text("Years of Experience") },
                        placeholder = { Text("e.g. 8") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyCard, unfocusedContainerColor = NavyCard)
                    )
                }

                item {
                    OutlinedTextField(
                        value = descriptionInput,
                        onValueChange = { descriptionInput = it },
                        label = { Text("Apprenticeship Course Description") },
                        placeholder = { Text("Describe what learners will be taught, hands-on tools, and duration...") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyCard, unfocusedContainerColor = NavyCard)
                    )
                }
            }

            // STEP 2: LOCATION
            if (currentStep == 2) {
                item {
                    Text("STEP 2: WORKSHOP LOCATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = stateDropdownExpanded,
                        onExpandedChange = { stateDropdownExpanded = !stateDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = stateInput,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("State (36 States + FCT) *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateDropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyCard, unfocusedContainerColor = NavyCard)
                        )

                        ExposedDropdownMenu(
                            expanded = stateDropdownExpanded,
                            onDismissRequest = { stateDropdownExpanded = false },
                            modifier = Modifier.background(NavyCard)
                        ) {
                            NigeriaData.getStates().forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s, color = Color.White) },
                                    onClick = {
                                        stateInput = s
                                        stateDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = lgaDropdownExpanded,
                        onExpandedChange = { lgaDropdownExpanded = !lgaDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = lgaInput,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Local Government Area (LGA) *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = lgaDropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyCard, unfocusedContainerColor = NavyCard)
                        )

                        ExposedDropdownMenu(
                            expanded = lgaDropdownExpanded,
                            onDismissRequest = { lgaDropdownExpanded = false },
                            modifier = Modifier.background(NavyCard)
                        ) {
                            lgas.forEach { lga ->
                                DropdownMenuItem(
                                    text = { Text(lga, color = Color.White) },
                                    onClick = {
                                        lgaInput = lga
                                        lgaDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = areaInput,
                        onValueChange = { areaInput = it },
                        label = { Text("Area / District / Bus Stop *") },
                        placeholder = { Text("e.g. Allen Avenue, Toyin Street") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyCard, unfocusedContainerColor = NavyCard)
                    )
                }

                item {
                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it },
                        label = { Text("Full Physical Address *") },
                        placeholder = { Text("e.g. 14 Toyin Street, Ikeja, Lagos") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyCard, unfocusedContainerColor = NavyCard)
                    )
                }

                item {
                    FilledTonalButton(
                        onClick = {
                            addressInput = "14 Toyin Street, Ikeja, Lagos"
                            areaInput = "Toyin Street"
                            Toast.makeText(context, "GPS Location Auto-Captured!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Use My Current Location (GPS Auto-Fill)")
                    }
                }
            }

            // STEP 3: PRICING & ACCOMMODATION
            if (currentStep == 3) {
                item {
                    Text("STEP 3: PRICING & ACCOMMODATION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                }

                item {
                    OutlinedTextField(
                        value = monthlyInput,
                        onValueChange = { monthlyInput = it },
                        label = { Text("Price Per Month (₦2,000 - ₦50,000) *") },
                        placeholder = { Text("5000") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyCard, unfocusedContainerColor = NavyCard)
                    )
                }

                item {
                    OutlinedTextField(
                        value = weeklyInput,
                        onValueChange = { weeklyInput = it },
                        label = { Text("Price Per Week (Optional)") },
                        placeholder = { Text("1500") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyCard, unfocusedContainerColor = NavyCard)
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Provides Apprentice Accommodation?", fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Check if apprentices can lodge during training", fontSize = 12.sp, color = TextSecondary)
                            }
                            Switch(
                                checked = hasAccomInput,
                                onCheckedChange = { hasAccomInput = it }
                            )
                        }
                    }
                }

                if (hasAccomInput) {
                    item {
                        OutlinedTextField(
                            value = accomFeeInput,
                            onValueChange = { accomFeeInput = it },
                            label = { Text("Accommodation Fee (₦)") },
                            placeholder = { Text("8000") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyCard, unfocusedContainerColor = NavyCard)
                        )
                    }
                }
            }

            // STEP 4: CONTACT & MEDIA
            if (currentStep == 4) {
                item {
                    Text("STEP 4: CONTACT & SHOP PHOTOS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                }

                item {
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Phone Number for Calls *") },
                        placeholder = { Text("+2348031234567") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyCard, unfocusedContainerColor = NavyCard)
                    )
                }

                item {
                    OutlinedTextField(
                        value = whatsappInput,
                        onValueChange = { whatsappInput = it },
                        label = { Text("WhatsApp Contact *") },
                        placeholder = { Text("2348031234567") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyCard, unfocusedContainerColor = NavyCard)
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        modifier = Modifier.fillMaxWidth().border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = SkillsOrange, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("3 Workshop Photos Attached", fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Images ready for verification audit", fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            // Navigation Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Previous", color = TextSecondary)
                        }
                    }

                    Button(
                        onClick = {
                            if (currentStep < 4) {
                                currentStep++
                            } else {
                                onBecomeMasterSubmit(
                                    bizNameInput.ifBlank { "Supreme Artisan Shop" },
                                    ownerNameInput.ifBlank { "Master Craftsman" },
                                    skillCategoryInput,
                                    skillNameInput.ifBlank { "Professional $skillCategoryInput" },
                                    yearsExpInput.toIntOrNull() ?: 5,
                                    descriptionInput.ifBlank { "Comprehensive apprentice hands-on training with certification." },
                                    stateInput,
                                    lgaInput,
                                    areaInput.ifBlank { "Central Area" },
                                    addressInput.ifBlank { "Workshop Hub" },
                                    monthlyInput.toIntOrNull() ?: 5000,
                                    weeklyInput.toIntOrNull(),
                                    hasAccomInput,
                                    accomFeeInput.toIntOrNull() ?: 0,
                                    phoneInput,
                                    whatsappInput
                                )
                                Toast.makeText(context, "Master Shop Created Successfully!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SkillsOrange, contentColor = Color.Black),
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            if (currentStep == 4) "Complete Registration" else "Continue",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }
}

@Composable
private fun MasterStatBox(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyBg),
        modifier = modifier.border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text(title, fontSize = 11.sp, color = TextSecondary)
        }
    }
}
