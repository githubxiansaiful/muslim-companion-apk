package com.muslimcompanion.app.ui.screens.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.model.DayPrayerSchedule
import com.muslimcompanion.app.data.model.PrayerTimeItem
import com.muslimcompanion.app.data.repository.PrayerRepository
import com.muslimcompanion.app.data.repository.PreferencesRepository
import com.muslimcompanion.app.ui.components.CitySelectorBottomSheet
import com.muslimcompanion.app.ui.components.NamajTimeHeroCard
import com.muslimcompanion.app.ui.navigation.Screen
import com.muslimcompanion.app.ui.theme.AmberPrimary
import com.muslimcompanion.app.ui.theme.EmeraldPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    preferencesRepo: PreferencesRepository,
    onNavigate: (String) -> Unit
) {
    val currentCity by preferencesRepo.currentCity.collectAsState()
    val calcMethod by preferencesRepo.calculationMethod.collectAsState()
    val madhab by preferencesRepo.madhab.collectAsState()
    val userName by preferencesRepo.userName.collectAsState()

    var showCityBottomSheet by remember { mutableStateOf(false) }

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

    val hijriDate = remember { PrayerRepository.getHijriDate() }

    val gregorianDateStr = remember {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("bn", "BD"))
        sdf.format(Date())
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "home_top_spacer") { Spacer(modifier = Modifier.height(8.dp)) }

        // 1. Greeting & Date Card
        item(key = "greeting_card") {
            GreetingHeader(
                userName = userName,
                cityName = currentCity.nameBn,
                hijriDate = hijriDate.formattedBn,
                gregorianDate = gregorianDateStr,
                onCityClick = { showCityBottomSheet = true }
            )
        }

        // 2. Namaj Time Section (Muslim Bangla Hero Section)
        item(key = "namaj_header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(18.dp)
                            .background(EmeraldPrimary, RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = "নামাজের সময়",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = "বিস্তারিত সময়সূচি >",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary,
                    modifier = Modifier.clickable { onNavigate(Screen.Prayer.route) }
                )
            }
        }

        item(key = "namaj_hero_card") {
            NamajTimeHeroCard(
                schedule = schedule,
                onClick = { onNavigate(Screen.Prayer.route) }
            )
        }

        // 4. Quick Shortcuts
        item(key = "quick_shortcuts") {
            QuickShortcutsGrid(onNavigate = onNavigate)
        }

        item(key = "home_bottom_spacer") { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (showCityBottomSheet) {
        CitySelectorBottomSheet(
            selectedCity = currentCity,
            onCitySelected = { preferencesRepo.saveCity(it) },
            onDismiss = { showCityBottomSheet = false }
        )
    }
}

@Composable
fun GreetingHeader(
    userName: String = "",
    cityName: String,
    hijriDate: String,
    gregorianDate: String,
    onCityClick: () -> Unit
) {
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
                Text(
                    text = if (userName.isNotBlank()) "আসসালামু আলাইকুম, $userName" else "আসসালামু আলাইকুম",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.clickable { onCityClick() }
                ) {
                    Text(
                        text = "📍 $cityName",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$hijriDate • $gregorianDate",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


data class ShortcutItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

@Composable
fun QuickShortcutsGrid(onNavigate: (String) -> Unit) {
    val shortcuts = listOf(
        ShortcutItem("তাসবিহ", Icons.Default.TouchApp, Screen.Tasbih.route, EmeraldPrimary),
        ShortcutItem("কিবলা", Icons.Default.CompassCalibration, Screen.Qibla.route, AmberPrimary),
        ShortcutItem("যিকির", Icons.Default.Nightlight, Screen.Azkar.route, EmeraldPrimary),
        ShortcutItem("রমজান", Icons.Default.CalendarMonth, Screen.Ramadan.route, AmberPrimary),
        ShortcutItem("উমরাহ", Icons.Default.Book, Screen.Umrah.route, EmeraldPrimary),
        ShortcutItem("জ্ঞান ও যাকাত", Icons.Default.School, Screen.Knowledge.route, AmberPrimary),
        ShortcutItem("বুকমার্কস", Icons.Default.Bookmark, Screen.Bookmarks.route, EmeraldPrimary),
        ShortcutItem("আরও", Icons.Default.MoreHoriz, Screen.More.route, AmberPrimary)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "প্রয়োজনীয় ফিচারসমূহ",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            shortcuts.take(4).forEach { item ->
                ShortcutButton(item = item, onClick = { onNavigate(item.route) }, modifier = Modifier.weight(1f))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            shortcuts.drop(4).take(4).forEach { item ->
                ShortcutButton(item = item, onClick = { onNavigate(item.route) }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ShortcutButton(
    item: ShortcutItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(item.color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = item.color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


