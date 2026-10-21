package com.muslimcompanion.app.ui.screens.prayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.model.ForbiddenTimeItem
import com.muslimcompanion.app.data.model.NaflPrayerItem
import com.muslimcompanion.app.data.model.PrayerTimeItem
import com.muslimcompanion.app.data.repository.PrayerRepository
import com.muslimcompanion.app.data.repository.PreferencesRepository
import com.muslimcompanion.app.receiver.PrayerAlarmScheduler
import com.muslimcompanion.app.ui.components.CitySelectorBottomSheet
import com.muslimcompanion.app.ui.components.NamajTimeHeroCard
import com.muslimcompanion.app.ui.components.PrayerSettingsBottomSheet
import com.muslimcompanion.app.ui.navigation.Screen
import com.muslimcompanion.app.ui.theme.AmberPrimary
import com.muslimcompanion.app.ui.theme.EmeraldPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerScreen(
    preferencesRepo: PreferencesRepository,
    onNavigate: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val currentCity by preferencesRepo.currentCity.collectAsState()
    val calcMethod by preferencesRepo.calculationMethod.collectAsState()
    val madhab by preferencesRepo.madhab.collectAsState()
    val prayerAlerts by preferencesRepo.prayerAlerts.collectAsState()

    var showSettingsBottomSheet by remember { mutableStateOf(false) }
    var showCityBottomSheet by remember { mutableStateOf(false) }

    // Launcher for Notification Permission on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "নোটিফিকেশন অনুমতি প্রদান করা হয়েছে", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "নামাজের অ্যালার্ম পেতে নোটিফিকেশন অনুমতি প্রয়োজন", Toast.LENGTH_LONG).show()
        }
    }

    fun togglePrayerAlert(prayerId: String, prayerNameBn: String, triggerTimeMillis: Long) {
        val currentEnabled = prayerAlerts[prayerId] ?: false
        val newEnabled = !currentEnabled

        if (newEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasPerm = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                if (!hasPerm) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            preferencesRepo.setPrayerAlert(prayerId, true)
            PrayerAlarmScheduler.scheduleAlarm(context, prayerId, prayerNameBn, triggerTimeMillis)
            Toast.makeText(context, "$prayerNameBn সালাতের অ্যালার্ম চালু করা হয়েছে", Toast.LENGTH_SHORT).show()
        } else {
            preferencesRepo.setPrayerAlert(prayerId, false)
            PrayerAlarmScheduler.cancelAlarm(context, prayerId)
            Toast.makeText(context, "$prayerNameBn সালাতের অ্যালার্ম বন্ধ করা হয়েছে", Toast.LENGTH_SHORT).show()
        }
    }

    val schedule = remember(currentCity, calcMethod, madhab) {
        PrayerRepository.calculateSchedule(
            currentCity.latitude,
            currentCity.longitude,
            currentCity.nameBn,
            currentCity.country,
            calcMethod,
            madhab
        )
    }

    val qiblaInfo = remember(currentCity) {
        PrayerRepository.calculateQibla(currentCity.latitude, currentCity.longitude)
    }

    val todayDateBn = remember {
        val sdf = SimpleDateFormat("d MMMM", Locale("bn", "BD"))
        sdf.format(Date())
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "top_spacer") { Spacer(modifier = Modifier.height(6.dp)) }

        // 1. Top Bar: [ 📍 ঢাকা ⌵ ]   [ 📅 ১৮ অক্টোবর ]   [ ⚙ পদ্ধতি ]
        item(key = "top_bar") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location Pill
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.clickable { showCityBottomSheet = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "📍 ${currentCity.nameBn} ⌵",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Date Pill
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "📅 $todayDateBn",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Settings Pill
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.clickable { showSettingsBottomSheet = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "পদ্ধতি",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 2. Namaj Time Hero Card (Mosque silhouette, 3 pillars, active prayer progress bar)
        item(key = "hero_card") {
            NamajTimeHeroCard(schedule = schedule)
        }

        // 3. ফরজ নামাজ (Obligatory 5 Prayers Section)
        item(key = "fard_header") {
            SectionHeader(
                accentColor = EmeraldPrimary,
                title = "ফরজ নামাজ",
                subtitle = "আজকের ৫ ওয়াক্ত সালাত"
            )
        }

        item(key = "fard_prayers_card") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    val fardPrayers = listOf(
                        schedule.fajr,
                        schedule.dhuhr,
                        schedule.asr,
                        schedule.maghrib,
                        schedule.isha
                    )

                    fardPrayers.forEachIndexed { index, prayer ->
                        val isAlert = prayerAlerts[prayer.id] ?: true
                        FardPrayerRow(
                            prayer = prayer,
                            isAlertOn = isAlert,
                            onToggleAlert = {
                                togglePrayerAlert(prayer.id, prayer.nameBn, prayer.date.time)
                            }
                        )

                        if (index < fardPrayers.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.8.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }

        // 4. নফল নামাজ (Nafl Prayers Horizontal Cards)
        item(key = "nafl_header") {
            SectionHeader(
                accentColor = Color(0xFF3B82F6),
                title = "নফল নামাজ",
                subtitle = "অতিরিক্ত সাওয়াবের সালাত"
            )
        }

        item(key = "nafl_prayers_row") {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(schedule.naflPrayers, key = { it.id }) { nafl ->
                    val isAlert = prayerAlerts[nafl.id] ?: false
                    val triggerTime = when (nafl.id) {
                        "tahajjud" -> schedule.fajr.date.time - 60 * 60 * 1000L
                        "ishraq" -> schedule.sunrise.date.time + 15 * 60 * 1000L
                        "chasht" -> schedule.sunrise.date.time + 105 * 60 * 1000L
                        "zawal" -> schedule.dhuhr.date.time - 6 * 60 * 1000L
                        else -> schedule.dhuhr.date.time
                    }
                    NaflPrayerCard(
                        item = nafl,
                        isAlertOn = isAlert,
                        onToggleAlert = {
                            togglePrayerAlert(nafl.id, nafl.nameBn, triggerTime)
                        }
                    )
                }
            }
        }

        // 5. নামাজের নিষিদ্ধ সময় (Forbidden Prayer Times)
        item(key = "forbidden_header") {
            SectionHeader(
                accentColor = Color(0xFFEF4444),
                title = "নামাজের নিষিদ্ধ সময়",
                subtitle = "সালাত আদায় মাকরূহ"
            )
        }

        item(key = "forbidden_prayers_row") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                schedule.forbiddenTimes.forEach { forbidden ->
                    ForbiddenTimeCard(
                        modifier = Modifier.weight(1f),
                        item = forbidden
                    )
                }
            }
        }

        // 6. কিবলা ও কাবার তথ্য (Qibla & Kaaba Information Card)
        item(key = "qibla_header") {
            SectionHeader(
                accentColor = AmberPrimary,
                title = "কিবলা ও কাবার তথ্য",
                subtitle = "সরাসরি দিক নির্দেশনা"
            )
        }

        item(key = "qibla_card") {
            QiblaSummaryCard(
                qiblaDegree = qiblaInfo.compassAngle,
                distanceKm = qiblaInfo.distanceKm,
                onCompassClick = { onNavigate(Screen.Qibla.route) }
            )
        }

        item(key = "bottom_spacer") { Spacer(modifier = Modifier.height(28.dp)) }
    }

    if (showSettingsBottomSheet) {
        PrayerSettingsBottomSheet(
            currentMethod = calcMethod,
            currentMadhab = madhab,
            onMethodChange = { preferencesRepo.saveCalculationMethod(it) },
            onMadhabChange = { preferencesRepo.saveMadhab(it) },
            onDismiss = { showSettingsBottomSheet = false }
        )
    }

    if (showCityBottomSheet) {
        CitySelectorBottomSheet(
            selectedCity = currentCity,
            onCitySelected = { preferencesRepo.saveCity(it) },
            onDismiss = { showCityBottomSheet = false }
        )
    }
}

/**
 * Reusable Section Header with vertical color bar matching Muslim Bangla styling.
 */
@Composable
fun SectionHeader(
    accentColor: Color,
    title: String,
    subtitle: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(18.dp)
                    .background(accentColor, RoundedCornerShape(2.dp))
            )
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (subtitle != null) {
            Text(
                text = subtitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Single Fard Prayer row with status badge, range, and bell alert toggle.
 */
@Composable
fun FardPrayerRow(
    prayer: PrayerTimeItem,
    isAlertOn: Boolean,
    onToggleAlert: () -> Unit
) {
    val icon = when (prayer.id) {
        "fajr" -> Icons.Default.WbTwilight
        "dhuhr" -> Icons.Default.WbSunny
        "asr" -> Icons.Default.Brightness6
        "maghrib" -> Icons.Default.WbTwilight
        else -> Icons.Default.Nightlight
    }

    val iconBgColor = when (prayer.id) {
        "fajr" -> Color(0xFF0EA5E9).copy(alpha = 0.12f)
        "dhuhr" -> Color(0xFFF59E0B).copy(alpha = 0.15f)
        "asr" -> Color(0xFFF97316).copy(alpha = 0.12f)
        "maghrib" -> Color(0xFF8B5CF6).copy(alpha = 0.12f)
        else -> Color(0xFF6366F1).copy(alpha = 0.12f)
    }

    val iconTintColor = when (prayer.id) {
        "fajr" -> Color(0xFF0284C7)
        "dhuhr" -> Color(0xFFD97706)
        "asr" -> Color(0xFFEA580C)
        "maghrib" -> Color(0xFF7C3AED)
        else -> Color(0xFF4F46E5)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = iconBgColor,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = prayer.nameBn,
                        tint = iconTintColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = prayer.nameBn,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (prayer.isCurrent) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "● চলমান",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    } else if (prayer.isNext) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AmberPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "○ আসন্ন",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberPrimary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = prayer.rangeFormatted.ifBlank { prayer.timeFormatted },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // Notification Bell Toggle
        IconButton(
            onClick = onToggleAlert,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (isAlertOn) Icons.Default.Notifications else Icons.Default.NotificationsNone,
                contentDescription = if (isAlertOn) "Alert Enabled" else "Alert Disabled",
                tint = if (isAlertOn) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/**
 * Nafl Prayer Card inside horizontal scrolling row.
 */
@Composable
fun NaflPrayerCard(
    item: NaflPrayerItem,
    isAlertOn: Boolean,
    onToggleAlert: () -> Unit
) {
    val icon = when (item.iconType) {
        "tahajjud" -> Icons.Default.Nightlight
        "ishraq" -> Icons.Default.Brightness5
        "chasht" -> Icons.Default.WbSunny
        else -> Icons.Default.Brightness6
    }

    Card(
        modifier = Modifier.width(168.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF3B82F6).copy(alpha = 0.12f),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = item.nameBn,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onToggleAlert,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isAlertOn) Icons.Default.Notifications else Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = if (isAlertOn) Color(0xFF2563EB) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.nameBn,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = if (item.startTimeFormatted == "--:--") "শেষ ${item.endTimeFormatted}"
                else if (item.endTimeFormatted == "--:--") "শুরু ${item.startTimeFormatted}"
                else "${item.startTimeFormatted} - ${item.endTimeFormatted}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Forbidden Prayer Time Card with warning badge.
 * Bell/alarm toggle is removed as prayer is strictly forbidden (makruh) during these intervals.
 */
@Composable
fun ForbiddenTimeCard(
    modifier: Modifier = Modifier,
    item: ForbiddenTimeItem
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFEF4444).copy(alpha = 0.12f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.NotInterested,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.nameBn,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.rangeFormatted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Qibla & Kaaba Info Card with compass graphic and navigation button.
 */
@Composable
fun QiblaSummaryCard(
    qiblaDegree: Double,
    distanceKm: Double,
    onCompassClick: () -> Unit
) {
    val roundedDegree = qiblaDegree.toInt()
    val formattedDistance = PrayerConstants.toBengaliDigits(distanceKm.toInt())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stylized Compass Visual
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Spacer(
                    modifier = Modifier
                        .size(70.dp)
                        .drawWithCache {
                            val radius = size.minDimension / 2f
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val ringColor = Color(0xFF10B981).copy(alpha = 0.25f)
                            val tickColor = Color.Gray.copy(alpha = 0.4f)
                            val tickPairs = (0 until 12).map { i ->
                                val angle = (i * 30.0) * Math.PI / 180.0
                                val startR = if (i % 3 == 0) radius - 8f else radius - 4f
                                val p1 = Offset(
                                    center.x + (startR * Math.cos(angle)).toFloat(),
                                    center.y + (startR * Math.sin(angle)).toFloat()
                                )
                                val p2 = Offset(
                                    center.x + (radius * Math.cos(angle)).toFloat(),
                                    center.y + (radius * Math.sin(angle)).toFloat()
                                )
                                Pair(p1, p2)
                            }

                            onDrawBehind {
                                drawCircle(
                                    color = ringColor,
                                    radius = radius,
                                    style = Stroke(width = 3f)
                                )
                                for ((p1, p2) in tickPairs) {
                                    drawLine(
                                        color = tickColor,
                                        start = p1,
                                        end = p2,
                                        strokeWidth = 2f
                                    )
                                }
                            }
                        }
                )

                // Needle pointing to Qibla angle
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier
                        .size(46.dp)
                        .rotate(roundedDegree.toFloat())
                )
            }

            // Information and Action Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "পবিত্র কাবার দিক",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${PrayerConstants.toBengaliDigits(roundedDegree)}°",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "পশ্চিম-দক্ষিণ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "দূরত্ব: $formattedDistance কিঃমিঃ",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                    modifier = Modifier.clickable { onCompassClick() }
                ) {
                    Text(
                        text = "🧭 কিবলা কম্পাস বিস্তারিত >",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
