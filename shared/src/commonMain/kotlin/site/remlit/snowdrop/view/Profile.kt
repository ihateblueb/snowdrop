package site.remlit.snowdrop.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.Dispatchers
import site.remlit.snowdrop.api.accounts.getAccount
import site.remlit.snowdrop.component.Avatar
import site.remlit.snowdrop.component.bigAvatarSize
import site.remlit.snowdrop.model.User
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow

@Composable
fun Profile(id: String) {
	val currentAccount by getCurrentAccountObjectFlow().collectAsStateWithLifecycle(null)

	var account by remember { mutableStateOf<User?>(null) }
	var isMe by remember { mutableStateOf(account?.id == currentAccount?.id) }
	var ready by remember { mutableStateOf(false) }

	LaunchedEffect(Dispatchers.Default) {
		// todo: handle errors
		val req = getAccount(id)
		if (req.error) return@LaunchedEffect
		account = req.response

		ready = true
	}

	TopAppBar(
		title = {
			if (account == null) Text("Profile")
			else Column {
				Text(account!!.displayName ?: account!!.username)
				Text(
					"${account!!.statusesCount} posts",
					fontSize = 14.sp
				)
			}
		}
	)

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
			Column {
				@Composable
				fun fallbackHeader() {
					Box(
						modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
							.height(125.dp)
							.fillMaxWidth()
					)
				}

				if (account!!.header != null) {
					KamelImage(
						{ asyncPainterResource(account!!.header!!) },
						account!!.headerDescription,
						onLoading = { fallbackHeader() },
						modifier = Modifier.height(200.dp)
							.fillMaxWidth(),
						contentScale = ContentScale.Crop

					)
				} else fallbackHeader()

				// The Rest
				Column(
					modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 0.dp, bottom = 10.dp)
						.offset(y = (-((bigAvatarSize/2))).dp)
				) {
					Row(
						modifier = Modifier.padding(bottom = 10.dp),
						verticalAlignment = Alignment.Bottom,
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Avatar(user = account!!, big = true)

						if (isMe) {
							OutlinedButton(onClick = {}) {
								// todo: pencil icon
								Text("Edit profile")
							}
						} else {
							OutlinedButton(onClick = {}) {
								// todo user plus icon
								Text("Follow")
							}
						}
					}
					Row {
						Column {
							Text(
								account!!.displayName ?: account!!.username,
								fontWeight = FontWeight.Bold,
								fontSize = 24.sp
							)
							Text("@${account!!.fqn}")
						}
					}
				}
			}
		}
	}
}