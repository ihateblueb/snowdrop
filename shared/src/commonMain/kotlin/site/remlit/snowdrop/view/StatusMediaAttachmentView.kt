package site.remlit.snowdrop.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.bottomNavEnterAnimation
import site.remlit.snowdrop.bottomNavExitAnimation
import site.remlit.snowdrop.component.StatusMediaAttachment
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.cache.fetchStatus
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_close_24px

@Composable
fun StatusMediaAttachmentView(id: String, startingPosition: Int = 0) = ViewSurface {
	val navHandler = LocalNavController.current

	val status by remember { fetchStatus(id) }.collectAsStateWithLifecycle(null)
	val pager = rememberPagerState(startingPosition) { status?.mediaAttachments?.size ?: 0 }

	// todo: certain actions (single tap, zoom in) should trigger this to be false and certain
	//  should make it true (single tap, zoom out)
	var showDecorations by remember { mutableStateOf(true) }

	Column(
		modifier = Modifier.background(Color.Black)
			.fillMaxSize()
	) {
		AnimatedVisibility(
			showDecorations,
			enter = fadeIn() + slideInVertically(),
			exit = slideOutVertically() + fadeOut()
		) {
			// todo: image should not be clipped so it can overflow and be shown behind the top app bar
			//  without it having to start there
			TopAppBar(
				navigationIcon = {
					IconButton(onClick = { navHandler.popBackStack() }) {
						Icon(painterResource(Res.drawable.icon_close_24px), null)
					}
				},
				title = {},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = Color(0x80000000),
					navigationIconContentColor = Color.White
				),
			)
		}

		if (status != null) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.Center
			) {
				HorizontalPager(
					state = pager,
					modifier = Modifier.fillMaxWidth()
				) { page ->
					Box(modifier = Modifier.fillMaxSize()) {
						val media = status!!.mediaAttachments[page]

						Column(
							verticalArrangement = Arrangement.Bottom,
							modifier = Modifier.fillMaxSize()
								.zIndex(10f)
						) {
							// this is a card for multiple reasons, mainly to respect someone's
							// light mode setting in case it's for text readability
							val alt = media.description
							if (!alt.isNullOrBlank()) {
								val scrollState = rememberScrollState()
								AnimatedVisibility(
									showDecorations,
									enter = bottomNavEnterAnimation,
									exit = bottomNavExitAnimation
								) {
									Card(
										colors = CardDefaults.cardColors(
											containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
										),
										modifier = Modifier.padding(10.dp)
											.heightIn(max = 100.dp)
											.fillMaxWidth()
									) {
										Column(
											modifier = Modifier.verticalScroll(scrollState)
										) {
											Text(
												modifier = Modifier.padding(10.dp),
												color = MaterialTheme.colorScheme.onSurface,
												fontSize = 13.sp,
												text = alt
											)
										}
									}
								}
							}
						}

						StatusMediaAttachment(
							media,
							includeFallback = false,
							supportZoomGestures = true,
							modifier = Modifier.fillMaxSize()
						)
					}
				}
			}
		}
	}
}
