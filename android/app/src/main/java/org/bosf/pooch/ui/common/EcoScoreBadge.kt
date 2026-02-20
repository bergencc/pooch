package org.bosf.pooch.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bosf.pooch.ui.theme.ecoScoreBackground
import org.bosf.pooch.ui.theme.ecoScoreColor

@Composable
fun EcoScoreBadge(score: String?, modifier: Modifier = Modifier) {
    val text = score?.uppercase() ?: "?"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ecoScoreBackground(score))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Eco $text",
            color = ecoScoreColor(score),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}