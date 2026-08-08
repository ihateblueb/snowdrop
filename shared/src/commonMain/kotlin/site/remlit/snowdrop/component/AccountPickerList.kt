package site.remlit.snowdrop.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import sh.calvin.reorderable.ReorderableColumn
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.util.ListItemShape
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.getAccountHost
import site.remlit.snowdrop.util.getAccountObject
import site.remlit.snowdrop.util.getAccounts
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.listItemClip
import site.remlit.snowdrop.util.listItemSpacing
import site.remlit.snowdrop.util.switchAccount
import site.remlit.snowdrop.util.vibrateSoft
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_drag_indicator_24px

/**
 * Account picker component.
 *
 * @param modifier Modifier for column container
 * @param onSelect Action to run when selecting an account
 *
 * @since 0.0.5-alpha
 * */
@Composable
fun AccountPickerList(
	modifier: Modifier = Modifier,
	onSelect: () -> Unit = {},
	reordering: Boolean = false,
) {
	val navController = LocalNavController.current
	val haptics = LocalHapticFeedback.current

	var refreshKey by remember { mutableStateOf(0) }
	val accounts = remember(refreshKey) {
		getAccounts().map { Pair(it, getAccountObject(it)) }
			.filter { it.second != null }
	}


	fun switchToThisAccount(id: String) {
		if (id != getCurrentAccountId()) {
			onSelect()
			switchAccount(id, navController)
			vibrateSoft(haptics)
		}
	}


	@Composable
	fun AccountCard(
		id: String,
		index: Int,
		account: Account,
		trailingButton: @Composable () -> Unit
	) {
		// todo: long press to log out

		// important: be careful with contrast here, always test
		Card(
			modifier = Modifier.fillMaxWidth()
				.listItemClip(index, accounts.size)
				.clickable { switchToThisAccount(id) },
			shape = ListItemShape(index, accounts.size),
			colors = if (getCurrentAccountId() == id)
				CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer,
					contentColor = MaterialTheme.colorScheme.onPrimaryContainer
				)
			else CardDefaults.cardColors()
		) {
			Row(
				modifier = Modifier.padding(10.dp),
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Avatar(account)

				Column(
					modifier = Modifier.weight(1f)
				) {
					HtmlContent(
						account.displayName(),
						emojis = account.emojis,
						fontWeight = FontWeight.Medium,
						color = if (getCurrentAccountId() == id) MaterialTheme.colorScheme.onPrimaryContainer
						else MaterialTheme.colorScheme.onSurface,
						simple = true,
						maxLines = 1,
					)
					Text(
						"@${account.username}@${getAccountHost(id)}",
						fontSize = 13.sp
					)
				}

				trailingButton()
			}
		}
	}


	if (reordering) {
		ReorderableColumn(
			modifier = modifier,
			verticalArrangement = Arrangement.spacedBy(listItemSpacing),
			list = accounts,
			onSettle = { from, to ->
				blockingSettings.putString("accounts", accounts.toMutableList().apply {
					add(to, removeAt(from))
				}.joinToString(separator = " ") { it.first })
				refreshKey++
			},
		) { index, pair, _ ->
			key(pair) {
				val id = pair.first
				val account = pair.second!!

				ReorderableItem {
					AccountCard(id, index, account) {
						IconButton(
							modifier = Modifier.draggableHandle(
								onDragStarted = {},
								onDragStopped = {},
							),
							onClick = {},
						) {
							Icon(painterResource(Res.drawable.icon_drag_indicator_24px), contentDescription = "Reorder")
						}
					}
				}
			}
		}
	} else {
		Column(
			modifier = modifier,
			verticalArrangement = Arrangement.spacedBy(listItemSpacing)
		) {
			accounts.forEachIndexed { index, pair ->
				val id = pair.first
				val account = pair.second!!

				AccountCard(id, index, account) {
					RadioButton(
						selected = getCurrentAccountId() == id,
						onClick = { switchToThisAccount(id) },
						colors = RadioButtonDefaults.colors(
							if (getCurrentAccountId() == id) MaterialTheme.colorScheme.onPrimaryContainer
							else MaterialTheme.colorScheme.onSurface
						)
					)
				}
			}
		}
	}
}
