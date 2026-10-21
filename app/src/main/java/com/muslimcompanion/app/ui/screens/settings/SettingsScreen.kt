package com.muslimcompanion.app.ui.screens.settings

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.repository.PreferencesRepository
import com.muslimcompanion.app.ui.components.CitySelectorBottomSheet
import com.muslimcompanion.app.ui.components.PrayerSettingsBottomSheet
import com.muslimcompanion.app.ui.theme.AmberPrimary
import com.muslimcompanion.app.ui.theme.EmeraldPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    preferencesRepo: PreferencesRepository,
    onBack: (() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val currentCity by preferencesRepo.currentCity.collectAsState()
    val calcMethod by preferencesRepo.calculationMethod.collectAsState()
    val madhab by preferencesRepo.madhab.collectAsState()
    val readerSettings by preferencesRepo.readerSettings.collectAsState()
    val isDarkMode by preferencesRepo.isDarkMode.collectAsState()
    val savedUserName by preferencesRepo.userName.collectAsState()

    var userNameInput by remember(savedUserName) { mutableStateOf(savedUserName) }
    var savedSuccess by remember { mutableStateOf(false) }

    var showCityBottomSheet by remember { mutableStateOf(false) }
    var showPrayerSettingsBottomSheet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // 1. Header Card with Save Button
        item {
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
                                text = "অ্যাপ সেটিংস",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "আপনার অবস্থান, নামাজের হিসাব পদ্ধতি ও পছন্দের থিম নির্বাচন করুন",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        onClick = {
                            preferencesRepo.saveUserName(userNameInput)
                            savedSuccess = true
                            coroutineScope.launch {
                                delay(2000)
                                savedSuccess = false
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = if (savedSuccess) EmeraldPrimary else MaterialTheme.colorScheme.onSurface,
                        border = BorderStroke(1.dp, if (savedSuccess) EmeraldPrimary else MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (savedSuccess) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "সংরক্ষিত!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            } else {
                                Text(
                                    text = "সেভ করুন",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. User Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmeraldPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "আপনার নাম (ঐচ্ছিক)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    OutlinedTextField(
                        value = userNameInput,
                        onValueChange = { userNameInput = it },
                        placeholder = {
                            Text(
                                text = "আপনার নাম লিখুন...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background
                        )
                    )

                    Text(
                        text = "নাম যুক্ত করলে হোম স্ক্রিনের ইসলামিক শুভেচ্ছা বার্তায় আপনার নাম প্রদর্শিত হবে। কোনো কাল্পনিক ডেমো তথ্য দেখানো হয় না।",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // 3. Theme Selection (Next.js exact style)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "পছন্দের থিম নির্বাচন",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Light Mode Card
                        val isLightActive = !isDarkMode
                        Surface(
                            onClick = { preferencesRepo.setDarkMode(false) },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isLightActive) EmeraldPrimary.copy(alpha = 0.10f) else MaterialTheme.colorScheme.background,
                            border = BorderStroke(
                                if (isLightActive) 1.5.dp else 1.dp,
                                if (isLightActive) EmeraldPrimary else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(AmberPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WbSunny,
                                        contentDescription = null,
                                        tint = AmberPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "উজ্জ্বল পেপার",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLightActive) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "পড়তে আরামদায়ক",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Dark Mode Card
                        val isDarkActive = isDarkMode
                        Surface(
                            onClick = { preferencesRepo.setDarkMode(true) },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isDarkActive) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.background,
                            border = BorderStroke(
                                if (isDarkActive) 1.5.dp else 1.dp,
                                if (isDarkActive) EmeraldPrimary else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF6366F1).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Nightlight,
                                        contentDescription = null,
                                        tint = Color(0xFF818CF8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "কাটলফিশ ডার্ক",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkActive) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "চোখবান্ধব রাতের থিম",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Prayer & Location Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "নামাজের ওয়াক্ত ও হিসেব সেটিংস",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // City row
                    Surface(
                        onClick = { showCityBottomSheet = true },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "আপনার শহর",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${currentCity.nameBn}, ${currentCity.country}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Madhab row
                    Surface(
                        onClick = { showPrayerSettingsBottomSheet = true },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column {
                                    Text(
                                        text = "মাযহাব (আসর নামাজের ওয়াক্ত নির্ধারণ)",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = madhab.titleBn,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Calculation Method row
                    Surface(
                        onClick = { showPrayerSettingsBottomSheet = true },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = AmberPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column(modifier = Modifier.weight(1f, fill = false)) {
                                    Text(
                                        text = "নামাজের সময় হিসাব পদ্ধতি",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = calcMethod.titleBn,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 5. Quran Text Size
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "কুরআন ফন্ট সাইজ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "আরবি তিলাওয়াত ফন্ট সাইজ: ${PrayerConstants.toBengaliDigits(readerSettings.arabicFontSize)} px",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Slider(
                        value = readerSettings.arabicFontSize.toFloat(),
                        onValueChange = {
                            preferencesRepo.updateReaderSettings(readerSettings.copy(arabicFontSize = it.toInt()))
                        },
                        valueRange = 16f..46f,
                        colors = SliderDefaults.colors(thumbColor = EmeraldPrimary, activeTrackColor = EmeraldPrimary)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "বাংলা অনুবাদ ফন্ট সাইজ: ${PrayerConstants.toBengaliDigits(readerSettings.bengaliFontSize)} px",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Slider(
                        value = readerSettings.bengaliFontSize.toFloat(),
                        onValueChange = {
                            preferencesRepo.updateReaderSettings(readerSettings.copy(bengaliFontSize = it.toInt()))
                        },
                        valueRange = 12f..24f,
                        colors = SliderDefaults.colors(thumbColor = EmeraldPrimary, activeTrackColor = EmeraldPrimary)
                    )
                }
            }
        }

        // 6. About
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "মুসলিম কম্প্যানিয়ন (Muslim Companion)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ভার্সন ১.০.০ • একটি সার্বিক ইসলামিক সহায়ক অ্যাপ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "কুরআন, সহিহ হাদিস ও নির্ভরযোগ্য ইসলামিক স্কলারদের রেফারেন্স অনুযায়ী মুসলিমদের দৈনন্দিন ইবাদত, কুরআন পাঠ, দোয়া, যিকির ও আমল পরিচালনায় সহায়তা করাই এই অ্যাপের উদ্দেশ্য।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (showCityBottomSheet) {
        CitySelectorBottomSheet(
            selectedCity = currentCity,
            onCitySelected = { preferencesRepo.saveCity(it) },
            onDismiss = { showCityBottomSheet = false }
        )
    }

    if (showPrayerSettingsBottomSheet) {
        PrayerSettingsBottomSheet(
            currentMethod = calcMethod,
            currentMadhab = madhab,
            onMethodChange = { preferencesRepo.saveCalculationMethod(it) },
            onMadhabChange = { preferencesRepo.saveMadhab(it) },
            onDismiss = { showPrayerSettingsBottomSheet = false }
        )
    }
}
