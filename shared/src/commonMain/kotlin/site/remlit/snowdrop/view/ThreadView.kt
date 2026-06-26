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
import site.remlit.snowdrop.api.statuses.getStatus
import site.remlit.snowdrop.api.statuses.getStatusContext
import site.remlit.snowdrop.api.timeline.getHomeTimeline
import site.remlit.snowdrop.component.Status
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.model.StatusContext
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.component.Status as StatusComponent

@Composable
fun ThreadView(id: String, status: Status? = null) = ViewSurface {
	val currentAccount by getCurrentAccountObjectFlow()
		.collectAsStateWithLifecycle(null)

	var status by remember { mutableStateOf(status) }

	val threadStatuses = remember { mutableStateListOf<Status>() }
	val context = remember { mutableStateOf<StatusContext?>(null) }

	val listState = rememberLazyListState()

	var ready by remember { mutableStateOf(false) }

	val scrollState = rememberScrollState()

	LaunchedEffect(Dispatchers.Default) {
		// todo: handle errors
		val req = getStatus(id)
		if (req.error) return@LaunchedEffect
		status = req.response

		threadStatuses.clear()
		threadStatuses.add(status!!)
		val res = getStatusContext(status!!.id!!)
		if (res.error) return@LaunchedEffect
		if (res.response == null) return@LaunchedEffect
		context.value = res.response
		threadStatuses.addAll(0, res.response.ancestors)
		threadStatuses.addAll(res.response.descendants)

		ready = true

		listState.scrollToItem(threadStatuses.indexOf(threadStatuses.find { sts -> status!!.id == sts.id }))
	}

	TopAppBar(
		title = {
			if (status == null) Column {
				Text("Post")
			}
			else Column {
				Text(
					"Post by " + (status!!.account?.displayName ?: status!!.account?.username),
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
				items = threadStatuses,
				key = { status ->
					status.id!!
				}
			) { status ->
				StatusComponent(status)
			}
		}
	}
}
