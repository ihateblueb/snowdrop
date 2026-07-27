package site.remlit.snowdrop.model.log

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Logger log with severity and timestamp
 *
 * @since 0.0.6-alpha
 * */
@Serializable
data class Log(
	val level: Level,
	val message: String,
	val at: Instant = Clock.System.now()
)
