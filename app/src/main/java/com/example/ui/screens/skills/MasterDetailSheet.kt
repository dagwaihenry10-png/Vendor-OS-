package com.example.ui.screens.skills

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.Master
import com.example.ui.theme.*

@Composable
fun MasterDetailSheet(
    master: Master,
    onDismiss: () -> Unit,
    onRequestToLearn: (message: String) -> Unit
) {
    val context = LocalContext.current
    var showInquiryDialog by remember { mutableStateOf(false) }
    var inquiryMessage by remember { mutableStateOf("Hello Master ${master.ownerName}, I saw your workshop profile on VendorOS and I am very interested in learning ${master.skillCategory} under your mentorship. When can I come for inspection?") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 580.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Image
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = master.images.firstOrNull() ?: "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=600",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            if (master.isPromoted) {
                                Box(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .background(SkillsOrange, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                        .align(Alignment.TopStart)
                                ) {
                                    Text("PROMOTED", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                }

                // Business Name & Verified
                item {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                master.businessName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            if (master.isVerified) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.Verified, contentDescription = "Verified", tint = VendorEmerald, modifier = Modifier.size(18.dp))
                            }
                        }

                        Text("Master Artisan: ${master.ownerName}", fontSize = 13.sp, color = TextSecondary)

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(SkillsOrange.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(master.skillCategory, color = SkillsOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("%.1f".format(master.rating), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(" (${master.totalReviews} reviews) · ${master.yearsExperience} yrs exp", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }

                // Description
                item {
                    Text("ABOUT THE APPRENTICESHIP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        master.description,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                }

                // Location & Hours
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyBg),
                        modifier = Modifier.fillMaxWidth().border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = SkillsOrange, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${master.address}, ${master.area}, ${master.lga}, ${master.state}", fontSize = 12.sp, color = Color.White)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open: ${master.openingHoursOpen} - ${master.openingHoursClose} (${master.openingHoursDays})", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }

                // Pricing Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyBg),
                        modifier = Modifier.fillMaxWidth().border(1.dp, VendorGreen.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Monthly Training Fee:", fontSize = 12.sp, color = TextSecondary)
                                Text("₦%,d / Month".format(master.pricePerMonth), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = VendorGreen)
                            }

                            if (master.pricePerWeek != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Weekly Option:", fontSize = 12.sp, color = TextSecondary)
                                    Text("₦%,d / Week".format(master.pricePerWeek), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                }
                            }

                            if (master.hasAccommodation) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Hostel Accommodation:", fontSize = 12.sp, color = TextSecondary)
                                    Text("+₦%,d (Available)".format(master.accommodationFee), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SkillsAmber)
                                }
                            }
                        }
                    }
                }

                // Action Buttons: Call, WhatsApp, Share
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${master.phone}"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A), contentColor = Color.White),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call")
                        }

                        Button(
                            onClick = {
                                val url = "https://wa.me/${master.whatsapp}?text=Hi%20Master%20${master.ownerName},%20I%20saw%20your%20profile%20on%20VendorOS!"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black),
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WhatsApp", fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Learn handwork with ${master.businessName} on VendorOS: https://vendoros.ng/m/${master.masterId}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Master Profile"))
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = TextSecondary)
                        }
                    }
                }

                // Primary Request Button
                item {
                    Button(
                        onClick = { showInquiryDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SkillsOrange, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.School, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Request to Learn (Apply Now)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close Profile", color = TextSecondary)
            }
        },
        containerColor = NavyCard
    )

    if (showInquiryDialog) {
        AlertDialog(
            onDismissRequest = { showInquiryDialog = false },
            title = { Text("Send Learner Application", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("To: ${master.businessName}", fontWeight = FontWeight.Bold, color = SkillsOrange)
                    Text("Your Message to the Master Artisan:", fontSize = 12.sp, color = TextSecondary)
                    OutlinedTextField(
                        value = inquiryMessage,
                        onValueChange = { inquiryMessage = it },
                        modifier = Modifier.fillMaxWidth().height(110.dp),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyBg, unfocusedContainerColor = NavyBg)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRequestToLearn(inquiryMessage)
                        showInquiryDialog = false
                        Toast.makeText(context, "Application Sent to Master!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkillsOrange, contentColor = Color.Black)
                ) {
                    Text("Submit Application", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showInquiryDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = NavyCard
        )
    }
}
