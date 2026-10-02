package com.example.ui.screens.skills

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.Master
import com.example.data.models.User
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SkillsMapTab(
    user: User,
    masters: List<Master>,
    radiusKm: Float,
    onRadiusChange: (Float) -> Unit,
    onMasterSelected: (Master) -> Unit
) {
    val context = LocalContext.current
    var selectedMarkerMaster by remember { mutableStateOf<Master?>(masters.firstOrNull()) }

    // Map viewport centers around user's chosen state/city
    var centerLat by remember { mutableDoubleStateOf(user.selectedState.let { if (it == "FCT") 9.0765 else 6.5244 }) }
    var centerLng by remember { mutableDoubleStateOf(user.selectedState.let { if (it == "FCT") 7.3986 else 3.3792 }) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
    ) {
        // Interactive Radar Map Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(masters) {
                    detectTapGestures { tapOffset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f

                        // Find closest master marker to tap position
                        var closest: Master? = null
                        var minDist = Float.MAX_VALUE

                        masters.forEach { m ->
                            val dx = ((m.lng - centerLng) * 2800f).toFloat()
                            val dy = -((m.lat - centerLat) * 2800f).toFloat()
                            val pinX = centerX + dx
                            val pinY = centerY + dy

                            val distanceToTap = kotlin.math.sqrt(
                                (tapOffset.x - pinX) * (tapOffset.x - pinX) +
                                        (tapOffset.y - pinY) * (tapOffset.y - pinY)
                            )
                            if (distanceToTap < 80f && distanceToTap < minDist) {
                                minDist = distanceToTap
                                closest = m
                            }
                        }

                        if (closest != null) {
                            selectedMarkerMaster = closest
                        }
                    }
                }
        ) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f

            // Background radar grid circles
            val ringStep = (size.width / 5f).coerceAtLeast(60f)
            for (i in 1..4) {
                drawCircle(
                    color = Color(0xFF1E293B),
                    radius = ringStep * i,
                    center = Offset(centerX, centerY),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                )
            }

            // Radar scan beam / line
            drawLine(
                color = Color(0xFF334155),
                start = Offset(0f, centerY),
                end = Offset(size.width, centerY),
                strokeWidth = 1f
            )
            drawLine(
                color = Color(0xFF334155),
                start = Offset(centerX, 0f),
                end = Offset(centerX, size.height),
                strokeWidth = 1f
            )

            // User center location pin
            drawCircle(
                color = VendorGreen.copy(alpha = 0.25f),
                radius = 24f,
                center = Offset(centerX, centerY)
            )
            drawCircle(
                color = VendorGreen,
                radius = 8f,
                center = Offset(centerX, centerY)
            )

            // Draw Master markers
            masters.forEach { master ->
                val dx = ((master.lng - centerLng) * 2800f).toFloat()
                val dy = -((master.lat - centerLat) * 2800f).toFloat()
                val pinX = (centerX + dx).coerceIn(40f, size.width - 40f)
                val pinY = (centerY + dy).coerceIn(40f, size.height - 40f)

                val markerColor = when (master.skillCategory.lowercase()) {
                    "barber" -> SkillsOrange
                    "tailoring" -> SuperBlueLight
                    "auto mechanic" -> VendorEmerald
                    "solar installation" -> Color(0xFFFBBF24)
                    else -> Color(0xFFEC4899)
                }

                val isSelected = selectedMarkerMaster?.masterId == master.masterId

                // Glow ring if selected
                if (isSelected) {
                    drawCircle(
                        color = markerColor.copy(alpha = 0.4f),
                        radius = 22f,
                        center = Offset(pinX, pinY)
                    )
                }

                drawCircle(
                    color = markerColor,
                    radius = if (isSelected) 14f else 10f,
                    center = Offset(pinX, pinY)
                )

                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = Offset(pinX, pinY)
                )
            }
        }

        // Top Overlay: Status and Map Key
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard.copy(alpha = 0.9f)),
                modifier = Modifier.border(1.dp, NavyBorder, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).background(VendorGreen, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "${masters.size} Masters Active on Radar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Skill color badges
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                MapLegendBadge("Barber", SkillsOrange)
                MapLegendBadge("Tailoring", SuperBlueLight)
                MapLegendBadge("Mechanic", VendorEmerald)
                MapLegendBadge("Solar", Color(0xFFFBBF24))
            }
        }

        // My Location Button
        FloatingActionButton(
            onClick = {
                centerLat = if (user.selectedState == "FCT") 9.0765 else 6.5244
                centerLng = if (user.selectedState == "FCT") 7.3986 else 3.3792
            },
            containerColor = NavyCard,
            contentColor = VendorGreen,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(48.dp)
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Center Map")
        }

        // Bottom Controls: Radius Slider & Selected Master Mini Card
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 76.dp, start = 16.dp, end = 16.dp)
        ) {
            // Radius Slider
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard.copy(alpha = 0.95f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Search Radius", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                        Text("${radiusKm.toInt()} km", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SkillsOrange)
                    }
                    Slider(
                        value = radiusKm,
                        onValueChange = onRadiusChange,
                        valueRange = 1f..20f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = SkillsOrange,
                            activeTrackColor = SkillsOrange,
                            inactiveTrackColor = NavyBg
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selected Master Mini-Card
            selectedMarkerMaster?.let { master ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, SkillsOrange.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NavyBg)
                            ) {
                                AsyncImage(
                                    model = master.images.firstOrNull() ?: "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=600",
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(master.businessName, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.White, maxLines = 1)
                                Text("${master.skillCategory} · ${master.area}", fontSize = 12.sp, color = SkillsOrange)
                                Text("From ₦%,d/month".format(master.pricePerMonth), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = VendorGreen)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("%.1f".format(master.rating), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

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
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    val url = "https://wa.me/${master.whatsapp}?text=Hello%20${master.ownerName},%20I%20saw%20your%20workshop%20on%20VendorOS!"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = VendorGreen, contentColor = Color.Black),
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("WhatsApp", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { onMasterSelected(master) },
                                colors = ButtonDefaults.buttonColors(containerColor = SkillsOrange, contentColor = Color.Black),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Text("Profile", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MapLegendBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .background(NavyCard.copy(alpha = 0.9f), RoundedCornerShape(6.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}
