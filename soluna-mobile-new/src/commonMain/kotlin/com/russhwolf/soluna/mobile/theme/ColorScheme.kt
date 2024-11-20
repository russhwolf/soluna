package com.russhwolf.soluna.mobile.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class SolunaColorScheme(
    private val materialColors: ColorScheme = lightColorScheme(),
    private val extendedColors: ExtendedColorScheme = ExtendedColorScheme(),
) {
    val primary: Color = materialColors.primary
    val onPrimary: Color = materialColors.onPrimary
    val primaryContainer: Color = materialColors.primaryContainer
    val onPrimaryContainer: Color = materialColors.onPrimaryContainer
    val inversePrimary: Color = materialColors.inversePrimary
    val secondary: Color = materialColors.secondary
    val onSecondary: Color = materialColors.onSecondary
    val secondaryContainer: Color = materialColors.secondaryContainer
    val onSecondaryContainer: Color = materialColors.onSecondaryContainer
    val tertiary: Color = materialColors.tertiary
    val onTertiary: Color = materialColors.onTertiary
    val tertiaryContainer: Color = materialColors.tertiaryContainer
    val onTertiaryContainer: Color = materialColors.onTertiaryContainer
    val background: Color = materialColors.background
    val onBackground: Color = materialColors.onBackground
    val surface: Color = materialColors.surface
    val onSurface: Color = materialColors.onSurface
    val surfaceVariant: Color = materialColors.surfaceVariant
    val onSurfaceVariant: Color = materialColors.onSurfaceVariant
    val surfaceTint: Color = materialColors.surfaceTint
    val inverseSurface: Color = materialColors.inverseSurface
    val inverseOnSurface: Color = materialColors.inverseOnSurface
    val error: Color = materialColors.error
    val onError: Color = materialColors.onError
    val errorContainer: Color = materialColors.errorContainer
    val onErrorContainer: Color = materialColors.onErrorContainer
    val outline: Color = materialColors.outline
    val outlineVariant: Color = materialColors.outlineVariant
    val scrim: Color = materialColors.scrim
    val surfaceBright: Color = materialColors.surfaceBright
    val surfaceDim: Color = materialColors.surfaceDim
    val surfaceContainer: Color = materialColors.surfaceContainer
    val surfaceContainerHigh: Color = materialColors.surfaceContainerHigh
    val surfaceContainerHighest: Color = materialColors.surfaceContainerHighest
    val surfaceContainerLow: Color = materialColors.surfaceContainerLow
    val surfaceContainerLowest: Color = materialColors.surfaceContainerLowest

    val sunrise = extendedColors.sunrise.color
    val onSunrise = extendedColors.sunrise.onColor
    val sunriseContainer = extendedColors.sunrise.colorContainer
    val onSunriseContainer = extendedColors.sunrise.onColorContainer
    val sunset = extendedColors.sunset.color
    val onSunset = extendedColors.sunset.onColor
    val sunsetContainer = extendedColors.sunset.colorContainer
    val onSunsetContainer = extendedColors.sunset.onColorContainer
    val moonrise = extendedColors.moonrise.color
    val onMoonrise = extendedColors.moonrise.onColor
    val moonriseContainer = extendedColors.moonrise.colorContainer
    val onMoonriseContainer = extendedColors.moonrise.onColorContainer
    val moonset = extendedColors.moonset.color
    val onMoonset = extendedColors.moonset.onColor
    val moonsetContainer = extendedColors.moonset.colorContainer
    val onMoonsetContainer = extendedColors.moonset.onColorContainer
}

@Immutable
data class ExtendedColorScheme(
    val sunrise: ColorFamily = unspecifiedFamily,
    val sunset: ColorFamily = unspecifiedFamily,
    val moonrise: ColorFamily = unspecifiedFamily,
    val moonset: ColorFamily = unspecifiedFamily,
)

internal val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

internal val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

internal val mediumContrastLightColorScheme = lightColorScheme(
    primary = primaryLightMediumContrast,
    onPrimary = onPrimaryLightMediumContrast,
    primaryContainer = primaryContainerLightMediumContrast,
    onPrimaryContainer = onPrimaryContainerLightMediumContrast,
    secondary = secondaryLightMediumContrast,
    onSecondary = onSecondaryLightMediumContrast,
    secondaryContainer = secondaryContainerLightMediumContrast,
    onSecondaryContainer = onSecondaryContainerLightMediumContrast,
    tertiary = tertiaryLightMediumContrast,
    onTertiary = onTertiaryLightMediumContrast,
    tertiaryContainer = tertiaryContainerLightMediumContrast,
    onTertiaryContainer = onTertiaryContainerLightMediumContrast,
    error = errorLightMediumContrast,
    onError = onErrorLightMediumContrast,
    errorContainer = errorContainerLightMediumContrast,
    onErrorContainer = onErrorContainerLightMediumContrast,
    background = backgroundLightMediumContrast,
    onBackground = onBackgroundLightMediumContrast,
    surface = surfaceLightMediumContrast,
    onSurface = onSurfaceLightMediumContrast,
    surfaceVariant = surfaceVariantLightMediumContrast,
    onSurfaceVariant = onSurfaceVariantLightMediumContrast,
    outline = outlineLightMediumContrast,
    outlineVariant = outlineVariantLightMediumContrast,
    scrim = scrimLightMediumContrast,
    inverseSurface = inverseSurfaceLightMediumContrast,
    inverseOnSurface = inverseOnSurfaceLightMediumContrast,
    inversePrimary = inversePrimaryLightMediumContrast,
    surfaceDim = surfaceDimLightMediumContrast,
    surfaceBright = surfaceBrightLightMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestLightMediumContrast,
    surfaceContainerLow = surfaceContainerLowLightMediumContrast,
    surfaceContainer = surfaceContainerLightMediumContrast,
    surfaceContainerHigh = surfaceContainerHighLightMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestLightMediumContrast,
)

internal val highContrastLightColorScheme = lightColorScheme(
    primary = primaryLightHighContrast,
    onPrimary = onPrimaryLightHighContrast,
    primaryContainer = primaryContainerLightHighContrast,
    onPrimaryContainer = onPrimaryContainerLightHighContrast,
    secondary = secondaryLightHighContrast,
    onSecondary = onSecondaryLightHighContrast,
    secondaryContainer = secondaryContainerLightHighContrast,
    onSecondaryContainer = onSecondaryContainerLightHighContrast,
    tertiary = tertiaryLightHighContrast,
    onTertiary = onTertiaryLightHighContrast,
    tertiaryContainer = tertiaryContainerLightHighContrast,
    onTertiaryContainer = onTertiaryContainerLightHighContrast,
    error = errorLightHighContrast,
    onError = onErrorLightHighContrast,
    errorContainer = errorContainerLightHighContrast,
    onErrorContainer = onErrorContainerLightHighContrast,
    background = backgroundLightHighContrast,
    onBackground = onBackgroundLightHighContrast,
    surface = surfaceLightHighContrast,
    onSurface = onSurfaceLightHighContrast,
    surfaceVariant = surfaceVariantLightHighContrast,
    onSurfaceVariant = onSurfaceVariantLightHighContrast,
    outline = outlineLightHighContrast,
    outlineVariant = outlineVariantLightHighContrast,
    scrim = scrimLightHighContrast,
    inverseSurface = inverseSurfaceLightHighContrast,
    inverseOnSurface = inverseOnSurfaceLightHighContrast,
    inversePrimary = inversePrimaryLightHighContrast,
    surfaceDim = surfaceDimLightHighContrast,
    surfaceBright = surfaceBrightLightHighContrast,
    surfaceContainerLowest = surfaceContainerLowestLightHighContrast,
    surfaceContainerLow = surfaceContainerLowLightHighContrast,
    surfaceContainer = surfaceContainerLightHighContrast,
    surfaceContainerHigh = surfaceContainerHighLightHighContrast,
    surfaceContainerHighest = surfaceContainerHighestLightHighContrast,
)

internal val mediumContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkMediumContrast,
    onPrimary = onPrimaryDarkMediumContrast,
    primaryContainer = primaryContainerDarkMediumContrast,
    onPrimaryContainer = onPrimaryContainerDarkMediumContrast,
    secondary = secondaryDarkMediumContrast,
    onSecondary = onSecondaryDarkMediumContrast,
    secondaryContainer = secondaryContainerDarkMediumContrast,
    onSecondaryContainer = onSecondaryContainerDarkMediumContrast,
    tertiary = tertiaryDarkMediumContrast,
    onTertiary = onTertiaryDarkMediumContrast,
    tertiaryContainer = tertiaryContainerDarkMediumContrast,
    onTertiaryContainer = onTertiaryContainerDarkMediumContrast,
    error = errorDarkMediumContrast,
    onError = onErrorDarkMediumContrast,
    errorContainer = errorContainerDarkMediumContrast,
    onErrorContainer = onErrorContainerDarkMediumContrast,
    background = backgroundDarkMediumContrast,
    onBackground = onBackgroundDarkMediumContrast,
    surface = surfaceDarkMediumContrast,
    onSurface = onSurfaceDarkMediumContrast,
    surfaceVariant = surfaceVariantDarkMediumContrast,
    onSurfaceVariant = onSurfaceVariantDarkMediumContrast,
    outline = outlineDarkMediumContrast,
    outlineVariant = outlineVariantDarkMediumContrast,
    scrim = scrimDarkMediumContrast,
    inverseSurface = inverseSurfaceDarkMediumContrast,
    inverseOnSurface = inverseOnSurfaceDarkMediumContrast,
    inversePrimary = inversePrimaryDarkMediumContrast,
    surfaceDim = surfaceDimDarkMediumContrast,
    surfaceBright = surfaceBrightDarkMediumContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkMediumContrast,
    surfaceContainerLow = surfaceContainerLowDarkMediumContrast,
    surfaceContainer = surfaceContainerDarkMediumContrast,
    surfaceContainerHigh = surfaceContainerHighDarkMediumContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkMediumContrast,
)

internal val highContrastDarkColorScheme = darkColorScheme(
    primary = primaryDarkHighContrast,
    onPrimary = onPrimaryDarkHighContrast,
    primaryContainer = primaryContainerDarkHighContrast,
    onPrimaryContainer = onPrimaryContainerDarkHighContrast,
    secondary = secondaryDarkHighContrast,
    onSecondary = onSecondaryDarkHighContrast,
    secondaryContainer = secondaryContainerDarkHighContrast,
    onSecondaryContainer = onSecondaryContainerDarkHighContrast,
    tertiary = tertiaryDarkHighContrast,
    onTertiary = onTertiaryDarkHighContrast,
    tertiaryContainer = tertiaryContainerDarkHighContrast,
    onTertiaryContainer = onTertiaryContainerDarkHighContrast,
    error = errorDarkHighContrast,
    onError = onErrorDarkHighContrast,
    errorContainer = errorContainerDarkHighContrast,
    onErrorContainer = onErrorContainerDarkHighContrast,
    background = backgroundDarkHighContrast,
    onBackground = onBackgroundDarkHighContrast,
    surface = surfaceDarkHighContrast,
    onSurface = onSurfaceDarkHighContrast,
    surfaceVariant = surfaceVariantDarkHighContrast,
    onSurfaceVariant = onSurfaceVariantDarkHighContrast,
    outline = outlineDarkHighContrast,
    outlineVariant = outlineVariantDarkHighContrast,
    scrim = scrimDarkHighContrast,
    inverseSurface = inverseSurfaceDarkHighContrast,
    inverseOnSurface = inverseOnSurfaceDarkHighContrast,
    inversePrimary = inversePrimaryDarkHighContrast,
    surfaceDim = surfaceDimDarkHighContrast,
    surfaceBright = surfaceBrightDarkHighContrast,
    surfaceContainerLowest = surfaceContainerLowestDarkHighContrast,
    surfaceContainerLow = surfaceContainerLowDarkHighContrast,
    surfaceContainer = surfaceContainerDarkHighContrast,
    surfaceContainerHigh = surfaceContainerHighDarkHighContrast,
    surfaceContainerHighest = surfaceContainerHighestDarkHighContrast,
)

internal val extendedLight = ExtendedColorScheme(
    sunrise = ColorFamily(
        sunriseLight,
        onSunriseLight,
        sunriseContainerLight,
        onSunriseContainerLight,
    ),
    sunset = ColorFamily(
        sunsetLight,
        onSunsetLight,
        sunsetContainerLight,
        onSunsetContainerLight,
    ),
    moonrise = ColorFamily(
        moonriseLight,
        onMoonriseLight,
        moonriseContainerLight,
        onMoonriseContainerLight,
    ),
    moonset = ColorFamily(
        moonsetLight,
        onMoonsetLight,
        moonsetContainerLight,
        onMoonsetContainerLight,
    ),
)

internal val extendedDark = ExtendedColorScheme(
    sunrise = ColorFamily(
        sunriseDark,
        onSunriseDark,
        sunriseContainerDark,
        onSunriseContainerDark,
    ),
    sunset = ColorFamily(
        sunsetDark,
        onSunsetDark,
        sunsetContainerDark,
        onSunsetContainerDark,
    ),
    moonrise = ColorFamily(
        moonriseDark,
        onMoonriseDark,
        moonriseContainerDark,
        onMoonriseContainerDark,
    ),
    moonset = ColorFamily(
        moonsetDark,
        onMoonsetDark,
        moonsetContainerDark,
        onMoonsetContainerDark,
    ),
)

internal val extendedLightMediumContrast = ExtendedColorScheme(
    sunrise = ColorFamily(
        sunriseLightMediumContrast,
        onSunriseLightMediumContrast,
        sunriseContainerLightMediumContrast,
        onSunriseContainerLightMediumContrast,
    ),
    sunset = ColorFamily(
        sunsetLightMediumContrast,
        onSunsetLightMediumContrast,
        sunsetContainerLightMediumContrast,
        onSunsetContainerLightMediumContrast,
    ),
    moonrise = ColorFamily(
        moonriseLightMediumContrast,
        onMoonriseLightMediumContrast,
        moonriseContainerLightMediumContrast,
        onMoonriseContainerLightMediumContrast,
    ),
    moonset = ColorFamily(
        moonsetLightMediumContrast,
        onMoonsetLightMediumContrast,
        moonsetContainerLightMediumContrast,
        onMoonsetContainerLightMediumContrast,
    ),
)

internal val extendedLightHighContrast = ExtendedColorScheme(
    sunrise = ColorFamily(
        sunriseLightHighContrast,
        onSunriseLightHighContrast,
        sunriseContainerLightHighContrast,
        onSunriseContainerLightHighContrast,
    ),
    sunset = ColorFamily(
        sunsetLightHighContrast,
        onSunsetLightHighContrast,
        sunsetContainerLightHighContrast,
        onSunsetContainerLightHighContrast,
    ),
    moonrise = ColorFamily(
        moonriseLightHighContrast,
        onMoonriseLightHighContrast,
        moonriseContainerLightHighContrast,
        onMoonriseContainerLightHighContrast,
    ),
    moonset = ColorFamily(
        moonsetLightHighContrast,
        onMoonsetLightHighContrast,
        moonsetContainerLightHighContrast,
        onMoonsetContainerLightHighContrast,
    ),
)

internal val extendedDarkMediumContrast = ExtendedColorScheme(
    sunrise = ColorFamily(
        sunriseDarkMediumContrast,
        onSunriseDarkMediumContrast,
        sunriseContainerDarkMediumContrast,
        onSunriseContainerDarkMediumContrast,
    ),
    sunset = ColorFamily(
        sunsetDarkMediumContrast,
        onSunsetDarkMediumContrast,
        sunsetContainerDarkMediumContrast,
        onSunsetContainerDarkMediumContrast,
    ),
    moonrise = ColorFamily(
        moonriseDarkMediumContrast,
        onMoonriseDarkMediumContrast,
        moonriseContainerDarkMediumContrast,
        onMoonriseContainerDarkMediumContrast,
    ),
    moonset = ColorFamily(
        moonsetDarkMediumContrast,
        onMoonsetDarkMediumContrast,
        moonsetContainerDarkMediumContrast,
        onMoonsetContainerDarkMediumContrast,
    ),
)

internal val extendedDarkHighContrast = ExtendedColorScheme(
    sunrise = ColorFamily(
        sunriseDarkHighContrast,
        onSunriseDarkHighContrast,
        sunriseContainerDarkHighContrast,
        onSunriseContainerDarkHighContrast,
    ),
    sunset = ColorFamily(
        sunsetDarkHighContrast,
        onSunsetDarkHighContrast,
        sunsetContainerDarkHighContrast,
        onSunsetContainerDarkHighContrast,
    ),
    moonrise = ColorFamily(
        moonriseDarkHighContrast,
        onMoonriseDarkHighContrast,
        moonriseContainerDarkHighContrast,
        onMoonriseContainerDarkHighContrast,
    ),
    moonset = ColorFamily(
        moonsetDarkHighContrast,
        onMoonsetDarkHighContrast,
        moonsetContainerDarkHighContrast,
        onMoonsetContainerDarkHighContrast,
    ),
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

private val unspecifiedFamily = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)


