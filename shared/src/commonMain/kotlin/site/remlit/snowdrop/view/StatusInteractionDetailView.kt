package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.ktor.websocket.Frame
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.api.statuses.getStatusFavouritedBy
import site.remlit.snowdrop.api.statuses.getStatusReactions
import site.remlit.snowdrop.api.statuses.getStatusRebloggedBy
import site.remlit.snowdrop.component.Avatar
import site.remlit.snowdrop.component.Emoji
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Emoji
import site.remlit.snowdrop.model.Reaction
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.SnackbarController
import site.remlit.snowdrop.util.bgIO
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.boosted_by
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.liked_by
import snowdrop.shared.generated.resources.post
import snowdrop.shared.generated.resources.reacted_by

enum class InteractionViewType {
	Boost, Like, Reaction
}

@Composable
fun StatusInteractionDetailView(
	id: String,
	type: InteractionViewType
) = ViewSurface {
	val navHandler = LocalNavController.current
	val snackbarHandler = SnackbarController.current

	@Composable
	fun AccountRow(account: Account) {
		Row(
			modifier = Modifier.padding(10.dp),
			horizontalArrangement = Arrangement.spacedBy(10.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Avatar(account)

			Column {
				Text(account.displayName(), fontWeight = FontWeight.Medium)
				Text("@${account.acct}")
			}
		}
		HorizontalDivider(
			thickness = 1.dp,
			color = MaterialTheme.colorScheme.surfaceContainer
		)
	}

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
			}
		},
		title = {
			when (type) {
				InteractionViewType.Boost -> Text(stringResource(Res.string.boosted_by))
				InteractionViewType.Like -> Text(stringResource(Res.string.liked_by))
				InteractionViewType.Reaction -> Text(stringResource(Res.string.reacted_by))
			}
		}
	)

	when (type) {
		InteractionViewType.Like, InteractionViewType.Boost -> {
			val accounts = remember { mutableStateListOf<Account>() }
			bgIO {
				val res: ApiResponse<List<Account>> = when (type) {
					InteractionViewType.Like -> getStatusFavouritedBy(id)
					InteractionViewType.Boost -> getStatusRebloggedBy(id)
				}
				if (res.error || res.response == null) {
					res.handleError(snackbarController = snackbarHandler)
					return@bgIO
				}
				accounts.addAll(res.response)
			}

			// todo: scrolling
			LazyColumn {
				accounts.forEach { account ->
					item { AccountRow(account) }
				}
			}
		}
		InteractionViewType.Reaction -> {
			var currentTab by remember { mutableStateOf(0) }
			val reactions = remember { mutableStateListOf<Reaction>() }
			bgIO {
				val res = getStatusReactions(id)
				if (res.error || res.response == null) {
					res.handleError(snackbarController = snackbarHandler)
					return@bgIO
				}
				reactions.addAll(res.response)
			}

			// todo: scrolling
			if (!reactions.isEmpty()) {
				PrimaryScrollableTabRow(currentTab) {
					reactions.forEach {
						val i = reactions.indexOf(it)
						Tab(
							selected = currentTab == i,
							onClick = { currentTab = i },
							text = {
								if (it.url != null) {
									Emoji(Emoji(it.name, it.staticUrl ?: it.url, it.url))
								} else Text(it.name)
							}
						)
					}
				}
				LazyColumn {
					reactions.filter { reactions.indexOf(it) == currentTab }.forEach { reaction ->
						item { AccountRow(reaction.account) }
					}
				}
			}
		}
	}
}
