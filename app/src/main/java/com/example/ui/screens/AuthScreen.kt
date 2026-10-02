package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    onSignInSuccess: (name: String, email: String) -> Unit
) {
    var emailInput by remember { mutableStateOf("vendor@vendoros.ng") }
    var nameInput by remember { mutableStateOf("Tunde Adeleke") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .background(VendorGreen, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("V", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 40.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "VendorOS Super App",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    "Auto-Reply · Trust Badge · HandworkNG",
                    fontSize = 13.sp,
                    color = SkillsOrange,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NavyBorder, RoundedCornerShape(24.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            "Instant Account Sign-In",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Your Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = NavyBg,
                                unfocusedContainerColor = NavyBg
                            )
                        )

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = NavyBg,
                                unfocusedContainerColor = NavyBg
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Google Sign-In button
                        Button(
                            onClick = {
                                onSignInSuccess(nameInput.ifBlank { "Nigerian Merchant" }, emailInput.ifBlank { "merchant@vendoros.ng" })
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFF4285F4), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("G", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    "Continue with Google",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                onSignInSuccess(nameInput.ifBlank { "Nigerian Merchant" }, emailInput.ifBlank { "merchant@vendoros.ng" })
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VendorGreen,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                "Quick Enter As Merchant",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = VendorEmerald, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Secure 256-bit encryption · Real OPay hidden",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Play Store Compliant · VendorOS Nigeria",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}
