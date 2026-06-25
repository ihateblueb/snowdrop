package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import site.remlit.snowdrop.api.statuses.getStatus
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.component.Status as StatusComponent

@Composable
fun StatusView(id: String) = ViewSurface {
	val currentAccount by getCurrentAccountObjectFlow()
		.collectAsStateWithLifecycle(null)

	var status by remember { mutableStateOf<Status?>(null) }
	var ready by remember { mutableStateOf(false) }

	val scrollState = rememberScrollState()

	LaunchedEffect(Dispatchers.Default) {
		// todo: handle errors
		val req = getStatus(id)
		if (req.error) return@LaunchedEffect
		status = req.response

		ready = true
	}

	TopAppBar(
		title = {
			if (status == null) Column {
				Text("Post")
			}
			else Column {
				Text(
					"Post by " + (status!!.account.displayName ?: status!!.account.username),
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
		StatusComponent(status!!)
	}
}
