package com.android.swar.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val DarkColorPalette = darkColorScheme(
    primary = Purple200,
    inversePrimary = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColorScheme(
    primary = Purple500,
    inversePrimary = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

private val YellowPrimary = Color(0xFFFFC107)
private val DarkBluePrimary = Color(0xFF0D47A1)
private val LightBluePrimary = Color(0xFF42A5F5)

// Variations for depth and lighting
val YellowLight = Color(0xFFFFD54F)
val YellowDark = Color(0xFFFFA000)
private val DarkBlueLight = Color(0xFF5472d3)
private val DarkBlueDark = Color(0xFF002171)
private val LightBlueLight = Color(0xFF80D6FF)
private val LightBlueDark = Color(0xFF0077C2)


// Dark color scheme with variations
private val DarkColoredScheme = darkColorScheme(
    primary = YellowPrimary,
    onPrimary = Color.Black,
    primaryContainer = YellowDark,
    onPrimaryContainer = Color.White,
    secondary = DarkBluePrimary,
    onSecondary = Color.White,
    secondaryContainer = DarkBlueDark,
    onSecondaryContainer = Color.White,
    tertiary = LightBluePrimary,
    onTertiary = Color.Black,
    tertiaryContainer = LightBlueDark,
    onTertiaryContainer = Color.Black,
    background = DarkBlueDark,
    onBackground = Color.White,
    surface = DarkBluePrimary,
    onSurface = Color.White
)

// Light color scheme with variations
private val LightColoredScheme = lightColorScheme(
    primary = YellowPrimary,
    onPrimary = Color.Black,
    primaryContainer = YellowLight,
    onPrimaryContainer = Color.Black,
    secondary = LightBluePrimary,
    onSecondary = Color.White,
    secondaryContainer = LightBlueLight,
    onSecondaryContainer = Color.Black,
    tertiary = DarkBluePrimary,
    onTertiary = Color.White,
    tertiaryContainer = DarkBlueLight,
    onTertiaryContainer = Color.White,
    background = LightBlueLight,
    onBackground = Color.Black,
    surface = LightBluePrimary,
    onSurface = Color.Black
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColoredScheme
        else -> LightColoredScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

@Composable
fun SwarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun ComposeGoogleSignInCleanArchitectureTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}