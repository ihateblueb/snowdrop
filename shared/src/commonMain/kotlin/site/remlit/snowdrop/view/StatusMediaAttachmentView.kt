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
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.saveImageToGallery
import io.github.vinceglb.filekit.saveVideoToGallery
import io.github.vinceglb.filekit.write
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.asSource
import kotlinx.coroutines.launch
import kotlinx.io.buffered
import kotlinx.io.readByteArray
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.component.NavigationBackButton
import site.remlit.snowdrop.component.StatusMediaAttachment
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.Platform
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.LocalSnackbarController
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.cache.fetchStatus
import site.remlit.snowdrop.util.config.httpClient
import site.remlit.snowdrop.util.convertToHeif
import site.remlit.snowdrop.util.getOSVersion
import site.remlit.snowdrop.util.getPlatform
import site.remlit.snowdrop.util.translation
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.converted_to_type
import snowdrop.shared.generated.resources.file_saved
import snowdrop.shared.generated.resources.icon_download_24px
import snowdrop.shared.generated.resources.icon_info_24px
import snowdrop.shared.generated.resources.icon_open_in_new_24px
import snowdrop.shared.generated.resources.image_saved
import snowdrop.shared.generated.resources.issue_saving_image
import snowdrop.shared.generated.resources.issue_saving_video
import snowdrop.shared.generated.resources.video_saved

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
	val __translation_file_saved = stringResource(Res.string.file_saved)
	val __translation_video_saved = stringResource(Res.string.video_saved)
	val __translation_converted_to_type = translation(Res.string.converted_to_type).toString()
	val __translation_issue_saving_image = stringResource(Res.string.issue_saving_image)
	val __translation_issue_saving_video = stringResource(Res.string.issue_saving_video)

	var currentFileBytes: ByteArray? = null
	val fkFileSaver = rememberFileSaverLauncher(FileKitDialogSettings.createDefault()) { file ->
		coroutineScope.launch {
			file?.write(currentFileBytes!!)
			snackbarHandler.showSnackbar(__translation_file_saved)
			currentFileBytes = null
		}
	}

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

				IconButton(
					onClick = {
						coroutineScope.launch {
							val attachment = status?.mediaAttachments[pager.currentPage]

							if (attachment?.url == null) return@launch

							val res = httpClient.get(attachment.url)
							var file = res.bodyAsChannel().asSource().buffered().readByteArray()
							val mimeType = res.headers["content-type"]

							val filenameFromUrlRegex = "[^/\\\\&?]+\\.\\w{3,4}(?=([?&].*$|$))".toRegex()
							val filenameFromContentDisposition = "filename[^;=\\n]*=((['\"]).*?\\2|[^;\\n]*)".toRegex()
							val urlFilename = filenameFromUrlRegex.find(attachment.url)?.value
							val disposition = res.headers["content-disposition"]
								?.let { filenameFromContentDisposition.find(it) }?.value
								?.replace("\"", "")
								?.replace("filename=", "")
							val filename = disposition ?: urlFilename ?: return@launch

							// welcome to my conversion code. enjoy your stay
							var converted = ""
							if (getPlatform() == Platform.IOS &&
								(mimeType == "image/webp" || mimeType == "image/jxl" ||
									(mimeType == "image/avif" && getOSVersion() < 26))) {
								val iosImageConversionChoice = blockingSettings.getString("ios_image_conversion_choice", "auto")
								val iosJpegQuality = blockingSettings.getInt("ios_jpeg_quality", 85)
								if (iosImageConversionChoice == "jpeg") {
									file = FileKit.compressImage(file, imageFormat = ImageFormat.JPEG, quality = iosJpegQuality)
									converted = "JPEG"
								} else if (iosImageConversionChoice == "png") {
									file = FileKit.compressImage(file, imageFormat = ImageFormat.PNG)
									converted = "PNG"
								} else if (iosImageConversionChoice == "heif") {
									val convertedImg = file.convertToHeif()
									if (convertedImg == null) {
										snackbarHandler.showSnackbar("Error converting to HEIF")
										return@launch
									}
									file = convertedImg
									converted = "HEIF"

								} else {
									val png = FileKit.compressImage(file, imageFormat = ImageFormat.PNG)
									val jpeg = FileKit.compressImage(file, imageFormat = ImageFormat.JPEG, quality = iosJpegQuality)

									// "where did you get this algorithm?" "i made it the fuck up"
									// although it works fairly well
									if (png.size > 4000000) { // 4mb
										file = jpeg
										converted = "JPEG"
									} else if (png.size / 5 < jpeg.size || png.size < 1000000) { // 1mb
										file = png
										converted = "PNG"
									} else {
										file = jpeg
										converted = "JPEG"
									}
								}
							}

							if (mimeType!!.startsWith("image")) {
								val saver = FileKit.saveImageToGallery(file, filename)

								if (saver.isSuccess)
									snackbarHandler.showSnackbar(__translation_image_saved +
										if (converted != "") " ${__translation_converted_to_type.replace("{type}", converted)}" else "")
								else
									snackbarHandler.showSnackbar(__translation_issue_saving_image)
							} else if (mimeType.startsWith("video")) {
								// this is stupid. double write.
								val platformFile = PlatformFile(FileKit.filesDir, filename)
								platformFile.write(file)
								val saver = FileKit.saveVideoToGallery(platformFile)
								platformFile.delete()

								if (saver.isSuccess)
									snackbarHandler.showSnackbar(__translation_video_saved)
								else
									snackbarHandler.showSnackbar(__translation_issue_saving_video)
							} else {
								currentFileBytes = file
								fkFileSaver.launch(suggestedName = filename, defaultExtension = null)
							}
						}
					},
					enabled = status?.mediaAttachments[pager.currentPage]?.type != null
				) {
					Icon(painterResource(Res.drawable.icon_download_24px), null)
				}

				IconButton(
					onClick = { uriHandler.openUri(status?.mediaAttachments[pager.currentPage]?.url!!) },
					enabled = status?.mediaAttachments[pager.currentPage]?.url != null
				) {
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
