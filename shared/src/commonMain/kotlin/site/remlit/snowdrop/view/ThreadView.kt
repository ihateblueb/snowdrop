package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.api.statuses.getStatusContext
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.SnackbarController
import site.remlit.snowdrop.util.cache.fetchStatus
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.post
import snowdrop.shared.generated.resources.post_by
import site.remlit.snowdrop.component.Status as StatusComponent

@Composable
fun ThreadView(id: String) = ViewSurface {
	val navHandler = LocalNavController.current
	val snackbarHandler = SnackbarController.current

	val currentAccount by getCurrentAccountObjectFlow()
		.collectAsStateWithLifecycle(null)

	val status by fetchStatus(id, snackbarHandler)
		.collectAsStateWithLifecycle(null)

	val ancestors = remember { mutableStateListOf<Status>() }
	val descendants = remember { mutableStateListOf<Status>() }

	val listState = rememberLazyListState()

	var ready by remember { mutableStateOf(false) }

	LaunchedEffect(Dispatchers.Default) {
		ancestors.clear()
		descendants.clear()

		val res = getStatusContext(id)
		if (res.error) return@LaunchedEffect
		if (res.response == null) return@LaunchedEffect
		ancestors.addAll(res.response.ancestors)
		descendants.addAll(res.response.descendants)

		ready = true

		listState.scrollToItem(ancestors.size)
	}

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
			}
		},
		title = {
			if (status == null) Column {
				Text(stringResource(Res.string.post))
			} else Column {

				Text(
					stringResource(Res.string.post_by, "${status!!.account?.displayName()}"),
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
			}
		}
	)

	if (!ready || status == null) {
		Column(
			modifier = Modifier.fillMaxHeight().fillMaxWidth(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			CircularProgressIndicator()
		}
	} else {
		LazyColumn(
			state = listState,
		) {
			items(
				items = ancestors,
				key = { it.id }
			) { status ->
				StatusComponent(status)
			}

			item(key = status!!.id) {
				StatusComponent(status!!)
			}

			items(
				items = descendants,
				key = { it.id }
			) { status ->
				StatusComponent(status)
			}
		}
	}
}
