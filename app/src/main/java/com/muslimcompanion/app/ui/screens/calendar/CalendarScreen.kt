package com.muslimcompanion.app.ui.screens.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.repository.PrayerRepository
import com.muslimcompanion.app.ui.theme.AmberPrimary
import com.muslimcompanion.app.ui.theme.EmeraldPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ImportantIslamicDate(
    val titleBn: String,
    val hijriDate: String,
    val desc: String,
    val highlight: Boolean
)

data class HijriMonth(
    val no: Int,
    val nameBn: String,
    val isSacred: Boolean
)

@Composable
fun CalendarScreen(onBack: (() -> Unit)? = null) {
    val hijriDate = remember { PrayerRepository.getHijriDate() }
    val gregorianDateStr = remember {
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("bn", "BD"))
        sdf.format(Date())
    }

    val importantDates = remember {
        listOf(
            ImportantIslamicDate("পবিত্র লাইলাতুল কদর", "২৭ রমজান", "হাজার মাসের চেয়েও উত্তম বরকতময় মহিমান্বিত রজনী।", true),
            ImportantIslamicDate("ঈদুল ফিতর", "১ শাওয়াল", "দীর্ঘ এক মাস সিয়াম সাধনা শেষে মুসলিম উম্মাহর আনন্দের দিন।", true),
            ImportantIslamicDate("আরাফাহ দিবস (হজের দিন)", "৯ জিলহজ্জ", "হজের মূল দিবস এবং পূর্ববর্তী ও পরবর্তী দুই বছরের গুনাহ মাফের দিন।", true),
            ImportantIslamicDate("ঈদুল আযহা ও কোরবানি", "১০ জিলহজ্জ", "হযরত ইব্রাহিম (আ.) এর সুন্নাত অনুসরণে পশু কোরবানি।", true),
            ImportantIslamicDate("পবিত্র আশুরা", "১০ মহররম", "মুসা (আ.) এবং বনি ইসরাঈলকে ফেরাউনের কবল থেকে মুক্তির শোকরিয়ার দিন।", false),
            ImportantIslamicDate("পবিত্র শবে মেরাজ", "২৭ রজব", "প্রিয় নবী ﷺ এর উর্ধ্বাকাশ ভ্রমণ ও সালাতের উপহার লাভ।", false),
            ImportantIslamicDate("শবে বরাত (লাইলাতুল বারাত)", "১৫ শাবান", "শাবান মাসের মধ্যবর্তী রজনী ও ক্ষমা প্রার্থনার বিশেষ রাত।", false)
        )
    }

    val hijriMonths = remember {
        listOf(
            HijriMonth(1, "মুহররম", true),
            HijriMonth(2, "সফর", false),
            HijriMonth(3, "রবিউল আউয়াল", false),
            HijriMonth(4, "রবিউস সানি", false),
            HijriMonth(5, "জমাদিউল আউয়াল", false),
            HijriMonth(6, "জমাদিউস সানি", false),
            HijriMonth(7, "রজব", true),
            HijriMonth(8, "শা'বান", false),
            HijriMonth(9, "রমজান", true),
            HijriMonth(10, "শাওয়াল", false),
            HijriMonth(11, "জিলকদ", true),
            HijriMonth(12, "জিলহজ্জ", true)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header Card with Dates
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
                                text = "ইসলামিক হিজরি ক্যালেন্ডার",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "উম্মুল কুরা গণনা পদ্ধতি অনুযায়ী নির্ভরযোগ্য দিনপঞ্জি",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = hijriDate.formattedBn,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = gregorianDateStr,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Important Islamic Events
        item {
            Text(
                text = "গুরুত্বপূর্ণ ইসলামিক দিবসসমূহ",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(importantDates) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (item.highlight) EmeraldPrimary.copy(alpha = 0.06f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.dp,
                    if (item.highlight) EmeraldPrimary.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.outline
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.titleBn,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.desc,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (item.highlight) AmberPrimary else MaterialTheme.colorScheme.background,
                        border = BorderStroke(
                            1.dp,
                            if (item.highlight) AmberPrimary else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(
                            text = item.hijriDate,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.highlight) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 12 Hijri Months
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "১২টি ইসলামিক হিজরি মাস",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(hijriMonths) { month ->
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
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = PrayerConstants.toBengaliDigits(month.no),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = month.nameBn,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (month.isSacred) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "সম্মানিত মাস",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
