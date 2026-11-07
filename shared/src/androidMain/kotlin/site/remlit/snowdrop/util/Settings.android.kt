package site.remlit.snowdrop.util

import android.content.Context
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings

@OptIn(ExperimentalSettingsApi::class)
actual val settings: FlowSettings = SharedPreferencesSettings(
	AndroidContext.context
		.getSharedPreferences("snowdrop_prefs", Context.MODE_PRIVATE)
).toFlowSettings()
