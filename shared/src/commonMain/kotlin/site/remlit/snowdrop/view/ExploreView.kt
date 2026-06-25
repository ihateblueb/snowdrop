package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import site.remlit.snowdrop.component.ViewSurface

@Composable
fun ExploreView() = ViewSurface {
	TopAppBar(
		title = {
			Text("Explore")
		}
	)

	Column(
		modifier = Modifier.fillMaxHeight()
			.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Text("Nothing to see here...")
	}
}