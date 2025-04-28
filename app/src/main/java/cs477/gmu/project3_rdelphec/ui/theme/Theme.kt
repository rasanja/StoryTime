package cs477.gmu.project3_rdelphec.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF243A5A),        // Deep navy blue
    onPrimary = Color.White,
    secondary = Color(0xFFF3C84C),   // Soft gold/yellow accent
    onSecondary = Color(0xFF243A5A) , // Navy text on yellow
    background = Color(0xFFF5FDFD),
    surface = Color.White,
    onSurface = Color(0xFF121212),
    secondaryContainer = Color(0xFFFFF8E1),        // very soft yellow highlight
    onSecondaryContainer = Color(0xFF243A5A)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF1C2E4A),
    onPrimary = Color.White,
    secondary = Color(0xFFF3C84C),
    onSecondary = Color(0xFF1C2E4A),
    background = Color(0xFF121212),
    surface = Color(0xFF1A1A1A),
    onSurface = Color(0xFFFAF3DC),

    //more visible selection highlight
    secondaryContainer = Color(0xFF3F4F6A),         // Brighter than background
    onSecondaryContainer = Color(0xFFFFF8E1)
)

@Composable
fun StoryTimeTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val  colorScheme = if(useDarkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // optional custom typography
        content = content
    )
}