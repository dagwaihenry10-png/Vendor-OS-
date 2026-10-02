package com.example.ui.screens.vendor

import android.content.Intent
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.Proof
import com.example.ui.components.QRMatrixView
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VendorProofsTab(
    proofs: List<Proof>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onCreateProof: (name: String, phone: String, photoUrl: String, comment: String) -> Unit,
    onConfirmProof: (String) -> Unit,
    onDeleteProof: (Proof) -> Unit
) {
    val context = LocalContext.current
    var showCreateModal by remember { mutableStateOf(false) }
    var selectedProofDetail by remember { mutableStateOf<Proof?>(null) }
    var showQrDialogForProof by remember { mutableStateOf<Proof?>(null) }

    val filteredProofs = remember(proofs, selectedTab) {
        when (selectedTab.lowercase()) {
            "pending" -> proofs.filter { it.status == "pending" }
            "confirmed" -> proofs.filter { it.status == "confirmed" }
            else -> proofs
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Top Header & FAB
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Trust & Verified Proofs",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Anti-scam evidence ledger for customers",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { showCreateModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Proof", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Tabs: All / Pending / Confirmed
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyCard, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("All", "Pending", "Confirmed").forEach { tab ->
                    val isSelected = selectedTab.equals(tab, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) VendorGreen else Color.Transparent)
                            .clickable { onTabSelected(tab) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        if (filteredProofs.isEmpty()) {
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
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No Proofs in $selectedTab", fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tap 'New Proof' to generate a verified delivery record.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }

        items(filteredProofs) { proof ->
            ProofItemCard(
                proof = proof,
                onClick = { selectedProofDetail = proof },
                onShowQR = { showQrDialogForProof = proof }
            )
        }

        item { Spacer(modifier = Modifier.height(72.dp)) }
    }

    // Detail Bottom Sheet Modal
    selectedProofDetail?.let { proof ->
        val isConfirmed = proof.status == "confirmed"
        val dateFormatted = remember(proof.createdAt) {
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(proof.createdAt))
        }

        AlertDialog(
            onDismissRequest = { selectedProofDetail = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(proof.orderId, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.White)
                    Box(
                        modifier = Modifier
                            .background(
                                if (isConfirmed) StatusConfirmed.copy(alpha = 0.2f) else StatusPending.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            if (isConfirmed) "CONFIRMED" else "PENDING",
                            color = if (isConfirmed) StatusConfirmed else StatusPending,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Photo Preview
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        AsyncImage(
                            model = proof.photoUrl,
                            contentDescription = "Order Proof",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Text("Customer: ${proof.customerName}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Text("Phone: ${proof.customerPhoneMasked} (Private)", color = TextSecondary, fontSize = 12.sp)
                    Text("Date Dispatched: $dateFormatted", color = TextSecondary, fontSize = 12.sp)
                    Text("Remarks: ${proof.comment}", color = Color(0xFFCBD5E1), fontSize = 12.sp)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavyBg, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Text(proof.publicLink, color = Color(0xFF38BDF8), fontSize = 11.sp)
                    }

                    if (!isConfirmed) {
                        Button(
                            onClick = {
                                onConfirmProof(proof.proofId)
                                selectedProofDetail = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VendorEmerald, contentColor = Color.Black),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simulate Customer Confirmation", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "View my verified VendorOS order proof: ${proof.publicLink}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Order Proof"))
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = NavyCardLight, contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share Link")
                    }

                    Button(
                        onClick = {
                            showQrDialogForProof = proof
                            selectedProofDetail = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black)
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Show QR")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProofDetail = null }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = NavyCard
        )
    }

    // QR Code Dialog
    showQrDialogForProof?.let { proof ->
        AlertDialog(
            onDismissRequest = { showQrDialogForProof = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCode2, contentDescription = null, tint = VendorGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Order QR Trust Badge", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(proof.orderId, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    Text("Customer: ${proof.customerName}", fontSize = 12.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(14.dp))

                    QRMatrixView(data = proof.qrData, size = 180.dp)

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        "Buyer scans this QR on receipt to verify order authenticity & boost your Trust Score.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showQrDialogForProof = null },
                    colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black)
                ) {
                    Text("Done")
                }
            },
            containerColor = NavyCard
        )
    }

    // Create Proof Modal Dialog
    if (showCreateModal) {
        var inputCustName by remember { mutableStateOf("") }
        var inputCustPhone by remember { mutableStateOf("") }
        var inputComment by remember { mutableStateOf("") }
        var photoUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500") }

        AlertDialog(
            onDismissRequest = { showCreateModal = false },
            title = { Text("Create Order Delivery Proof", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Customer Full Name *", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = inputCustName,
                        onValueChange = { inputCustName = it },
                        placeholder = { Text("e.g. Chioma Eze") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyBg, unfocusedContainerColor = NavyBg)
                    )

                    Text("Customer Phone (Auto-masked for privacy) *", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = inputCustPhone,
                        onValueChange = { inputCustPhone = it },
                        placeholder = { Text("e.g. +2348039871122") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyBg, unfocusedContainerColor = NavyBg)
                    )

                    Text("Order Item / Delivery Notes", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = inputComment,
                        onValueChange = { inputComment = it },
                        placeholder = { Text("e.g. Delivered 2 pairs of sneakers to Ikeja") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = NavyBg, unfocusedContainerColor = NavyBg)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavyBg, RoundedCornerShape(10.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = VendorGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Order Photo: Package & Waybill Attached", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputCustName.isNotBlank() && inputCustPhone.isNotBlank()) {
                            onCreateProof(inputCustName, inputCustPhone, photoUrl, inputComment)
                            showCreateModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black)
                ) {
                    Text("Create Proof", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateModal = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = NavyCard
        )
    }
}

@Composable
private fun ProofItemCard(
    proof: Proof,
    onClick: () -> Unit,
    onShowQR: () -> Unit
) {
    val isConfirmed = proof.status == "confirmed"
    val dateStr = remember(proof.createdAt) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(proof.createdAt))
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isConfirmed) VendorEmerald.copy(alpha = 0.3f) else SkillsAmber.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo Thumbnail
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavyBg)
            ) {
                AsyncImage(
                    model = proof.photoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(proof.orderId, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
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
                Text("Delivered to: ${proof.customerName}", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                Text("Phone: ${proof.customerPhoneMasked} · $dateStr", fontSize = 11.sp, color = TextSecondary)
            }

            IconButton(onClick = onShowQR) {
                Icon(imageVector = Icons.Default.QrCode, contentDescription = "QR", tint = VendorGreen)
            }
        }
    }
}
