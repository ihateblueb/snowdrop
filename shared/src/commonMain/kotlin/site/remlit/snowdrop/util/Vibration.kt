package site.remlit.snowdrop.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import site.remlit.snowdrop.model.Platform

fun vibratePositive(haptics: HapticFeedback) {
	when (getPlatform()) {
		Platform.ANDROID -> haptics.performHapticFeedback(HapticFeedbackType.Confirm)
		Platform.IOS -> haptics.performHapticFeedback(HapticFeedbackType.ToggleOn)
	}
}

fun vibrateNegative(haptics: HapticFeedback) {
	when (getPlatform()) {
		Platform.ANDROID -> haptics.performHapticFeedback(HapticFeedbackType.Reject)
		Platform.IOS -> haptics.performHapticFeedback(HapticFeedbackType.ToggleOff)
	}
}

fun vibrate(direction: Boolean, haptics: HapticFeedback) = when (direction) {
	true -> vibratePositive(haptics)
	false -> vibrateNegative(haptics)
}
