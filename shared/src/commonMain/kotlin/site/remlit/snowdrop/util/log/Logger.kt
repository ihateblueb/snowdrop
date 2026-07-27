package site.remlit.snowdrop.util.log

import androidx.compose.runtime.mutableStateListOf
import co.touchlab.kermit.Logger
import site.remlit.snowdrop.model.log.Level
import site.remlit.snowdrop.model.log.Log
import site.remlit.snowdrop.util.bg

val logs = mutableStateListOf<Log>()

/**
 * Log a message at the provided level
 *
 * @param level Level of severity
 * @param message Message to log
 *
 * @since 0.0.6-alpha
 * */
private fun log(level: Level, message: () -> String) = bg {
	when (level) {
		Level.Debug -> Logger.d { message() }
		Level.Info -> Logger.i { message() }
		Level.Warn -> Logger.w { message() }
		Level.Error -> Logger.e { message() }
	}

	logs.add(Log(level, message()))
}

/**
 * Log a message at debug level
 *
 * @param message Message to log
 *
 * @since 0.0.6-alpha
 * */
fun debug(message: () -> String) = log(Level.Debug, message)

/**
 * Log a message at info level
 *
 * @param message Message to log
 *
 * @since 0.0.6-alpha
 * */
fun info(message: () -> String) = log(Level.Info, message)

/**
 * Log a message at warn level
 *
 * @param message Message to log
 *
 * @since 0.0.6-alpha
 * */
fun warn(message: () -> String) = log(Level.Warn, message)

/**
 * Log a message at error level
 *
 * @param message Message to log
 *
 * @since 0.0.6-alpha
 * */
fun err(message: () -> String) = log(Level.Error, message)

/**
 * Log an exception at error level
 *
 * @param exception Exception to log
 *
 * @since 0.0.6-alpha
 * */
fun exception(throwable: () -> Throwable) = err { throwable().stackTraceToString() }
