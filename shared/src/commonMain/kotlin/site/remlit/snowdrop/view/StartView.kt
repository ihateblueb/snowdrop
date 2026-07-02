package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.russhwolf.settings.ExperimentalSettingsApi
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.util.settings

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun StartView(
	navigateToLogin: () -> Unit,
	navigateToTimeline: () -> Unit
) = ViewSurface {
	val loggedIn by settings.getBooleanOrNullFlow("logged_in")
		.collectAsState(null)

	Column(
		modifier = Modifier.fillMaxHeight()
			.fillMaxWidth(),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		CircularProgressIndicator()
	}

	if (loggedIn == true) navigateToTimeline()
	else if (loggedIn == false) navigateToLogin()
	// else, just load until it reads
}
