package com.muslimcompanion.app.ui.screens.ramadan

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import com.muslimcompanion.app.data.constants.RamadanConstants
import com.muslimcompanion.app.data.repository.PrayerRepository
import com.muslimcompanion.app.data.repository.PreferencesRepository
import com.muslimcompanion.app.ui.theme.AmberPrimary
import com.muslimcompanion.app.ui.theme.EmeraldPrimary

@Composable
fun RamadanScreen(
    preferencesRepo: PreferencesRepository,
    onBack: (() -> Unit)? = null
) {
    val currentCity by preferencesRepo.currentCity.collectAsState()
    val calcMethod by preferencesRepo.calculationMethod.collectAsState()
    val madhab by preferencesRepo.madhab.collectAsState()

    val fastingToday by preferencesRepo.fastingToday.collectAsState()
    val completedJuz by preferencesRepo.completedJuz.collectAsState()

    var activeTab by remember { mutableStateOf("overview") }

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
                        text = "রমজান মোবারক প্ল্যানার",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "সেহরি ও ইফতারের সময়সূচি, রোজা ও খতম ট্র্যাকার",
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
                val tabs = listOf(
                    "overview" to "আজকের সূচি",
                    "calendar" to "৩০ দিন",
                    "khatam" to "খতম ট্র্যাকার",
                    "duas" to "রোজার দোয়া"
                )
                tabs.forEach { (key, label) ->
                    val isSelected = activeTab == key
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { activeTab = key },
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(vertical = 10.dp),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (activeTab) {
            "overview" -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Fasting Today Switch Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (fastingToday) EmeraldPrimary.copy(alpha = 0.08f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (fastingToday) EmeraldPrimary else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "আজ কি রোজা রাখছেন?",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (fastingToday) "আলহামদুলিল্লাহ, রোজা সম্পন্ন হচ্ছে" else "রোজার সংকল্প চালু করতে টগল করুন",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Switch(
                                    checked = fastingToday,
                                    onCheckedChange = { preferencesRepo.toggleFastingToday() }
                                )
                            }
                        }
                    }

                    // Sehri & Iftar Cards
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "সেহরির শেষ সময়",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = schedule.fajr.timeFormatted,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "ফজরের পূর্বে সমাপ্ত",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "ইফতারের সময়",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = schedule.maghrib.timeFormatted,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AmberPrimary
                                    )
                                    Text(
                                        text = "মাগরিবের ওয়াক্ত",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }

                    // Ramadan Fasting virtues note
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "সিয়ামের ফজিলত",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "প্রিয় নবী ﷺ বলেছেন: 'যে ব্যক্তি ঈমানের সাথে ও সওয়াবের আশায় রমজানের রোজা রাখবে, তার পূর্বের সকল গুনাহ ক্ষমা করে দেওয়া হবে।' (সহিহ বুখারি)",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }

            "calendar" -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Text(
                            text = "৩০ দিনের সেহরি ও ইফতার সময়সূচি (${currentCity.nameBn})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    items((1..30).toList()) { day ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = PrayerConstants.toBengaliDigits(day),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(
                                        text = "${PrayerConstants.toBengaliDigits(day)} রমজান",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text(
                                        text = "সেহরি: ${schedule.fajr.timeFormatted}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "ইফতার: ${schedule.maghrib.timeFormatted}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberPrimary
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }

            "khatam" -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "৩০ পারা কুরআন খতম ট্র্যাকার",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${PrayerConstants.toBengaliDigits(completedJuz.size)} / ৩০ পারা সম্পন্ন",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    items((1..30).toList()) { juz ->
                        val isDone = completedJuz.contains(juz)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { preferencesRepo.toggleJuz(juz) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDone) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${PrayerConstants.toBengaliDigits(juz)} নং পারা",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )

                                Icon(
                                    imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isDone) EmeraldPrimary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }

            "duas" -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(RamadanConstants.DUAS, key = { it.id }) { dua ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = dua.titleBn,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = dua.occasionBn,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = dua.arabic,
                                    fontSize = 20.sp,
                                    lineHeight = 32.sp,
                                    textAlign = TextAlign.Right,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "উচ্চারণ: ${dua.pronunciationBn}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "অর্থ: ${dua.meaningBn}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "রেফারেন্স: ${dua.reference}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}
