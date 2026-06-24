package site.remlit.snowdrop.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import site.remlit.snowdrop.api.accounts.getAccount
import site.remlit.snowdrop.component.Avatar
import site.remlit.snowdrop.model.User

@Composable
fun Profile(id: String) {
	var account by remember { mutableStateOf<User?>(null) }
	var ready by remember { mutableStateOf(false) }

	LaunchedEffect(Dispatchers.Default) {
		// todo: handle errors
		val req = getAccount(id)
		if (req.error) return@LaunchedEffect
		account = req.response

		ready = true
	}

	MaterialTheme {
		if (!ready || account == null) {
			Column(
				modifier = Modifier.fillMaxHeight().fillMaxWidth(),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center
			) {
				CircularProgressIndicator()
			}
		} else {
			Column {
				Row {
					Avatar(user = account!!)

					Column {
						Text(account!!.displayName ?: account!!.username)
						Text(account!!.fqn)
					}
				}
			}
		}
	}
}