package com.muslimcompanion.app.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.model.CityOption
import com.muslimcompanion.app.data.repository.LocationHelper
import com.muslimcompanion.app.ui.theme.AmberPrimary
import com.muslimcompanion.app.ui.theme.EmeraldPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelectorBottomSheet(
    selectedCity: CityOption,
    onCitySelected: (CityOption) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") } // "all", "bd", "international"
    var isLocating by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }

    fun fetchCurrentGpsLocation() {
        if (!LocationHelper.isLocationServiceEnabled(context)) {
            locationError = "ফোনের লোকেশন/GPS সার্ভিস বন্ধ রয়েছে। অনুগ্রহ করে অন করুন।"
            return
        }
        locationError = null
        isLocating = true
        coroutineScope.launch {
            try {
                val loc = LocationHelper.getBestCurrentLocation(context)
                if (loc != null) {
                    val resolvedCity = LocationHelper.resolveCityOption(context, loc)
                    isLocating = false
                    onCitySelected(resolvedCity)
                    onDismiss()
                } else {
                    isLocating = false
                    locationError = "জিপিএস অবস্থান সনাক্ত করা যায়নি। অনুগ্রহ করে নেটওয়ার্ক ও GPS নিশ্চিত করুন।"
                }
            } catch (e: Exception) {
                isLocating = false
                locationError = "লোকেশন সনাক্ত করতে সমস্যা হয়েছে: ${e.localizedMessage}"
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (isGranted) {
            fetchCurrentGpsLocation()
        } else {
            locationError = "লোকেশন পারমিশন দেওয়া হয়নি। অনুগ্রহ করে সেটিংসে পারমিশন দিন।"
        }
    }

    val filteredCities = remember(searchQuery, selectedFilter) {
        PrayerConstants.POPULAR_CITIES.filter { city ->
            val matchesFilter = when (selectedFilter) {
                "bd" -> city.country == "বাংলাদেশ"
                "international" -> city.country != "বাংলাদেশ"
                else -> true
            }
            val q = searchQuery.trim().lowercase()
            val matchesQuery = q.isEmpty() ||
                    city.nameBn.lowercase().contains(q) ||
                    city.name.lowercase().contains(q) ||
                    city.country.lowercase().contains(q)
            matchesFilter && matchesQuery
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "আপনার শহর বা অবস্থান",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "সঠিক নামাজের ওয়াক্তের জন্য শহর নির্বাচন করুন",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldPrimary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = selectedCity.nameBn,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Phone GPS Location Picker Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable(enabled = !isLocating) {
                        if (LocationHelper.hasLocationPermission(context)) {
                            fetchCurrentGpsLocation()
                        } else {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EmeraldPrimary.copy(alpha = 0.10f)
                ),
                border = BorderStroke(1.5.dp, EmeraldPrimary.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLocating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = "Current GPS Location",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = if (isLocating) "লোকেশন খোঁজা হচ্ছে..." else "বর্তমান অবস্থান নিন (GPS)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isLocating) "স্যাটেলাইট ও GPS দিয়ে অবস্থান সনাক্ত হচ্ছে..." else "ফোনের রিয়েল-টাইম GPS দিয়ে স্বয়ংক্রিয় ওয়াক্ত নির্ধারণ",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Text(
                            text = if (isLocating) "অপেক্ষা..." else "সনাক্ত করুন",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Location Error Message banner if any
            if (locationError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AmberPrimary.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚠️ $locationError",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "শহরের নাম লিখুন (যেমন: ঢাকা, মক্কা, চট্টগ্রাম)...",
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

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Pills
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isSel = selectedFilter == "all"
                    Surface(
                        onClick = { selectedFilter = "all" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = "সকল (${PrayerConstants.POPULAR_CITIES.size})",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                item {
                    val isSel = selectedFilter == "bd"
                    Surface(
                        onClick = { selectedFilter = "bd" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = "বাংলাদেশ",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                item {
                    val isSel = selectedFilter == "international"
                    Surface(
                        onClick = { selectedFilter = "international" },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = "পবিত্র স্থান ও আন্তর্জাতিক",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cities List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCities) { city ->
                    val isCurrent = city.name == selectedCity.name
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCitySelected(city)
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
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (city.country == "বাংলাদেশ") EmeraldPrimary.copy(alpha = 0.12f) else AmberPrimary.copy(alpha = 0.12f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (city.country == "বাংলাদেশ") Icons.Default.LocationOn else Icons.Default.Public,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = if (city.country == "বাংলাদেশ") EmeraldPrimary else AmberPrimary
                                    )
                                }

                                Column {
                                    Text(
                                        text = city.nameBn,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${city.name} • ${city.country} • (${city.timezone})",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
