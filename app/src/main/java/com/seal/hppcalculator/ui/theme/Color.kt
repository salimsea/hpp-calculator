package com.seal.hppcalculator.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Clean & Modern Purple-Orange Minimalist Palette (Refer to Dribbble reference)
val PurplePrimary = Color(0xFF6C5CE7) // Modern Vibrant Purple/Indigo
val PurpleDark = Color(0xFF5647C9)
val PurpleLight = Color(0xFFF0EEFF) // Soft Lavender Container

val OrangeAccent = Color(0xFFFFA114) // Warm Tangerine / Amber
val OrangeDark = Color(0xFFE68900)
val OrangeLight = Color(0xFFFFF4E5) // Soft Peach Container

val BackgroundLight = Color(0xFFF8F9FE) // Crisp Clean Off-White Background
val SurfaceLight = Color(0xFFFFFFFF) // Pure White Cards
val SurfaceVariantLight = Color(0xFFF1F3F9) // Soft Pill / Field Background
val TextPrimaryLight = Color(0xFF1E1B4B) // Dark Indigo-Slate for high readability
val TextSecondaryLight = Color(0xFF8C8FA7) // Muted Lavender-Gray
val OutlineLight = Color(0xFFE2E5F0) // Subtle Divider & Border
val CardBorderSubtle = Color(0xFFE5E8F2) // Soft Card & Input Border

// Semantic Tag Colors (Pastel badges as seen on cards)
val TagBlueBg = Color(0xFFE8F1FF)
val TagBlueText = Color(0xFF2F80ED)
val TagOrangeBg = Color(0xFFFFF0E0)
val TagOrangeText = Color(0xFFFF8A00)
val TagPurpleBg = Color(0xFFF3EFFF)
val TagPurpleText = Color(0xFF6C5CE7)
val TagGreenBg = Color(0xFFE6F9F0)
val TagGreenText = Color(0xFF00AA5B)
val TagAmberBg = Color(0xFFFFFBEB)
val TagAmberText = Color(0xFFD97706)

// Bento Gradients (from reference image)
val PurpleCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF8170FA), Color(0xFF6352E6))
)
val OrangeCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFFFB547), Color(0xFFFF9214))
)

// Dark Theme Variants
val BackgroundDark = Color(0xFF10121E)
val SurfaceDark = Color(0xFF1A1D2D)
val TextPrimaryDark = Color(0xFFF8F9FE)
val TextSecondaryDark = Color(0xFF9EA2B8)
val OutlineDark = Color(0xFF282D42)

val ErrorColor = Color(0xFFEF4444)

val md_theme_light_primary = PurplePrimary
val md_theme_light_onPrimary = Color.White
val md_theme_light_primaryContainer = PurpleLight
val md_theme_light_onPrimaryContainer = PurpleDark
val md_theme_light_secondary = OrangeAccent
val md_theme_light_onSecondary = Color.White
val md_theme_light_secondaryContainer = OrangeLight
val md_theme_light_onSecondaryContainer = OrangeDark
val md_theme_light_error = ErrorColor
val md_theme_light_onError = Color.White
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = BackgroundLight
val md_theme_light_onBackground = TextPrimaryLight
val md_theme_light_surface = SurfaceLight
val md_theme_light_onSurface = TextPrimaryLight
val md_theme_light_surfaceVariant = SurfaceVariantLight
val md_theme_light_onSurfaceVariant = TextSecondaryLight
val md_theme_light_outline = OutlineLight

val md_theme_dark_primary = Color(0xFF9587FF)
val md_theme_dark_onPrimary = Color(0xFF1F1066)
val md_theme_dark_primaryContainer = Color(0xFF382A85)
val md_theme_dark_onPrimaryContainer = Color(0xFFE4DFFF)
val md_theme_dark_secondary = Color(0xFFFFB85E)
val md_theme_dark_onSecondary = Color(0xFF472600)
val md_theme_dark_secondaryContainer = Color(0xFF663900)
val md_theme_dark_onSecondaryContainer = Color(0xFFFFDDB8)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = BackgroundDark
val md_theme_dark_onBackground = TextPrimaryDark
val md_theme_dark_surface = SurfaceDark
val md_theme_dark_onSurface = TextPrimaryDark
val md_theme_dark_surfaceVariant = Color(0xFF282D42)
val md_theme_dark_onSurfaceVariant = TextSecondaryDark
val md_theme_dark_outline = OutlineDark