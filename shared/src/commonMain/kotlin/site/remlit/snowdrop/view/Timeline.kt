package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import site.remlit.snowdrop.api.timeline.getHomeTimeline
import site.remlit.snowdrop.component.Status
import site.remlit.snowdrop.model.Status

@Composable
@Preview
fun Timeline() {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		var timeline = remember { mutableStateListOf<Status>() }

		LaunchedEffect(Unit) {
			val res = getHomeTimeline()
			if (res.error) return@LaunchedEffect
			if (res.response == null) return@LaunchedEffect
			timeline.addAll(res.response)
		}

		val scrollState = rememberScrollState()

		Column(
			modifier = Modifier
				.verticalScroll(scrollState)
				.fillMaxSize()
		) {
			for (status in timeline) {
				Status(status)
			}
		}
	}
}