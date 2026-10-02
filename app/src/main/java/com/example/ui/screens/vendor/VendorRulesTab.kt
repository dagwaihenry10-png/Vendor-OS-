package com.example.ui.screens.vendor

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.models.AutoRule
import com.example.data.models.ChatLog
import com.example.data.models.User
import com.example.ui.theme.*

@Composable
fun VendorRulesTab(
    user: User,
    rules: List<AutoRule>,
    chatLogs: List<ChatLog>,
    onAddRule: (keywords: List<String>, reply: String, type: String) -> Unit,
    onToggleRule: (AutoRule) -> Unit,
    onDeleteRule: (AutoRule) -> Unit,
    onUpdateWorkingHours: (start: String, end: String) -> Unit
) {
    val context = LocalContext.current
    var showAddRuleModal by remember { mutableStateOf(false) }
    var showWorkingHoursModal by remember { mutableStateOf(false) }
    var showLogsModal by remember { mutableStateOf(false) }

    // Add Rule form state
    var inputKeywords by remember { mutableStateOf("") }
    var inputReplyText by remember { mutableStateOf("") }
    var selectedRuleType by remember { mutableStateOf("keyword") }

    val templates = listOf(
        RuleTemplate(
            name = "Price List",
            keywords = "price, cost, how much, rate",
            reply = "Here is our updated price list: Shoes (₦15,000), Shirts (₦8,500), Wristwatches (₦12,000). Delivery available nationwide!"
        ),
        RuleTemplate(
            name = "Account / Gateway",
            keywords = "account, pay, bank, transfer, send account",
            reply = "🔒 All payments are protected by VendorOS Gateway: https://vendoros.ng/pay - Transfer directly or verify instantly to safeguard your money!"
        ),
        RuleTemplate(
            name = "Store Hours",
            keywords = "open, hours, close, sunday, working time",
            reply = "We are open Monday to Saturday from 08:00 AM to 09:00 PM. Messages outside hours will be answered first thing in the morning!"
        ),
        RuleTemplate(
            name = "Location",
            keywords = "where, shop address, location, office, pickup",
            reply = "Our main office & pickup hub is located at ${user.selectedArea}, ${user.selectedLGA}, ${user.selectedState} State. Walk-ins are warmly welcome!"
        ),
        RuleTemplate(
            name = "Delivery Info",
            keywords = "delivery, waybill, interstate, lagos delivery, ship",
            reply = "Delivery within state takes 24 hours (₦2,000). Interstate waybill takes 2-3 business days with tracking number provided."
        ),
        RuleTemplate(
            name = "Thank You",
            keywords = "thanks, thank you, recieved, arrived, good",
            reply = "Thank you so much for your patronage! 🙏 Please tap our Trust Page link to rate your order and receive a 5% discount on your next buy!"
        ),
        RuleTemplate(
            name = "Payment Details",
            keywords = "receipt, paid, i don pay, confirmation",
            reply = "Awesome! We have received your payment notice. Our dispatch team is packaging your package right now."
        ),
        RuleTemplate(
            name = "Follow-up",
            keywords = "still available, available, in stock",
            reply = "Yes! This item is currently 100% available in stock. Reply with your desired size or color so we can reserve it for you."
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Top Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Auto Reply Rules",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "${rules.count { it.isActive }} active rules running",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = { showLogsModal = true },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = NavyCard, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Logs (${chatLogs.size})", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            inputKeywords = ""
                            inputReplyText = ""
                            selectedRuleType = "keyword"
                            showAddRuleModal = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Rule", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // 8 Quick Templates Grid / Horizontal Carousel
        item {
            Column {
                Text(
                    "QUICK TEMPLATES (TAP TO USE)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(templates) { t ->
                        SuggestionChip(
                            onClick = {
                                inputKeywords = t.keywords
                                inputReplyText = t.reply
                                showAddRuleModal = true
                            },
                            label = { Text(t.name, fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                            icon = { Icon(Icons.Default.Bolt, contentDescription = null, tint = SkillsOrange, modifier = Modifier.size(14.dp)) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = NavyCard,
                                labelColor = Color.White
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = NavyBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // Notification Access Explainer Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF1E3A8A), RoundedCornerShape(18.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFF1E3A8A).copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF60A5FA))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Auto-Detect WhatsApp Messages",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Enable notification listener so VendorOS can read customer inquiries and dispatch replies automatically.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            try {
                                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                            } catch (e: Exception) {
                                // Fallback
                            }
                        }
                    ) {
                        Text("Enable", color = VendorGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Working Hours Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
                    .clickable { showWorkingHoursModal = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(SkillsAmber.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = SkillsAmber)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Store Working Hours", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            Text(
                                "${user.workingHoursStart} to ${user.workingHoursEnd} (Away rule triggers outside this)",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Rules List
        items(rules) { rule ->
            RuleCard(
                rule = rule,
                onToggle = { onToggleRule(rule) },
                onDelete = { onDeleteRule(rule) }
            )
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }

    // Add Rule Modal Dialog
    if (showAddRuleModal) {
        AlertDialog(
            onDismissRequest = { showAddRuleModal = false },
            title = {
                Text("Create Auto-Reply Rule", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Keywords (Comma separated):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = inputKeywords,
                        onValueChange = { inputKeywords = it },
                        placeholder = { Text("e.g. price, how much, cost") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VendorGreen,
                            unfocusedBorderColor = NavyBorder,
                            focusedContainerColor = NavyBg,
                            unfocusedContainerColor = NavyBg
                        )
                    )

                    Text(
                        "Automated Reply Message:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = inputReplyText,
                        onValueChange = { inputReplyText = it },
                        placeholder = { Text("Type reply message sent to customer...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        minLines = 3,
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VendorGreen,
                            unfocusedBorderColor = NavyBorder,
                            focusedContainerColor = NavyBg,
                            unfocusedContainerColor = NavyBg
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("keyword", "welcome", "away", "busy").forEach { type ->
                            FilterChip(
                                selected = selectedRuleType == type,
                                onClick = { selectedRuleType = type },
                                label = { Text(type.uppercase(), fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VendorGreen,
                                    selectedLabelColor = Color.Black,
                                    containerColor = NavyBg,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputKeywords.isNotBlank() && inputReplyText.isNotBlank()) {
                            val kwList = inputKeywords.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            onAddRule(kwList, inputReplyText, selectedRuleType)
                            showAddRuleModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black)
                ) {
                    Text("Save Rule", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddRuleModal = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = NavyCard
        )
    }

    // Working Hours Dialog
    if (showWorkingHoursModal) {
        var startInput by remember { mutableStateOf(user.workingHoursStart) }
        var endInput by remember { mutableStateOf(user.workingHoursEnd) }

        AlertDialog(
            onDismissRequest = { showWorkingHoursModal = false },
            title = { Text("Set Working Hours", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Opening Time (24h format):", fontSize = 12.sp, color = TextSecondary)
                    OutlinedTextField(
                        value = startInput,
                        onValueChange = { startInput = it },
                        placeholder = { Text("08:00") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = NavyBg, unfocusedContainerColor = NavyBg
                        )
                    )
                    Text("Closing Time (24h format):", fontSize = 12.sp, color = TextSecondary)
                    OutlinedTextField(
                        value = endInput,
                        onValueChange = { endInput = it },
                        placeholder = { Text("21:00") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = NavyBg, unfocusedContainerColor = NavyBg
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateWorkingHours(startInput, endInput)
                        showWorkingHoursModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black)
                ) {
                    Text("Update", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWorkingHoursModal = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = NavyCard
        )
    }

    // Chat Logs Dialog
    if (showLogsModal) {
        AlertDialog(
            onDismissRequest = { showLogsModal = false },
            title = { Text("Auto-Reply History Logs", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Box(modifier = Modifier.height(300.dp)) {
                    if (chatLogs.isEmpty()) {
                        Text("No messages logged yet.", color = TextSecondary)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(chatLogs) { log ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = NavyBg),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(log.customerName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                        Text("Q: ${log.incomingMessage}", fontSize = 11.sp, color = TextSecondary)
                                        Text("A: ${log.repliedWith}", fontSize = 11.sp, color = VendorGreen)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showLogsModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black)
                ) {
                    Text("Done")
                }
            },
            containerColor = NavyCard
        )
    }
}

@Composable
private fun RuleCard(
    rule: AutoRule,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (rule.isActive) VendorGreen.copy(alpha = 0.4f) else NavyBorder, RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                when (rule.type) {
                                    "welcome" -> Color(0xFF3B82F6).copy(alpha = 0.2f)
                                    "away" -> SkillsAmber.copy(alpha = 0.2f)
                                    else -> VendorGreen.copy(alpha = 0.2f)
                                },
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            rule.type.uppercase(),
                            color = when (rule.type) {
                                "welcome" -> Color(0xFF60A5FA)
                                "away" -> SkillsAmber
                                else -> VendorGreen
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        "Triggered ${rule.triggeredCount} times",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = rule.isActive,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = VendorGreen,
                            uncheckedThumbColor = Color(0xFF64748B),
                            uncheckedTrackColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.scale(0.8f)
                    )

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("IF CONTAINS KEYWORDS:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                rule.keywords.forEach { kw ->
                    Box(
                        modifier = Modifier
                            .background(NavyBg, RoundedCornerShape(6.dp))
                            .border(1.dp, NavyBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(kw, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("THEN AUTO-REPLY:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                rule.replyText,
                fontSize = 13.sp,
                color = if (rule.isActive) Color.White else TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

private fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.size(48.dp * scale)
)

data class RuleTemplate(val name: String, val keywords: String, val reply: String)
