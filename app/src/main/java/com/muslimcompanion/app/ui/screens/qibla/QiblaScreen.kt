package com.muslimcompanion.app.ui.screens.qibla

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.repository.CompassSensorManager
import com.muslimcompanion.app.data.repository.PrayerRepository
import com.muslimcompanion.app.data.repository.PreferencesRepository
import com.muslimcompanion.app.ui.components.CitySelectorBottomSheet
import com.muslimcompanion.app.ui.theme.AmberPrimary
import com.muslimcompanion.app.ui.theme.EmeraldPrimary
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun QiblaScreen(
    preferencesRepo: PreferencesRepository,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val compassManager = remember { CompassSensorManager(context) }

    DisposableEffect(Unit) {
        compassManager.start()
        onDispose {
            compassManager.stop()
        }
    }

    val currentCity by preferencesRepo.currentCity.collectAsState()
    val userDarkMode by preferencesRepo.isDarkMode.collectAsState()
    val systemDark = isSystemInDarkTheme()
    val isDark = if (preferencesRepo.hasUserSelectedDarkMode()) userDarkMode else systemDark

    val compassData by compassManager.compassData.collectAsState()
    val hasHardwareSensor by compassManager.hasSensor.collectAsState()

    var isManualMode by remember { mutableStateOf(false) }
    var manualHeading by remember { mutableFloatStateOf(0f) }
    var showCityBottomSheet by remember { mutableStateOf(false) }

    val qiblaInfo = remember(currentCity) {
        PrayerRepository.calculateQibla(currentCity.latitude, currentCity.longitude)
    }
    val qiblaBearing = qiblaInfo.directionDegrees.toFloat()

    val effectiveHeading = if (isManualMode || !hasHardwareSensor) manualHeading else compassData.heading

    // Continuous angle calculation to eliminate 360-degree reverse spin
    var unwrappedHeading by remember { mutableFloatStateOf(0f) }
    var lastReportedHeading by remember { mutableFloatStateOf(0f) }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(effectiveHeading) {
        if (!isInitialized) {
            unwrappedHeading = effectiveHeading
            lastReportedHeading = effectiveHeading
            isInitialized = true
        } else {
            var diff = (effectiveHeading - lastReportedHeading) % 360f
            if (diff < -180f) diff += 360f
            if (diff > 180f) diff -= 360f
            unwrappedHeading += diff
            lastReportedHeading = effectiveHeading
        }
    }

    val animatedDialRotation by animateFloatAsState(
        targetValue = -unwrappedHeading,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "dialRotation"
    )

    // Relative angle: how far phone heading is from Mecca
    var relativeAngle = (qiblaBearing - effectiveHeading) % 360f
    if (relativeAngle < -180f) relativeAngle += 360f
    if (relativeAngle > 180f) relativeAngle -= 360f

    val isAligned = abs(relativeAngle) <= 3.5f

    // Trigger haptic vibration upon reaching alignment
    var wasAligned by remember { mutableStateOf(false) }
    LaunchedEffect(isAligned) {
        if (isAligned && !wasAligned) {
            triggerCompassVibration(context)
            wasAligned = true
        } else if (!isAligned) {
            wasAligned = false
        }
    }

    // Alignment pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
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
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "কিবলা কম্পাস",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "পবিত্র কাবার নিখুঁত দিক নির্দেশনা",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Quick City Selector Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.clickable { showCityBottomSheet = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "City",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentCity.nameBn,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                        Text(
                            text = " ▾",
                            fontSize = 12.sp,
                            color = EmeraldPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Day / Night Mode Quick Toggle
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.size(36.dp)
                ) {
                    IconButton(onClick = { preferencesRepo.setDarkMode(!isDark) }) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.WbSunny else Icons.Default.Nightlight,
                            contentDescription = if (isDark) "Day Mode" else "Dark Mode",
                            tint = if (isDark) AmberPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hero Alignment Guidance Card
        val cardBorderColor by animateColorAsState(
            targetValue = if (isAligned) EmeraldPrimary else MaterialTheme.colorScheme.outline,
            label = "cardBorder"
        )
        val cardBgColor by animateColorAsState(
            targetValue = if (isAligned) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
            label = "cardBg"
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBgColor),
            border = BorderStroke(1.5.dp, cardBorderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isAligned) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isAligned) {
                            Text(text = "🕋", fontSize = 22.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    if (isAligned) {
                        Text(
                            text = "আপনি সরাসরি কাবার মুখোমুখি আছেন!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldPrimary
                        )
                        Text(
                            text = "নিখুঁত কিবলা কোণ: ${PrayerConstants.toBengaliDigits(qiblaBearing.roundToInt())}° (${getCardinalNameBn(qiblaBearing)})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        val turnAngle = abs(relativeAngle).roundToInt()
                        val turnDirection = if (relativeAngle > 0) "ডানে" else "বামে"
                        val turnArrow = if (relativeAngle > 0) "👉" else "👈"

                        Text(
                            text = "$turnArrow $turnDirection ${PrayerConstants.toBengaliDigits(turnAngle)}° ঘুরুন",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "কম্পাসের সবুজ নির্দেশক ও কাবা আইকনের সাথে মিলান",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Realistic Compass Dial
        RealisticCompassInstrument(
            dialRotation = animatedDialRotation,
            qiblaBearing = qiblaBearing,
            isAligned = isAligned,
            isFlat = compassData.isFlat,
            pitch = compassData.pitch,
            roll = compassData.roll,
            pulseAlpha = pulseAlpha,
            isManualMode = isManualMode,
            onManualDrag = { deltaAngle ->
                manualHeading = (manualHeading + deltaAngle + 360f) % 360f
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Flat Surface Alert Banner if tilted
        if (!compassData.isFlat && !isManualMode && hasHardwareSensor) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AmberPrimary.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ScreenRotation,
                        contentDescription = "Tilt",
                        tint = AmberPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "মোবাইলটি অনুভূমিকভাবে সমতলে রাখুন (মাঝের বৃত্তে বাবল মেলান)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AmberPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Metrics Grid (3 Info Cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Phone Heading
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "মোবাইলের মুখ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${PrayerConstants.toBengaliDigits(effectiveHeading.roundToInt())}°",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = getCardinalShortBn(effectiveHeading),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Qibla Angle
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "কাবার কোণ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${PrayerConstants.toBengaliDigits(qiblaInfo.directionDegrees)}°",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldPrimary
                    )
                    Text(
                        text = "উত্তর হতে",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Makkah Distance
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "মক্কার দূরত্ব",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${PrayerConstants.toBengaliDigits(qiblaInfo.distanceKm.toInt())}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "কিলোমিটার",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // City Selector Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.clickable { showCityBottomSheet = true }
                ) {
                    Text(
                        text = "📍 শহর বদলান ▾",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                }
            }

            items(PrayerConstants.POPULAR_CITIES.take(8)) { city ->
                val isSelected = currentCity.name == city.name
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.clickable { preferencesRepo.saveCity(city) }
                ) {
                    Text(
                        text = city.nameBn,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Manual Test Mode & Calibration Details
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "Manual",
                            tint = if (isManualMode) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "ম্যানুয়াল টেস্ট মোড (হাত দিয়ে ঘোরান)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isManualMode) "কম্পাসে আঙুল দিয়ে ঘুরিয়ে টেস্ট করুন" else "স্বয়ংক্রিয় কম্পাস সেন্সর চলছে",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isManualMode,
                        onCheckedChange = { isManualMode = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = EmeraldPrimary
                        )
                    )
                }

                if (isManualMode) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "দিক কোণ:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${PrayerConstants.toBengaliDigits(manualHeading.roundToInt())}°",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldPrimary,
                            modifier = Modifier.width(44.dp)
                        )
                        Slider(
                            value = manualHeading,
                            onValueChange = { manualHeading = it },
                            valueRange = 0f..360f,
                            colors = SliderDefaults.colors(
                                thumbColor = EmeraldPrimary,
                                activeTrackColor = EmeraldPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, EmeraldPrimary),
                                modifier = Modifier.clickable { manualHeading = qiblaBearing }
                            ) {
                                Text(
                                    text = "🕋 কাবার দিক (${PrayerConstants.toBengaliDigits(qiblaBearing.roundToInt())}°)",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldPrimary
                                )
                            }
                        }
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { manualHeading = 0f }
                            ) {
                                Text(
                                    text = "উত্তর (০°)",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { manualHeading = 90f }
                            ) {
                                Text(
                                    text = "পূর্ব (৯০°)",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { manualHeading = 180f }
                            ) {
                                Text(
                                    text = "দক্ষিণ (১৮০°)",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { manualHeading = 270f }
                            ) {
                                Text(
                                    text = "পশ্চিম (২৭০°)",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "পবিত্র কাবা স্থানাঙ্ক: ২১.৪২২৫° উত্তর, ৩৯.৮২৬২° পূর্ব",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (hasHardwareSensor) "সেন্সর সক্রিয়" else "সিমুলেশন মোড",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasHardwareSensor) EmeraldPrimary else AmberPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
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
 * Realistic Dual-Layered Compass Instrument
 * Contains:
 * - Rotating Bezel Dial with 360 precision ticks, cardinal letters (N, E, S, W in Bangla/English), and degree numbers
 * - Golden Kaaba badge & radial ray at Qibla bearing
 * - Center Spirit Bubble Level (detects tilt)
 * - Fixed Top Reference Lubber Notch
 * - Pulsing Emerald Aura when aligned
 */
@Composable
fun RealisticCompassInstrument(
    dialRotation: Float,
    qiblaBearing: Float,
    isAligned: Boolean,
    isFlat: Boolean,
    pitch: Float,
    roll: Float,
    pulseAlpha: Float,
    isManualMode: Boolean,
    onManualDrag: (Float) -> Unit
) {
    val density = LocalDensity.current
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val outlineColor = MaterialTheme.colorScheme.outline
    val primaryColor = MaterialTheme.colorScheme.primary

    val dialSize = 310.dp

    Box(
        modifier = Modifier
            .size(dialSize)
            .pointerInput(isManualMode) {
                if (isManualMode) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        // Convert horizontal drag to angle change
                        val delta = dragAmount.x * 0.5f
                        onManualDrag(-delta)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // 1. Alignment Halo Glow (outer radiant ring when aligned)
        if (isAligned) {
            Canvas(modifier = Modifier.size(dialSize + 24.dp)) {
                drawCircle(
                    color = EmeraldPrimary.copy(alpha = pulseAlpha * 0.28f),
                    radius = size.minDimension / 2
                )
                drawCircle(
                    color = EmeraldPrimary.copy(alpha = pulseAlpha * 0.45f),
                    radius = size.minDimension / 2 - 8.dp.toPx(),
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }

        // 2. Bezel Frame & Outer Dial Background
        Canvas(modifier = Modifier.size(dialSize)) {
            val center = Offset(size.width / 2, size.height / 2)
            val outerRadius = size.minDimension / 2

            // Outer bezel ring
            drawCircle(
                color = surfaceColor,
                radius = outerRadius,
                center = center
            )
            // Metallic rim
            drawCircle(
                color = if (isAligned) EmeraldPrimary else outlineColor.copy(alpha = 0.7f),
                radius = outerRadius - 1.dp.toPx(),
                center = center,
                style = Stroke(width = if (isAligned) 3.dp.toPx() else 1.5.dp.toPx())
            )
            // Inner track ring
            drawCircle(
                color = outlineColor.copy(alpha = 0.25f),
                radius = outerRadius - 28.dp.toPx(),
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
            // Second inner track ring
            drawCircle(
                color = outlineColor.copy(alpha = 0.15f),
                radius = outerRadius - 46.dp.toPx(),
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // 3. Rotating Compass Dial (ticks, degrees, cardinals, and Kaaba marker)
        Box(
            modifier = Modifier
                .size(dialSize)
                .rotate(dialRotation),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2
                val cy = size.height / 2
                val center = Offset(cx, cy)
                val outerRadius = size.minDimension / 2 - 2.dp.toPx()

                // Precision Ticks (every 2 degrees, 10 degrees, and 30 degrees)
                for (deg in 0 until 360 step 2) {
                    val rad = Math.toRadians(deg.toDouble())
                    val cosRad = cos(rad).toFloat()
                    val sinRad = sin(rad).toFloat()

                    val isMajor = deg % 30 == 0
                    val isMedium = deg % 10 == 0 && !isMajor

                    val tickLength = when {
                        isMajor -> 14.dp.toPx()
                        isMedium -> 9.dp.toPx()
                        else -> 5.dp.toPx()
                    }

                    val tickWidth = when {
                        isMajor -> 2.5.dp.toPx()
                        isMedium -> 1.5.dp.toPx()
                        else -> 0.8.dp.toPx()
                    }

                    val tickColor = when {
                        deg == 0 -> Color(0xFFEF4444) // North in Red
                        isMajor -> onSurfaceColor.copy(alpha = 0.85f)
                        isMedium -> onSurfaceColor.copy(alpha = 0.55f)
                        else -> onSurfaceColor.copy(alpha = 0.25f)
                    }

                    val start = Offset(
                        cx + (outerRadius - tickLength) * cosRad,
                        cy + (outerRadius - tickLength) * sinRad
                    )
                    val end = Offset(
                        cx + outerRadius * cosRad,
                        cy + outerRadius * sinRad
                    )

                    drawLine(
                        color = tickColor,
                        start = start,
                        end = end,
                        strokeWidth = tickWidth
                    )
                }

                // Draw Degree Numbers & Cardinals using Android native canvas
                val nativeCanvas = drawContext.canvas.nativeCanvas

                val numberPaint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.CENTER
                    color = onSurfaceColor.copy(alpha = 0.6f).toArgb()
                    textSize = with(density) { 9.sp.toPx() }
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                }

                val cardinalPaint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = with(density) { 13.sp.toPx() }
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                }

                // Degree numerals at every 30 degrees (30, 60, 90, 120, ...)
                for (deg in 0 until 360 step 30) {
                    nativeCanvas.save()
                    nativeCanvas.rotate(deg.toFloat(), cx, cy)
                    val textY = cy - outerRadius + 24.dp.toPx()
                    nativeCanvas.drawText("$deg°", cx, textY, numberPaint)
                    nativeCanvas.restore()
                }

                // Cardinal Labels (N, E, S, W)
                val cardinals = listOf(
                    Pair(0f, "উ (N)"),
                    Pair(90f, "পূ (E)"),
                    Pair(180f, "দ (S)"),
                    Pair(270f, "প (W)")
                )

                for ((deg, label) in cardinals) {
                    nativeCanvas.save()
                    nativeCanvas.rotate(deg, cx, cy)
                    cardinalPaint.color = if (deg == 0f) {
                        android.graphics.Color.parseColor("#EF4444")
                    } else {
                        onSurfaceColor.toArgb()
                    }
                    val textY = cy - outerRadius + 38.dp.toPx()
                    nativeCanvas.drawText(label, cx, textY, cardinalPaint)
                    nativeCanvas.restore()
                }

                // Inner Islamic 8-Pointed Star Rosette Watermark
                val starRadius = outerRadius - 62.dp.toPx()
                val starPath1 = Path()
                val starPath2 = Path()

                for (i in 0 until 4) {
                    val angle1 = Math.toRadians((i * 90.0))
                    val x1 = cx + starRadius * cos(angle1).toFloat()
                    val y1 = cy + starRadius * sin(angle1).toFloat()
                    if (i == 0) starPath1.moveTo(x1, y1) else starPath1.lineTo(x1, y1)

                    val angle2 = Math.toRadians((i * 90.0 + 45.0))
                    val x2 = cx + starRadius * cos(angle2).toFloat()
                    val y2 = cy + starRadius * sin(angle2).toFloat()
                    if (i == 0) starPath2.moveTo(x2, y2) else starPath2.lineTo(x2, y2)
                }
                starPath1.close()
                starPath2.close()

                drawPath(starPath1, color = primaryColor.copy(alpha = 0.04f))
                drawPath(starPath2, color = primaryColor.copy(alpha = 0.04f))
                drawPath(starPath1, color = outlineColor.copy(alpha = 0.15f), style = Stroke(width = 1.dp.toPx()))
                drawPath(starPath2, color = outlineColor.copy(alpha = 0.15f), style = Stroke(width = 1.dp.toPx()))
            }

            // Kaaba Needle / Pointer locked to Qibla angle on the dial
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(qiblaBearing),
                contentAlignment = Alignment.Center
            ) {
                // Radial Golden Beam from center to Kaaba marker
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    val outerRadius = size.minDimension / 2 - 2.dp.toPx()

                    // Glowing Golden/Emerald line pointing to Mecca
                    drawLine(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                AmberPrimary,
                                EmeraldPrimary.copy(alpha = 0.8f),
                                Color.Transparent
                            ),
                            startY = cy - outerRadius,
                            endY = cy
                        ),
                        start = Offset(cx, cy - 25.dp.toPx()),
                        end = Offset(cx, cy - outerRadius + 6.dp.toPx()),
                        strokeWidth = 3.dp.toPx()
                    )

                    // Directional 3D Arrow Head towards Kaaba
                    val arrowPath = Path().apply {
                        moveTo(cx, cy - outerRadius + 8.dp.toPx()) // Tip
                        lineTo(cx + 8.dp.toPx(), cy - outerRadius + 26.dp.toPx())
                        lineTo(cx, cy - outerRadius + 20.dp.toPx())
                        lineTo(cx - 8.dp.toPx(), cy - outerRadius + 26.dp.toPx())
                        close()
                    }
                    drawPath(arrowPath, color = EmeraldPrimary)

                    // Opposite Tail (South)
                    val tailPath = Path().apply {
                        moveTo(cx, cy + outerRadius - 18.dp.toPx())
                        lineTo(cx + 6.dp.toPx(), cy + outerRadius - 32.dp.toPx())
                        lineTo(cx - 6.dp.toPx(), cy + outerRadius - 32.dp.toPx())
                        close()
                    }
                    drawPath(tailPath, color = Color.Gray.copy(alpha = 0.45f))
                }

                // 3D Kaaba Badge at the Qibla Bearing on the dial
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 22.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                        .border(BorderStroke(1.5.dp, AmberPrimary), RoundedCornerShape(8.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🕋",
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "কাবা",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 4. Center Pivot Hub & Spirit Level (Bubble Level)
        // Bubble moves based on roll (X) and pitch (Y)
        val maxBubbleOffset = 14.dp.value
        val clampedRoll = (roll / 25f).coerceIn(-1f, 1f)
        val clampedPitch = (pitch / 25f).coerceIn(-1f, 1f)
        val bubbleX = clampedRoll * maxBubbleOffset
        val bubbleY = clampedPitch * maxBubbleOffset

        Surface(
            shape = CircleShape,
            color = surfaceColor,
            border = BorderStroke(2.dp, if (isAligned) EmeraldPrimary else outlineColor),
            modifier = Modifier
                .size(54.dp)
                .shadow(4.dp, CircleShape)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Crosshairs
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    // Target ring
                    drawCircle(
                        color = outlineColor.copy(alpha = 0.5f),
                        radius = 8.dp.toPx(),
                        style = Stroke(width = 1.dp.toPx())
                    )
                    // Crosshair lines
                    drawLine(
                        color = outlineColor.copy(alpha = 0.4f),
                        start = Offset(cx - 12.dp.toPx(), cy),
                        end = Offset(cx + 12.dp.toPx(), cy),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = outlineColor.copy(alpha = 0.4f),
                        start = Offset(cx, cy - 12.dp.toPx()),
                        end = Offset(cx, cy + 12.dp.toPx()),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Spirit Bubble
                val bubbleColor = if (isFlat) EmeraldPrimary else AmberPrimary
                Surface(
                    shape = CircleShape,
                    color = bubbleColor.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .size(14.dp)
                        .offset { IntOffset(bubbleX.roundToInt(), bubbleY.roundToInt()) }
                ) {}
            }
        }

        // 5. Fixed Top Lubber Notch (Reference Indicator at 12 o'clock, NOT rotating)
        Canvas(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(24.dp, 18.dp)
        ) {
            val cx = size.width / 2
            val notchPath = Path().apply {
                moveTo(cx - 9.dp.toPx(), 0f)
                lineTo(cx + 9.dp.toPx(), 0f)
                lineTo(cx, 16.dp.toPx()) // Triangle tip pointing inward to dial
                close()
            }
            drawPath(
                notchPath,
                color = if (isAligned) EmeraldPrimary else AmberPrimary
            )
            // Accent highlight line
            drawLine(
                color = Color.White,
                start = Offset(cx, 0f),
                end = Offset(cx, 14.dp.toPx()),
                strokeWidth = 1.5.dp.toPx()
            )
        }
    }
}

/**
 * Trigger crisp, single vibration feedback on alignment
 */
private fun triggerCompassVibration(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(
                VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(50)
            }
        }
    } catch (_: Exception) {}
}

/**
 * Full Bengali cardinal direction name
 */
private fun getCardinalNameBn(degrees: Float): String {
    val norm = ((degrees % 360f) + 360f) % 360f
    return when {
        norm >= 337.5f || norm < 22.5f -> "উত্তর"
        norm >= 22.5f && norm < 67.5f -> "উত্তর-পূর্ব"
        norm >= 67.5f && norm < 112.5f -> "পূর্ব"
        norm >= 112.5f && norm < 157.5f -> "দক্ষিণ-পূর্ব"
        norm >= 157.5f && norm < 202.5f -> "দক্ষিণ"
        norm >= 202.5f && norm < 247.5f -> "দক্ষিণ-পশ্চিম"
        norm >= 247.5f && norm < 292.5f -> "পশ্চিম"
        else -> "উত্তর-পশ্চিম"
    }
}

/**
 * Short cardinal direction with English acronym
 */
private fun getCardinalShortBn(degrees: Float): String {
    val norm = ((degrees % 360f) + 360f) % 360f
    return when {
        norm >= 337.5f || norm < 22.5f -> "উত্তর (N)"
        norm >= 22.5f && norm < 67.5f -> "উত্তর-পূর্ব (NE)"
        norm >= 67.5f && norm < 112.5f -> "পূর্ব (E)"
        norm >= 112.5f && norm < 157.5f -> "দক্ষিণ-পূর্ব (SE)"
        norm >= 157.5f && norm < 202.5f -> "দক্ষিণ (S)"
        norm >= 202.5f && norm < 247.5f -> "দক্ষিণ-পশ্চিম (SW)"
        norm >= 247.5f && norm < 292.5f -> "পশ্চিম (W)"
        else -> "উত্তর-পশ্চিম (NW)"
    }
}
