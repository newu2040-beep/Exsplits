package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Theme 1: Clean Minimalism (Default)
val CleanMinimalismLight = lightColorScheme(
    primary = Color(0xFF0061A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF535F70),
    background = Color(0xFFFDFBFF),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE1E2EC),
    onSurfaceVariant = Color(0xFF44474E),
    outline = Color(0xFFE1E2EC),
    outlineVariant = Color(0x80E1E2EC),
    error = Color(0xFFBA1A1A)
)

val CleanMinimalismDark = darkColorScheme(
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFBBC7DB),
    background = Color(0xFF1A1C1E),
    surface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFF43474E),
    onSurfaceVariant = Color(0xFFC3C7CF),
    outline = Color(0xFF8D9199),
    outlineVariant = Color(0x608D9199),
    error = Color(0xFFFFB4AB)
)

// Theme 2: Amethyst
val AmethystLight = lightColorScheme(primary = Color(0xFF6750A4), secondary = Color(0xFF625B71), tertiary = Color(0xFF7D5260), surface = Color(0xFFFFFBFE))
val AmethystDark = darkColorScheme(primary = Color(0xFFD0BCFF), secondary = Color(0xFFCCC2DC), tertiary = Color(0xFFEFB8C8), surface = Color(0xFF1C1B1F))

// Theme 3: Coral
val CoralLight = lightColorScheme(primary = Color(0xFF984061), secondary = Color(0xFF74565F), tertiary = Color(0xFF7C5635), surface = Color(0xFFFFFBFA))
val CoralDark = darkColorScheme(primary = Color(0xFFFFB1C8), secondary = Color(0xFFE3BDC6), tertiary = Color(0xFFEFBD94), surface = Color(0xFF201A1B))

// Theme 4: Sapphire
val SapphireLight = lightColorScheme(primary = Color(0xFF00629F), secondary = Color(0xFF526070), tertiary = Color(0xFF6A5779), surface = Color(0xFFFDFCFF))
val SapphireDark = darkColorScheme(primary = Color(0xFF9CCBFB), secondary = Color(0xFFBAC8DB), tertiary = Color(0xFFD7BDE4), surface = Color(0xFF1A1C1E))

// Theme 5: Amber
val AmberLight = lightColorScheme(primary = Color(0xFF7F5700), secondary = Color(0xFF6B5D3F), tertiary = Color(0xFF4B6547), surface = Color(0xFFFFFBFF))
val AmberDark = darkColorScheme(primary = Color(0xFFFABF4B), secondary = Color(0xFFD6C4AB), tertiary = Color(0xFFB1CFA8), surface = Color(0xFF1E1B16))

@Composable
fun ExsplitsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic color to highlight our themes
    themeIndex: Int = 0,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> when (themeIndex) {
            0 -> CleanMinimalismDark
            1 -> AmethystDark
            2 -> CoralDark
            3 -> SapphireDark
            4 -> AmberDark
            5 -> darkColorScheme(primary = Color(0xFFFFB4AB), primaryContainer = Color(0xFF93000A), secondary = Color(0xFFE7BDB2), surface = Color(0xFF201A19)) // Pastel Pink
            6 -> darkColorScheme(primary = Color(0xFFAEC6FF), primaryContainer = Color(0xFF004493), secondary = Color(0xFFBCC7DB), surface = Color(0xFF1A1C1E)) // Pastel Blue
            7 -> darkColorScheme(primary = Color(0xFF6EDFA6), primaryContainer = Color(0xFF005234), secondary = Color(0xFFB2CCC0), surface = Color(0xFF191C1A)) // Pastel Green
            8 -> darkColorScheme(primary = Color(0xFFE8C349), primaryContainer = Color(0xFF534300), secondary = Color(0xFFD2C5A1), surface = Color(0xFF1E1B16)) // Pastel Yellow
            9 -> darkColorScheme(primary = Color(0xFFD0BCFF), primaryContainer = Color(0xFF4F378B), secondary = Color(0xFFCCC2DC), surface = Color(0xFF1C1B1F)) // Pastel Purple
            else -> CleanMinimalismDark
        }
        else -> when (themeIndex) {
            0 -> CleanMinimalismLight
            1 -> AmethystLight
            2 -> CoralLight
            3 -> SapphireLight
            4 -> AmberLight
            5 -> lightColorScheme(primary = Color(0xFFBF0010), primaryContainer = Color(0xFFFFDAD6), secondary = Color(0xFF775653), surface = Color(0xFFFFFBFF)) // Pastel Pink
            6 -> lightColorScheme(primary = Color(0xFF005AC1), primaryContainer = Color(0xFFD8E2FF), secondary = Color(0xFF565E71), surface = Color(0xFFFDFBFF)) // Pastel Blue
            7 -> lightColorScheme(primary = Color(0xFF006C47), primaryContainer = Color(0xFF8CF8C1), secondary = Color(0xFF4D6357), surface = Color(0xFFFBFDF9)) // Pastel Green
            8 -> lightColorScheme(primary = Color(0xFF6F5B00), primaryContainer = Color(0xFFFFE088), secondary = Color(0xFF675F4B), surface = Color(0xFFFFFBFF)) // Pastel Yellow
            9 -> lightColorScheme(primary = Color(0xFF6750A4), primaryContainer = Color(0xFFEADDFF), secondary = Color(0xFF625B71), surface = Color(0xFFFFFBFE)) // Pastel Purple
            else -> CleanMinimalismLight
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            var context = view.context
            while (context is android.content.ContextWrapper) {
                if (context is Activity) break
                context = context.baseContext
            }
            val window = (context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
