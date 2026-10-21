package com.muslimcompanion.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.constants.QuranConstants
import com.muslimcompanion.app.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahSelectorBottomSheet(
    currentSurahNumber: Int,
    onSurahSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    val filteredSurahs = remember(searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) {
            QuranConstants.SURAH_LIST
        } else {
            QuranConstants.SURAH_LIST.filter { s ->
                s.number.toString() == q ||
                        PrayerConstants.toBengaliDigits(s.number).contains(q) ||
                        s.bengaliName.lowercase().contains(q) ||
                        s.englishName.lowercase().contains(q) ||
                        s.name.contains(q)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = "অন্য কোনো সূরা পড়ুন",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "পবিত্র আল-কুরআনের ১১৪টি পূর্ণাঙ্গ সূরার তালিকা",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "সূরার নাম বা নম্বর দিয়ে খুঁজুন (যেমন: ফাতিহা, ৩৬)...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredSurahs) { surah ->
                    val isCurrent = surah.number == currentSurahNumber
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSurahSelected(surah.number)
                                onDismiss()
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) EmeraldPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.background
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isCurrent) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.size(36.dp),
                                    shape = CircleShape,
                                    color = if (isCurrent) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, if (isCurrent) EmeraldPrimary else MaterialTheme.colorScheme.outline)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = PrayerConstants.toBengaliDigits(surah.number),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrent) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = surah.bengaliName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${PrayerConstants.toBengaliDigits(surah.numberOfAyahs)} টি আয়াত • ${if (surah.revelationType == "Meccan") "মাক্কী" else "মাদানী"}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = surah.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
