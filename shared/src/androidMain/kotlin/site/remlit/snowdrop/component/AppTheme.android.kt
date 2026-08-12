package site.remlit.snowdrop.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getLightColorScheme(): ColorScheme {
	return if (Build.VERSION.SDK_INT < 31) {
		lightColorScheme()
	} else {
		dynamicLightColorScheme(LocalContext.current)
	}
}

@Composable
actual fun getDarkColorScheme(): ColorScheme {
	return if (Build.VERSION.SDK_INT < 31) {
		darkColorScheme()
	} else {
		dynamicDarkColorScheme(LocalContext.current)
	}
}
