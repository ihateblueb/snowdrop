package site.remlit.snowdrop.view.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import site.remlit.snowdrop.component.ViewSurface

@Composable
fun SettingsView() = ViewSurface {
	TopAppBar(
		title = {
			Text("Settings")
		}
	)

	val scrollState = rememberScrollState()

	Column(
		modifier = Modifier
			.verticalScroll(scrollState)
			.fillMaxSize()
	) {

	}
}