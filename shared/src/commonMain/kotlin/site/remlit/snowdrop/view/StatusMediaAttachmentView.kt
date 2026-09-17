package site.remlit.snowdrop.view

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.ImageFormat
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.saveImageToGallery
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.component.NavigationBackButton
import site.remlit.snowdrop.component.StatusMediaAttachment
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.Platform
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.LocalSnackbarController
import site.remlit.snowdrop.util.cache.fetchStatus
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.getPlatform
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.converted_to_png
import snowdrop.shared.generated.resources.icon_download_24px
import snowdrop.shared.generated.resources.icon_info_24px
import snowdrop.shared.generated.resources.icon_open_in_new_24px
import snowdrop.shared.generated.resources.image_saved
import snowdrop.shared.generated.resources.issue_saving_image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusMediaAttachmentView(id: String, startingPosition: Int = 0) = ViewSurface {
	val navHandler = LocalNavController.current
	val uriHandler = LocalUriHandler.current
	val snackbarHandler = LocalSnackbarController.current
	val coroutineScope = rememberCoroutineScope()

	val status by remember { fetchStatus(id) }.collectAsStateWithLifecycle(null)
	val pager = rememberPagerState(startingPosition) { status?.mediaAttachments?.size ?: 0 }

	// todo: certain actions (single tap, zoom in) should trigger this to be false and certain
	//  should make it true (single tap, zoom out)
	var showDecorations by remember { mutableStateOf(true) }
	var showAltSheet by remember { mutableStateOf(false) }

	val __translation_image_saved = stringResource(Res.string.image_saved)
	val __converted_to_png = stringResource(Res.string.converted_to_png)
	val __issue_saving_image = stringResource(Res.string.issue_saving_image)

	Column(
		modifier = Modifier.background(Color.Black)
			.fillMaxSize()
	) {
		TopAppBar(
			navigationIcon = { NavigationBackButton(close = true) },
			title = {},
			colors = TopAppBarDefaults.topAppBarColors(
				containerColor = Color(0x80000000),
				navigationIconContentColor = Color.White
			),
			modifier = Modifier.animateContentSize(tween(100))
				.height(if (showDecorations) Dp.Unspecified else 0.dp),
			actions = {
				IconButton(
					onClick = { showAltSheet = !showAltSheet },
					enabled = !status?.mediaAttachments[pager.currentPage]?.description.isNullOrBlank()
				) {
					Icon(painterResource(Res.drawable.icon_info_24px), null)
				}

				if (status?.mediaAttachments[pager.currentPage]?.type == "image") {
					IconButton(
						onClick = {
							coroutineScope.launch {
								val attachment = status?.mediaAttachments[pager.currentPage]

								if (attachment?.url == null) return@launch

								val res = httpClient.get(attachment.url)
								var image = res.bodyAsBytes()
								val mimeType = res.headers["content-type"]
								val regex = "[^/\\\\&?]+\\.\\w{3,4}(?=([?&].*$|$))".toRegex()
								val filename = regex.find(attachment.url)?.value ?: return@launch

								var converted = false
								if (getPlatform() == Platform.IOS && mimeType == "image/webp") {
									image = FileKit.compressImage(image, imageFormat = ImageFormat.PNG)
									converted = true
								}

								val saver = FileKit.saveImageToGallery(image, filename)

								if (saver.isSuccess)
									snackbarHandler.showSnackbar(__translation_image_saved + if (converted) " $__converted_to_png" else "")
								else
									snackbarHandler.showSnackbar(__issue_saving_image)
							}
						},
					) {
						Icon(painterResource(Res.drawable.icon_download_24px), null)
					}
				}

				IconButton(onClick = { uriHandler.openUri(status?.mediaAttachments[pager.currentPage]?.url ?: "") }) {
					Icon(painterResource(Res.drawable.icon_open_in_new_24px), null)
				}

			/*
			* var dropdown by remember { mutableStateOf(false) }
			IconButton(onClick = { dropdown = !dropdown }) {
				Icon(painterResource(Res.drawable.icon_more_vert_24px), null)
			}

				PreparedDropdownMenu(
				expanded = dropdown,
				onDismissRequest = { dropdown = false }
			) {
			}*/

			}
		)

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

						val alt = media.description
						if (showAltSheet) ModalBottomSheet(
							onDismissRequest = { showAltSheet = false }
						) {
							SelectionContainer {
								if (!alt.isNullOrBlank()) Text(
									alt,
									modifier = Modifier.padding(10.dp)
								)
							}
						}

						StatusMediaAttachment(
							media,
							includeFallback = false,
							showVideoProgress = true,
							onVideoPlayerStateChange = { state ->
								showDecorations = !state.isPlaying
							},
							supportZoomGestures = true,
							modifier = Modifier.fillMaxSize(),
							onTransform = { userTransform ->
								showDecorations = !(userTransform.scale.scaleX != 1.0f &&
									userTransform.scale.scaleY != 1.0f)
							}
						)
					}
				}
			}
		}
	}
}
