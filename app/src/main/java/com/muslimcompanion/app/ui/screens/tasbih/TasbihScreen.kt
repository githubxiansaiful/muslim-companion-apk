package com.muslimcompanion.app.ui.screens.tasbih

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RotateLeft
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.constants.TasbihConstants
import com.muslimcompanion.app.data.model.PresetTasbih
import com.muslimcompanion.app.data.repository.PreferencesRepository
import com.muslimcompanion.app.ui.theme.AmberPrimary
import com.muslimcompanion.app.ui.theme.EmeraldPrimary

@Composable
fun TasbihScreen(
    preferencesRepo: PreferencesRepository,
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedPreset by remember { mutableStateOf(TasbihConstants.PRESETS[0]) }
    var count by remember { mutableStateOf(0) }
    var target by remember { mutableStateOf(33) }

    val todayTotal by preferencesRepo.tasbihToday.collectAsState()

    fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(25)
        }
    }

    val isCompleted = target > 0 && count >= target

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
                        text = "ডিজিটাল তাসবিহ কাউন্টার",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "আজকের মোট যিকির: ${PrayerConstants.toBengaliDigits(todayTotal)} বার",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.clickable { count = 0 }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
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
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Preset Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TasbihConstants.PRESETS) { preset ->
                val isSelected = selectedPreset.nameBn == preset.nameBn
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.clickable {
                        selectedPreset = preset
                        target = preset.defaultTarget
                        count = 0
                    }
                ) {
                    Text(
                        text = preset.nameBn,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Selected Zikr Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedPreset.arabic,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = selectedPreset.pronunciationBn,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = selectedPreset.meaningBn,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Tap Button (Interactive Large Circle)
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(
                    if (isCompleted) EmeraldPrimary.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surface
                )
                .clickable {
                    count += 1
                    preferencesRepo.incrementTasbih()
                    vibrate()
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = PrayerConstants.toBengaliDigits(count),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isCompleted) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = if (isCompleted) "মাশাআল্লাহ সম্পন্ন!" else "ট্যাপ করুন",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "লক্ষ্য: ${PrayerConstants.toBengaliDigits(target)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Target quick selectors (33, 100, 500, 1000)
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(33, 34, 100, 500).forEach { tgt ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (target == tgt) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        if (target == tgt) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.clickable {
                        target = tgt
                    }
                ) {
                    Text(
                        text = "${PrayerConstants.toBengaliDigits(tgt)} বার",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        fontWeight = if (target == tgt) FontWeight.Bold else FontWeight.Medium,
                        color = if (target == tgt) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
