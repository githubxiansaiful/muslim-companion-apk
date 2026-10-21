package com.muslimcompanion.app.ui.screens.knowledge

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.data.constants.KnowledgeConstants
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.model.KnowledgeTopic
import com.muslimcompanion.app.ui.theme.AmberPrimary
import com.muslimcompanion.app.ui.theme.EmeraldPrimary

@Composable
fun KnowledgeScreen(onBack: (() -> Unit)? = null) {
    var activeTab by remember { mutableStateOf("topics") } // "topics" or "zakat"
    var expandedTopicId by remember { mutableStateOf<String?>("salah-fard") }

    // Zakat Calculator Inputs
    var cash by remember { mutableStateOf("") }
    var goldSilver by remember { mutableStateOf("") }
    var businessAssets by remember { mutableStateOf("") }
    var debts by remember { mutableStateOf("") }

    val totalAssets = (cash.toDoubleOrNull() ?: 0.0) +
            (goldSilver.toDoubleOrNull() ?: 0.0) +
            (businessAssets.toDoubleOrNull() ?: 0.0)
    val totalDebts = debts.toDoubleOrNull() ?: 0.0
    val netWealth = (totalAssets - totalDebts).coerceAtLeast(0.0)
    val zakatPayable = netWealth * 0.025

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header Card with Back Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (onBack != null) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ইসলামিক জ্ঞান ও যাকাত",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "সালাত, সাওম, সুন্নাত ও যাকাত ক্যালকুলেটর",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { activeTab = "topics" },
                    color = if (activeTab == "topics") MaterialTheme.colorScheme.primary else Color.Transparent
                ) {
                    Text(
                        text = "ইসলামিক বিধান ও সুন্নাত",
                        modifier = Modifier.padding(vertical = 10.dp),
                        fontSize = 12.sp,
                        fontWeight = if (activeTab == "topics") FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == "topics") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { activeTab = "zakat" },
                    color = if (activeTab == "zakat") MaterialTheme.colorScheme.primary else Color.Transparent
                ) {
                    Text(
                        text = "যাকাত ক্যালকুলেটর",
                        modifier = Modifier.padding(vertical = 10.dp),
                        fontSize = 12.sp,
                        fontWeight = if (activeTab == "zakat") FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == "zakat") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeTab == "topics") {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(KnowledgeConstants.TOPICS, key = { it.id }) { topic ->
                    val isExpanded = expandedTopicId == topic.id
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedTopicId = if (isExpanded) null else topic.id
                                    },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = topic.titleBn,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = topic.summaryBn,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                    topic.points.forEach { pt ->
                                        Text(
                                            text = "• $pt",
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(vertical = 3.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "রেফারেন্স: ${topic.reference}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        } else {
            // Zakat Calculator View
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "ওয়াজিব যাকাতের পরিমাণ (২.৫%)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "৳ ${PrayerConstants.toBengaliDigits(String.format("%,.2f", zakatPayable))}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "মোট যাকাতযোগ্য সম্পদ: ৳ ${PrayerConstants.toBengaliDigits(String.format("%,.2f", netWealth))}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "সম্পদের হিসাব প্রদান করুন (টাকায়):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                item {
                    OutlinedTextField(
                        value = cash,
                        onValueChange = { cash = it },
                        label = { Text("নগদ টাকা ও ব্যাংক ব্যালেন্স (৳)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = goldSilver,
                        onValueChange = { goldSilver = it },
                        label = { Text("স্বর্ণ ও রূপার আনুমানিক বর্তমান মূল্য (৳)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = businessAssets,
                        onValueChange = { businessAssets = it },
                        label = { Text("ব্যবসায়িক পণ্য ও বিনিয়োগের মূল্য (৳)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = debts,
                        onValueChange = { debts = it },
                        label = { Text("প্রদেয় ঋণ বা দেনা (যা বাদ যাবে) (৳)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
