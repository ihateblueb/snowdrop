package site.remlit.snowdrop.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.api.statuses.createStatus
import site.remlit.snowdrop.component.Avatar
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.component.Visibility
import site.remlit.snowdrop.model.request.CreateStatusRequest
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.util.settings
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_close_24px
import snowdrop.shared.generated.resources.icon_globe_20px
import snowdrop.shared.generated.resources.icon_home_20px
import snowdrop.shared.generated.resources.icon_lock_20px
import snowdrop.shared.generated.resources.icon_mail_20px
import snowdrop.shared.generated.resources.icon_send_24px

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun ComposeView() = ViewSurface {
	val navHandler = LocalNavController.current

	val currentAccount by getCurrentAccountObjectFlow()
		.collectAsStateWithLifecycle(null)

	var visibilityDropdownOpen by remember { mutableStateOf(false) }

	var cw by remember { mutableStateOf("") }
	var content by remember { mutableStateOf("") }
	var visibility by remember { mutableStateOf(blockingSettings.getString("default_visibility", "public")) }


	suspend fun sendPost() {
		val res = createStatus(CreateStatusRequest(
			status = content,
			spoilerText = cw,
			visibility = visibility
		))
		if (res.error) return
		if (res.response == null) return
		navHandler.popBackStack()
	}

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_close_24px), null)
			}
		},
		title = {
			Text("Compose")
		}
	)

	Column(
		modifier = Modifier.fillMaxHeight()
			.fillMaxWidth()
	) {
		if (currentAccount != null) {
			Row(
				modifier = Modifier.padding(10.dp)
					.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Avatar(currentAccount!!)

				Column(
					modifier = Modifier.weight(1f)
				) {
					Text(
						currentAccount!!.displayName ?: currentAccount!!.username,
						fontWeight = FontWeight.Medium,
						overflow = TextOverflow.Ellipsis,
						maxLines = 1
					)
					Text(
						"@${currentAccount!!.acct}",
						overflow = TextOverflow.Ellipsis,
						color = MaterialTheme.colorScheme.onSurfaceVariant,
						fontSize = 13.sp,
						maxLines = 1
					)
				}

				Row(
					horizontalArrangement = Arrangement.End
				) {
					Row {
						TextButton(onClick = { visibilityDropdownOpen = !visibilityDropdownOpen }) {
							Visibility(visibility, true)
						}

						DropdownMenu(
							expanded = visibilityDropdownOpen,
							onDismissRequest = { visibilityDropdownOpen = !visibilityDropdownOpen }
						) {
							DropdownMenuItem(
								leadingIcon = {
									Icon(painterResource(Res.drawable.icon_globe_20px) ,null)
								},
								text = {
									Column(modifier = Modifier.padding(vertical = 5.dp)) {
										Text(
											"Public",
											fontWeight = FontWeight.Medium
										)
										Text(
											"Visible to anyone, shows on global timeline",
											fontSize = 13.sp
										)
									}
								},
								onClick = {
									visibility = "public"
									visibilityDropdownOpen = !visibilityDropdownOpen
								}
							)
							DropdownMenuItem(
								leadingIcon = {
									Icon(painterResource(Res.drawable.icon_home_20px) ,null)
								},
								text = {
									Column(modifier = Modifier.padding(vertical = 5.dp)) {
										Text(
											"Unlisted",
											fontWeight = FontWeight.Medium
										)
										Text(
											"Visible to anyone, not shown on the global timeline",
											fontSize = 13.sp
										)
									}
								},
								onClick = {
									visibility = "unlisted"
									visibilityDropdownOpen = !visibilityDropdownOpen
								}
							)
							DropdownMenuItem(
								leadingIcon = {
									Icon(painterResource(Res.drawable.icon_lock_20px) ,null)
								},
								text = {
									Column(modifier = Modifier.padding(vertical = 5.dp)) {
										Text(
											"Followers",
											fontWeight = FontWeight.Medium
										)
										Text(
											"Visible to followers only",
											fontSize = 13.sp
										)
									}
								},
								onClick = {
									visibility = "private"
									visibilityDropdownOpen = !visibilityDropdownOpen
								}
							)
							DropdownMenuItem(
								leadingIcon = {
									Icon(painterResource(Res.drawable.icon_mail_20px) ,null)
								},
								text = {
									Column(modifier = Modifier.padding(vertical = 5.dp)) {
										Text(
											"Direct",
											fontWeight = FontWeight.Medium
										)
										Text(
											"Visible to only to mentioned users",
											fontSize = 13.sp
										)
									}
								},
								onClick = {
									visibility = "direct"
									visibilityDropdownOpen = !visibilityDropdownOpen
								}
							)
						}
					}
				}
			}

			Column(
				verticalArrangement = Arrangement.spacedBy(10.dp)
			) {
				TextField(
					content,
					placeholder = { Text("Write your post here...") },
					onValueChange = { content = it },
					modifier = Modifier.imePadding()
						.fillMaxWidth(),
					colors = TextFieldDefaults.colors(
						unfocusedContainerColor = Color(0x00000000),
						unfocusedIndicatorColor = Color(0x00000000),
						focusedContainerColor = Color(0x00000000),
						focusedIndicatorColor = Color(0x00000000),
					)
				)
			}

			Row(
				modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
					.padding(all = 5.dp)
					.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				Row(
					modifier = Modifier.fillMaxWidth()
						.navigationBarsPadding(),
					horizontalArrangement = Arrangement.End
				) {
					Row {
						TextButton(onClick = { runBlocking { sendPost() } }) {
							Icon(painterResource(Res.drawable.icon_send_24px), null)
						}
					}
				}
			}
		}
	}
}
