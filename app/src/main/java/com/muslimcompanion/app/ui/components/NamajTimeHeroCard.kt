package com.muslimcompanion.app.ui.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.model.DayPrayerSchedule
import com.muslimcompanion.app.ui.theme.EmeraldPrimary
import java.util.Calendar
import kotlin.math.roundToInt

/**
 * Creative Live Animated Namaj Time Hero Card modeled 1:1 after 'Muslim Bangla'.
 * Features:
 * - Parabolic celestial sky arc with warm gradient glow fill beneath the passed trajectory.
 * - Solid golden stroke for passed time and dashed stroke for remaining time.
 * - Live animated celestial pill (Moon / Sun) with current time in Bengali digits,
 *   subtle floating bobbing, breathing scale, and pulsing radial halo rings.
 * - Mosque architectural silhouette with domes and minarets in the background.
 * - Balanced two-pillar layout (Left: Prayer Start time; Right: Prayer/Tahajjud End time with checkmark).
 * - Left and Right horizon anchors for Sunset and Sunrise with custom vector icons.
 * - Active prayer card with bold title, full start-end range, smooth animated emerald progress bar,
 *   and live pulsing beacon dot with radar ripple wave.
 */
@Composable
fun NamajTimeHeroCard(
    schedule: DayPrayerSchedule,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    // Card Palette
    val cardBgBrush = if (isDark) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF111E2E),
                Color(0xFF09131F)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFFF1F5F9),
                Color(0xFFE8EEF5)
            )
        )
    }

    val cardBorder = if (isDark) {
        BorderStroke(1.dp, Color(0xFF1E3248))
    } else {
        BorderStroke(1.dp, Color(0xFFDCE4ED))
    }

    val silhouetteColor = if (isDark) {
        Color(0xFF223A52).copy(alpha = 0.38f)
    } else {
        Color(0xFFBAC9D8).copy(alpha = 0.35f)
    }

    val primaryTextColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
    val secondaryTextColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val dividerColor = if (isDark) Color(0xFF283F57) else Color(0xFFCBD5E1)
    val trackColor = if (isDark) Color(0xFF1D2E42) else Color(0xFFE2E8F0)

    val pillBg = if (isDark) {
        Color(0xFF162537).copy(alpha = 0.92f)
    } else {
        Color.White.copy(alpha = 0.94f)
    }
    val pillBorder = if (isDark) Color(0xFF2E4661) else Color(0xFFCBD5E1)

    // Live Infinite Transitions
    val infiniteTransition = rememberInfiniteTransition(label = "hero_infinite_anims")

    // Breathing scale for celestial icon
    val celestialScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "celestial_scale"
    )

    // Gentle floating bob
    val celestialBob by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "celestial_bob"
    )

    // Pulsing halo ripple around celestial pill
    val haloProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "halo_ripple"
    )

    // Live beacon sonar wave on "চলমান"
    val beaconPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "beacon_sonar"
    )

    // Smooth animated progress fraction
    val animatedProgress by animateFloatAsState(
        targetValue = schedule.progressFraction.coerceIn(0.04f, 1f),
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "progress_anim"
    )

    // Smooth celestial progress across the sky arc (clamped safely between 0.08 and 0.92)
    val targetCelestialPct = schedule.celestialProgress.coerceIn(0.08f, 0.92f)
    val animatedCelestialPct by animateFloatAsState(
        targetValue = targetCelestialPct,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "celestial_pct_anim"
    )

    // Current live Bengali time for the badge (updated every minute)
    val liveBengaliTime = remember {
        val cal = Calendar.getInstance()
        val rawHour = cal.get(Calendar.HOUR)
        val hour = if (rawHour == 0) 12 else rawHour
        val minute = cal.get(Calendar.MINUTE)
        val minPadded = if (minute < 10) "০${PrayerConstants.toBengaliDigits(minute)}" else PrayerConstants.toBengaliDigits(minute)
        "${PrayerConstants.toBengaliDigits(hour)}:$minPadded"
    }

    // Active prayer display values
    val activeTitle = schedule.activePrayerName.ifBlank { schedule.currentPrayer ?: "ফজর" }
    val activeRange = schedule.activePrayerRange.ifBlank {
        "${schedule.activePrayerTime.ifBlank { schedule.fajr.timeFormatted }} - ${schedule.sahurEndTimeFormatted.ifBlank { "০৪:৩৬" }}"
    }

    // Pillars display values
    val leftPillarDisplay = if (schedule.leftPillarTitle.isNotBlank()) {
        "${schedule.leftPillarTitle} ${schedule.leftPillarTime}"
    } else {
        "$activeTitle ${schedule.activePrayerTime.ifBlank { "০৬:৪৫" }} মি."
    }

    val rightPillarDisplay = if (schedule.rightPillarTitle.isNotBlank()) {
        "${schedule.rightPillarTitle} ${schedule.rightPillarTime}"
    } else {
        "তাহাজ্জুদ শেষ ${schedule.sahurEndTimeFormatted.ifBlank { "০৪:৩৭" }} মি."
    }

    val hasCheck = schedule.rightPillarHasCheck ||
            rightPillarDisplay.contains("তাহাজ্জুদ") ||
            rightPillarDisplay.contains("সাহরি")

    val density = LocalDensity.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = cardBorder,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBgBrush)
        ) {
            // ==========================================
            // TOP SECTION: CELESTIAL SKY & LIVE ARC
            // ==========================================
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                val wPx = constraints.maxWidth.toFloat()
                val hPx = with(density) { 160.dp.toPx() }

                // Parabolic Arc Geometry
                val baseY = hPx * 0.90f
                val peakY = with(density) { 18.dp.toPx() }
                val startX = wPx * 0.05f
                val endX = wPx * 0.95f
                val spanX = endX - startX

                fun arcAt(s: Float): Offset {
                    val x = startX + s * spanX
                    val norm = s - 0.5f
                    val y = peakY + 4f * (baseY - peakY) * (norm * norm)
                    return Offset(x, y)
                }

                val currentPos = arcAt(animatedCelestialPct)

                // 1. Canvas: Mosque Silhouette + Celestial Arc + Gradient Fill
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // 1A. Background Mosque Silhouette
                    val mosquePath = Path().apply {
                        moveTo(0f, h)
                        lineTo(0f, h * 0.65f)

                        // Left Minaret
                        lineTo(w * 0.12f, h * 0.65f)
                        lineTo(w * 0.12f, h * 0.30f)
                        lineTo(w * 0.105f, h * 0.30f)
                        lineTo(w * 0.105f, h * 0.28f)
                        lineTo(w * 0.135f, h * 0.28f)
                        lineTo(w * 0.135f, h * 0.30f)
                        lineTo(w * 0.12f, h * 0.30f)
                        lineTo(w * 0.128f, h * 0.16f) // Left spire
                        lineTo(w * 0.136f, h * 0.30f)
                        lineTo(w * 0.136f, h * 0.65f)
                        lineTo(w * 0.22f, h * 0.65f)

                        // Left Side Dome
                        quadraticTo(w * 0.28f, h * 0.46f, w * 0.35f, h * 0.65f)

                        // Grand Central Onion Dome
                        lineTo(w * 0.38f, h * 0.65f)
                        cubicTo(
                            w * 0.39f, h * 0.40f,
                            w * 0.45f, h * 0.26f,
                            w * 0.50f, h * 0.22f
                        )
                        cubicTo(
                            w * 0.55f, h * 0.26f,
                            w * 0.61f, h * 0.40f,
                            w * 0.62f, h * 0.65f
                        )

                        // Right Side Dome
                        lineTo(w * 0.65f, h * 0.65f)
                        quadraticTo(w * 0.72f, h * 0.46f, w * 0.78f, h * 0.65f)

                        // Right Minaret
                        lineTo(w * 0.864f, h * 0.65f)
                        lineTo(w * 0.864f, h * 0.30f)
                        lineTo(w * 0.850f, h * 0.30f)
                        lineTo(w * 0.850f, h * 0.28f)
                        lineTo(w * 0.880f, h * 0.28f)
                        lineTo(w * 0.880f, h * 0.30f)
                        lineTo(w * 0.864f, h * 0.30f)
                        lineTo(w * 0.872f, h * 0.16f) // Right spire
                        lineTo(w * 0.880f, h * 0.30f)
                        lineTo(w * 0.880f, h * 0.65f)
                        lineTo(w, h * 0.65f)
                        lineTo(w, h)
                        close()
                    }
                    drawPath(path = mosquePath, color = silhouetteColor, style = Fill)

                    // 1B. Gradient Light Fill under Passed Arc
                    val fillPath = Path().apply {
                        val pStart = arcAt(0f)
                        moveTo(pStart.x, baseY)
                        lineTo(pStart.x, pStart.y)
                        val steps = 30
                        for (i in 1..steps) {
                            val s = (i.toFloat() / steps) * animatedCelestialPct
                            val pt = arcAt(s)
                            lineTo(pt.x, pt.y)
                        }
                        lineTo(currentPos.x, baseY)
                        close()
                    }
                    val arcFillBrush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF59E0B).copy(alpha = if (isDark) 0.16f else 0.18f),
                            Color(0xFFFDE68A).copy(alpha = 0.01f)
                        ),
                        startY = peakY,
                        endY = baseY
                    )
                    drawPath(path = fillPath, brush = arcFillBrush, style = Fill)

                    // 1C. Solid Golden Stroke for Passed Path
                    val passedPath = Path().apply {
                        val pStart = arcAt(0f)
                        moveTo(pStart.x, pStart.y)
                        val steps = 35
                        for (i in 1..steps) {
                            val s = (i.toFloat() / steps) * animatedCelestialPct
                            val pt = arcAt(s)
                            lineTo(pt.x, pt.y)
                        }
                    }
                    drawPath(
                        path = passedPath,
                        color = Color(0xFFF59E0B),
                        style = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // 1D. Dashed Golden Stroke for Future Path
                    val futurePath = Path().apply {
                        moveTo(currentPos.x, currentPos.y)
                        val steps = 35
                        val remainingFrac = 1f - animatedCelestialPct
                        for (i in 1..steps) {
                            val s = animatedCelestialPct + (i.toFloat() / steps) * remainingFrac
                            val pt = arcAt(s)
                            lineTo(pt.x, pt.y)
                        }
                    }
                    drawPath(
                        path = futurePath,
                        color = Color(0xFFF59E0B).copy(alpha = if (isDark) 0.50f else 0.60f),
                        style = Stroke(
                            width = 1.8.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f),
                            cap = StrokeCap.Round
                        )
                    )

                    // Endpoints dots
                    drawCircle(color = Color(0xFFF59E0B), radius = 3.2.dp.toPx(), center = arcAt(0f))
                    drawCircle(color = Color(0xFFF59E0B).copy(alpha = 0.65f), radius = 3.2.dp.toPx(), center = arcAt(1f))
                }

                // 2. Two Pillars Under the Arc (Left: Prayer Start | Right: Prayer/Tahajjud End)
                // Positioned comfortably below the apex so the moon never overlaps the text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .offset(y = 66.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Pillar (Centered in Left Half)
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = leftPillarDisplay,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryTextColor
                        )
                    }

                    // Center hairline divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(20.dp)
                            .background(dividerColor)
                    )

                    // Right Pillar (Centered in Right Half)
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = rightPillarDisplay,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryTextColor
                            )

                            if (hasCheck) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = EmeraldPrimary.copy(alpha = 0.16f),
                                    border = BorderStroke(0.6.dp, EmeraldPrimary.copy(alpha = 0.45f)),
                                    modifier = Modifier.size(17.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = EmeraldPrimary,
                                            modifier = Modifier.size(11.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Horizon Anchors (Bottom Corners: Sunset & Sunrise)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Horizon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SunsetHorizonIcon(tint = secondaryTextColor)
                        Text(
                            text = if (schedule.isNight) schedule.sunsetTimeFormatted.ifBlank { "৫:২৯" } else schedule.sunriseTimeFormatted.ifBlank { "৫:৫৭" },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = secondaryTextColor
                        )
                    }

                    // Right Horizon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SunriseHorizonIcon(tint = secondaryTextColor)
                        Text(
                            text = if (schedule.isNight) schedule.sunriseTimeFormatted.ifBlank { "৫:৫৭" } else schedule.sunsetTimeFormatted.ifBlank { "৫:২৯" },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = secondaryTextColor
                        )
                    }
                }

                // 4. Live Celestial Pill Badge on the Arc with Pulsing Halo
                val pillHalfWidthPx = with(density) { 36.dp.toPx() }
                val pillHalfHeightPx = with(density) { 14.dp.toPx() }
                val bobPx = with(density) { celestialBob.dp.toPx() }

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (currentPos.x - pillHalfWidthPx).roundToInt(),
                                (currentPos.y - pillHalfHeightPx + bobPx).roundToInt()
                            )
                        }
                        .scale(celestialScale),
                    contentAlignment = Alignment.Center
                ) {
                    // Pulsing Sonar Halo Ring
                    Canvas(modifier = Modifier.size(54.dp)) {
                        val maxR = size.minDimension / 2f
                        val curR = haloProgress * maxR
                        val curA = (1f - haloProgress).coerceIn(0f, 1f) * 0.45f
                        drawCircle(
                            color = Color(0xFFF59E0B).copy(alpha = curA),
                            radius = curR
                        )
                    }

                    // Frosted Pill Surface
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = pillBg,
                        border = BorderStroke(1.dp, pillBorder),
                        shadowElevation = if (isDark) 0.dp else 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (schedule.isNight) Icons.Default.Nightlight else Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = liveBengaliTime,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryTextColor
                            )
                        }
                    }
                }
            }

            // ==========================================
            // BOTTOM SECTION: ACTIVE PRAYER & COUNTDOWN
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top Row: Active Prayer Title & Range
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = activeTitle,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryTextColor
                    )

                    Text(
                        text = activeRange,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryTextColor
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar: Thick rounded track with vibrant emerald fill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(trackColor)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(5.dp))
                            .background(EmeraldPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Row: Live Beacon Status & Remaining Countdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Live Beacon Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Expanding radar sonar wave
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val maxR = size.minDimension / 2f
                                val curR = beaconPulse * maxR
                                val curA = (1f - beaconPulse).coerceIn(0f, 1f) * 0.75f
                                drawCircle(
                                    color = EmeraldPrimary.copy(alpha = curA),
                                    radius = curR
                                )
                            }
                            // Solid core dot
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(EmeraldPrimary, CircleShape)
                            )
                        }

                        Text(
                            text = schedule.activePrayerStatus.ifBlank { "চলমান" },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPrimary
                        )
                    }

                    // Right: Remaining Countdown
                    Text(
                        text = schedule.formattedRemainingNext.ifBlank { "৫ ঘণ্টা ১৬ মিনিট বাকি" },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = secondaryTextColor
                    )
                }
            }
        }
    }
}

/**
 * Custom vector icon representing Sunset (setting sun over horizon line).
 */
@Composable
private fun SunsetHorizonIcon(
    modifier: Modifier = Modifier,
    tint: Color
) {
    Canvas(modifier = modifier.size(16.dp, 12.dp)) {
        val w = size.width
        val h = size.height
        val horizonY = h * 0.72f

        // Horizon baseline
        drawLine(
            color = tint,
            start = Offset(0f, horizonY),
            end = Offset(w, horizonY),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Setting sun arc
        drawArc(
            color = tint,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.25f, horizonY - w * 0.25f),
            size = Size(w * 0.50f, w * 0.50f),
            style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
        )

        // Downward arrow/ray indicator
        drawLine(
            color = tint,
            start = Offset(w * 0.50f, 0f),
            end = Offset(w * 0.50f, horizonY - w * 0.30f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

/**
 * Custom vector icon representing Sunrise (rising sun over horizon with rays).
 */
@Composable
private fun SunriseHorizonIcon(
    modifier: Modifier = Modifier,
    tint: Color
) {
    Canvas(modifier = modifier.size(16.dp, 12.dp)) {
        val w = size.width
        val h = size.height
        val horizonY = h * 0.72f

        // Horizon baseline
        drawLine(
            color = tint,
            start = Offset(0f, horizonY),
            end = Offset(w, horizonY),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Rising sun arc
        drawArc(
            color = tint,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(w * 0.25f, horizonY - w * 0.25f),
            size = Size(w * 0.50f, w * 0.50f),
            style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
        )

        // Center vertical ray
        drawLine(
            color = tint,
            start = Offset(w * 0.50f, horizonY - w * 0.32f),
            end = Offset(w * 0.50f, 0f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Left diagonal ray
        drawLine(
            color = tint,
            start = Offset(w * 0.22f, horizonY - w * 0.18f),
            end = Offset(w * 0.08f, horizonY - w * 0.38f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Right diagonal ray
        drawLine(
            color = tint,
            start = Offset(w * 0.78f, horizonY - w * 0.18f),
            end = Offset(w * 0.92f, horizonY - w * 0.38f),
            strokeWidth = 1.4.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
