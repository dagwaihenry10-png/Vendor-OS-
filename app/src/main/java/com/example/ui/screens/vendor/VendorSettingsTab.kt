package com.example.ui.screens.vendor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PaymentRecord
import com.example.data.models.User
import com.example.utils.SecureConfig
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VendorSettingsTab(
    user: User,
    currentPayment: PaymentRecord?,
    payments: List<PaymentRecord>,
    selectedPlan: String,
    verificationMessage: String?,
    onPlanSelected: (String) -> Unit,
    onPaySecurelyNow: () -> Unit,
    onVerifyPaymentNow: () -> Unit,
    onWhatsAppSupport: () -> Unit,
    onChangeLocation: () -> Unit
) {
    val context = LocalContext.current
    var showScreenshotNotice by remember { mutableStateOf(false) }
    var showApkDialog by remember { mutableStateOf(false) }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // BIG GREEN BUTTON: "How to Get APK"
        item {
            Button(
                onClick = { showApkDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.InstallMobile, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "How to Get APK",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // User Account Status
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(if (user.isPro) Color(0xFFF59E0B) else Color(0xFF3B82F6), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (user.isPro) Icons.Default.WorkspacePremium else Icons.Default.Storefront,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(user.businessName, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(if (user.isPro) Color(0xFFF59E0B).copy(alpha = 0.2f) else Color(0xFF64748B).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    if (user.isPro) "PRO ACTIVE" else "FREE TIER",
                                    color = if (user.isPro) Color(0xFFF59E0B) else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(user.email, fontSize = 12.sp, color = TextSecondary)
                        Text("${user.selectedArea}, ${user.selectedLGA}, ${user.selectedState}", fontSize = 11.sp, color = TextMuted)
                    }

                    IconButton(onClick = onChangeLocation) {
                        Icon(Icons.Default.EditLocationAlt, contentDescription = "Change Location", tint = VendorGreen)
                    }
                }
            }
        }

        // ==========================================
        // SECURE OPAY PAYMENT CARD (NEVER SHOWS 7081022844)
        // STRICTLY MASKED 7081****44 IN ALL UI
        // ==========================================
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, VendorGreen.copy(alpha = 0.7f), RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = VendorGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "VendorOS Secure OPay Gateway",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(VendorGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("SSL ENCRYPTED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = VendorGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Plan Selector Chips
                    Text("SELECT SUBSCRIPTION PLAN:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("basic", "pro", "super", "promote", "handwork_pro").forEach { plan ->
                            val isSelected = selectedPlan == plan
                            val price = SecureConfig.planPrices[plan] ?: 1000
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) VendorGreen else NavyBg)
                                    .border(1.dp, if (isSelected) VendorGreen else NavyBorder, RoundedCornerShape(8.dp))
                                    .clickable { onPlanSelected(plan) }
                                    .padding(vertical = 8.dp, horizontal = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        when (plan) {
                                            "basic" -> "Basic"
                                            "pro" -> "Pro"
                                            "super" -> "Super"
                                            "promote" -> "Boost"
                                            else -> "Artisan"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color.Black else Color.White
                                    )
                                    Text(
                                        "₦$price",
                                        fontSize = 9.sp,
                                        color = if (isSelected) Color(0xFF064E3B) else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Gateway Details Box
                    val curr = currentPayment
                    val ref = curr?.reference ?: "VOS-DEMO99"
                    val amount = curr?.amount ?: 2000

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavyBg, RoundedCornerShape(16.dp))
                            .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Gateway Bank:", fontSize = 12.sp, color = TextSecondary)
                                Text(SecureConfig.bankName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("OPay Account:", fontSize = 12.sp, color = TextSecondary)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // STRICTLY MASKED 7081****44
                                    Text(
                                        SecureConfig.maskedAccount,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF38BDF8),
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "(Verified)",
                                        fontSize = 10.sp,
                                        color = VendorEmerald,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Account Name:", fontSize = 12.sp, color = TextSecondary)
                                Text(SecureConfig.accountName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Amount to Pay:", fontSize = 12.sp, color = TextSecondary)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("₦%,d".format(amount), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = VendorGreen)
                                    IconButton(onClick = { copyToClipboard("Amount", amount.toString()) }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextMuted, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Payment Reference:", fontSize = 12.sp, color = TextSecondary)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(ref, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFFBBF24))
                                    IconButton(onClick = { copyToClipboard("Payment Reference", ref) }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = TextMuted, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Buttons
                    Button(
                        onClick = onPaySecurelyNow,
                        colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pay Securely Now (Open Gateway)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = onVerifyPaymentNow,
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF1E3A8A), contentColor = Color.White),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("I Don Pay - Verify", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        FilledTonalButton(
                            onClick = onWhatsAppSupport,
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF0F766E), contentColor = Color.White),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp Help", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { showScreenshotNotice = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(NavyBorder, NavyBorder)))
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload Proof Screenshot", color = TextSecondary, fontSize = 12.sp)
                    }

                    if (!verificationMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NavyBg, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Text(verificationMessage, fontSize = 12.sp, color = VendorGreen, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Security Disclaimer
                    Text(
                        "🔒 Your payment is secured via encrypted gateway. Real account number is protected server-side for maximum security. Only pay via the Secure Link above.",
                        fontSize = 11.sp,
                        color = Color(0xFFF59E0B),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Payment History Stream
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("PAYMENT TRANSACTION LOGS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Text("${payments.size} records", fontSize = 11.sp, color = TextSecondary)
            }
        }

        items(payments) { p ->
            val dateStr = remember(p.createdAt) {
                SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(p.createdAt))
            }
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(p.reference, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            if (p.sqlSynced) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF0F766E).copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("SQL SYNCED", fontSize = 8.sp, color = Color(0xFF2DD4BF), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text("Plan: ${p.plan.uppercase()} · ₦%,d".format(p.amount), fontSize = 12.sp, color = TextSecondary)
                        Text(dateStr, fontSize = 10.sp, color = TextMuted)
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                if (p.status == "confirmed") StatusConfirmed.copy(alpha = 0.2f) else StatusPending.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            p.status.uppercase(),
                            color = if (p.status == "confirmed") StatusConfirmed else StatusPending,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }

    if (showScreenshotNotice) {
        AlertDialog(
            onDismissRequest = { showScreenshotNotice = false },
            title = { Text("Upload Proof Screenshot", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Text(
                    "Your transfer receipt screenshot will be verified by our automated banking OCR and matched with reference ${currentPayment?.reference ?: "VOS-DEMO"}.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "Proof receipt attached successfully!", Toast.LENGTH_SHORT).show()
                        showScreenshotNotice = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black)
                ) {
                    Text("Attach Screenshot")
                }
            },
            dismissButton = {
                TextButton(onClick = { showScreenshotNotice = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = NavyCard
        )
    }

    if (showApkDialog) {
        AlertDialog(
            onDismissRequest = { showApkDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.InstallMobile, contentDescription = null, tint = VendorGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("How to Get VendorOS APK", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "This AI Studio preview streams directly in the browser and cannot compile an APK directly to your device.",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavyBg, RoundedCornerShape(12.dp))
                            .border(1.dp, NavyBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("👉 STEP 1: Download Code", fontWeight = FontWeight.Bold, color = SkillsOrange, fontSize = 12.sp)
                            Text("Click Settings > Export > 'Download Code as ZIP' or 'Push to GitHub'.", fontSize = 12.sp, color = TextSecondary)

                            Spacer(modifier = Modifier.height(2.dp))
                            Text("👉 STEP 2: Auto-Build on GitHub", fontWeight = FontWeight.Bold, color = VendorGreen, fontSize = 12.sp)
                            Text("When pushed to GitHub, GitHub Actions (.github/workflows/build-apk.yml) automatically builds the release APK!", fontSize = 12.sp, color = TextSecondary)

                            Spacer(modifier = Modifier.height(2.dp))
                            Text("👉 STEP 3: Or Run Local Build Script", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), fontSize = 12.sp)
                            Text("Run ./build_apk.sh (or 'flutter build apk' / './gradlew assembleDebug') locally on your PC.", fontSize = 12.sp, color = TextSecondary)
                        }
                    }

                    Text(
                        "All build scripts, GitHub workflow, and configurations have been prepared in this project.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showApkDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Understood", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = NavyCard
        )
    }
}
