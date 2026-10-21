package com.muslimcompanion.app.ui.screens.umrah

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.constants.UmrahConstants
import com.muslimcompanion.app.data.model.UmrahStep
import com.muslimcompanion.app.data.repository.PreferencesRepository
import com.muslimcompanion.app.ui.theme.EmeraldPrimary

@Composable
fun UmrahScreen(
    preferencesRepo: PreferencesRepository,
    onBack: (() -> Unit)? = null
) {
    val umrahChecklist by preferencesRepo.umrahChecklist.collectAsState()
    var activeTab by remember { mutableStateOf("steps") }
    var expandedStepId by remember { mutableStateOf<Int?>(1) }

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
                        text = "উমরাহ ধারাবাহিক পূর্ণাঙ্গ গাইড",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "সুন্নাতসম্মত উমরার প্রতিটি ধাপ, মাসনুন দোয়া ও প্রস্তুতি",
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
                        .clickable { activeTab = "steps" },
                    color = if (activeTab == "steps") MaterialTheme.colorScheme.primary else Color.Transparent
                ) {
                    Text(
                        text = "উমরার ৫টি ধাপ ও দোয়া",
                        modifier = Modifier.padding(vertical = 10.dp),
                        fontSize = 12.sp,
                        fontWeight = if (activeTab == "steps") FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == "steps") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { activeTab = "checklist" },
                    color = if (activeTab == "checklist") MaterialTheme.colorScheme.primary else Color.Transparent
                ) {
                    Text(
                        text = "উমরাহ প্রস্তুতি চেকলিস্ট",
                        modifier = Modifier.padding(vertical = 10.dp),
                        fontSize = 12.sp,
                        fontWeight = if (activeTab == "checklist") FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == "checklist") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeTab == "steps") {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(UmrahConstants.STEPS, key = { it.id }) { step ->
                    val isExpanded = expandedStepId == step.id
                    UmrahStepCard(
                        step = step,
                        isExpanded = isExpanded,
                        onToggle = {
                            expandedStepId = if (isExpanded) null else step.id
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text(
                        text = "উমরাহযাত্রীর প্রয়োজনীয় সামগ্রী (${PrayerConstants.toBengaliDigits(umrahChecklist.size)} / ${PrayerConstants.toBengaliDigits(UmrahConstants.DEFAULT_CHECKLIST.size)} সম্পন্ন)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                items(UmrahConstants.DEFAULT_CHECKLIST, key = { it.id }) { item ->
                    val isChecked = umrahChecklist.contains(item.id)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { preferencesRepo.toggleUmrahItem(item.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = item.titleBn,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = item.category,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Icon(
                                imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isChecked) EmeraldPrimary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun UmrahStepCard(
    step: UmrahStep,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
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
                    .clickable { onToggle() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = PrayerConstants.toBengaliDigits(step.id),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column {
                        Text(
                            text = step.titleBn,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = step.subtitleBn,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Text(
                        text = step.descBn,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Duas
                    if (step.duas.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        step.duas.forEach { dua ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.background,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = dua.arabic,
                                        fontSize = 18.sp,
                                        lineHeight = 28.sp,
                                        textAlign = TextAlign.Right,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "উচ্চারণ: ${dua.pronunciationBn}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "অর্থ: ${dua.meaningBn}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Tips
                    if (step.tips.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "জরুরি সতর্কতা ও টিপস:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        step.tips.forEach { tip ->
                            Text(
                                text = "• $tip",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
