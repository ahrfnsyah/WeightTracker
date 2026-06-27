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
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Fastfood
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
import com.example.weighttracker.data.FoodEntity
import com.example.weighttracker.viewmodel.FoodViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ── Palette ───────────────────────────────────────────────────────────────────
private val PrimaryBlue   = Color(0xFF4F8EF7)
private val PrimaryPurple = Color(0xFF8B6FF7)
private val AccentRed     = Color(0xFFFF5C6A)
private val SurfaceBg     = Color(0xFFF5F7FF)
private val CardWhite     = Color(0xFFFFFFFF)
private val TextPrimary   = Color(0xFF1A1D2E)
private val TextSecondary = Color(0xFF6B7280)

// ── Screen ────────────────────────────────────────────────────────────────────
@Composable
fun FoodScreen(
    viewModel: FoodViewModel
) {
    var foodName       by remember { mutableStateOf("") }
    var calories       by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }
    var selectedFood   by remember { mutableStateOf<FoodEntity?>(null) }

    val foodList by viewModel.allFoods.collectAsState()

    val todayCalories = remember(foodList) {
        val today = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        foodList
            .filter { it.date.startsWith(today) }
            .sumOf { it.calories }
    }

    val filteredList = remember(foodList, selectedFilter) {
        val now = LocalDateTime.now()
        foodList.filter { item ->
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
                    text       = "Food Tracker",
                    style      = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text  = "Catat asupan makanan harian Anda",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            // ── Input Card ────────────────────────────────────────────────────
            item {
                FoodInputCard(
                    foodName         = foodName,
                    calories         = calories,
                    isEditing        = selectedFood != null,
                    onFoodChange     = { foodName = it },
                    onCaloriesChange = { calories = it },
                    onSave           = {
                        val cal = calories.toIntOrNull()
                        if (foodName.isNotBlank() && cal != null) {
                            val currentDate = LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))

                            if (selectedFood == null) {
                                viewModel.insertFood(
                                    name     = foodName,
                                    calories = cal,
                                    date     = currentDate
                                )
                            } else {
                                viewModel.updateFood(
                                    selectedFood!!.copy(
                                        name     = foodName,
                                        calories = cal,
                                        date     = currentDate
                                    )
                                )
                                selectedFood = null
                            }

                            foodName = ""
                            calories = ""
                        }
                    }
                )
            }

            // ── Summary Banner ────────────────────────────────────────────────
            if (todayCalories > 0) {
                item {
                    FoodSummaryBanner(totalCalories = todayCalories)
                }
            }

            // ── Section title ─────────────────────────────────────────────────
            item {
                Text(
                    text       = "Riwayat Makanan",
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
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
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

            // ── Food list ─────────────────────────────────────────────────────
            items(filteredList) { food ->
                FoodItemCard(
                    item     = food,
                    onEdit   = {
                        selectedFood = food
                        foodName     = food.name
                        calories     = food.calories.toString()
                    },
                    onDelete = {
                        viewModel.deleteFood(food)
                        if (selectedFood?.id == food.id) {
                            selectedFood = null
                            foodName     = ""
                            calories     = ""
                        }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ── Summary Banner ────────────────────────────────────────────────────────────
@Composable
private fun FoodSummaryBanner(totalCalories: Int) {
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
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text  = "Total Kalori Hari Ini",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text       = "$totalCalories",
                        style      = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text     = "kcal",
                        style    = MaterialTheme.typography.bodyMedium,
                        color    = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Rounded.Fastfood,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(28.dp)
                )
            }
        }
    }
}

// ── Input Card ────────────────────────────────────────────────────────────────
@Composable
private fun FoodInputCard(
    foodName        : String,
    calories        : String,
    isEditing       : Boolean,
    onFoodChange    : (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onSave          : () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(24.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Mini-header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isEditing) PrimaryPurple.copy(alpha = 0.12f)
                            else PrimaryBlue.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = if (isEditing) Icons.Rounded.Edit else Icons.Rounded.Add,
                        contentDescription = null,
                        tint               = if (isEditing) PrimaryPurple else PrimaryBlue,
                        modifier           = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text       = if (isEditing) "Edit Makanan" else "Input Makanan Hari Ini",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Food name field
            OutlinedTextField(
                value         = foodName,
                onValueChange = onFoodChange,
                label         = { Text("Nama Makanan") },
                leadingIcon   = {
                    Icon(
                        imageVector        = Icons.Rounded.Fastfood,
                        contentDescription = null,
                        tint               = PrimaryBlue,
                        modifier           = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(14.dp),
                colors   = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE0E4F0),
                    focusedLabelColor    = PrimaryBlue
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Calories field
            OutlinedTextField(
                value           = calories,
                onValueChange   = onCaloriesChange,
                label           = { Text("Kalori (kcal)") },
                leadingIcon     = {
                    Text(
                        text     = "🔥",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier        = Modifier.fillMaxWidth(),
                shape           = RoundedCornerShape(14.dp),
                colors          = OutlinedTextFieldDefaults.colors(
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
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEditing) PrimaryPurple else PrimaryBlue
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

// ── Food Item Card ────────────────────────────────────────────────────────────
@Composable
private fun FoodItemCard(
    item    : FoodEntity,
    onEdit  : () -> Unit,
    onDelete: () -> Unit
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

            // Icon box gradient
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
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
                    imageVector        = Icons.Rounded.Fastfood,
                    contentDescription = null,
                    tint               = PrimaryBlue,
                    modifier           = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = item.name,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text       = "${item.calories} kcal",
                    style      = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color      = PrimaryBlue
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
                    modifier = Modifier
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
                    modifier = Modifier
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