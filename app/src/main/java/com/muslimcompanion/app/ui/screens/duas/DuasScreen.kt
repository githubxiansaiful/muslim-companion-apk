package com.muslimcompanion.app.ui.screens.duas

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.data.constants.DuaConstants
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.model.DuaItem
import com.muslimcompanion.app.data.repository.PreferencesRepository
import com.muslimcompanion.app.ui.theme.EmeraldPrimary

@Composable
fun DuasScreen(
    preferencesRepo: PreferencesRepository
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }

    val bookmarkedDuas by preferencesRepo.bookmarkedDuas.collectAsState()

    val filteredDuas = remember(selectedCategory, searchQuery) {
        var list = DuaConstants.DUAS
        if (selectedCategory != "all") {
            list = list.filter { it.categorySlug == selectedCategory }
        }
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            list = list.filter {
                it.titleBn.lowercase().contains(q) ||
                        it.translationBn.lowercase().contains(q) ||
                        it.transliteration.lowercase().contains(q)
            }
        }
        list
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "দোয়া বা বিষয় দিয়ে খুঁজুন (ঘুম, সফর, ক্ষমা)...",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(18.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Categories Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                val isAll = selectedCategory == "all"
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isAll) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        if (isAll) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.clickable { selectedCategory = "all" }
                ) {
                    Text(
                        text = "সকল দোয়া (${PrayerConstants.toBengaliDigits(DuaConstants.DUAS.size)})",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        fontSize = 12.sp,
                        fontWeight = if (isAll) FontWeight.Bold else FontWeight.Medium,
                        color = if (isAll) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            items(DuaConstants.CATEGORIES) { cat ->
                val isSelected = selectedCategory == cat.slug
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.clickable { selectedCategory = cat.slug }
                ) {
                    Text(
                        text = cat.nameBn,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Duas List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredDuas, key = { it.id }) { dua ->
                val isBookmarked = bookmarkedDuas.contains(dua.id)

                DuaItemCard(
                    dua = dua,
                    isBookmarked = isBookmarked,
                    onBookmarkToggle = { preferencesRepo.toggleDuaBookmark(dua.id) },
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val copyText = "${dua.titleBn}\n\n${dua.arabic}\n\nউচ্চারণ: ${dua.transliteration}\n\nঅর্থ: ${dua.translationBn}\n(রেফারেন্স: ${dua.reference})"
                        clipboard.setPrimaryClip(ClipData.newPlainText("Dua", copyText))
                        Toast.makeText(context, "দোয়াটি কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun DuaItemCard(
    dua: DuaItem,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onCopy: () -> Unit
) {
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
            // Title & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dua.titleBn,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onBookmarkToggle, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic Text
            Text(
                text = dua.arabic,
                fontSize = 20.sp,
                lineHeight = 32.sp,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pronunciation
            Text(
                text = "উচ্চারণ: ${dua.transliteration}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Translation
            Text(
                text = dua.translationBn,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Reference
            Text(
                text = "রেফারেন্স: ${dua.reference}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
