package com.example.weighttracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weighttracker.data.WeightEntity
import com.example.weighttracker.viewmodel.WeightViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ── Palette (sama dengan HomeScreen) ─────────────────────────────────────────
private val PrimaryBlue   = Color(0xFF4F8EF7)
private val PrimaryPurple = Color(0xFF8B6FF7)
private val AccentGreen   = Color(0xFF34C789)
private val AccentRed     = Color(0xFFFF5C6A)
private val SurfaceBg     = Color(0xFFF5F7FF)
private val CardWhite     = Color(0xFFFFFFFF)
private val TextPrimary   = Color(0xFF1A1D2E)
private val TextSecondary = Color(0xFF6B7280)

@Composable
fun WeightScreen(
    viewModel: WeightViewModel
) {
    // ── Unchanged logic ───────────────────────────────────────────────────────
    var weight by remember { mutableStateOf("") }
    var selectedWeight by remember { mutableStateOf<WeightEntity?>(null) }
    val weights by viewModel.allWeights.collectAsState()
    var selectedFilter by remember { mutableStateOf("Semua") }

    val filteredWeights = remember(weights, selectedFilter) {
        val now = LocalDateTime.now()
        weights.filter { item ->
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SurfaceBg
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Text(
                    text = "Weight Tracker",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Catat perkembangan berat badan Anda",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))

                InputCard(
                    weight = weight,
                    onWeightChange = { weight = it },
                    isEditing = selectedWeight != null,
                    onSave = {
                        val weightValue = weight.toFloatOrNull()

                        if (weightValue != null) {
                            val currentDate = LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                            )

                            if (selectedWeight == null) {
                                viewModel.insertWeight(
                                    weight = weightValue,
                                    date = currentDate
                                )
                            } else {
                                viewModel.updateWeight(
                                    selectedWeight!!.copy(
                                        weight = weightValue,
                                        date = currentDate
                                    )
                                )
                                selectedWeight = null
                            }

                            weight = ""
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Riwayat Berat Badan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "Semua",
                        "3 Hari",
                        "7 Hari",
                        "14 Hari"
                    ).forEach { filter ->

                        val isSelected = selectedFilter == filter

                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedFilter = filter
                            },
                            label = {
                                Text(
                                    text = filter,
                                    fontWeight =
                                        if (isSelected)
                                            FontWeight.SemiBold
                                        else
                                            FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                selectedLabelColor = Color.White,
                                containerColor = CardWhite,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = PrimaryBlue,
                                borderColor = Color(0xFFE0E4F0),
                                selectedBorderWidth = 0.dp,
                                borderWidth = 1.dp
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            items(filteredWeights) { item ->
                WeightItemCard(
                    item = item,
                    onEdit = {
                        selectedWeight = item
                        weight = item.weight.toString()
                    },
                    onDelete = {
                        viewModel.deleteWeight(item)

                        if (selectedWeight?.id == item.id) {
                            selectedWeight = null
                            weight = ""
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ── Input card ────────────────────────────────────────────────────────────────
@Composable
private fun InputCard(
    weight         : String,
    onWeightChange : (String) -> Unit,
    isEditing      : Boolean,
    onSave         : () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(24.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Card mini-header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier         = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isEditing) PrimaryPurple.copy(alpha = 0.12f)
                            else PrimaryBlue.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector      = if (isEditing) Icons.Rounded.Edit else Icons.Rounded.Add,
                        contentDescription = null,
                        tint             = if (isEditing) PrimaryPurple else PrimaryBlue,
                        modifier         = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text       = if (isEditing) "Edit Berat Badan" else "Input Berat Hari Ini",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Text field
            OutlinedTextField(
                value         = weight,
                onValueChange = onWeightChange,
                label         = { Text("Berat Badan (Kg)") },
                leadingIcon   = {
                    Icon(
                        imageVector      = Icons.Rounded.FitnessCenter,
                        contentDescription = null,
                        tint             = PrimaryBlue,
                        modifier         = Modifier.size(20.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(14.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE0E4F0),
                    focusedLabelColor    = PrimaryBlue
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save / Update button
            Button(
                onClick  = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = if (isEditing) PrimaryPurple else PrimaryBlue
                )
            ) {
                Icon(
                    imageVector      = if (isEditing) Icons.Rounded.Edit else Icons.Rounded.Add,
                    contentDescription = null,
                    modifier         = Modifier.size(18.dp)
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

// ── Weight item card ──────────────────────────────────────────────────────────
@Composable
private fun WeightItemCard(
    item     : WeightEntity,
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

            // Weight icon
            Box(
                modifier         = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                PrimaryBlue.copy(alpha = 0.15f),
                                PrimaryPurple.copy(alpha = 0.15f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "${item.weight}",
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color      = PrimaryBlue,
                    fontSize   = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Weight & date info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "${item.weight} Kg",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector      = Icons.Rounded.DateRange,
                        contentDescription = null,
                        tint             = TextSecondary,
                        modifier         = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text  = item.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            // Action buttons
            IconButton(onClick = onEdit) {
                Box(
                    modifier         = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryBlue.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector      = Icons.Rounded.Edit,
                        contentDescription = "Edit",
                        tint             = PrimaryBlue,
                        modifier         = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(onClick = onDelete) {
                Box(
                    modifier         = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentRed.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector      = Icons.Rounded.Delete,
                        contentDescription = "Hapus",
                        tint             = AccentRed,
                        modifier         = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}