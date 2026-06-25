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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.ProfileRoute
import site.remlit.snowdrop.api.accounts.getAccount
import site.remlit.snowdrop.component.Avatar
import site.remlit.snowdrop.component.HtmlContent
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.component.bigAvatarRadius
import site.remlit.snowdrop.component.bigAvatarSize
import site.remlit.snowdrop.model.User
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.atRoute
import site.remlit.snowdrop.util.formatNumber
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.util.settings
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_arrow_back_24

const val headerHeight = 200

@Composable
@OptIn(ExperimentalSettingsApi::class)
fun ProfileView(id: String) = ViewSurface {
	val navHandler = LocalNavController.current
	val currentDest = navHandler.currentDestination

	/* Preferences */
	val hideFollowCounters by settings.getBooleanFlow("hide_follow_counters", false)
		.collectAsStateWithLifecycle(false)

	/* View variables */
	val currentAccount by getCurrentAccountObjectFlow()
		.collectAsStateWithLifecycle(null)

	var account by remember { mutableStateOf<User?>(null) }
	var isMe by remember { mutableStateOf(account != null && currentAccount != null &&
			account?.id == currentAccount?.id) }
	var ready by remember { mutableStateOf(false) }

	val scrollState = rememberScrollState()

	LaunchedEffect(Dispatchers.Default) {
		// todo: handle errors
		val req = getAccount(id)
		if (req.error) return@LaunchedEffect
		account = req.response

		ready = true
	}

	val verticalOffset = (-((bigAvatarSize/2) - 4)).dp
	var selectedTab by remember { mutableStateOf(0) }

	Column {
		TopAppBar(
			navigationIcon = {
				// not sure why you can't just check isMe.. if you do it just doesn't ever show up
				if (atRoute<ProfileRoute>(currentDest)) {
					IconButton(onClick = { navHandler.popBackStack() }) {
						Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
					}
				}
			},
			title = {
				if (account == null) Column {
					Text("Profile")
					Text(
						"0 posts",
						fontSize = 14.sp
					)
				}
				else Column {
					Text(
						account!!.displayName ?: account!!.username,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
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
						modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 0.dp, bottom = 15.dp)
							.offset(y = verticalOffset)
					) {
						// top of header, avatar and button
						Row(
							modifier = Modifier.padding(bottom = 10.dp)
								.fillMaxWidth(),

							verticalAlignment = Alignment.Bottom
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

							Row(
								modifier = Modifier.fillMaxWidth(),
								horizontalArrangement = Arrangement.End
							) {
								Row {
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
							}
						}

						// display name
						Row(
							modifier = Modifier.padding(bottom = 10.dp),
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
							HtmlContent(account!!.note!!)
						}

						/* UGLY...
						if (!account!!.fields.isEmpty()) {
							HorizontalDivider()
							Column(
								modifier = Modifier.padding(10.dp),
								verticalArrangement = Arrangement.spacedBy(5.dp)
							) {
								account!!.fields.forEach { (name, value) ->
									Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
										Text(
											name,
											fontWeight = FontWeight.Bold
										)
										HtmlContent(value)
									}
								}
							}
							HorizontalDivider()
						}
						 */

						Row(
							modifier = Modifier.padding(top = 10.dp)
						) {
							Text("Joined at " + account!!.createdAt)
						}

						// bottom of header
						if (!hideFollowCounters)
							Row(
								modifier = Modifier.padding(top = 10.dp),
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

					Column(
						modifier = Modifier.offset(y = verticalOffset)
					) {
						HorizontalDivider()

						PrimaryTabRow(selectedTabIndex = selectedTab) {
							Tab(selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Posts") })
							Tab(selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Posts & Replies") })
							Tab(selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Media") })
						}

					}
					// stay inside this above column ^^^
					// if you don't, there's a weird bottom space caused by the offset
					// and other things. this fixes it.
				}
			}
		}
	}
}
