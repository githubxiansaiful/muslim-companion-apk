package com.muslimcompanion.app.ui.screens.quran

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.constants.QuranConstants
import com.muslimcompanion.app.data.model.SurahMeta
import com.muslimcompanion.app.ui.theme.EmeraldPrimary

@Composable
fun QuranListScreen(
    onSurahClick: (Int) -> Unit
) {
    var search by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf<String>("all") } // "all", "Meccan", "Medinan"

    val filteredSurahs = remember(search, filterType) {
        QuranConstants.SURAH_LIST.filter { surah ->
            val matchesFilter = filterType == "all" || surah.revelationType == filterType
            val q = search.trim().lowercase()
            if (q.isBlank()) {
                matchesFilter
            } else {
                val matchesSearch = surah.number.toString() == q ||
                        surah.bengaliName.lowercase().contains(q) ||
                        surah.englishName.lowercase().contains(q) ||
                        surah.name.contains(q)
                matchesFilter && matchesSearch
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            // Header Banner matching Next.js app/quran/page.tsx
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Small Pill Badge
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "পবিত্র আল-কুরআনুল কারীম",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Main Title
                    Text(
                        text = "১১৪টি পূর্ণাঙ্গ সূরা, বাংলা উচ্চারণ ও অর্থ",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Description
                    Text(
                        text = "মুদ্রিত বাংলা কুরআন শরীফের চিরচেনা মুসহাফ অনুভূতির সাথে প্রতিটি আয়াতের বিশুদ্ধ আরবি পাঠ, বাংলা উচ্চারণ, বঙ্গানুবাদ ও মিষ্টি তিলাওয়াত।",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 21.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Search input
                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "সূরার নাম বা নম্বর দিয়ে খুঁজুন (যেমন: ফাতিহা, ৩৬)...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (search.isNotEmpty()) {
                                IconButton(onClick = { search = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Filter Pills (All, Meccan, Medinan)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterPill(
                            label = "সকল সূরা (১১৪)",
                            isSelected = filterType == "all",
                            onClick = { filterType = "all" }
                        )
                        FilterPill(
                            label = "মাক্কী সূরা (৮৬)",
                            isSelected = filterType == "Meccan",
                            onClick = { filterType = "Meccan" }
                        )
                        FilterPill(
                            label = "মাদানী সূরা (২৮)",
                            isSelected = filterType == "Medinan",
                            onClick = { filterType = "Medinan" }
                        )
                    }
                }
            }
        }

        // Surah Items
        items(filteredSurahs, key = { it.number }) { surah ->
            SurahListItem(surah = surah, onClick = { onSurahClick(surah.number) })
        }

        if (filteredSurahs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "কোনো সূরা পাওয়া যায়নি।",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

@Composable
fun FilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
fun SurahListItem(
    surah: SurahMeta,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Number badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = PrayerConstants.toBengaliDigits(surah.number),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column {
                    Text(
                        text = surah.bengaliName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${PrayerConstants.toBengaliDigits(surah.numberOfAyahs)} টি আয়াত • ${if (surah.revelationType == "Meccan") "মাক্কী" else "মাদানী"}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Arabic Calligraphic Name
            Text(
                text = surah.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldPrimary,
                textAlign = TextAlign.Right
            )
        }
    }
}
