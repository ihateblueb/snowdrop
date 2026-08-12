package site.remlit.snowdrop.util

import io.ktor.utils.io.CancellationException
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.util.log.err
import site.remlit.snowdrop.util.log.exception

/**
 * Runs block of code with exception handling. Supports any Throwable.
 * Ignores CancellationException.
 *
 * @since 0.0.1-alpha
 * */
inline fun safe(block: () -> Unit) =
	try { block() } catch(e: CancellationException) {
		throw e
	} catch (e: Throwable) {
		err { "(safe) Safely caught exception: ${e.message}" }
		exception { e }
	}

/**
 * Runs API request with exception handling. Supports any Throwable.
 *
 * @see site.remlit.snowdrop.util.safe
 * @since 0.0.1-alpha
 * */
inline fun <T> safeApiRequest(
	block: (accountId: String, host: String) -> T
): ApiResponse<T> =
	try {
		val accountId = getCurrentAccountId()
		val host = getCurrentAccountHost()

		return ApiResponse(response = block(accountId, host))
	} catch(e: CancellationException) {
		throw e
	} catch (e: Throwable) {
		err { "(safeApiRequest) Safely caught exception: ${e.message}" }
		exception { e }
		return ApiResponse(error = true, message = e.message)
	}

/**
 * Runs nullable returnable block of code with exception handling. Supports any Throwable.
 * If exception is thrown, returns null.
 *
 * @see site.remlit.snowdrop.util.safe
 * @since 0.0.1-alpha
 * */
inline fun <T> safeReturnable(block: () -> T): T? =
	try { return block() } catch(e: CancellationException) {
		throw e
	} catch (e: Throwable) {
		err { "(safeReturnable) Safely caught exception: ${e.message}" }
		exception { e }
		return null
	}
