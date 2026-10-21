package com.muslimcompanion.app.ui.screens.quran

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import com.muslimcompanion.app.ui.components.SurahSelectorBottomSheet
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.muslimcompanion.app.data.constants.QuranConstants
import com.muslimcompanion.app.data.constants.SurahContextConstants
import com.muslimcompanion.app.data.model.AyahItem
import com.muslimcompanion.app.data.model.QuranReaderSettings
import com.muslimcompanion.app.data.repository.PreferencesRepository
import com.muslimcompanion.app.data.repository.QuranRepository
import com.muslimcompanion.app.ui.theme.AmberPrimary
import com.muslimcompanion.app.ui.theme.EmeraldPrimary
import com.muslimcompanion.app.ui.theme.MushafPronunciationBgDark
import com.muslimcompanion.app.ui.theme.MushafPronunciationBgLight
import com.muslimcompanion.app.ui.theme.MushafTranslationBgDark
import com.muslimcompanion.app.ui.theme.MushafTranslationBgLight
import com.muslimcompanion.app.ui.theme.ReaderDarkBg
import com.muslimcompanion.app.ui.theme.ReaderDarkBorder
import com.muslimcompanion.app.ui.theme.ReaderDarkSheet
import com.muslimcompanion.app.ui.theme.ReaderDarkText
import com.muslimcompanion.app.ui.theme.ReaderPaperBg
import com.muslimcompanion.app.ui.theme.ReaderPaperBorder
import com.muslimcompanion.app.ui.theme.ReaderPaperSheet
import com.muslimcompanion.app.ui.theme.ReaderPaperText
import com.muslimcompanion.app.ui.theme.ReaderSepiaAccent
import com.muslimcompanion.app.ui.theme.ReaderSepiaBg
import com.muslimcompanion.app.ui.theme.ReaderSepiaBorder
import com.muslimcompanion.app.ui.theme.ReaderSepiaSheet
import com.muslimcompanion.app.ui.theme.ReaderSepiaText
import com.muslimcompanion.app.ui.theme.ReaderWhiteBg
import com.muslimcompanion.app.ui.theme.ReaderWhiteBorder
import com.muslimcompanion.app.ui.theme.ReaderWhiteSheet
import com.muslimcompanion.app.ui.theme.ReaderWhiteText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahReaderScreen(
    surahNumber: Int,
    onBack: () -> Unit,
    onNavigateToSurah: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val preferencesRepo = remember { PreferencesRepository(context) }
    val listState = rememberLazyListState()

    val surahMeta = remember(surahNumber) {
        QuranConstants.SURAH_LIST.find { it.number == surahNumber } ?: QuranConstants.SURAH_LIST[0]
    }
    val surahContext = remember(surahNumber, surahMeta.revelationType) {
        SurahContextConstants.getSurahContext(surahNumber, surahMeta.revelationType)
    }

    val readerSettings by preferencesRepo.readerSettings.collectAsState()
    val bookmarkedAyahs by preferencesRepo.bookmarkedAyahs.collectAsState()
    val isDarkMode by preferencesRepo.isDarkMode.collectAsState()

    var ayahs by remember { mutableStateOf<List<AyahItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDrawer by remember { mutableStateOf(false) }
    var showSurahSelectorSheet by remember { mutableStateOf(false) }
    var showOfflineAudioDialog by remember { mutableStateOf(false) }

    // Audio Playback
    var playingAyahGlobalNumber by remember { mutableStateOf<Int?>(null) }
    val mediaPlayer = remember { MediaPlayer() }

    fun isNetworkAvailable(ctx: Context): Boolean {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                if (mediaPlayer.isPlaying) mediaPlayer.stop()
                mediaPlayer.release()
            } catch (e: Exception) {}
        }
    }

    fun playAyahAudio(globalAyahNumber: Int, audioUrl: String) {
        if (!isNetworkAvailable(context)) {
            if (mediaPlayer.isPlaying) {
                try {
                    mediaPlayer.stop()
                } catch (e: Exception) {}
            }
            playingAyahGlobalNumber = null
            showOfflineAudioDialog = true
            return
        }

        try {
            if (playingAyahGlobalNumber == globalAyahNumber) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    playingAyahGlobalNumber = null
                }
            } else {
                mediaPlayer.reset()
                mediaPlayer.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                mediaPlayer.setDataSource(audioUrl)
                mediaPlayer.setOnPreparedListener {
                    it.start()
                    playingAyahGlobalNumber = globalAyahNumber
                }
                // Continuous sequential audio playback
                mediaPlayer.setOnCompletionListener {
                    val currentIndex = ayahs.indexOfFirst { a -> a.number == globalAyahNumber }
                    if (currentIndex != -1 && currentIndex + 1 < ayahs.size) {
                        val nextAyah = ayahs[currentIndex + 1]
                        playAyahAudio(nextAyah.number, nextAyah.audioUrl)
                    } else {
                        playingAyahGlobalNumber = null
                    }
                }
                mediaPlayer.prepareAsync()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "অডিও চালাতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show()
            playingAyahGlobalNumber = null
        }
    }

    LaunchedEffect(surahNumber) {
        isLoading = true
        ayahs = QuranRepository.getSurahAyahs(context, surahNumber)
        isLoading = false
        preferencesRepo.saveLastRead(surahNumber, 1)
        listState.scrollToItem(0)
    }

    // Bound directly to app theme (isDarkMode) so entire app stays unified
    val readerBg = MaterialTheme.colorScheme.background
    val readerSheet = MaterialTheme.colorScheme.surface
    val readerText = MaterialTheme.colorScheme.onSurface
    val readerBorder = MaterialTheme.colorScheme.outline

    val pronunciationBg = if (isDarkMode) MushafPronunciationBgDark else MushafPronunciationBgLight
    val translationBg = if (isDarkMode) MushafTranslationBgDark else MushafTranslationBgLight

    // Group ayahs into traditional printed strips (typically 2 ayahs per block, or 1 if very long)
    val strips = remember(ayahs, readerSettings.stripMode) {
        val result = mutableListOf<List<AyahItem>>()
        if (readerSettings.stripMode == "single") {
            for (ayah in ayahs) {
                result.add(listOf(ayah))
            }
        } else {
            var i = 0
            while (i < ayahs.size) {
                val current = ayahs[i]
                val next = if (i + 1 < ayahs.size) ayahs[i + 1] else null
                if (current.text.length > 150) {
                    result.add(listOf(current))
                    i++
                } else if (next != null && next.text.length <= 150) {
                    result.add(listOf(current, next))
                    i += 2
                } else {
                    result.add(listOf(current))
                    i++
                }
            }
        }
        result
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(readerBg)
    ) {
        // App Top Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            color = readerSheet,
            border = BorderStroke(1.dp, readerBorder),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = readerText)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showSurahSelectorSheet = true }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "সূরা ${surahMeta.bengaliName}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = readerText
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select Surah",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "${surahMeta.englishName} • ${surahMeta.name} • ${PrayerConstants.toBengaliDigits(surahMeta.numberOfAyahs)} আয়াত",
                                fontSize = 13.sp,
                                color = readerText.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick View Mode switch (Mushaf vs Card)
                    IconButton(
                        onClick = {
                            val next = if (readerSettings.viewMode == "mushaf") "card" else "mushaf"
                            preferencesRepo.updateReaderSettings(readerSettings.copy(viewMode = next))
                        }
                    ) {
                        Icon(
                            imageVector = if (readerSettings.viewMode == "mushaf") Icons.Default.ViewAgenda else Icons.Default.MenuBook,
                            contentDescription = "Toggle View Mode",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Settings Drawer Button
                    IconButton(onClick = { showDrawer = true }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Reader Settings",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = EmeraldPrimary)
                    Text(
                        text = "সূরা তিলাওয়াত ও অনুবাদ লোড হচ্ছে...",
                        fontSize = 14.sp,
                        color = readerText.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(10.dp)) }

                // Bismillah Calligraphy Box (for all surahs except At-Tawbah 9)
                if (surahNumber != 9) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = readerSheet),
                            border = BorderStroke(1.dp, readerBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary,
                                    textAlign = TextAlign.Center
                                )
                                if (readerSettings.showPronunciation) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "বিস্মিল্লাহির রাহমা-নির রাহীম",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = readerText.copy(alpha = 0.85f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                if (readerSettings.showTranslation) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "পরম করুণাময় ও দয়ালু আল্লাহর নামে",
                                        fontSize = 14.sp,
                                        color = readerText.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(readerBorder)
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "সূরা ${surahMeta.bengaliName}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = readerText
                                )
                                Text(
                                    text = "${surahMeta.name} • ${surahMeta.englishName}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${if (surahMeta.revelationType == "Meccan") "মক্কায় অবতীর্ণ" else "মদিনায় অবতীর্ণ"} • রুকু : ${PrayerConstants.toBengaliDigits(surahContext.rukuCount)} • মোট আয়াত : ${PrayerConstants.toBengaliDigits(surahMeta.numberOfAyahs)}",
                                    fontSize = 13.sp,
                                    color = readerText.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                // MODE 1: Traditional Bangla Printed Quran Mushaf View
                if (readerSettings.viewMode == "mushaf") {
                    items(strips, key = { strip -> strip.first().number }) { strip ->
                        val isPlayingThisStrip = strip.any { it.number == playingAyahGlobalNumber }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = readerSheet),
                            border = BorderStroke(
                                1.dp,
                                if (isPlayingThisStrip) AmberPrimary else readerBorder
                            )
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Header bar of the strip with audio play, bookmark, copy
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(readerSheet.copy(alpha = 0.85f))
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val headerText = if (strip.size == 1) {
                                        "আয়াত ${PrayerConstants.toBengaliDigits(strip[0].numberInSurah)}"
                                    } else {
                                        "আয়াত ${PrayerConstants.toBengaliDigits(strip[0].numberInSurah)} - ${PrayerConstants.toBengaliDigits(strip.last().numberInSurah)}"
                                    }

                                    Text(
                                        text = headerText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        strip.forEach { ayah ->
                                            val isPlaying = playingAyahGlobalNumber == ayah.number
                                            val isBookmarked = bookmarkedAyahs.contains("$surahNumber:${ayah.numberInSurah}")

                                            // Play button with ayah number
                                            Surface(
                                                onClick = { playAyahAudio(ayah.number, ayah.audioUrl) },
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isPlaying) AmberPrimary else readerSheet,
                                                border = BorderStroke(1.dp, if (isPlaying) AmberPrimary else readerBorder)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(12.dp),
                                                        tint = if (isPlaying) Color.White else readerText
                                                    )
                                                    Text(
                                                        text = PrayerConstants.toBengaliDigits(ayah.numberInSurah),
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isPlaying) Color.White else readerText
                                                    )
                                                }
                                            }

                                            // Bookmark button
                                            IconButton(
                                                onClick = { preferencesRepo.toggleAyahBookmark(surahNumber, ayah.numberInSurah) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = if (isBookmarked) AmberPrimary else readerText.copy(alpha = 0.5f)
                                                )
                                            }

                                            // Copy button
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    val copyText = "${ayah.text}\n\nউচ্চারণ: ${ayah.pronunciationBn}\n\nঅনুবাদ: ${ayah.translationBn}\n(সূরা ${surahMeta.bengaliName}, আয়াত: ${ayah.numberInSurah})"
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("Ayah", copyText))
                                                    Toast.makeText(context, "আয়াতটি কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp),
                                                    tint = readerText.copy(alpha = 0.5f)
                                                )
                                            }
                                        }
                                    }
                                }

                                // 1. Arabic Block with authentic ornamental end-ayah marker
                                if (readerSettings.showArabic) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp)
                                    ) {
                                        val arabicJoined = buildString {
                                            strip.forEach { ayah ->
                                                append(ayah.text)
                                                append(" ")
                                                append("۝")
                                                append(SurahContextConstants.toArabicDigits(ayah.numberInSurah))
                                                append("  ")
                                            }
                                        }

                                        Text(
                                            text = arabicJoined,
                                            fontSize = readerSettings.arabicFontSize.sp,
                                            lineHeight = (readerSettings.arabicFontSize * 1.8).sp,
                                            fontWeight = FontWeight.Normal,
                                            color = readerText,
                                            textAlign = TextAlign.Right,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }

                                // 2. Bengali Pronunciation Strip (Soft Sand/Tinted Background)
                                if (readerSettings.showPronunciation) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(pronunciationBg)
                                            .padding(horizontal = 12.dp, vertical = 10.dp)
                                    ) {
                                        val pronunJoined = buildString {
                                            strip.forEach { ayah ->
                                                val clean = ayah.pronunciationBn.trim().trimEnd('।', '.', '|')
                                                append("${PrayerConstants.toBengaliDigits(ayah.numberInSurah)} । $clean ।  ")
                                            }
                                        }

                                        Text(
                                            text = pronunJoined,
                                            fontSize = readerSettings.bengaliFontSize.sp,
                                            lineHeight = (readerSettings.bengaliFontSize * 1.55).sp,
                                            fontWeight = FontWeight.Medium,
                                            color = readerText,
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                }

                                // 3. Bengali Translation Strip (Soft Warm Background)
                                if (readerSettings.showTranslation) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(translationBg)
                                            .padding(horizontal = 12.dp, vertical = 10.dp)
                                    ) {
                                        val transJoined = buildString {
                                            strip.forEach { ayah ->
                                                val clean = ayah.translationBn.trim().trimEnd('।', '.', '|')
                                                append("(${PrayerConstants.toBengaliDigits(ayah.numberInSurah)}) $clean ।  ")
                                            }
                                        }

                                        Text(
                                            text = transJoined,
                                            fontSize = readerSettings.bengaliFontSize.sp,
                                            lineHeight = (readerSettings.bengaliFontSize * 1.55).sp,
                                            fontWeight = FontWeight.Normal,
                                            color = readerText.copy(alpha = 0.85f),
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // MODE 2: Individual Ayah Card View
                if (readerSettings.viewMode == "card") {
                    items(ayahs, key = { it.number }) { ayah ->
                        val isBookmarked = bookmarkedAyahs.contains("$surahNumber:${ayah.numberInSurah}")
                        val isPlaying = playingAyahGlobalNumber == ayah.number

                        AyahCardItem(
                            ayah = ayah,
                            surahMeta = surahMeta,
                            arabicFontSize = readerSettings.arabicFontSize,
                            bengaliFontSize = readerSettings.bengaliFontSize,
                            showArabic = readerSettings.showArabic,
                            showPronunciation = readerSettings.showPronunciation,
                            showTranslation = readerSettings.showTranslation,
                            isBookmarked = isBookmarked,
                            isPlaying = isPlaying,
                            readerText = readerText,
                            readerSheet = readerSheet,
                            readerBorder = readerBorder,
                            onPlayClick = { playAyahAudio(ayah.number, ayah.audioUrl) },
                            onBookmarkClick = { preferencesRepo.toggleAyahBookmark(surahNumber, ayah.numberInSurah) },
                            onCopyClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val copyText = "${ayah.text}\n\nউচ্চারণ: ${ayah.pronunciationBn}\n\nঅনুবাদ: ${ayah.translationBn}\n(সূরা ${surahMeta.bengaliName}, আয়াত: ${ayah.numberInSurah})"
                                clipboard.setPrimaryClip(ClipData.newPlainText("Ayah", copyText))
                                Toast.makeText(context, "আয়াতটি কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                // Surah Context Footer: Naming, Revelation Circumstances, Fazilat, Tika
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = readerSheet),
                        border = BorderStroke(1.dp, readerBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (surahContext.namkoron != null) {
                                Text(
                                    text = "নামকরণ : ${surahContext.namkoron}",
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = readerText
                                )
                            }

                            Text(
                                text = "শানেনুযূল : ${surahContext.shaneNuzul}",
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = readerText
                            )

                            Text(
                                text = "ফযীলত : ${surahContext.fazilat}",
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = AmberPrimary
                            )

                            surahContext.tika.forEachIndexed { index, tikaItem ->
                                Text(
                                    text = "টীকা-${PrayerConstants.toBengaliDigits(index + 1)} : $tikaItem",
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    color = readerText.copy(alpha = 0.75f)
                                )
                            }
                        }
                    }
                }

                // Bottom Surah Navigation Bar (Previous Surah / Next Surah)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (surahNumber > 1) {
                            OutlinedButton(
                                onClick = { onNavigateToSurah(surahNumber - 1) },
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, readerBorder)
                            ) {
                                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "পূর্ববর্তী সূরা", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = readerText)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        if (surahNumber < 114) {
                            Button(
                                onClick = { onNavigateToSurah(surahNumber + 1) },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Text(text = "পরবর্তী সূরা", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }

    // Comprehensive Quran Reader Settings Bottom Sheet / Drawer
    if (showDrawer) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showDrawer = false },
            sheetState = sheetState,
            containerColor = readerSheet
        ) {
            QuranReaderDrawerContent(
                surahMeta = surahMeta,
                settings = readerSettings,
                onUpdateSettings = { preferencesRepo.updateReaderSettings(it) },
                onJumpToAyah = { targetAyah ->
                    showDrawer = false
                    coroutineScope.launch {
                        // Find index of strip containing this ayah
                        val index = strips.indexOfFirst { s -> s.any { it.numberInSurah == targetAyah } }
                        if (index != -1) {
                            listState.animateScrollToItem(index + 1) // +1 for banner
                        }
                    }
                },
                onSwitchSurah = { newSurah ->
                    showDrawer = false
                    onNavigateToSurah(newSurah)
                },
                onClose = { showDrawer = false }
            )
        }
    }

    if (showSurahSelectorSheet) {
        SurahSelectorBottomSheet(
            currentSurahNumber = surahNumber,
            onSurahSelected = { newSurah ->
                showSurahSelectorSheet = false
                onNavigateToSurah(newSurah)
            },
            onDismiss = { showSurahSelectorSheet = false }
        )
    }

    if (showOfflineAudioDialog) {
        Dialog(
            onDismissRequest = { showOfflineAudioDialog = false }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(AmberPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = "No Internet",
                            tint = AmberPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ইন্টারনেট সংযোগ প্রয়োজন",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "ক্বারী মিশারি রাশিদ আল-আফাসির অডিও তিলাওয়াত শোনার জন্য ইন্টারনেট কানেকশন প্রয়োজন।\n\nঅফলাইনে আপনি সম্পূর্ণ কুরআন, বাংলা উচ্চারণ ও অর্থ সহ পড়তে পারবেন।",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 19.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = EmeraldPrimary.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "সম্পূর্ণ কুরআন অফলাইনে সংরক্ষিত",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldPrimary
                            )
                        }
                    }

                    Button(
                        onClick = { showOfflineAudioDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text(
                            text = "ঠিক আছে",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AyahCardItem(
    ayah: AyahItem,
    surahMeta: com.muslimcompanion.app.data.model.SurahMeta,
    arabicFontSize: Int,
    bengaliFontSize: Int,
    showArabic: Boolean,
    showPronunciation: Boolean,
    showTranslation: Boolean,
    isBookmarked: Boolean,
    isPlaying: Boolean,
    readerText: Color,
    readerSheet: Color,
    readerBorder: Color,
    onPlayClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onCopyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) AmberPrimary.copy(alpha = 0.08f) else readerSheet
        ),
        border = BorderStroke(
            1.dp,
            if (isPlaying) AmberPrimary else readerBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Ayah badge and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = PrayerConstants.toBengaliDigits(ayah.numberInSurah),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPlayClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = if (isPlaying) AmberPrimary else readerText.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(onClick = onBookmarkClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) AmberPrimary else readerText.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(onClick = onCopyClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = readerText.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Arabic text
            if (showArabic) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${ayah.text} ۝${SurahContextConstants.toArabicDigits(ayah.numberInSurah)}",
                    fontSize = arabicFontSize.sp,
                    lineHeight = (arabicFontSize * 1.75).sp,
                    fontWeight = FontWeight.Normal,
                    color = readerText,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Bengali pronunciation
            if (showPronunciation && ayah.pronunciationBn.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "উচ্চারণ: ${ayah.pronunciationBn}",
                    fontSize = bengaliFontSize.sp,
                    lineHeight = (bengaliFontSize * 1.5).sp,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            // Bengali translation
            if (showTranslation && ayah.translationBn.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = ayah.translationBn,
                    fontSize = bengaliFontSize.sp,
                    lineHeight = (bengaliFontSize * 1.5).sp,
                    color = readerText.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun QuranReaderDrawerContent(
    surahMeta: com.muslimcompanion.app.data.model.SurahMeta,
    settings: QuranReaderSettings,
    onUpdateSettings: (QuranReaderSettings) -> Unit,
    onJumpToAyah: (Int) -> Unit,
    onSwitchSurah: (Int) -> Unit,
    onClose: () -> Unit
) {
    var jumpAyahInput by remember { mutableStateOf("") }
    var showSurahSelectorSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Drawer Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "কুরআন পাঠ সেটিংস",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "সূরা ${surahMeta.bengaliName} • ${PrayerConstants.toBengaliDigits(surahMeta.numberOfAyahs)} আয়াত",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Reset Defaults button
            TextButton(
                onClick = {
                    onUpdateSettings(
                        QuranReaderSettings(
                            arabicFontSize = 28,
                            bengaliFontSize = 17,
                            viewMode = "mushaf",
                            stripMode = "double",
                            showArabic = true,
                            showPronunciation = true,
                            showTranslation = true,
                            theme = "paper"
                        )
                    )
                }
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "রিসেট", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Section 1: Display Layers (Arabic, Pronunciation, Translation)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "পঠন স্তর প্রদর্শন (অন / অফ)", fontSize = 14.sp, fontWeight = FontWeight.Bold)

                DrawerSwitchRow(
                    label = "আরবি মূল পাঠ",
                    subLabel = "উসমানী লিপিতে বিশুদ্ধ আরবি তিলাওয়াত",
                    checked = settings.showArabic,
                    onCheckedChange = { onUpdateSettings(settings.copy(showArabic = it)) }
                )

                DrawerSwitchRow(
                    label = "বাংলা উচ্চারণ",
                    subLabel = "সহজ ও শুদ্ধ উচ্চারণে পড়ার সুবিধা",
                    checked = settings.showPronunciation,
                    onCheckedChange = { onUpdateSettings(settings.copy(showPronunciation = it)) }
                )

                DrawerSwitchRow(
                    label = "বাংলা অনুবাদ ও অর্থ",
                    subLabel = "আয়াতের পূর্ণ ভাব ও মর্মার্থ",
                    checked = settings.showTranslation,
                    onCheckedChange = { onUpdateSettings(settings.copy(showTranslation = it)) }
                )
            }
        }

        // Section 2: Arabic Font Size
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "আরবি ফন্ট সাইজ", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${PrayerConstants.toBengaliDigits(settings.arabicFontSize)} px",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { onUpdateSettings(settings.copy(arabicFontSize = (settings.arabicFontSize - 2).coerceAtLeast(10))) }) {
                        Text(text = "—", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = settings.arabicFontSize.toFloat(),
                        onValueChange = { onUpdateSettings(settings.copy(arabicFontSize = it.toInt())) },
                        valueRange = 10f..46f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(thumbColor = EmeraldPrimary, activeTrackColor = EmeraldPrimary)
                    )
                    IconButton(onClick = { onUpdateSettings(settings.copy(arabicFontSize = (settings.arabicFontSize + 2).coerceAtMost(46))) }) {
                        Text(text = "+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Presets
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("১০ px" to 10, "ছোট" to 18, "আদর্শ" to 28, "বড়" to 36).forEach { (label, sz) ->
                        Surface(
                            onClick = { onUpdateSettings(settings.copy(arabicFontSize = sz)) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (settings.arabicFontSize == sz) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "$label (${PrayerConstants.toBengaliDigits(sz)})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (settings.arabicFontSize == sz) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Live Preview
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ۝١",
                        fontSize = settings.arabicFontSize.sp,
                        textAlign = TextAlign.Center,
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // Section 3: Bengali Font Size
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "বাংলা ফন্ট সাইজ", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${PrayerConstants.toBengaliDigits(settings.bengaliFontSize)} px",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { onUpdateSettings(settings.copy(bengaliFontSize = (settings.bengaliFontSize - 1).coerceAtLeast(10))) }) {
                        Text(text = "—", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = settings.bengaliFontSize.toFloat(),
                        onValueChange = { onUpdateSettings(settings.copy(bengaliFontSize = it.toInt())) },
                        valueRange = 10f..26f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(thumbColor = AmberPrimary, activeTrackColor = AmberPrimary)
                    )
                    IconButton(onClick = { onUpdateSettings(settings.copy(bengaliFontSize = (settings.bengaliFontSize + 1).coerceAtMost(26))) }) {
                        Text(text = "+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Live Bengali Preview
                Text(
                    text = "পরম করুণাময় ও অসীম দয়ালু আল্লাহর নামে",
                    fontSize = settings.bengaliFontSize.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Section 4: View Mode & Strip Layout
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "পঠন বিন্যাস ও মোড", fontSize = 14.sp, fontWeight = FontWeight.Bold)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        onClick = { onUpdateSettings(settings.copy(viewMode = "mushaf")) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (settings.viewMode == "mushaf") MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "মুসহাফ মোড",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (settings.viewMode == "mushaf") MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "বাংলা কুরআন বই",
                                fontSize = 12.sp,
                                color = if (settings.viewMode == "mushaf") MaterialTheme.colorScheme.surface.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        onClick = { onUpdateSettings(settings.copy(viewMode = "card")) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (settings.viewMode == "card") MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "কার্ড মোড",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (settings.viewMode == "card") MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "পৃথক কার্ড তালিকা",
                                fontSize = 12.sp,
                                color = if (settings.viewMode == "card") MaterialTheme.colorScheme.surface.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (settings.viewMode == "mushaf") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "স্ট্রিপ বিন্যাস (মুদ্রিত বইয়ের মতো):", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                onClick = { onUpdateSettings(settings.copy(stripMode = "double")) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (settings.stripMode == "double") EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "২ আয়াত",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (settings.stripMode == "double") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                onClick = { onUpdateSettings(settings.copy(stripMode = "single")) },
                                shape = RoundedCornerShape(8.dp),
                                color = if (settings.stripMode == "single") EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "একক",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (settings.stripMode == "single") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 5: Direct Ayah Jump
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "নির্দিষ্ট আয়াতে দ্রুত স্ক্রোল করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = jumpAyahInput,
                        onValueChange = { jumpAyahInput = it },
                        placeholder = { Text("১ থেকে ${surahMeta.numberOfAyahs}", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Button(
                        onClick = {
                            val num = jumpAyahInput.trim().toIntOrNull()
                            if (num != null && num in 1..surahMeta.numberOfAyahs) {
                                onJumpToAyah(num)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text(text = "যান", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 6: Quick Surah Switcher
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "অন্য কোনো সূরা পড়ুন", fontSize = 14.sp, fontWeight = FontWeight.Bold)

                OutlinedButton(
                    onClick = { showSurahSelectorSheet = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${PrayerConstants.toBengaliDigits(surahMeta.number)}. সূরা ${surahMeta.bengaliName} (${surahMeta.name}) ▾",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (showSurahSelectorSheet) {
                    SurahSelectorBottomSheet(
                        currentSurahNumber = surahMeta.number,
                        onSurahSelected = { num ->
                            showSurahSelectorSheet = false
                            onSwitchSurah(num)
                        },
                        onDismiss = { showSurahSelectorSheet = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun DrawerSwitchRow(
    label: String,
    subLabel: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subLabel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EmeraldPrimary)
        )
    }
}
