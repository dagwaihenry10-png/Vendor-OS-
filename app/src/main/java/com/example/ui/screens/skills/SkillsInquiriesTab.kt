package com.example.ui.screens.skills

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Inquiry
import com.example.data.models.User
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SkillsInquiriesTab(
    user: User,
    inquiries: List<Inquiry>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onUpdateInquiryStatus: (inquiryId: String, status: String) -> Unit
) {
    val context = LocalContext.current
    var selectedInquiryForReview by remember { mutableStateOf<Inquiry?>(null) }

    val sentInquiries = remember(inquiries, user.uid) {
        inquiries.filter { it.learnerId == user.uid }
    }

    val receivedInquiries = remember(inquiries, user.uid) {
        inquiries.filter { it.learnerId != user.uid || inquiries.size > 1 }
    }

    val displayedList = if (selectedTab.equals("Sent", ignoreCase = true)) sentInquiries else receivedInquiries

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Top Header
        item {
            Text(
                "Apprenticeship Inquiries",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Track applications to learn craft from master artisans",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // TabBar Sent / Received
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyCard, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Sent", "Received").forEach { tab ->
                    val isSelected = selectedTab.equals(tab, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) SkillsOrange else Color.Transparent)
                            .clickable { onTabSelected(tab) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (tab == "Sent") "My Applications (${sentInquiries.size})" else "Incoming Requests (${receivedInquiries.size})",
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        if (displayedList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Inbox, contentDescription = null, tint = TextMuted, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No Inquiries Found", fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (selectedTab.equals("Sent", ignoreCase = true))
                                "Browse masters and tap 'Request to Learn' to send an inquiry."
                            else
                                "You haven't received learner applications yet.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        items(displayedList) { inq ->
            val isSent = selectedTab.equals("Sent", ignoreCase = true)
            val dateStr = remember(inq.createdAt) {
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(inq.createdAt))
            }

            val statusColor = when (inq.status) {
                "accepted" -> Color(0xFF10B981) // Green
                "completed" -> Color(0xFF1E40AF) // Blue
                "rejected" -> Color(0xFFEF4444)
                else -> Color(0xFFFF6B00) // Pending Orange
            }

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (isSent) inq.masterName else inq.learnerName,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = Color.White
                        )

                        Box(
                            modifier = Modifier
                                .background(statusColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                inq.status.uppercase(),
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(SkillsOrange.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(inq.skill, color = SkillsOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(dateStr, fontSize = 11.sp, color = TextMuted)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        "\"${inq.message}\"",
                        fontSize = 13.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )

                    if (!inq.meetingDate.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = VendorEmerald, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Interview/Start Date: ${inq.meetingDate}", fontSize = 11.sp, color = VendorEmerald, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isSent) {
                        // Sent: Call & WhatsApp buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+2348000000000"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = NavyBg, contentColor = Color.White),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val url = "https://wa.me/2348000000000?text=Hello,%20following%20up%20on%20my%20VendorOS%20apprenticeship%20inquiry%20for%20${inq.skill}"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black),
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WhatsApp", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    } else {
                        // Received: Accept / Reject / Complete buttons
                        if (inq.status == "pending") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onUpdateInquiryStatus(inq.inquiryId, "rejected") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Decline", color = Color(0xFFEF4444), fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { onUpdateInquiryStatus(inq.inquiryId, "accepted") },
                                    colors = ButtonDefaults.buttonColors(containerColor = VendorEmerald, contentColor = Color.Black),
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Accept Learner", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        } else if (inq.status == "accepted") {
                            Button(
                                onClick = { selectedInquiryForReview = inq },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A), contentColor = Color.White),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Mark Apprenticeship as Completed", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }

    selectedInquiryForReview?.let { inq ->
        AlertDialog(
            onDismissRequest = { selectedInquiryForReview = null },
            title = { Text("Apprenticeship Graduation", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Text(
                    "Congratulations! Marking this apprenticeship with ${inq.learnerName} as completed will record their craft certificate and boost your Master Artisan rating on HandworkNG.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateInquiryStatus(inq.inquiryId, "completed")
                        selectedInquiryForReview = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VendorEmerald, contentColor = Color.Black)
                ) {
                    Text("Confirm Completion", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedInquiryForReview = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = NavyCard
        )
    }
}
