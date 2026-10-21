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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.data.model.CalculationMethod
import com.muslimcompanion.app.data.model.Madhab
import com.muslimcompanion.app.ui.theme.EmeraldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerSettingsBottomSheet(
    currentMethod: CalculationMethod,
    currentMadhab: Madhab,
    onMethodChange: (CalculationMethod) -> Unit,
    onMadhabChange: (Madhab) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedMethod by remember { mutableStateOf(currentMethod) }
    var selectedMadhab by remember { mutableStateOf(currentMadhab) }

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
            Text(
                text = "নামাজের ওয়াক্ত ও হিসেব সেটিংস",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "আপনার এলাকার উপযোগী মাযহাব ও হিসাব পদ্ধতি নির্বাচন করুন",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Madhab Section
                item {
                    Text(
                        text = "মাযহাব (আসর নামাজের ওয়াক্ত নির্ধারণ)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Madhab.values().forEach { madhab ->
                            val isSelected = selectedMadhab == madhab
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedMadhab = madhab
                                        onMadhabChange(madhab)
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) EmeraldPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.background
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (madhab == Madhab.HANAFI) "হানাফি" else "শাফেয়ি / হাম্বলি / মালিকি",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (madhab == Madhab.HANAFI)
                                                "আসর নামাজ কিছুটা বিলম্বে শুরু হয় (দ্বিগুণ ছায়া পদ্ধতি)"
                                            else
                                                "আসর প্রথম ওয়াক্তে শুরু হয় (এক গুণ ছায়া পদ্ধতি)",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    if (isSelected) {
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

                // Calculation Method Section
                item {
                    Text(
                        text = "নামাজের সময় হিসাব পদ্ধতি",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CalculationMethod.values().forEach { method ->
                            val isSelected = selectedMethod == method
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedMethod = method
                                        onMethodChange(method)
                                    },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) EmeraldPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.background
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = method.titleBn,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    if (isSelected) {
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

            Spacer(modifier = Modifier.height(16.dp))

            // Done Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "সংরক্ষণ করুন",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
