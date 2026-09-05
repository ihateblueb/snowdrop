package site.remlit.snowdrop.util

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Wait until condition is met.
 * */
suspend fun waitUntil(condition: () -> Boolean, wait: Duration = 1.milliseconds) {
	while (!condition()) delay(wait)
}
