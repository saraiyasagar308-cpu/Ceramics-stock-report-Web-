package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = TerracottaPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = TerracottaPrimaryDark,
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = CeladonSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = CeladonSecondaryDark,
    onSecondaryContainer = Color(0xFFCCE8DF),
    tertiary = OchreGlazeTertiary,
    background = ClayUmberDark,
    surface = ClaySurfaceDark,
    surfaceVariant = Color(0xFF38312B),
    onBackground = Color(0xFFEDE6E0),
    onSurface = Color(0xFFEDE6E0),
    onSurfaceVariant = Color(0xFFC7BCB3),
    outline = ClayBorderDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCF),
    onPrimaryContainer = Color(0xFF380D00),
    secondary = CeladonSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCE8DF),
    onSecondaryContainer = Color(0xFF04201A),
    tertiary = OchreGlazeTertiary,
    background = StonewareSandLight,
    surface = Color(0xFFFFFFFF),
    surfaceVariant = StonewareCardLight,
    onBackground = CharcoalText,
    onSurface = CharcoalText,
    onSurfaceVariant = CharcoalMuted,
    outline = StonewareBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our handcrafted ceramics theme by default
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
