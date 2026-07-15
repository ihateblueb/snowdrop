package site.remlit.snowdrop.util

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import site.remlit.snowdrop.model.Platform

/**
 * Vibrates either positively (vibratePositive) or negatively (vibrateNegative).
 *
 * @param direction Direction as boolean
 * @param haptics HapticFeedback controller
 *
 * @see site.remlit.snowdrop.util.vibratePositive
 * @see site.remlit.snowdrop.util.vibrateNegative
 *
 * @since 0.0.2-alpha
 * */
fun vibrate(direction: Boolean, haptics: HapticFeedback) = when (direction) {
	true -> vibratePositive(haptics)
	false -> vibrateNegative(haptics)
}


/*
* NOTE:
* This stuff is platform specific! Do not change unless you can test it on that platform,
* and it feels natural by that platform's standards.
* */

/**
 * Vibrates positively, or in a way that signals something was confirmed/accepted or toggled on.
 *
 * @param haptics HapticFeedback controller
 *
 * @since 0.0.2-alpha
 * */
fun vibratePositive(haptics: HapticFeedback) {
	when (getPlatform()) {
		Platform.ANDROID -> haptics.performHapticFeedback(HapticFeedbackType.Confirm)
		Platform.IOS -> haptics.performHapticFeedback(HapticFeedbackType.ToggleOn)
	}
}

/**
 * Vibrates negatively, or in a way that signals something was rejected or turned off.
 *
 * @param haptics HapticFeedback controller
 *
 * @since 0.0.2-alpha
 * */
fun vibrateNegative(haptics: HapticFeedback) {
	when (getPlatform()) {
		Platform.ANDROID -> haptics.performHapticFeedback(HapticFeedbackType.Reject)
		Platform.IOS -> haptics.performHapticFeedback(HapticFeedbackType.ToggleOff)
	}
}
