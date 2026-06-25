package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking
import site.remlit.snowdrop.api.search
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.response.SearchResponse

@Composable
fun ExploreView() = ViewSurface {
	var query by remember { mutableStateOf("") }
	var results by remember { mutableStateOf<SearchResponse?>(null) }

	suspend fun submitSearch() {
		val res = search(query)
		if (res.error) return
		if (res.response == null) return
		results = res.response
	}

	TopAppBar(
		title = {
			Text("Explore")
		}
	)

	Column(
		modifier = Modifier.padding(10.dp)
			.fillMaxWidth()
	) {
		OutlinedTextField(
			value = query,
			onValueChange = { query = it },
			placeholder = { Text("Search for posts or users...") },
			keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
			keyboardActions = KeyboardActions(onGo = { runBlocking { submitSearch() } }),
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
