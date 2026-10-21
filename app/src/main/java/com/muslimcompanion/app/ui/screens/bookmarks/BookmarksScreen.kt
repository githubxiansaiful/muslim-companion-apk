package com.muslimcompanion.app.ui.screens.bookmarks

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
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
import com.muslimcompanion.app.data.constants.DuaConstants
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.constants.QuranConstants
import com.muslimcompanion.app.data.repository.PreferencesRepository
import com.muslimcompanion.app.ui.navigation.Screen
import com.muslimcompanion.app.ui.theme.EmeraldPrimary

@Composable
fun BookmarksScreen(
    preferencesRepo: PreferencesRepository,
    onNavigate: (String) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val bookmarkedAyahs by preferencesRepo.bookmarkedAyahs.collectAsState()
    val bookmarkedDuas by preferencesRepo.bookmarkedDuas.collectAsState()

    var activeTab by remember { mutableStateOf("ayahs") } // "ayahs" or "duas"

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
                        text = "সংরক্ষিত বুকমার্কস",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "আপনার পছন্দের আয়াত ও দোয়াসমূহের সংগ্রহ",
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
                        .clickable { activeTab = "ayahs" },
                    color = if (activeTab == "ayahs") MaterialTheme.colorScheme.primary else Color.Transparent
                ) {
                    Text(
                        text = "আয়াতসমূহ (${PrayerConstants.toBengaliDigits(bookmarkedAyahs.size)})",
                        modifier = Modifier.padding(vertical = 10.dp),
                        fontSize = 12.sp,
                        fontWeight = if (activeTab == "ayahs") FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == "ayahs") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { activeTab = "duas" },
                    color = if (activeTab == "duas") MaterialTheme.colorScheme.primary else Color.Transparent
                ) {
                    Text(
                        text = "দোয়াসমূহ (${PrayerConstants.toBengaliDigits(bookmarkedDuas.size)})",
                        modifier = Modifier.padding(vertical = 10.dp),
                        fontSize = 12.sp,
                        fontWeight = if (activeTab == "duas") FontWeight.Bold else FontWeight.Medium,
                        color = if (activeTab == "duas") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeTab == "ayahs") {
            if (bookmarkedAyahs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "কোনো আয়াত বুকমার্ক করা হয়নি।\nকুরআন পড়ার সময় বুকমার্ক আইকনে ট্যাপ করুন।",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(bookmarkedAyahs.toList()) { key ->
                        val parts = key.split(":")
                        val surahNum = parts.getOrNull(0)?.toIntOrNull() ?: 1
                        val ayahNum = parts.getOrNull(1)?.toIntOrNull() ?: 1
                        val surah = QuranConstants.SURAH_LIST.find { it.number == surahNum }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigate(Screen.SurahReader.createRoute(surahNum)) },
                            shape = RoundedCornerShape(18.dp),
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
                                Column {
                                    Text(
                                        text = "সূরা ${surah?.bengaliName ?: "আল-কুরআন"}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "আয়াত নম্বর: ${PrayerConstants.toBengaliDigits(ayahNum)}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(
                                    onClick = { preferencesRepo.toggleAyahBookmark(surahNum, ayahNum) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        } else {
            val savedList = remember(bookmarkedDuas) {
                DuaConstants.DUAS.filter { bookmarkedDuas.contains(it.id) }
            }

            if (savedList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "কোনো দোয়া বুকমার্ক করা হয়নি।\nদোয়া পড়ার সময় বুকমার্ক আইকনে ট্যাপ করুন।",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(savedList, key = { it.id }) { dua ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dua.titleBn,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )

                                    IconButton(
                                        onClick = { preferencesRepo.toggleDuaBookmark(dua.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = dua.arabic,
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    textAlign = TextAlign.Right,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "অনুবাদ: ${dua.translationBn}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
