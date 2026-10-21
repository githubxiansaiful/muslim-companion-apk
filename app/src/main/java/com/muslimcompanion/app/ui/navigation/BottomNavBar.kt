package com.muslimcompanion.app.ui.navigation

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.ui.theme.EmeraldPrimary

data class NavTabItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    val items = listOf(
        NavTabItem(Screen.Home.route, "হোম", Icons.Default.Home),
        NavTabItem(Screen.Quran.route, "কুরআন", Icons.Default.AutoStories),
        NavTabItem(Screen.Prayer.route, "নামাজ", Icons.Default.AccessTimeFilled),
        NavTabItem(Screen.Duas.route, "দোয়া", Icons.Default.VolunteerActivism),
        NavTabItem(Screen.More.route, "মেনু", Icons.Default.GridView)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 14.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = EmeraldPrimary.copy(alpha = 0.15f),
                    spotColor = EmeraldPrimary.copy(alpha = 0.25f)
                ),
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route ||
                            (item.route == Screen.More.route && (
                                currentRoute == Screen.Ramadan.route ||
                                currentRoute == Screen.Knowledge.route ||
                                currentRoute == Screen.Bookmarks.route ||
                                currentRoute == Screen.Azkar.route ||
                                currentRoute == Screen.Tasbih.route ||
                                currentRoute == Screen.Umrah.route ||
                                currentRoute == Screen.Calendar.route ||
                                currentRoute == Screen.Settings.route
                            ))

                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        label = "navIconScale"
                    )

                    val itemColor by animateColorAsState(
                        targetValue = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                        animationSpec = tween(220),
                        label = "navItemColor"
                    )

                    val pillAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 0.14f else 0.0f,
                        animationSpec = tween(200),
                        label = "navPillAlpha"
                    )

                    val pillWidth by animateDpAsState(
                        targetValue = if (isSelected) 52.dp else 38.dp,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "navPillWidth"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (currentRoute != item.route) {
                                    try {
                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    } catch (e: Exception) {}
                                    onNavigate(item.route)
                                }
                            }
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Animated pill container around icon
                        Box(
                            modifier = Modifier
                                .width(pillWidth)
                                .height(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(EmeraldPrimary.copy(alpha = pillAlpha)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = itemColor,
                                modifier = Modifier
                                    .size(22.dp)
                                    .scale(iconScale)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Text Label
                        Text(
                            text = item.label,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = itemColor,
                            letterSpacing = 0.2.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Dynamic Active Capsule Indicator
                        Box(
                            modifier = Modifier
                                .width(if (isSelected) 12.dp else 0.dp)
                                .height(3.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) EmeraldPrimary else Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}
