package com.muslimcompanion.app.ui.screens.more

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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.data.repository.PreferencesRepository
import com.muslimcompanion.app.ui.navigation.Screen
import com.muslimcompanion.app.ui.theme.AmberPrimary
import com.muslimcompanion.app.ui.theme.EmeraldPrimary

data class MoreMenuItem(
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val route: String,
    val color: Color,
    val badge: String? = null
)

data class MenuCategory(
    val title: String,
    val subtitle: String,
    val accentColor: Color,
    val items: List<MoreMenuItem>
)

@Composable
fun MoreScreen(
    preferencesRepo: PreferencesRepository? = null,
    onNavigate: (String) -> Unit
) {
    val isDarkMode by preferencesRepo?.isDarkMode?.collectAsState() ?: remember { androidx.compose.runtime.mutableStateOf(false) }
    val currentCity by preferencesRepo?.currentCity?.collectAsState() ?: remember { androidx.compose.runtime.mutableStateOf(null) }

    val categories = remember {
        listOf(
            MenuCategory(
                title = "ইবাদত ও প্রাত্যহিক আমল",
                subtitle = "তাসবিহ, যিকির, কিবলা ও নামাজের পূর্ণাঙ্গ হিসাব",
                accentColor = EmeraldPrimary,
                items = listOf(
                    MoreMenuItem(
                        title = "ডিজিটাল তাসবিহ",
                        desc = "সহজ ট্যাপ কাউন্টার ও সংরক্ষিত যিকির",
                        icon = Icons.Default.TouchApp,
                        route = Screen.Tasbih.route,
                        color = EmeraldPrimary
                    ),
                    MoreMenuItem(
                        title = "সকাল ও সন্ধ্যার যিকির",
                        desc = "মাসনুন হেফাজতের দোয়া ও ফজিলত",
                        icon = Icons.Default.Nightlight,
                        route = Screen.Azkar.route,
                        color = EmeraldPrimary
                    ),
                    MoreMenuItem(
                        title = "কিবলা নির্দেশক",
                        desc = "লাইভ দিকনির্ণয় ও কাবা শরিফের দূরত্ব হিসাব",
                        icon = Icons.Default.CompassCalibration,
                        route = Screen.Qibla.route,
                        color = AmberPrimary
                    ),
                    MoreMenuItem(
                        title = "রমজান প্ল্যানার ও সেহরি-ইফতার",
                        desc = "৩০ দিনের সময়সূচি, রোজা ও খতম ট্র্যাকার",
                        icon = Icons.Default.CalendarMonth,
                        route = Screen.Ramadan.route,
                        color = AmberPrimary,
                        badge = "স্পেশাল"
                    ),
                    MoreMenuItem(
                        title = "নামাজের পূর্ণাঙ্গ সময়সূচি",
                        desc = "৫ ওয়াক্ত নামাজ, ওয়াক্ত শুরু, শেষ ও নিষিদ্ধ সময়",
                        icon = Icons.Default.Schedule,
                        route = Screen.Prayer.route,
                        color = EmeraldPrimary
                    )
                )
            ),
            MenuCategory(
                title = "জ্ঞান, শিক্ষা ও গাইড",
                subtitle = "ইসলামিক বিধান, যাকাত হিসাব ও হজের প্রস্তুতি",
                accentColor = AmberPrimary,
                items = listOf(
                    MoreMenuItem(
                        title = "ইসলামিক জ্ঞান ও যাকাত",
                        desc = "সালাত, সাওম, সুন্নাত ও যাকাত ক্যালকুলেটর",
                        icon = Icons.Default.School,
                        route = Screen.Knowledge.route,
                        color = EmeraldPrimary
                    ),
                    MoreMenuItem(
                        title = "উমরাহ ধারাবাহিক গাইড",
                        desc = "ইহরাম, তাওয়াফ, সায়ী এবং চেকলিস্ট",
                        icon = Icons.Default.Book,
                        route = Screen.Umrah.route,
                        color = AmberPrimary
                    ),
                    MoreMenuItem(
                        title = "ইসলামিক হিজরি ক্যালেন্ডার",
                        desc = "ইসলামিক মাস ও গুরুত্বপূর্ণ বরকতময় দিনসমূহ",
                        icon = Icons.Default.CalendarMonth,
                        route = Screen.Calendar.route,
                        color = EmeraldPrimary
                    )
                )
            ),
            MenuCategory(
                title = "ব্যক্তিগত ও সেটিংস",
                subtitle = "সংরক্ষিত আমল, থিম ও অ্যাপের যাবতীয় সেটিংস",
                accentColor = Color(0xFF6366F1),
                items = listOf(
                    MoreMenuItem(
                        title = "সংরক্ষিত বুকমার্কস",
                        desc = "পছন্দের আয়াত ও বুকমার্ক করা দোয়াসমূহ",
                        icon = Icons.Default.Bookmark,
                        route = Screen.Bookmarks.route,
                        color = AmberPrimary
                    ),
                    MoreMenuItem(
                        title = "অ্যাপ সেটিংস ও নোটিফিকেশন",
                        desc = "শহর, মাযহাব, গণনা পদ্ধতি ও ফন্ট সাইজ",
                        icon = Icons.Default.Settings,
                        route = Screen.Settings.route,
                        color = EmeraldPrimary
                    )
                )
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Header Card with Quick Theme Toggle and Location
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "মেনু ও প্রয়োজনীয় সেবা",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "প্রয়োজনীয় সকল ইসলামিক ফিচার এক নজরে",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Quick Theme Toggle Button
                        if (preferencesRepo != null) {
                            Surface(
                                onClick = { preferencesRepo.toggleDarkMode() },
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.background,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isDarkMode) Icons.Default.Nightlight else Icons.Default.WbSunny,
                                        contentDescription = "Toggle Theme",
                                        tint = if (isDarkMode) Color(0xFF818CF8) else AmberPrimary,
                                        modifier = Modifier.size(19.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (currentCity != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            onClick = { onNavigate(Screen.Settings.route) },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.background,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "বর্তমান অবস্থান: ${currentCity?.nameBn} (${currentCity?.country})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Categorized Menu Sections
        categories.forEach { category ->
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Category Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(18.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(category.accentColor)
                        )
                        Column {
                            Text(
                                text = category.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = category.subtitle,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Grouped Card Container
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        category.items.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigate(item.route) }
                                    .padding(horizontal = 16.dp, vertical = 13.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(13.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(item.color.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.title,
                                            tint = item.color,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = item.title,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (item.badge != null) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = AmberPrimary
                                                ) {
                                                    Text(
                                                        text = item.badge,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.desc,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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

                            if (index < category.items.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .height(0.8.dp)
                                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                                )
                            }
                        }
                    }
                }
            }
        }

        // App Info / Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "মুসলিম সঙ্গী • Muslim Companion",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "সংস্করণ ১.০.০ • সহীহ সুন্নাহ ও নির্ভরযোগ্য হিসেব",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
