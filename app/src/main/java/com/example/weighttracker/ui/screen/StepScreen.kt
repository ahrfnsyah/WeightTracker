package com.example.weighttracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weighttracker.data.StepEntity
import com.example.weighttracker.viewmodel.StepViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ── Palette (konsisten dengan semua screen) ───────────────────────────────────
private val PrimaryBlue   = Color(0xFF4F8EF7)
private val PrimaryPurple = Color(0xFF8B6FF7)
private val AccentOrange  = Color(0xFFFF8C42)
private val AccentRed     = Color(0xFFFF5C6A)
private val SurfaceBg     = Color(0xFFF5F7FF)
private val CardWhite     = Color(0xFFFFFFFF)
private val TextPrimary   = Color(0xFF1A1D2E)
private val TextSecondary = Color(0xFF6B7280)

@Composable
fun StepScreen(
    viewModel: StepViewModel
) {
    // ── Unchanged logic ───────────────────────────────────────────────────────
    var stepInput    by remember { mutableStateOf("") }
    var selectedStep by remember { mutableStateOf<StepEntity?>(null) }
    var selectedFilter by remember { mutableStateOf("Semua") }
    val stepList by viewModel.allSteps.collectAsState()

    val filteredSteps = remember(stepList, selectedFilter) {
        val now = LocalDateTime.now()
        stepList.filter { item ->
            if (selectedFilter == "Semua") {
                true
            } else {
                try {
                    val itemDate = LocalDateTime.parse(
                        item.date,
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                    )
                    when (selectedFilter) {
                        "3 Hari"  -> itemDate.isAfter(now.minusDays(3))
                        "7 Hari"  -> itemDate.isAfter(now.minusDays(7))
                        "14 Hari" -> itemDate.isAfter(now.minusDays(14))
                        else      -> true
                    }
                } catch (e: Exception) { true }
            }
        }
    }
    // ─────────────────────────────────────────────────────────────────────────

    // Hitung todayTotal di luar LazyColumn agar bisa dipakai sebagai kondisi item
    val todayTotal = remember(stepList) {
        val today = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        stepList
            .filter { it.date.startsWith(today) }
            .sumOf { it.steps }
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
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            // ── Header ────────────────────────────────────────────────────────
            item {
                Text(
                    text       = "Step Tracker",
                    style      = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text  = "Catat jumlah langkah harian Anda",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ── Input card ────────────────────────────────────────────────────
            item {
                InputCard(
                    stepInput      = stepInput,
                    onInputChange  = { stepInput = it },
                    isEditing      = selectedStep != null,
                    onSave         = {
                        val value = stepInput.toIntOrNull()
                        if (value != null) {
                            val currentDate = LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                            )
                            if (selectedStep == null) {
                                viewModel.insertStep(steps = value, date = currentDate)
                            } else {
                                viewModel.updateStep(
                                    selectedStep!!.copy(steps = value, date = currentDate)
                                )
                                selectedStep = null
                            }
                            stepInput = ""
                        }
                    }
                )
                Spacer(modifier = Modifier.height(28.dp))
            }

            // ── Today summary banner ──────────────────────────────────────────
            if (todayTotal > 0) {
                item {
                    TodaySummaryBanner(totalSteps = todayTotal)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // ── Section header ────────────────────────────────────────────────
            item {
                Text(
                    text       = "Riwayat Langkah",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
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
                                selectedContainerColor = AccentOrange,
                                selectedLabelColor     = Color.White,
                                containerColor         = CardWhite,
                                labelColor             = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled             = true,
                                selected            = isSelected,
                                selectedBorderColor = AccentOrange,
                                borderColor         = Color(0xFFE0E4F0),
                                selectedBorderWidth = 0.dp,
                                borderWidth         = 1.dp
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Step list ─────────────────────────────────────────────────────
            items(filteredSteps) { item ->
                StepItemCard(
                    item     = item,
                    onEdit   = {
                        selectedStep = item
                        stepInput    = item.steps.toString()
                    },
                    onDelete = { viewModel.deleteStep(item) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

// ── Input card ────────────────────────────────────────────────────────────────
@Composable
private fun InputCard(
    stepInput     : String,
    onInputChange : (String) -> Unit,
    isEditing     : Boolean,
    onSave        : () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(24.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Mini header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier         = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isEditing) PrimaryPurple.copy(alpha = 0.12f)
                            else AccentOrange.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = if (isEditing) Icons.Rounded.Edit
                        else Icons.Rounded.DirectionsWalk,
                        contentDescription = null,
                        tint               = if (isEditing) PrimaryPurple else AccentOrange,
                        modifier           = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text       = if (isEditing) "Edit Langkah" else "Catat Langkah Hari Ini",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value           = stepInput,
                onValueChange   = onInputChange,
                label           = { Text("Jumlah Langkah") },
                leadingIcon     = {
                    Icon(
                        imageVector        = Icons.Rounded.DirectionsWalk,
                        contentDescription = null,
                        tint               = AccentOrange,
                        modifier           = Modifier.size(20.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier        = Modifier.fillMaxWidth(),
                shape           = RoundedCornerShape(14.dp),
                colors          = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = if (isEditing) PrimaryPurple else AccentOrange,
                    unfocusedBorderColor = Color(0xFFE0E4F0),
                    focusedLabelColor    = if (isEditing) PrimaryPurple else AccentOrange
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick  = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = if (isEditing) PrimaryPurple else AccentOrange
                )
            ) {
                Icon(
                    imageVector        = if (isEditing) Icons.Rounded.Edit else Icons.Rounded.Add,
                    contentDescription = null,
                    modifier           = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = if (isEditing) "Update" else "Simpan",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp
                )
            }
        }
    }
}

// ── Today summary banner ──────────────────────────────────────────────────────
@Composable
private fun TodaySummaryBanner(totalSteps: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(listOf(AccentOrange, Color(0xFFFF6B35)))
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier              = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text  = "Total Hari Ini",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text       = "$totalSteps",
                    style      = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White
                )
                Text(
                    text  = "langkah",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            Box(
                modifier         = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Rounded.DirectionsWalk,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(30.dp)
                )
            }
        }
    }
}

// ── Step item card ────────────────────────────────────────────────────────────
@Composable
private fun StepItemCard(
    item     : StepEntity,
    onEdit   : () -> Unit,
    onDelete : () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Step icon box
            Box(
                modifier         = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                AccentOrange.copy(alpha = 0.15f),
                                AccentOrange.copy(alpha = 0.08f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Rounded.DirectionsWalk,
                    contentDescription = null,
                    tint               = AccentOrange,
                    modifier           = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Steps & date
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "${item.steps} langkah",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector        = Icons.Rounded.DateRange,
                        contentDescription = null,
                        tint               = TextSecondary,
                        modifier           = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text  = item.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            // Edit button
            IconButton(onClick = onEdit) {
                Box(
                    modifier         = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryBlue.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Rounded.Edit,
                        contentDescription = "Edit",
                        tint               = PrimaryBlue,
                        modifier           = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Delete button
            IconButton(onClick = onDelete) {
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
                        modifier           = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}