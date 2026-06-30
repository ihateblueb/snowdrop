package site.remlit.snowdrop.util

import co.touchlab.kermit.Logger
import io.ktor.utils.io.CancellationException
import site.remlit.snowdrop.model.ApiResponse

inline fun safe(block: () -> Unit) =
	try { block() } catch(e: CancellationException) {
		throw e
	} catch (e: Throwable) {
		Logger.e { "(safe) Safely caught exception: ${e.message}" }
		e.printStackTrace()
	}

inline fun <T> safeApiRequest(block: () -> T): ApiResponse<T> =
	try {
		// todo: send alerts from here
		return ApiResponse(response = block())
	} catch(e: CancellationException) {
		throw e
	} catch (e: Throwable) {
		Logger.e { "(safeApiRequest) Safely caught exception: ${e.message}" }
		e.printStackTrace()
		return ApiResponse(error = true, message = e.message)
	}

inline fun <T> safeReturnable(block: () -> T): T? =
	try { return block() } catch(e: CancellationException) {
		throw e
	} catch (e: Throwable) {
		Logger.e { "(safeReturnable) Safely caught exception: ${e.message}" }
		e.printStackTrace()
		return null
	}
