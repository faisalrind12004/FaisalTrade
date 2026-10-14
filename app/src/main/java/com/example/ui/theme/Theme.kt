package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = GoldenYolk,
  onPrimary = TextPrimaryDark,
  primaryContainer = FarmAmberDark,
  onPrimaryContainer = GoldenYolkSoft,
  secondary = FarmGreen,
  onSecondary = PureWhiteSurface,
  secondaryContainer = FarmGreenDark,
  onSecondaryContainer = FarmGreenSoft,
  tertiary = FarmAmberPrimary,
  background = FarmDarkBackground,
  onBackground = TextLight,
  surface = FarmDarkSurface,
  onSurface = TextLight,
  surfaceVariant = FarmDarkSurfaceVariant,
  onSurfaceVariant = WarmSandVariant,
  outline = FarmAmberDark
)

private val LightColorScheme = lightColorScheme(
  primary = FarmAmberPrimary,
  onPrimary = PureWhiteSurface,
  primaryContainer = FarmAmberLight,
  onPrimaryContainer = FarmAmberDark,
  secondary = FarmGreen,
  onSecondary = PureWhiteSurface,
  secondaryContainer = FarmGreenSoft,
  onSecondaryContainer = FarmGreenDark,
  tertiary = GoldenYolk,
  background = CreamBackground,
  onBackground = TextPrimaryDark,
  surface = PureWhiteSurface,
  onSurface = TextPrimaryDark,
  surfaceVariant = WarmSandVariant,
  onSurfaceVariant = TextSecondaryMuted,
  outline = WarmBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set false to prioritize our signature farm yolk branding
  content: @Composable () -> Unit,
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
