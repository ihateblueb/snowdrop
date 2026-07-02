package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.api.search
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.response.SearchResponse
import site.remlit.snowdrop.util.bgIO
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.explore
import snowdrop.shared.generated.resources.followers
import snowdrop.shared.generated.resources.search_for_posts_or_users

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreView() = ViewSurface {
	var query by remember { mutableStateOf("") }
	var results by remember { mutableStateOf<SearchResponse?>(null) }

	val searchBarState = rememberSearchBarState()

	suspend fun submitSearch() {
		val res = search(query)
		if (res.error) return
		if (res.response == null) return
		results = res.response
	}

	TopAppBar(
		title = {
			Text(stringResource(Res.string.explore))
		}
	)

	Column(
		modifier = Modifier.padding(10.dp)
			.fillMaxWidth()
	) {
		SearchBar(
			state = searchBarState,
			inputField = {
				SearchBarDefaults.InputField(
					query = query,
					onQueryChange = { query = it },
					onSearch = { bgIO { submitSearch() } },
					expanded = false,
					onExpandedChange = {},
					placeholder = { Text(stringResource(Res.string.search_for_posts_or_users)) }
				)
			},
			modifier = Modifier.fillMaxWidth()
		)
	}

	if (results == null) {
		Column(
			modifier = Modifier.fillMaxWidth()
				.fillMaxWidth()
		) {
			// todo: trending
			Text("TODO: Trending")
		}
	} else {

	}
}
