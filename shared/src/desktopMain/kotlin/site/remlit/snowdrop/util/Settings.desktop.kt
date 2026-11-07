package site.remlit.snowdrop.util

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import java.util.prefs.Preferences

@OptIn(ExperimentalSettingsApi::class)
actual val settings: FlowSettings = PreferencesSettings(
	Preferences.userRoot().node("snowdrop_prefs")
).toFlowSettings()
