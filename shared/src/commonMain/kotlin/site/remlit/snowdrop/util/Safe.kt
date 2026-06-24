package site.remlit.snowdrop.util

import co.touchlab.kermit.Logger
import site.remlit.snowdrop.model.ApiResponse

inline fun safe(block: () -> Unit) =
	try { block() } catch (e: Throwable) {
		Logger.e { "Safely caught exception: ${e.message}" }
		e.printStackTrace()
	}

inline fun <T> safeApiRequest(block: () -> T): ApiResponse<T> =
	try {
		return ApiResponse(response = block())
	} catch (e: Throwable) {
		Logger.e { "Safely caught exception: ${e.message}" }
		e.printStackTrace()
		return ApiResponse(error = true, message = e.message)
	}

inline fun <T> safeReturnable(block: () -> Unit): T? =
	try { return block() as? T } catch (e: Throwable) {
		Logger.e { "Safely caught exception: ${e.message}" }
		e.printStackTrace()
		return null
	}