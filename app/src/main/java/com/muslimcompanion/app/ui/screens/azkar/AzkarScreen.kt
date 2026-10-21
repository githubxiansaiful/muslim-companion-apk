package com.muslimcompanion.app.ui.screens.azkar

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.data.constants.AzkarConstants
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.model.AzkarTimeOfDay
import com.muslimcompanion.app.data.model.ZikrItem
import com.muslimcompanion.app.ui.theme.EmeraldPrimary

@Composable
fun AzkarScreen(onBack: (() -> Unit)? = null) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(AzkarTimeOfDay.MORNING) }
    val counts = remember { mutableStateMapOf<String, Int>() }

    fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(20)
        }
    }

    val currentList = remember(selectedTab) {
        AzkarConstants.AZKAR_LIST.filter { it.timeOfDay == selectedTab }
    }

    val completedCount = currentList.count { item ->
        (counts[item.id] ?: 0) >= item.countTarget
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                        Column {
                            Text(
                                text = "সকাল ও সন্ধ্যার দৈনন্দিন যিকির",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "অনিষ্ট হতে হেফাজতের আত্মিক দুর্গ ও ফজিলত",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.clickable { counts.clear() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RotateLeft,
                                contentDescription = "Reset",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "রিসেট",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "অগ্রগতি: ${PrayerConstants.toBengaliDigits(completedCount)} / ${PrayerConstants.toBengaliDigits(currentList.size)} টি সম্পন্ন",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
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
                TabButton(
                    title = "সকালের যিকির",
                    isSelected = selectedTab == AzkarTimeOfDay.MORNING,
                    onClick = { selectedTab = AzkarTimeOfDay.MORNING },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    title = "সন্ধ্যার যিকির",
                    isSelected = selectedTab == AzkarTimeOfDay.EVENING,
                    onClick = { selectedTab = AzkarTimeOfDay.EVENING },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    title = "সার্বক্ষণিক",
                    isSelected = selectedTab == AzkarTimeOfDay.GENERAL,
                    onClick = { selectedTab = AzkarTimeOfDay.GENERAL },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Zikr Cards
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(currentList, key = { it.id }) { zikr ->
                val currentCount = counts[zikr.id] ?: 0
                val isCompleted = currentCount >= zikr.countTarget

                ZikrItemCard(
                    zikr = zikr,
                    count = currentCount,
                    isCompleted = isCompleted,
                    onTap = {
                        if (currentCount < zikr.countTarget) {
                            counts[zikr.id] = currentCount + 1
                            vibrate()
                        }
                    },
                    onReset = { counts[zikr.id] = 0 }
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun TabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(vertical = 10.dp),
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ZikrItemCard(
    zikr: ZikrItem,
    count: Int,
    isCompleted: Boolean,
    onTap: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title & Count Target Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = zikr.titleBn,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isCompleted) EmeraldPrimary else MaterialTheme.colorScheme.background,
                    border = BorderStroke(
                        1.dp,
                        if (isCompleted) EmeraldPrimary else MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text(
                        text = if (isCompleted) "✓ সম্পন্ন" else "লক্ষ্য: ${PrayerConstants.toBengaliDigits(zikr.countTarget)} বার",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic Text
            Text(
                text = zikr.arabic,
                fontSize = 20.sp,
                lineHeight = 32.sp,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pronunciation
            Text(
                text = "উচ্চারণ: ${zikr.transliteration}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Translation
            Text(
                text = zikr.translationBn,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Reference
            Text(
                text = "রেফারেন্স: ${zikr.reference}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Tap Counter & Reset Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${PrayerConstants.toBengaliDigits(count)} / ${PrayerConstants.toBengaliDigits(zikr.countTarget)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isCompleted) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (count > 0 && !isCompleted) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.background,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.clickable { onReset() }
                        ) {
                            Text(
                                text = "পুনরায়",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isCompleted) EmeraldPrimary.copy(alpha = 0.2f) else EmeraldPrimary,
                        modifier = Modifier.clickable(enabled = !isCompleted) { onTap() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = if (isCompleted) "সম্পন্ন" else "ট্যাপ করুন (+১)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCompleted) EmeraldPrimary else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
