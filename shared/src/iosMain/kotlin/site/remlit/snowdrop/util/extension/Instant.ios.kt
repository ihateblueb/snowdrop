package site.remlit.snowdrop.util.extension

import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.systemTimeZone
import platform.Foundation.timeZoneForSecondsFromGMT
import site.remlit.snowdrop.util.safeReturnable
import kotlin.time.Instant

actual fun Instant.toLocalizedString(): String {
	val parser = NSDateFormatter().apply {
		this.timeZone = NSTimeZone.timeZoneForSecondsFromGMT(0)
		this.dateFormat = instantFormat
	}

	val date = safeReturnable {
		parser.dateFromString(this.toString())
	} ?: return this.toString()

	return safeReturnable {
		NSDateFormatter().apply {
			this.timeZone = NSTimeZone.systemTimeZone()
			// https://developer.apple.com/documentation/foundation/dateformatter/style, enum 0-4
			this.dateStyle = 2.toULong() // medium
			this.timeStyle = 1.toULong() // short
		}.stringFromDate(date)
	} ?: this.toString()
}
