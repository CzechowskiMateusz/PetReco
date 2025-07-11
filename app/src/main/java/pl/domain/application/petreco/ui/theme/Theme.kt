package pl.domain.application.petreco.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    background = BackgroundGreen,
    onBackground = White,
    onSurface = White,
    primary = DarkCucumberGreen,
    onPrimary = White,
    secondary = DarkHonestGreen,
    tertiary = DarkerGreen,

    onSecondary = White,
    onTertiary = Grey
)

val DarkGradientBackgroundBrush = Brush.verticalGradient(
    colors = listOf(
        DarkGradientTop,
        DarkGradientMiddle,
        DarkGradientBottom
    ),
    startY = 0f,
    endY = Float.POSITIVE_INFINITY
)

private val LightColorScheme = lightColorScheme(
    background = LightGreen,
    onBackground = Black,
    onSurface = White,
    primary = CucumberGreen,
    onPrimary = White,
    secondary = HonestGreen,
    tertiary = DarkGreen,

    onSecondary = DarkGreen,
    onTertiary = White

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

val LightGradientBackgroundBrush = Brush.verticalGradient(
    colors = listOf(
        LightGradientTop,
        LightGradientMiddle,
        LightGradientBottom
    ),
    startY = 0f,
    endY = Float.POSITIVE_INFINITY
)

@Composable
fun PetRecoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun themedGradientBackground(): Brush {
    return if (isSystemInDarkTheme()) {
        DarkGradientBackgroundBrush
    } else {
        LightGradientBackgroundBrush
    }
}
