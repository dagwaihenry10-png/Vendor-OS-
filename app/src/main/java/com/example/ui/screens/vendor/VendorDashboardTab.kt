package com.example.ui.screens.vendor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ChatLog
import com.example.data.models.Proof
import com.example.data.models.User
import com.example.ui.components.QRMatrixView
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VendorDashboardTab(
    user: User,
    proofs: List<Proof>,
    logs: List<ChatLog>,
    onToggleAutoReply: (Boolean) -> Unit,
    onCreateProofClick: () -> Unit,
    onViewPublicTrustPage: () -> Unit,
    onTestMessageSubmit: (String) -> Unit,
    testReplyResult: String?
) {
    var testInput by remember { mutableStateOf("") }
    var showPublicTrustModal by remember { mutableStateOf(false) }

    val todayReplied = logs.size * 12 + 28
    val salesSaved = todayReplied * 500

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // TOP CARD 1: Auto Reply Switch (#25D366)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = VendorGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(if (user.autoReplyOn) Color(0xFF064E3B) else Color.Red, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (user.autoReplyOn) "AUTO REPLY ACTIVE" else "AUTO REPLY PAUSED",
                                color = Color(0xFF022C22),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Working Hours: ${user.workingHoursStart} - ${user.workingHoursEnd}",
                            color = Color(0xFF064E3B),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Auto-responding to WhatsApp & Instagram buyers",
                            color = Color(0xFF064E3B).copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }

                    Switch(
                        checked = user.autoReplyOn,
                        onCheckedChange = { onToggleAutoReply(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF064E3B),
                            uncheckedThumbColor = Color(0xFF64748B),
                            uncheckedTrackColor = Color(0xFFCBD5E1)
                        )
                    )
                }
            }
        }

        // TOP CARD 2: Trust Score Card (Gradient #10B981 to #1E40AF)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF10B981), Color(0xFF1E40AF))
                        ),
                        RoundedCornerShape(22.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "TRUST & VERIFICATION",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 0.8.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    "%.1f".format(user.trustScore),
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    "/5.0",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                                )
                            }
                        }

                        if (user.verifiedDeliveries >= 10) {
                            Box(
                                modifier = Modifier
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = VendorEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Trusted Vendor",
                                        color = Color(0xFF0F172A),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    // 5 Stars
                    Row(modifier = Modifier.padding(vertical = 6.dp)) {
                        repeat(5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${user.verifiedDeliveries} Verified Orders Completed",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        FilledTonalButton(
                            onClick = { showPublicTrustModal = true },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("View Public Page", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // STATS GRID 2x2
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Today Replied",
                        value = "$todayReplied buyers",
                        subtitle = "WhatsApp & IG DMs",
                        icon = Icons.Default.ChatBubble,
                        accentColor = VendorGreen,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Sales Saved",
                        value = "₦%,d".format(salesSaved),
                        subtitle = "Est. Nigerian sales",
                        icon = Icons.Default.Payments,
                        accentColor = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Verified Deliveries",
                        value = "${user.verifiedDeliveries} Orders",
                        subtitle = "Public proof verified",
                        icon = Icons.Default.TaskAlt,
                        accentColor = VendorEmerald,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Pending Proofs",
                        value = "${user.pendingDeliveries} Waiting",
                        subtitle = "Customer confirm",
                        icon = Icons.Default.Schedule,
                        accentColor = SkillsAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // LIVE MESSAGE SIMULATOR (Test your auto reply keywords in real time)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = VendorGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Auto-Reply Test Console",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Type a message as a buyer (e.g., 'price', 'send account', 'hello') to test keywords:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = testInput,
                            onValueChange = { testInput = it },
                            placeholder = { Text("e.g. How much is your shoe?") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VendorGreen,
                                unfocusedBorderColor = NavyBorder,
                                focusedContainerColor = NavyBg,
                                unfocusedContainerColor = NavyBg
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (testInput.isNotBlank()) {
                                    onTestMessageSubmit(testInput)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black)
                        ) {
                            Text("Send", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (!testReplyResult.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NavyBg, RoundedCornerShape(12.dp))
                                .border(1.dp, VendorGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    "VendorOS Automated Response:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VendorGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    testReplyResult,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // RECENT MIXED FEED (LOGS + PROOFS)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "RECENT ACTIVITY & PROOFS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.8.sp
                )

                Text(
                    "${proofs.size} Proofs · ${logs.size} Logs",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        items(proofs.take(4)) { proof ->
            ProofFeedCard(proof)
        }

        items(logs.take(3)) { log ->
            LogFeedCard(log)
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }

    // Public Trust Page Modal
    if (showPublicTrustModal) {
        AlertDialog(
            onDismissRequest = { showPublicTrustModal = false },
            confirmButton = {
                Button(
                    onClick = { showPublicTrustModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black)
                ) {
                    Text("Close Preview", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = VendorEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Public Trust Page Preview", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        user.businessName,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        "${user.selectedArea}, ${user.selectedLGA}, ${user.selectedState}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val publicUrl = "https://vendoros.ng/v/${user.businessName.lowercase().replace(" ", "-")}-${user.uid.takeLast(4)}"
                    QRMatrixView(data = publicUrl, size = 160.dp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Scan to view verified order ledger & ratings",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavyBg, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            publicUrl,
                            fontSize = 12.sp,
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            containerColor = NavyCard
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        modifier = modifier.border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(subtitle, fontSize = 11.sp, color = TextMuted)
        }
    }
}

@Composable
private fun ProofFeedCard(proof: Proof) {
    val isConfirmed = proof.status == "confirmed"
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(if (isConfirmed) VendorEmerald.copy(alpha = 0.2f) else SkillsAmber.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isConfirmed) Icons.Default.CheckCircle else Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (isConfirmed) VendorEmerald else SkillsAmber
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(proof.orderId, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(if (isConfirmed) StatusConfirmed.copy(alpha = 0.2f) else StatusPending.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            if (isConfirmed) "CONFIRMED" else "PENDING",
                            color = if (isConfirmed) StatusConfirmed else StatusPending,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Customer: ${proof.customerName} (${proof.customerPhoneMasked})",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    proof.comment,
                    fontSize = 11.sp,
                    color = TextMuted,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun LogFeedCard(log: ChatLog) {
    val dateStr = remember(log.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
    }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(VendorGreen.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = VendorGreen)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(log.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    Text(dateStr, fontSize = 11.sp, color = TextMuted)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("In: \"${log.incomingMessage}\"", fontSize = 12.sp, color = TextSecondary, maxLines = 1)
                Text("Out: \"${log.repliedWith}\"", fontSize = 11.sp, color = VendorGreen, maxLines = 1)
            }
        }
    }
}
