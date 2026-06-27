package com.example.weighttracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weighttracker.data.SummaryEntity
import com.example.weighttracker.viewmodel.FoodViewModel
import com.example.weighttracker.viewmodel.StepViewModel
import com.example.weighttracker.viewmodel.SummaryViewModel
import com.example.weighttracker.viewmodel.WeightViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.abs

// ── Palette ───────────────────────────────────────────────────────────────────
private val PrimaryBlue   = Color(0xFF4F8EF7)
private val PrimaryPurple = Color(0xFF8B6FF7)
private val AccentRed     = Color(0xFFFF5C6A)
private val SurfaceBg     = Color(0xFFF5F7FF)
private val CardWhite     = Color(0xFFFFFFFF)
private val TextPrimary   = Color(0xFF1A1D2E)
private val TextSecondary = Color(0xFF6B7280)

@Composable
fun SummaryScreen(
    summaryViewModel: SummaryViewModel,
    weightViewModel : WeightViewModel,
    stepViewModel   : StepViewModel,
    foodViewModel   : FoodViewModel,
    onBack          : () -> Unit
) {
    val summaries by summaryViewModel.allSummary.collectAsState()
    val weights   by weightViewModel.allWeights.collectAsState()
    val steps     by stepViewModel.allSteps.collectAsState()
    val foods     by foodViewModel.allFoods.collectAsState()

    val scope = rememberCoroutineScope()

    var selectedFilter by remember { mutableStateOf("Semua") }

    val currentDate = SimpleDateFormat(
        "dd MMMM yyyy", Locale("id", "ID")
    ).format(Date())

    val todaySummary = summaries.find { it.date == currentDate }

    // ── Filter logic ──────────────────────────────────────────────────────────
    val filteredSummaries = remember(summaries, selectedFilter) {
        if (selectedFilter == "Semua") {
            summaries
        } else {
            val formatter  = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id", "ID"))
            val now        = LocalDate.now()
            val daysBack   = when (selectedFilter) {
                "3 Hari"  -> 3L
                "7 Hari"  -> 7L
                "14 Hari" -> 14L
                else      -> Long.MAX_VALUE
            }
            summaries.filter { item ->
                try {
                    val itemDate = LocalDate.parse(item.date, formatter)
                    itemDate.isAfter(now.minusDays(daysBack))
                } catch (e: Exception) { true }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color    = SurfaceBg
    ) {
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding      = PaddingValues(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Header ────────────────────────────────────────────────────────
            item {
                Text(
                    text       = "Summary Progress",
                    style      = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text  = "Ringkasan otomatis perkembangan berat badan, langkah kaki, dan asupan kalori harian Anda dalam satu laporan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            // ── Info banner ───────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(PrimaryBlue, PrimaryPurple)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier         = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.20f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint               = Color.White,
                                modifier           = Modifier.size(28.dp)
                            )
                        }
                        Column {
                            Text(
                                text       = "Laporan Hari Ini",
                                style      = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color      = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text  = if (todaySummary != null)
                                    "Summary sudah dibuat — Anda bisa memperbaruinya"
                                else
                                    "Belum ada summary hari ini — generate sekarang",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // ── Generate / Update & Back buttons ──────────────────────────────
            item {
                Button(
                    onClick = {
                        scope.launch {
                            if (
                                weights.isEmpty() &&
                                steps.isEmpty()   &&
                                foods.isEmpty()
                            ) return@launch

                            val latestWeight   = weights.firstOrNull()?.weight ?: 0f
                            val previousWeight = weights.getOrNull(1)?.weight ?: latestWeight
                            val difference     = latestWeight - previousWeight
                            val latestSteps    = steps.firstOrNull()?.steps ?: 0
                            val totalCalories  = foods.sumOf { it.calories }

                            val summaryText = buildString {
                                append("Berat badan hari ini ")
                                when {
                                    difference > 0f -> append(
                                        "naik ${String.format("%.1f", difference)} kg dibanding sebelumnya. "
                                    )
                                    difference < 0f -> append(
                                        "turun ${String.format("%.1f", abs(difference))} kg dibanding sebelumnya. "
                                    )
                                    else -> append("tidak mengalami perubahan. ")
                                }
                                append("Jumlah langkah hari ini adalah $latestSteps langkah. ")
                                append("Total kalori yang dikonsumsi hari ini sebesar $totalCalories kkal.")
                            }

                            val today = SimpleDateFormat(
                                "dd MMMM yyyy", Locale("id", "ID")
                            ).format(Date())

                            val existing = summaryViewModel.getSummaryByDate(today)

                            if (existing == null) {
                                summaryViewModel.saveSummary(
                                    SummaryEntity(date = today, summary = summaryText)
                                )
                            } else {
                                summaryViewModel.updateSummary(
                                    existing.copy(summary = summaryText)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape  = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (todaySummary == null) PrimaryBlue else PrimaryPurple
                    )
                ) {
                    Icon(
                        imageVector        = if (todaySummary == null)
                            Icons.Rounded.AutoAwesome else Icons.Rounded.Refresh,
                        contentDescription = null,
                        modifier           = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text       = if (todaySummary == null)
                            "Generate Summary Hari Ini" else "Update Summary Hari Ini",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick  = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape  = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextSecondary
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color(0xFFE0E4F0)
                    )
                ) {
                    Icon(
                        imageVector        = Icons.Rounded.ArrowBack,
                        contentDescription = null,
                        modifier           = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text       = "Kembali",
                        fontWeight = FontWeight.Medium,
                        fontSize   = 15.sp
                    )
                }
            }

            // ── Section title ─────────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text       = "Riwayat Summary",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
            }

            // ── Filter chips ──────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Semua", "3 Hari", "7 Hari", "14 Hari").forEach { filter ->
                        val isSelected = selectedFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick  = { selectedFilter = filter },
                            label    = {
                                Text(
                                    text       = filter,
                                    fontWeight = if (isSelected) FontWeight.SemiBold
                                    else FontWeight.Normal,
                                    fontSize   = 13.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                selectedLabelColor     = Color.White,
                                containerColor         = CardWhite,
                                labelColor             = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled             = true,
                                selected            = isSelected,
                                selectedBorderColor = PrimaryBlue,
                                borderColor         = Color(0xFFE0E4F0),
                                selectedBorderWidth = 0.dp,
                                borderWidth         = 1.dp
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // ── Summary list ──────────────────────────────────────────────────
            items(filteredSummaries) { item ->
                SummaryItemCard(
                    item     = item,
                    onDelete = { summaryViewModel.deleteSummary(item) }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ── Summary Item Card ─────────────────────────────────────────────────────────
@Composable
private fun SummaryItemCard(
    item    : SummaryEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Date row
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier              = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier         = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        PrimaryBlue.copy(alpha = 0.15f),
                                        PrimaryPurple.copy(alpha = 0.15f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Rounded.DateRange,
                            contentDescription = null,
                            tint               = PrimaryBlue,
                            modifier           = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text       = item.date,
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary
                    )
                }

                IconButton(
                    onClick  = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(
                        modifier         = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentRed.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Rounded.Delete,
                            contentDescription = "Hapus",
                            tint               = AccentRed,
                            modifier           = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0E4F0))
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text       = item.summary,
                style      = MaterialTheme.typography.bodyMedium,
                color      = TextSecondary,
                lineHeight = 22.sp
            )
        }
    }
}