package site.remlit.snowdrop.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.ComposeRoute
import site.remlit.snowdrop.api.statuses.getStatusContext
import site.remlit.snowdrop.component.Avatar
import site.remlit.snowdrop.component.NavigationBackButton
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalStatusStateController
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.LocalSnackbarController
import site.remlit.snowdrop.util.annotatedString.mapEmojisToInlineTextContent
import site.remlit.snowdrop.util.annotatedString.withEmojis
import site.remlit.snowdrop.util.cache.fetchStatus
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.util.translation
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.icon_visibility_24px
import snowdrop.shared.generated.resources.icon_visibility_off_24px
import snowdrop.shared.generated.resources.post
import snowdrop.shared.generated.resources.post_by_x
import snowdrop.shared.generated.resources.reply_to_x
import kotlin.collections.mapOf
import site.remlit.snowdrop.component.Status as StatusComponent

@Composable
fun ThreadView(id: String) = ViewSurface {
	val navHandler = LocalNavController.current
	val snackbarHandler = LocalSnackbarController.current
	val statusStateController = LocalStatusStateController.current
	var forcedContentWarning by remember { mutableStateOf(false) }

	val currentAccount by remember { getCurrentAccountObjectFlow() }
		.collectAsStateWithLifecycle(null)

	val status by remember { fetchStatus(id, snackbarHandler) }
		.collectAsStateWithLifecycle(null)

	val ancestors = remember { mutableStateListOf<Status>() }
	val descendants = remember { mutableStateListOf<Status>() }

	val listState = rememberLazyListState()

	var ready by remember { mutableStateOf(false) }

	LaunchedEffect(Dispatchers.Default) {
		ancestors.clear()
		descendants.clear()

		val res = getStatusContext(id)
		if (res.error || res.response == null) {
			res.handleError(snackbarHandler)
			return@LaunchedEffect
		}
		ancestors.addAll(res.response.ancestors)
		descendants.addAll(res.response.descendants)

		ready = true

		listState.scrollToItem(ancestors.size)
	}

	TopAppBar(
		navigationIcon = { NavigationBackButton() },
		title = {
			if (status == null) Column {
				Text(stringResource(Res.string.post))
			} else Column {
				val mappedEmojis = mapEmojisToInlineTextContent(status!!.account!!.emojis)
				Text(
					translation(
						Res.string.post_by_x,
						mapOf("display_name" to AnnotatedString(status!!.account!!.displayName())
							.withEmojis(mappedEmojis))
					),
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					inlineContent = mappedEmojis
				)
			}
		},
		actions = {
			IconButton(onClick = {
				forcedContentWarning = !forcedContentWarning
				statusStateController.defaultCwValue = forcedContentWarning
				statusStateController.setAllCw(forcedContentWarning)
			}) {
				if (!forcedContentWarning) Icon(painterResource(Res.drawable.icon_visibility_24px), null)
				else Icon(painterResource(Res.drawable.icon_visibility_off_24px), null)
			}
		}
	)

	// todo: implement onUpdate
	if (!ready || status == null) {
		Column(
			modifier = Modifier.fillMaxHeight().fillMaxWidth(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			CircularProgressIndicator()
		}
	} else {
		Column(modifier = Modifier.fillMaxSize()) {
			LazyColumn(
				state = listState,
				modifier = Modifier.weight(1f)
			) {
				items(
					items = ancestors,
					key = { it.id }
				) { item ->
					StatusComponent(item, {  })
				}

				item(key = status!!.id) {
					StatusComponent(status!!, {  })
				}

				items(
					items = descendants,
					key = { it.id }
				) { item ->
					StatusComponent(item, {  })
				}
			}
			Column(
				modifier = Modifier.fillMaxWidth()
					.padding(all = 10.dp)
			) {
				val replyToString = translation(
					Res.string.reply_to_x,
					mapOf("handle" to AnnotatedString("@${status!!.account!!.acct}"))
				)

				Row(
					modifier = Modifier.clip(RoundedCornerShape(100))
						.background(MaterialTheme.colorScheme.surfaceContainer)
						.semantics { contentDescription = replyToString.text }
						.clickable {
							navHandler.navigate(
								ComposeRoute(
									inReplyToId = status!!.id,
									visibility = status!!.visibility
								)
							)
						}
						.padding(15.dp)
						.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically
				) {
					Avatar(
						account = currentAccount!!,
						smaller = true,
						modifier = Modifier.clip(RoundedCornerShape(100))
					)

					Spacer(Modifier.size(10.dp))

					Text(
						replyToString,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
				}
			}
		}
	}
}
