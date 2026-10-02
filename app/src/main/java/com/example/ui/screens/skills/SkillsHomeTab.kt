package com.example.ui.screens.skills

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.Master
import com.example.data.models.User
import com.example.data.nigeria.NigeriaData
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsHomeTab(
    user: User,
    masters: List<Master>,
    selectedCategory: String,
    searchQuery: String,
    onCategorySelected: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onMasterClicked: (Master) -> Unit,
    onSwitchToMap: () -> Unit,
    onBecomeMasterClick: () -> Unit
) {
    var selectedState by remember { mutableStateOf(user.selectedState) }
    var stateDropdownExpanded by remember { mutableStateOf(false) }

    val filteredMasters = remember(masters, selectedState, selectedCategory, searchQuery) {
        masters.filter { master ->
            val matchState = master.state.equals(selectedState, ignoreCase = true) || selectedState.isBlank()
            val matchCategory = selectedCategory == "All" || master.skillCategory.equals(selectedCategory, ignoreCase = true)
            val matchSearch = searchQuery.isBlank() ||
                    master.businessName.contains(searchQuery, ignoreCase = true) ||
                    master.skillCategory.contains(searchQuery, ignoreCase = true) ||
                    master.skillName.contains(searchQuery, ignoreCase = true) ||
                    master.area.contains(searchQuery, ignoreCase = true) ||
                    master.lga.contains(searchQuery, ignoreCase = true)
            matchState && matchCategory && matchSearch
        }
    }

    val promotedMasters = remember(masters, selectedState) {
        masters.filter { it.isPromoted && (it.state.equals(selectedState, ignoreCase = true) || selectedState.isBlank()) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Handwork Header & State Filter
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(SkillsOrange, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Handyman, contentDescription = null, tint = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "HandworkNG",
                                fontWeight = FontWeight.Black,
                                fontSize = 19.sp,
                                color = Color.White
                            )
                            Text(
                                "36 States Craft Master Directory",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // State Dropdown
                    Box {
                        FilledTonalButton(
                            onClick = { stateDropdownExpanded = true },
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = NavyCard, contentColor = SkillsOrange),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(selectedState, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
                        }

                        DropdownMenu(
                            expanded = stateDropdownExpanded,
                            onDismissRequest = { stateDropdownExpanded = false },
                            modifier = Modifier.background(NavyCard)
                        ) {
                            NigeriaData.getStates().forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s, color = Color.White) },
                                    onClick = {
                                        selectedState = s
                                        stateDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    placeholder = { Text("Search craft e.g. Barber in Ikeja, Tailor in Abuja...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChanged("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SkillsOrange,
                        unfocusedBorderColor = NavyBorder,
                        focusedContainerColor = NavyCard,
                        unfocusedContainerColor = NavyCard
                    )
                )
            }

            // 20 Skill Categories Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedCategory == "All",
                            onClick = { onCategorySelected("All") },
                            label = { Text("All Crafts", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SkillsOrange,
                                selectedLabelColor = Color.Black,
                                containerColor = NavyCard,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selectedCategory == "All", borderColor = NavyBorder)
                        )
                    }

                    items(NigeriaData.allSkills) { skill ->
                        val isSelected = selectedCategory.equals(skill, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategorySelected(skill) },
                            label = { Text(skill, fontWeight = FontWeight.Medium, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SkillsOrange,
                                selectedLabelColor = Color.Black,
                                containerColor = NavyCard,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = isSelected, borderColor = NavyBorder)
                        )
                    }
                }
            }

            // PROMOTED MASTERS CAROUSEL
            if (promotedMasters.isNotEmpty() && searchQuery.isBlank() && selectedCategory == "All") {
                item {
                    Text(
                        "🔥 PROMOTED MASTERS IN $selectedState",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SkillsAmber,
                        letterSpacing = 0.8.sp
                    )
                }

                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(promotedMasters) { m ->
                            PromotedMasterCard(master = m, onClick = { onMasterClicked(m) })
                        }
                    }
                }
            }

            // NEARBY MASTERS LIST
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "NEARBY MASTERS (${filteredMasters.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.8.sp
                    )

                    TextButton(onClick = onSwitchToMap) {
                        Icon(Icons.Default.Map, contentDescription = null, tint = SkillsOrange, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Map Radar", color = SkillsOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            if (filteredMasters.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Engineering, contentDescription = null, tint = TextMuted, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No Masters Found in $selectedState", fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Be the first master artisan registered in this area!", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onBecomeMasterClick,
                                colors = ButtonDefaults.buttonColors(containerColor = SkillsOrange, contentColor = Color.Black),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Register as Master", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            items(filteredMasters) { master ->
                MasterListItem(master = master, onClick = { onMasterClicked(master) })
            }

            item { Spacer(modifier = Modifier.height(72.dp)) }
        }

        // Floating Map View Toggle
        FloatingActionButton(
            onClick = onSwitchToMap,
            containerColor = SkillsOrange,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 80.dp)
        ) {
            Icon(imageVector = Icons.Default.Explore, contentDescription = "Map Radar View")
        }
    }
}

@Composable
private fun PromotedMasterCard(master: Master, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        modifier = Modifier
            .width(260.dp)
            .border(1.5.dp, SkillsOrange.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = master.images.firstOrNull() ?: "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=600",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(SkillsOrange, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text("PROMOTED", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(master.businessName, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color.White, maxLines = 1)
                Text(master.skillCategory, color = SkillsOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${master.rating} (${master.totalReviews}) · ${master.area}", fontSize = 11.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("From ₦%,d/mo".format(master.pricePerMonth), fontWeight = FontWeight.Bold, color = VendorGreen, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun MasterListItem(master: Master, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NavyBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(NavyBg)
            ) {
                AsyncImage(
                    model = master.images.firstOrNull() ?: "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=600",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(master.businessName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White, maxLines = 1)
                    if (master.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = VendorEmerald, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Box(
                    modifier = Modifier
                        .background(SkillsOrange.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(master.skillCategory, color = SkillsOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    "${master.area} · ${master.lga} · 1.2km away",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("%.1f".format(master.rating), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(" (${master.totalReviews})", fontSize = 11.sp, color = TextMuted)
                    }

                    Text("From ₦%,d/mo".format(master.pricePerMonth), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = VendorGreen)
                }
            }
        }
    }
}
