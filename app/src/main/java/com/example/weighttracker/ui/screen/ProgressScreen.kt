package com.example.weighttracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weighttracker.utils.GoalPreferences
import com.example.weighttracker.viewmodel.WeightViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

// ── Palette (sama dengan HomeScreen & WeightScreen) ───────────────────────────
private val PrimaryBlue   = Color(0xFF4F8EF7)
private val PrimaryPurple = Color(0xFF8B6FF7)
private val AccentGreen   = Color(0xFF34C789)
private val AccentOrange  = Color(0xFFFF8C42)
private val SurfaceBg     = Color(0xFFF5F7FF)
private val CardWhite     = Color(0xFFFFFFFF)
private val TextPrimary   = Color(0xFF1A1D2E)
private val TextSecondary = Color(0xFF6B7280)

@Composable
fun ProgressScreen(
    viewModel: WeightViewModel
) {
    // ── Unchanged logic ───────────────────────────────────────────────────────
    val weights by viewModel.allWeights.collectAsState()
    val context = LocalContext.current
    val goalPreferences = remember { GoalPreferences(context) }

    var goalInput by remember {
        mutableStateOf(
            if (goalPreferences.getGoal() == 0f) "" else goalPreferences.getGoal().toString()
        )
    }
    var goalWeight by remember { mutableStateOf(goalPreferences.getGoal()) }
    var selectedWeightInfo by remember { mutableStateOf<String?>(null) }
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }

    val latestWeight = weights.firstOrNull()?.weight
    val firstWeight  = weights.lastOrNull()?.weight
    val difference   = if (latestWeight != null && firstWeight != null) latestWeight - firstWeight else 0f
    val remaining    = if (goalWeight > 0f && latestWeight != null) latestWeight - goalWeight else 0f

    val formatter        = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val currentMonth     = selectedMonth
    val daysInMonth      = currentMonth.lengthOfMonth()
    val firstDayOffset   = currentMonth.atDay(1).dayOfWeek.value % 7

    val daysWithData = remember(weights, currentMonth) {
        weights.mapNotNull {
            try {
                val date = LocalDate.parse(it.date.take(10), formatter)
                if (date.month == currentMonth.month && date.year == currentMonth.year) date else null
            } catch (e: Exception) { null }
        }
    }
    // ─────────────────────────────────────────────────────────────────────────

    Surface(
        modifier = Modifier.fillMaxSize(),
        color    = SurfaceBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {

            // ── Header ────────────────────────────────────────────────────────
            Text(
                text       = "Progress",
                style      = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text  = "Pantau perkembangan berat badan Anda",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Ringkasan ─────────────────────────────────────────────────────
            SummarySection(
                firstWeight  = firstWeight,
                latestWeight = latestWeight,
                difference   = difference,
                goalWeight   = goalWeight,
                remaining    = remaining
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Target card ───────────────────────────────────────────────────
            GoalCard(
                goalInput      = goalInput,
                onInputChange  = { goalInput = it },
                onSave         = {
                    goalInput.toFloatOrNull()?.let {
                        goalPreferences.saveGoal(it)
                        goalWeight = it
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Calendar card ─────────────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(24.dp),
                colors    = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Month navigator
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick  = { selectedMonth = selectedMonth.minusMonths(1) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryBlue.copy(alpha = 0.10f))
                        ) {
                            Icon(
                                imageVector        = Icons.Rounded.ChevronLeft,
                                contentDescription = "Bulan sebelumnya",
                                tint               = PrimaryBlue
                            )
                        }

                        Text(
                            text       = "${currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary
                        )

                        IconButton(
                            onClick  = { selectedMonth = selectedMonth.plusMonths(1) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryBlue.copy(alpha = 0.10f))
                        ) {
                            Icon(
                                imageVector        = Icons.Rounded.ChevronRight,
                                contentDescription = "Bulan berikutnya",
                                tint               = PrimaryBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Day labels
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab").forEach { day ->
                            Text(
                                text      = day,
                                modifier  = Modifier.size(38.dp),
                                textAlign = TextAlign.Center,
                                style     = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color     = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar grid
                    var dayCounter = 1
                    for (week in 0 until 6) {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (day in 0 until 7) {
                                if (week == 0 && day < firstDayOffset) {
                                    Spacer(modifier = Modifier.size(38.dp))
                                } else if (dayCounter <= daysInMonth) {
                                    val currentDate = currentMonth.atDay(dayCounter)
                                    val hasData     = daysWithData.any { it == currentDate }
                                    val isToday     = currentDate == LocalDate.now()

                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    hasData  -> Brush.linearGradient(listOf(PrimaryBlue, PrimaryPurple))
                                                    isToday  -> Brush.linearGradient(listOf(PrimaryBlue.copy(alpha = 0.15f), PrimaryPurple.copy(alpha = 0.15f)))
                                                    else     -> Brush.linearGradient(listOf(Color(0xFFF0F2FF), Color(0xFFF0F2FF)))
                                                }
                                            )
                                            .clickable {
                                                if (hasData) {
                                                    val data = weights.firstOrNull {
                                                        try {
                                                            LocalDate.parse(it.date.take(10), formatter) == currentDate
                                                        } catch (e: Exception) { false }
                                                    }
                                                    selectedWeightInfo = "${data?.weight} Kg\n${data?.date}"
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text       = dayCounter.toString(),
                                            fontSize   = 13.sp,
                                            fontWeight = if (hasData || isToday) FontWeight.Bold else FontWeight.Normal,
                                            color      = when {
                                                hasData -> Color.White
                                                isToday -> PrimaryBlue
                                                else    -> TextSecondary
                                            }
                                        )
                                    }
                                    dayCounter++
                                } else {
                                    Spacer(modifier = Modifier.size(38.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(PrimaryBlue, PrimaryPurple)))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text  = "Hari dengan data berat badan",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // ── Alert dialog (unchanged logic) ────────────────────────────────────────
    if (selectedWeightInfo != null) {
        AlertDialog(
            onDismissRequest = { selectedWeightInfo = null },
            shape            = RoundedCornerShape(20.dp),
            containerColor   = CardWhite,
            title = {
                Text(
                    text       = "Riwayat Berat Badan",
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
            },
            text = {
                Text(
                    text  = selectedWeightInfo!!,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = { selectedWeightInfo = null },
                    shape   = RoundedCornerShape(12.dp),
                    colors  = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("OK", fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}

// ── Summary section ───────────────────────────────────────────────────────────
@Composable
private fun SummarySection(
    firstWeight  : Float?,
    latestWeight : Float?,
    difference   : Float,
    goalWeight   : Float,
    remaining    : Float
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(24.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                text       = "Ringkasan",
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MiniStatCard(
                    modifier = Modifier.weight(1f),
                    label    = "Berat Awal",
                    value    = "${firstWeight ?: 0f} Kg",
                    color    = PrimaryBlue
                )
                MiniStatCard(
                    modifier = Modifier.weight(1f),
                    label    = "Berat Terbaru",
                    value    = "${latestWeight ?: 0f} Kg",
                    color    = PrimaryPurple
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MiniStatCard(
                    modifier = Modifier.weight(1f),
                    label    = "Perubahan",
                    value    = "$difference Kg",
                    color    = if (difference <= 0f) AccentGreen else AccentOrange,
                    icon     = if (difference <= 0f) Icons.Rounded.TrendingDown else Icons.Rounded.TrendingUp
                )
                if (goalWeight > 0f) {
                    MiniStatCard(
                        modifier = Modifier.weight(1f),
                        label    = "Sisa Target",
                        value    = "$remaining Kg",
                        color    = PrimaryBlue,
                        icon     = Icons.Rounded.Star
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MiniStatCard(
    modifier : Modifier = Modifier,
    label    : String,
    value    : String,
    color    : Color,
    icon     : ImageVector? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(12.dp)
    ) {
        Column {
            if (icon != null) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = color,
                    modifier           = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            Text(
                text       = value,
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color      = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text  = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

// ── Goal card ─────────────────────────────────────────────────────────────────
@Composable
private fun GoalCard(
    goalInput     : String,
    onInputChange : (String) -> Unit,
    onSave        : () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(24.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier         = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Rounded.Flag,
                        contentDescription = null,
                        tint               = AccentGreen,
                        modifier           = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text       = "Target Berat Badan",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value           = goalInput,
                onValueChange   = onInputChange,
                label           = { Text("Target (Kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier        = Modifier.fillMaxWidth(),
                shape           = RoundedCornerShape(14.dp),
                colors          = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AccentGreen,
                    unfocusedBorderColor = Color(0xFFE0E4F0),
                    focusedLabelColor    = AccentGreen
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick  = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = AccentGreen)
            ) {
                Icon(
                    imageVector        = Icons.Rounded.Flag,
                    contentDescription = null,
                    modifier           = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = "Simpan Target",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp
                )
            }
        }
    }
}