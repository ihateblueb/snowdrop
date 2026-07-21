package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.api.search
import site.remlit.snowdrop.component.AccountRow
import site.remlit.snowdrop.component.RefreshableTimeline
import site.remlit.snowdrop.component.Status
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.ApiResponse
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.accounts
import snowdrop.shared.generated.resources.explore
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.icon_search_24px
import snowdrop.shared.generated.resources.nothing_to_see_here
import snowdrop.shared.generated.resources.posts
import snowdrop.shared.generated.resources.search_for_posts_or_users
import snowdrop.shared.generated.resources.search_results

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreView(immediateFocus: Boolean = false) = ViewSurface {
	val focusRequester = remember { FocusRequester() }
	val keyboardController = LocalSoftwareKeyboardController.current

	var query by remember { mutableStateOf("") }
	var showResults by remember { mutableStateOf(false) }
	var refreshKey by remember { mutableStateOf(0) }

	LaunchedEffect(immediateFocus) {
		if (!immediateFocus) return@LaunchedEffect
		focusRequester.requestFocus()
		keyboardController?.show()
	}

	TopAppBar(
		navigationIcon = {
			if (showResults)
				IconButton(onClick = { showResults = false; query = "" }) {
					Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
				}
		},
		title = {
			if (!showResults) Text(stringResource(Res.string.explore))
			else Text(stringResource(Res.string.search_results))
		}
	)

	TextField(
		value = query,
		onValueChange = { query = it },
		keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
		keyboardActions = KeyboardActions(onSearch = { showResults = true; keyboardController?.hide(); refreshKey++ }),

		placeholder = { Text(stringResource(Res.string.search_for_posts_or_users)) },
		leadingIcon = { Icon(painterResource(Res.drawable.icon_search_24px), null) },

		maxLines = 1,
		modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 0.dp, bottom = 10.dp)
			.clip(RoundedCornerShape(100))
			.focusRequester(focusRequester)
			.fillMaxWidth(),
		colors = TextFieldDefaults.colors(
			unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
			unfocusedIndicatorColor = Color(0x00000000),
			focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
			focusedIndicatorColor = Color(0x00000000),
		)
	)

	if (!showResults) {
		Column(
			modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 0.dp, bottom = 10.dp)
				.fillMaxSize()
		) {
			Text("Trending")
		}
	} else {
		var selectedTab by remember { mutableStateOf(0) }

		PrimaryTabRow(
			selectedTabIndex = selectedTab
		) {
			Tab(selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text(stringResource(Res.string.posts)) })
			Tab(selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(stringResource(Res.string.accounts)) })
			// todo: implement hashtags in search results
			// Tab(selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text(stringResource(Res.string.hashtags)) })
		}

		// pagination is very different with search, and just uses an offset/limit system
		// sooo... it works weird.
		if (!showResults) {
			Row(
				modifier = Modifier.padding(10.dp),
				horizontalArrangement = Arrangement.Center
			) {
				Text(stringResource(Res.string.nothing_to_see_here))
			}
		} else {
			val limit = 20
			var offset by remember { mutableStateOf(0) }
			when (selectedTab) {
				0 -> RefreshableTimeline(
					fetchMethod = { _, _, _ ->
						val res = search(query, resolve = true, offset = offset, limit = limit, type = "statuses")
						offset += limit
						ApiResponse(error = res.error, message = res.message, response = res.response?.statuses)
					},
					onRefresh = { offset = 0 },
					refreshKey = refreshKey,
					timelineComponent = { Status(it) },
					distinctCheck = true
				)
				1 -> RefreshableTimeline(
					fetchMethod = { _, _, _ ->
						val res = search(query, resolve = true, offset = offset, limit = limit, type = "accounts")
						offset += limit
						ApiResponse(error = res.error, message = res.message, response = res.response?.accounts)
					},
					onRefresh = { offset = 0 },
					refreshKey = refreshKey,
					timelineComponent = { AccountRow(it) },
					distinctCheck = true
				)
			}
		}
	}
}
