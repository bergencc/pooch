package org.bosf.pooch.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand Colors
val Orange500 = Color(0xFFFF6B35)
val Orange600 = Color(0xFFE85D2E)
val Orange100 = Color(0xFFFFE0D5)
val Orange50  = Color(0xFFFFF3EE)

val Brown900  = Color(0xFF2D1B0E)
val Brown700  = Color(0xFF5C3317)
val Cream100  = Color(0xFFFAF6F1)

val Green500  = Color(0xFF2ECC71)
val Green100  = Color(0xFFD5F5E3)
val Red500    = Color(0xFFE74C3C)
val Red100    = Color(0xFFFADEDB)
val Yellow500 = Color(0xFFF39C12)
val Yellow100 = Color(0xFFFEF3D7)

private val LightColorScheme = lightColorScheme(
    primary            = Orange500,
    onPrimary          = Color.White,
    primaryContainer   = Orange100,
    onPrimaryContainer = Brown700,
    secondary          = Brown700,
    onSecondary        = Color.White,
    secondaryContainer = Orange50,
    onSecondaryContainer = Brown900,
    background         = Cream100,
    onBackground       = Brown900,
    surface            = Color.White,
    onSurface          = Brown900,
    surfaceVariant     = Orange50,
    onSurfaceVariant   = Brown700,
    error              = Red500,
    onError            = Color.White,
    errorContainer     = Red100,
    onErrorContainer   = Red500,
    outline            = Color(0xFFCCC0B8)
)

private val DarkColorScheme = darkColorScheme(
    primary            = Orange500,
    onPrimary          = Brown900,
    primaryContainer   = Orange600,
    onPrimaryContainer = Color.White,
    secondary          = Orange100,
    onSecondary        = Brown900,
    background         = Color(0xFF1A0E07),
    onBackground       = Color(0xFFFAF6F1),
    surface            = Color(0xFF2A1A0E),
    onSurface          = Color(0xFFFAF6F1),
)

@Composable
fun PoochScanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

// Eco score colors
fun ecoScoreColor(score: String?): Color {
    return when (score?.uppercase()) {
        "A" -> Green500
        "B" -> Color(0xFF82CA9D)
        "C" -> Yellow500
        "D" -> Color(0xFFF0965A)
        "E", "F" -> Red500
        else -> Color.Gray
    }
}

fun ecoScoreBackground(score: String?): Color {
    return when (score?.uppercase()) {
        "A" -> Green100
        "B" -> Color(0xFFDDF0E5)
        "C" -> Yellow100
        "D" -> Orange100
        "E", "F" -> Red100
        else -> Color.LightGray
    }
}
