package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.api.statuses.getStatusFavouritedBy
import site.remlit.snowdrop.api.statuses.getStatusReactions
import site.remlit.snowdrop.api.statuses.getStatusRebloggedBy
import site.remlit.snowdrop.component.AccountRow
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Reaction
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.LocalSnackbarController
import site.remlit.snowdrop.util.bgIO
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.boosted_by
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.liked_by
import snowdrop.shared.generated.resources.nothing_to_see_here
import snowdrop.shared.generated.resources.reacted_by
import kotlin.collections.forEach

enum class InteractionViewType {
	Boost, Like, Reaction
}

@Composable
fun StatusInteractionDetailView(
	id: String,
	type: InteractionViewType
) = ViewSurface {
	val navHandler = LocalNavController.current
	val snackbarHandler = LocalSnackbarController.current
	val coroutineScope = rememberCoroutineScope()

	var loaded by remember { mutableStateOf(false) }

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
				loaded = true
			}

			// todo: scrolling
			LazyColumn {
				if (loaded && accounts.isNotEmpty()) {
					accounts.forEach { account ->
						item { AccountRow(account = account) }
					}
				} else if (loaded && accounts.isEmpty()) {
					item {
						Row(
							modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.Center
						) {
							Text(
								stringResource(Res.string.nothing_to_see_here),
								color = MaterialTheme.colorScheme.onSurfaceVariant,
								fontSize = 13.sp
							)
						}
					}
				} else {
					item {
						Row(
							modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.Center
						) {
							CircularProgressIndicator()
						}
					}
				}
			}
		}
		InteractionViewType.Reaction -> {
			var currentTab by remember { mutableStateOf<String?>(null) }
			val reactions = remember { mutableStateListOf<Reaction>() }
			val tabs = remember { mutableStateListOf<String>() }

			LaunchedEffect(Unit) {
				coroutineScope.launch {
					reactions.clear()
					val res = getStatusReactions(id)
					if (res.error || res.response == null) {
						res.handleError(snackbarController = snackbarHandler)
						return@launch
					}
					reactions.addAll(res.response)
					loaded = true

					if (reactions.isEmpty()) return@launch

					reactions.forEach {
						if (!tabs.contains(it.name)) tabs.add(it.name)
					}

					currentTab = tabs.firstOrNull()
				}
			}


			if (!reactions.isEmpty()) {
				key(currentTab) {
					PrimaryScrollableTabRow(
						selectedTabIndex = if (currentTab != null) tabs.indexOf(currentTab) else 0,
						edgePadding = 15.dp
					) {
						tabs.forEach { tab ->
							val reaction = reactions.first { it.name == tab }
							val count = reactions.count { it.name == tab }

							Tab(
								selected = currentTab == tab,
								onClick = { currentTab = tab },
								text = {
									Row(
										horizontalArrangement = Arrangement.spacedBy(5.dp)
									) {
										site.remlit.snowdrop.component.Reaction(
											Status.Reaction(
												count = count.toLong(),
												me = false,
												name = reaction.name,
												url = reaction.url,
												staticUrl = reaction.staticUrl
											)
										)

										Text("$count")
									}
								}
							)
						}
					}
					LazyColumn {
						items(
							items = reactions.filter { it.name == currentTab },
							key = { it.account.id }
						) {
							AccountRow(account = it.account)
						}
					}
				}
			} else if (!loaded) {
				Row(
					modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.Center
				) {
					CircularProgressIndicator()
				}
			} else {
				Row(
					modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.Center
				) {
					Text(
						stringResource(Res.string.nothing_to_see_here),
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						fontSize = 13.sp
					)
				}
			}
		}
	}
}
