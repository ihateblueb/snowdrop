package site.remlit.snowdrop.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.Dispatchers
import site.remlit.snowdrop.api.accounts.getAccount
import site.remlit.snowdrop.component.Avatar
import site.remlit.snowdrop.component.bigAvatarRadius
import site.remlit.snowdrop.component.bigAvatarSize
import site.remlit.snowdrop.model.User
import site.remlit.snowdrop.util.formatNumber
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow

const val headerHeight = 200

@Composable
fun ProfileView(id: String) {
	val currentAccount by getCurrentAccountObjectFlow().collectAsStateWithLifecycle(null)

	var account by remember { mutableStateOf<User?>(null) }
	var isMe by remember { mutableStateOf(account?.id == currentAccount?.id) }
	var ready by remember { mutableStateOf(false) }

	val scrollState = rememberScrollState()

	LaunchedEffect(Dispatchers.Default) {
		// todo: handle errors
		val req = getAccount(id)
		if (req.error) return@LaunchedEffect
		account = req.response

		ready = true
	}

	Column {
		TopAppBar(
			title = {
				if (account == null) Text("Profile")
				else Column {
					Text(account!!.displayName ?: account!!.username)
					Text(
						"${formatNumber(account!!.statusesCount)} posts",
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
			Column(
				modifier = Modifier
					.verticalScroll(scrollState)
			) {
				Column {
					@Composable
					fun fallbackHeader() {
						Box(
							modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
								.height(headerHeight.dp)
								.fillMaxWidth()
						)
					}

					if (account!!.header != null) {
						KamelImage(
							{ asyncPainterResource(account!!.header!!) },
							account!!.headerDescription,
							onLoading = { fallbackHeader() },
							modifier = Modifier.height(headerHeight.dp)
								.fillMaxWidth(),
							contentScale = ContentScale.Crop

						)
					} else fallbackHeader()

					// The Rest
					Column(
						modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 0.dp, bottom = 10.dp)
							.offset(y = (-((bigAvatarSize/2))).dp)
					) {
						// top of header, avatar and button
						Row(
							modifier = Modifier.padding(bottom = 10.dp),

							verticalAlignment = Alignment.Bottom,
							horizontalArrangement = Arrangement.SpaceBetween
						) {
							// jank outer border
							Box(contentAlignment = Alignment.Center) {
								Box(
									modifier = Modifier.background(
										MaterialTheme.colorScheme.background,
										RoundedCornerShape((bigAvatarRadius + 2).dp)
									).height((bigAvatarSize + 6).dp)
										.width((bigAvatarSize + 6).dp)
								)
								Avatar(user = account!!, big = true)
							}

							if (isMe) {
								OutlinedButton(onClick = {}) {
									Text("Edit profile")
								}
							} else {
								OutlinedButton(onClick = {}) {
									Text("Follow")
								}
							}
						}

						// display name
						Row(
							modifier = Modifier.padding(bottom = 5.dp),
						) {
							Column {
								Text(
									account!!.displayName ?: account!!.username,
									fontWeight = FontWeight.Bold,
									fontSize = 24.sp
								)
								Text("@${account!!.fqn}")
							}
						}

						// bio
						if (account!!.note != null) {
							Text(text = remember(account!!.note!!) {
								htmlToAnnotatedString(account!!.note!!)
							})
						}

						// bottom of header
						Row(
							modifier = Modifier.padding(top = 5.dp),
							horizontalArrangement = Arrangement.spacedBy(10.dp)
						) {
							Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
								Text(
									"${account!!.followersCount}",
									fontWeight = FontWeight.Bold
								)
								Text("followers")
							}
							Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
								Text(
									"${account!!.followingCount}",
									fontWeight = FontWeight.Bold
								)
								Text("following")
							}
						}
					}
				}
			}
		}
	}
}