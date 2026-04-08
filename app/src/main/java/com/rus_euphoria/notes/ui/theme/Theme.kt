package com.rus_euphoria.notes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NoStressColorScheme = lightColorScheme(
    primary = SoftBrown,
    onPrimary = WarmWhite,
    secondary = MutedBrown,
    onSecondary = WarmWhite,
    tertiary = AccentTeal,
    onTertiary = WarmWhite,
    background = Cream,
    onBackground = DarkBrown,
    surface = WarmWhite,
    onSurface = DarkBrown,
    surfaceVariant = Cream,
    onSurfaceVariant = SoftBrown,
    outline = MutedBrown,
    outlineVariant = Color(0xFFDDD5CC)
)

@Composable
fun NotesAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NoStressColorScheme,
        typography = Typography,
        content = content
    )
}
