package com.example.weighttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.rounded.DirectionsWalk
import com.example.weighttracker.data.StepRepository
import com.example.weighttracker.ui.screens.StepScreen
import com.example.weighttracker.viewmodel.StepViewModel
import com.example.weighttracker.viewmodel.StepViewModelFactory
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weighttracker.data.WeightDatabase
import com.example.weighttracker.data.WeightRepository
import com.example.weighttracker.ui.screens.HomeScreen
import com.example.weighttracker.ui.screens.ProgressScreen
import com.example.weighttracker.ui.screens.WeightScreen
import com.example.weighttracker.viewmodel.WeightViewModel
import com.example.weighttracker.viewmodel.WeightViewModelFactory
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.unit.dp

// ── Palette (konsisten dengan semua screen) ───────────────────────────────────
private val PrimaryBlue   = Color(0xFF4F8EF7)
private val SurfaceBg     = Color(0xFFF5F7FF)
private val CardWhite     = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF6B7280)

// ── Nav item data class ───────────────────────────────────────────────────────
private data class NavItem(
    val label : String,
    val icon  : ImageVector
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // ── Unchanged logic ───────────────────────────────────────────────
            var selectedIndex by remember { mutableStateOf(0) }
            val database      = WeightDatabase.getDatabase(applicationContext)
            val repository    = WeightRepository(database.weightDao())
            val factory       = WeightViewModelFactory(repository)
            val weightViewModel: WeightViewModel = viewModel(factory = factory)
            val stepRepository = StepRepository(database.stepDao())
            val stepFactory = StepViewModelFactory(stepRepository)
            val stepViewModel: StepViewModel = viewModel(factory = stepFactory)
            // ─────────────────────────────────────────────────────────────────

            val navItems = listOf(
                NavItem("Home",     Icons.Rounded.Home),
                NavItem("Weight",   Icons.Rounded.FitnessCenter),
                NavItem("Steps", Icons.Rounded.DirectionsWalk),
                NavItem("Progress", Icons.Rounded.ShowChart)
            )

            Scaffold(
                containerColor = SurfaceBg,
                bottomBar = {
                    NavigationBar(
                        containerColor = CardWhite,
                        tonalElevation = 0.dp
                    ) {
                        navItems.forEachIndexed { index, item ->
                            val isSelected = selectedIndex == index

                            NavigationBarItem(
                                selected = isSelected,
                                onClick  = { selectedIndex = index },
                                icon = {
                                    Icon(
                                        imageVector        = item.icon,
                                        contentDescription = item.label,
                                        modifier           = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text       = item.label,
                                        fontSize   = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold
                                        else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor      = PrimaryBlue,
                                    selectedTextColor      = PrimaryBlue,
                                    unselectedIconColor    = TextSecondary,
                                    unselectedTextColor    = TextSecondary,
                                    indicatorColor         = PrimaryBlue.copy(alpha = 0.12f)
                                )
                            )
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    when (selectedIndex) {
                        0 -> HomeScreen(viewModel = weightViewModel)
                        1 -> WeightScreen(viewModel = weightViewModel)
                        2 -> StepScreen(viewModel = stepViewModel)
                        3 -> ProgressScreen(viewModel = weightViewModel)
                    }
                }
            }
        }
    }
}