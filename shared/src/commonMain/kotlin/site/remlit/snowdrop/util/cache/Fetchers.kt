package site.remlit.snowdrop.util.cache

import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import site.remlit.snowdrop.api.accounts.getAccount
import site.remlit.snowdrop.api.getEmojis
import site.remlit.snowdrop.api.instance.getInstanceV1
import site.remlit.snowdrop.api.statuses.getStatus
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.model.Emoji
import site.remlit.snowdrop.model.InstanceV1
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.safe

/**
 * Gets the cached representation of an account (if available) before
 * the request to get a fresh version finishes.
 * */
fun fetchAccount(id: String, snackbarHostState: SnackbarHostState? = null): Flow<Account> = flow {
	val cached = getCacheEntry("account_$id")
	if (cached != null) safe {
		emit(cached.getContent<Account>())
	}

	val req = getAccount(id)
	if (req.error || req.response == null) {
		if (snackbarHostState != null) req.handleError(snackbarHostState)
	} else {
		emit(req.response)
		putCacheEntry("account_$id", req.response)
	}
}

/**
 * Gets the cached representation of an account (if available) before
 * the request to get a fresh version finishes. Returns null if ID is
 * null.
 * */
fun fetchAccountOrNull(id: String?, snackbarHostState: SnackbarHostState? = null): Flow<Account?> =
	if (id != null) fetchAccount(id, snackbarHostState) else flow { emit(null) }

/**
 * Gets the cached representation of a status (if available) before
 * the request to get a fresh version finishes.
 * */
fun fetchStatus(id: String, snackbarHostState: SnackbarHostState? = null): Flow<Status> = flow {
	val cached = getCacheEntry("status_$id")
	if (cached != null) safe {
		emit(cached.getContent<Status>())
	}

	val req = getStatus(id)
	if (req.error || req.response == null) {
		if (snackbarHostState != null) req.handleError(snackbarHostState)
	} else {
		emit(req.response)
		putCacheEntry("status_$id", req.response)
	}
}

/**
 * Gets the cached representation of a status (if available) before
 * the request to get a fresh version finishes. Returns null if ID is
 * null.
 * */
fun fetchStatusOrNull(id: String?, snackbarHostState: SnackbarHostState? = null): Flow<Status?> =
	if (id != null) fetchStatus(id, snackbarHostState) else flow { emit(null) }

/**
 * Gets the cached list of emojis (if available) before
 * the request to get a fresh version finishes.
 * */
fun fetchEmojis(snackbarHostState: SnackbarHostState? = null): Flow<List<Emoji>> = flow {
	val cached = getCacheEntry("emojis")
	if (cached != null) safe {
		emit(cached.getContent<List<Emoji>>())
	}

	val req = getEmojis()
	if (req.error || req.response == null) {
		if (snackbarHostState != null) req.handleError(snackbarHostState)
	} else {
		emit(req.response)
		putCacheEntry("emojis", req.response)
	}
}

/**
 * Gets the cached list of emojis (if available) before
 * the request to get a fresh version finishes.
 * */
fun fetchInstance(snackbarHostState: SnackbarHostState? = null): Flow<InstanceV1> = flow {
	val cached = getCacheEntry("instance")
	if (cached != null) safe {
		emit(cached.getContent<InstanceV1>())
	}

	val req = getInstanceV1()
	if (req.error || req.response == null) {
		if (snackbarHostState != null) req.handleError(snackbarHostState)
	} else {
		emit(req.response)
		putCacheEntry("instance", req.response)
	}
}
