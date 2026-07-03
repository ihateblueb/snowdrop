package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.api.accounts.patchProfile
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.request.PatchProfileRequest
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.SnackbarController
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.util.updateCurrentAccountObject
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.automated_account
import snowdrop.shared.generated.resources.discoverable
import snowdrop.shared.generated.resources.discoverable_description
import snowdrop.shared.generated.resources.edit_profile
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.locked_account
import snowdrop.shared.generated.resources.locked_account_description
import snowdrop.shared.generated.resources.save

@Composable
fun EditProfileView() = ViewSurface {
	val navHandler = LocalNavController.current
	val snackbarHandler = SnackbarController.current
	val coroutineScope = rememberCoroutineScope()

	val currentAccount by getCurrentAccountObjectFlow()
		.collectAsStateWithLifecycle(null)

	var displayName by remember { mutableStateOf("") }
	var displayNameChanged by remember { mutableStateOf(false) }

	var note by remember { mutableStateOf("") } // bio
	var noteChanged by remember { mutableStateOf(false) }

	var bot by remember { mutableStateOf(false) }
	var botChanged by remember { mutableStateOf(false) }

	var locked by remember { mutableStateOf(false) }
	var lockedChange by remember { mutableStateOf(false) }

	var discoverable by remember { mutableStateOf(false) }
	var discoverableChanged by remember { mutableStateOf(false) }

	var indexable by remember { mutableStateOf(false) }
	var indexableChanged by remember { mutableStateOf(false) }


	val profileChanged = displayNameChanged || noteChanged || botChanged || lockedChange || discoverableChanged
		|| indexableChanged

	// todo: language


	LaunchedEffect(currentAccount) {
		displayName = currentAccount?.displayName ?: ""
		note = currentAccount?.note ?: ""

		bot = currentAccount?.bot ?: false
		locked = currentAccount?.locked ?: false
		discoverable = currentAccount?.discoverable ?: false
		indexable = false // todo: how does this work?
	}


	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
			}
		},
		title = {
			Text(stringResource(Res.string.edit_profile))
		},
		actions = {
			FilledTonalButton(
				onClick = {
					coroutineScope.launch {
						// todo: doesn't work on iceshrimp.js
						val res = patchProfile(
							PatchProfileRequest(
								displayName = if (displayNameChanged) displayName else null,
								note = if (noteChanged) note else null,
								bot = if (botChanged) bot else null,
								locked = if (lockedChange) locked else null,
								discoverable = if (discoverableChanged) discoverable else null,
								indexable = if (indexableChanged) indexable else null
							)
						)
						if (res.error || res.response == null) {
							res.handleError(snackbarHandler)
							return@launch
						}

						updateCurrentAccountObject()
					}
				},
				enabled = profileChanged
			) {
				Text(stringResource(Res.string.save))
			}
		}
	)

	if (currentAccount != null) {
		LazyColumn(
			modifier = Modifier.padding(10.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp)
		) {
			item {
				TextField(
					value = displayName,
					onValueChange = { displayName = it; displayNameChanged = true },
					maxLines = 1,
					label = { Text("Display name") },
					placeholder = { Text(currentAccount!!.displayName()) },
					modifier = Modifier.fillMaxWidth()
				)
			}

			item {
				// todo: make it source and not html
				TextField(
					value = note,
					onValueChange = { note = it; noteChanged = true },
					label = { Text("Description") },
					placeholder = { Text(currentAccount!!.note ?: "") },
					modifier = Modifier.fillMaxWidth().height(200.dp)
				)
			}

			item {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.automated_account)) },
					trailingContent = {
						Switch(
							bot,
							onCheckedChange = { bot = it; botChanged = true }
						)
					}
				)
			}

			item {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.locked_account)) },
					supportingContent = { Text(stringResource(Res.string.locked_account_description)) },
					trailingContent = {
						Switch(
							locked,
							onCheckedChange = { locked = it; lockedChange = true }
						)
					}
				)
			}

			item {
				ListItem(
					headlineContent = { Text(stringResource(Res.string.discoverable)) },
					supportingContent = { Text(stringResource(Res.string.discoverable_description)) },
					trailingContent = {
						Switch(
							discoverable,
							onCheckedChange = { discoverable = it; discoverableChanged = true }
						)
					}
				)
			}
		}
	}
}
