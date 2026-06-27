package com.example.weighttracker.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weighttracker.utils.GoalPreferences
import com.example.weighttracker.viewmodel.WeightViewModel

// ── Palette ──────────────────────────────────────────────────────────────────
private val PrimaryBlue    = Color(0xFF4F8EF7)
private val PrimaryPurple  = Color(0xFF8B6FF7)
private val AccentGreen    = Color(0xFF34C789)
private val AccentOrange   = Color(0xFFFF8C42)
private val SurfaceBg      = Color(0xFFF5F7FF)
private val CardWhite      = Color(0xFFFFFFFF)
private val TextPrimary    = Color(0xFF1A1D2E)
private val TextSecondary  = Color(0xFF6B7280)

@Composable
fun HomeScreen(
    viewModel: WeightViewModel,
    onSummaryClick: () -> Unit
) {
    // ── Unchanged logic ───────────────────────────────────────────────────────
    val weights by viewModel.allWeights.collectAsState()
    val context = LocalContext.current
    val goalPreferences = remember { GoalPreferences(context) }

    val goalWeight    = goalPreferences.getGoal()
    val latestWeight  = weights.firstOrNull()?.weight ?: 0f
    val firstWeight   = weights.lastOrNull()?.weight  ?: 0f
    val difference    = latestWeight - firstWeight
    val remaining     = if (goalWeight > 0f) latestWeight - goalWeight else 0f
    val lastActivity  = weights.firstOrNull()?.date ?: "-"
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
            HeaderSection()

            Spacer(modifier = Modifier.height(24.dp))

            // ── Hero card: berat saat ini + target ────────────────────────────
            HeroCard(
                latestWeight = latestWeight,
                goalWeight   = goalWeight
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Stat cards row ────────────────────────────────────────────────
            Row(
                modifier             = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier    = Modifier.weight(1f),
                    icon        = if (difference >= 0) Icons.Rounded.TrendingUp
                    else Icons.Rounded.TrendingDown,
                    iconTint    = if (difference >= 0) AccentOrange else AccentGreen,
                    iconBg      = if (difference >= 0) AccentOrange.copy(alpha = 0.12f)
                    else AccentGreen.copy(alpha = 0.12f),
                    value       = "$difference Kg",
                    label       = "Perubahan"
                )
                StatCard(
                    modifier    = Modifier.weight(1f),
                    icon        = Icons.Rounded.Star,
                    iconTint    = PrimaryPurple,
                    iconBg      = PrimaryPurple.copy(alpha = 0.12f),
                    value       = if (goalWeight > 0f) "$remaining Kg" else "-",
                    label       = "Sisa Menuju Target"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Aktivitas terakhir ────────────────────────────────────────────
            LastActivityCard(lastActivity = lastActivity)

            Spacer(modifier = Modifier.height(16.dp))

            SummaryButton(
                onClick = onSummaryClick
            )
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────
@Composable
private fun HeaderSection() {
    Column {
        Text(
            text       = "Welcome Back 👋",
            style      = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color      = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text  = "Tetap jaga progresmu hari ini.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

// ── Hero card ─────────────────────────────────────────────────────────────────
@Composable
private fun HeroCard(
    latestWeight : Float,
    goalWeight   : Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(PrimaryBlue, PrimaryPurple)
                )
            )
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // Berat saat ini
            Column {
                Text(
                    text  = "Berat Saat Ini",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text       = "$latestWeight",
                    style      = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White
                )
                Text(
                    text  = "Kilogram",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Divider
            Box(
                modifier = Modifier
                    .height(64.dp)
                    .width(1.dp)
                    .background(Color.White.copy(alpha = 0.3f))
            )

            // Target
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text  = "Target",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text       = if (goalWeight > 0f) "$goalWeight" else "-",
                    style      = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White
                )
                if (goalWeight > 0f) {
                    Text(
                        text  = "Kilogram",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

// ── Stat card ─────────────────────────────────────────────────────────────────
@Composable
fun StatCard(
    modifier : Modifier = Modifier,
    icon     : ImageVector,
    iconTint : Color,
    iconBg   : Color,
    value    : String,
    label    : String
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier          = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment  = Alignment.Center
            ) {
                Icon(
                    imageVector      = icon,
                    contentDescription = null,
                    tint             = iconTint,
                    modifier         = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text       = value,
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text  = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

// ── Last activity card ────────────────────────────────────────────────────────
@Composable
private fun LastActivityCard(lastActivity: String) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier          = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PrimaryBlue.copy(alpha = 0.12f)),
                contentAlignment  = Alignment.Center
            ) {
                Icon(
                    imageVector      = Icons.Rounded.DateRange,
                    contentDescription = null,
                    tint             = PrimaryBlue,
                    modifier         = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text       = "Aktivitas Terakhir",
                    style      = MaterialTheme.typography.labelMedium,
                    color      = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text       = lastActivity,
                    style      = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary
                )
            }
        }
    }
}

// ── Legacy DashboardCard (kept for compatibility) ─────────────────────────────
@Composable
fun DashboardCard(
    modifier : Modifier = Modifier,
    value    : String,
    label    : String
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text       = value,
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text  = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun SummaryButton(
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryBlue
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Summary Hari Ini",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}