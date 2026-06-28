package site.remlit.snowdrop.util.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import site.remlit.snowdrop.api.accounts.getAccount
import site.remlit.snowdrop.api.statuses.getStatus
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.safe

/**
 * Gets the cached representation of an account (if available) before
 * the request to get a fresh version finishes.
 * */
fun fetchAccount(id: String): Flow<Account> = flow {
	val cached = getCacheEntry("account_$id")
	if (cached != null) safe {
		emit(cached.getContent<Account>())
	}

	val req = getAccount(id)
	if (!req.error && req.response != null) {
		emit(req.response)
		putCacheEntry("account_$id", req.response)
	}
}

/**
 * Gets the cached representation of an account (if available) before
 * the request to get a fresh version finishes. Returns null if ID is
 * null.
 * */
fun fetchAccountOrNull(id: String?): Flow<Account?> =
	if (id != null) fetchAccount(id) else flow { emit(null) }

/**
 * Gets the cached representation of a status (if available) before
 * the request to get a fresh version finishes.
 * */
fun fetchStatus(id: String): Flow<Status> = flow {
	val cached = getCacheEntry("status_$id")
	if (cached != null) safe {
		emit(cached.getContent<Status>())
	}

	val req = getStatus(id)
	if (!req.error && req.response != null) {
		emit(req.response)
		putCacheEntry("status_$id", req.response)
	}
}

/**
 * Gets the cached representation of a status (if available) before
 * the request to get a fresh version finishes. Returns null if ID is
 * null.
 * */
fun fetchStatusOrNull(id: String?): Flow<Status?> =
	if (id != null) fetchStatus(id) else flow { emit(null) }
