package site.remlit.snowdrop.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
@RequiresApi(Build.VERSION_CODES.S)
actual fun getLightColorScheme(): ColorScheme = dynamicLightColorScheme(LocalContext.current)

@Composable
@RequiresApi(Build.VERSION_CODES.S)
actual fun getDarkColorScheme(): ColorScheme = dynamicDarkColorScheme(LocalContext.current)
