package site.remlit.snowdrop.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import io.github.vinceglb.filekit.mimeType
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.api.media.uploadMedia
import site.remlit.snowdrop.api.statuses.createStatus
import site.remlit.snowdrop.component.Avatar
import site.remlit.snowdrop.component.EmojiPicker
import site.remlit.snowdrop.component.MiniStatus
import site.remlit.snowdrop.component.StatusMediaAttachment
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.component.Visibility
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.model.request.CreateStatusRequest
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.LocalSnackbarController
import site.remlit.snowdrop.util.WarningColor25
import site.remlit.snowdrop.util.annotatedString.simpleAnnotatedString
import site.remlit.snowdrop.util.cache.fetchInstance
import site.remlit.snowdrop.util.cache.fetchStatusOrNull
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.util.getDefaultVisibilityBlocking
import site.remlit.snowdrop.util.translation
import site.remlit.snowdrop.util.vibrateConfirm
import site.remlit.snowdrop.util.vibrateError
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.alt_text
import snowdrop.shared.generated.resources.compose
import snowdrop.shared.generated.resources.content_warning
import snowdrop.shared.generated.resources.describe_important_elements_of_your_media
import snowdrop.shared.generated.resources.icon_add_24px
import snowdrop.shared.generated.resources.icon_attach_file_24px
import snowdrop.shared.generated.resources.icon_close_24px
import snowdrop.shared.generated.resources.icon_image_24
import snowdrop.shared.generated.resources.icon_mood_24px
import snowdrop.shared.generated.resources.icon_notes_24px
import snowdrop.shared.generated.resources.icon_send_24px
import snowdrop.shared.generated.resources.icon_warning_24px
import snowdrop.shared.generated.resources.icon_warning_filled_24px
import snowdrop.shared.generated.resources.reply
import snowdrop.shared.generated.resources.unknown_media_type_x
import snowdrop.shared.generated.resources.visibility_direct
import snowdrop.shared.generated.resources.visibility_direct_description
import snowdrop.shared.generated.resources.visibility_followers
import snowdrop.shared.generated.resources.visibility_followers_description
import snowdrop.shared.generated.resources.visibility_public
import snowdrop.shared.generated.resources.visibility_public_description
import snowdrop.shared.generated.resources.visibility_unlisted
import snowdrop.shared.generated.resources.visibility_unlisted_description
import snowdrop.shared.generated.resources.write_your_post_here
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeView(
	inReplyToId: String? = null,
	initialCw: String = "",
	initialContent: String = "",
	visibility: String? = null
) = ViewSurface {
	val navHandler = LocalNavController.current
	val snackbarHandler = LocalSnackbarController.current
	val haptics = LocalHapticFeedback.current
	val focusManager = LocalFocusManager.current
	val keyboardController = LocalSoftwareKeyboardController.current

	val instance by remember { fetchInstance(snackbarHandler) }
		.collectAsStateWithLifecycle(null)

	val focusRequester = remember { FocusRequester() }
	val coroutineScope = rememberCoroutineScope()

	val mediaAttachments = remember { mutableStateListOf<PlatformFile>() }
	val mediaAttachmentsAlt = remember { mutableStateListOf<String?>() }

	val filekitMode = remember { FileKitMode.Multiple(instance?.configuration?.statuses?.maxMediaAttachments ?: 4) }
	val galleryLauncher = rememberFilePickerLauncher(
		type = FileKitType.ImageAndVideo,
		mode = filekitMode
	) { files -> if (files != null) mediaAttachments.addAll(files) }
	//val fileLauncher = rememberDirectoryPickerLauncher { file -> if (file != null) mediaAttachments.add(file) }

	val currentAccount by remember { getCurrentAccountObjectFlow() }
		.collectAsStateWithLifecycle(null)

	var canSubmit by remember { mutableStateOf(false) }

	var visibilityDropdownOpen by remember { mutableStateOf(false) }
	var showCwField by remember { mutableStateOf(false) }
	var showEmojiPicker by remember { mutableStateOf(false) }
	var showAddAttachmentMenu by remember { mutableStateOf(false) }
	val textFieldState = rememberTextFieldState(initialContent)

	if (!initialCw.isBlank()) showCwField = true

	var cw by remember { mutableStateOf(initialCw) }
	var visibility by remember { mutableStateOf(visibility ?: getDefaultVisibilityBlocking()) }

	val replyTarget by remember { fetchStatusOrNull(inReplyToId, snackbarHandler) }
		.collectAsStateWithLifecycle(null)

	val maxChars = (instance?.maxTootChars ?: 500)
	val remainingChars = maxChars - (textFieldState.text.length + cw.length)


	var sendingDone by remember { mutableStateOf(false) }
	var isSending by remember { mutableStateOf(false) }
	var sendingTaskCount by remember { mutableStateOf(0) }
	var sendingTask by remember { mutableStateOf(0) }

	LaunchedEffect(sendingDone) { if (sendingDone) navHandler.popBackStack() }

	// can submit stuff
	canSubmit = !isSending && (textFieldState.text.isNotBlank() || mediaAttachments.isNotEmpty()) && remainingChars >= 0

	suspend fun sendPost() {
		isSending = true
		sendingTask = 0
		// the main createStatus, plus all media uploads
		sendingTaskCount = 1 + mediaAttachments.size

		fun <T> handleError(res: ApiResponse<T>) {
			res.handleError(snackbarHandler)
			vibrateError(haptics)
			isSending = false
			sendingTaskCount = 0
			sendingTask = 0
		}

		val uploadedMedia = mutableListOf<Status.MediaAttachment>()

		mediaAttachments.forEachIndexed { index, file ->
			val uploadRes = uploadMedia(file, mediaAttachmentsAlt.getOrNull(index))
			if (uploadRes.error || uploadRes.response == null) {
				handleError(uploadRes)
				return
			}
			uploadedMedia.add(uploadRes.response)

			sendingTask++
		}

		sendingTask++
		val res = createStatus(CreateStatusRequest(
			inReplyToId = inReplyToId,
			status = textFieldState.text as String?,
			spoilerText = cw,
			visibility = visibility,
			mediaIds = uploadedMedia.map { it.id }
		))

		if (res.error || res.response == null) {
			handleError(res)
			return
		}

		isSending = false
		sendingDone = true
		vibrateConfirm(haptics)
	}

	@Composable
	fun AttachmentPreview(
		detailedView: Boolean,
		file: PlatformFile
	) {
		var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
		LaunchedEffect(file) {
			coroutineScope.launch { bitmap = file.toImageBitmap() }
		}

		Box(
			modifier = Modifier.clip(RoundedCornerShape(10.dp))
				.background(MaterialTheme.colorScheme.surfaceContainerHigh)
		)
		if (bitmap != null) {
			when (val type = file.mimeType()?.primaryType) {
				"image" -> {
					Image(
						bitmap = bitmap!!,
						contentDescription = null,
						modifier = Modifier.fillMaxWidth().let {
							if (detailedView) it.heightIn(min = 100.dp, max = 350.dp)
							else it.fillMaxHeight()
						}.clip(RoundedCornerShape(10.dp))
					)
				}

				else -> {
					Column(
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.Center,
						modifier = Modifier.fillMaxSize()
					) {
						Text(translation(
							Res.string.unknown_media_type_x,
							mapOf("type" to simpleAnnotatedString(type ?: "unknown"))
						))
					}
				}
			}
		}
	}


	LaunchedEffect(Unit) {
		// kinda jank but there's no good way around this
		delay(50.milliseconds)
		focusRequester.requestFocus()
	}

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_close_24px), null)
			}
		},
		title = {
			if (inReplyToId != null) Text(stringResource(Res.string.reply))
			else Text(stringResource(Res.string.compose))
		},
		actions = {
			FilledTonalIconButton(
				onClick = { coroutineScope.launch { sendPost() } },
				enabled = canSubmit
			) {
				if (isSending) {
					if (sendingTaskCount > 0 && sendingTask > 0)
						CircularProgressIndicator(
							progress = { (sendingTask / sendingTaskCount).toFloat() },
							modifier = Modifier.padding(4.dp),
							strokeWidth = 4.dp
						)
					else CircularProgressIndicator(
						modifier = Modifier.padding(4.dp),
						strokeWidth = 4.dp
					)
				} else Icon(painterResource(Res.drawable.icon_send_24px), null)
			}
		}
	)

	Box(
		modifier = Modifier.fillMaxSize()
	) {
		Column(
			modifier = Modifier.fillMaxSize()
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
							currentAccount!!.displayName(),
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

							// Visibility picker
							DropdownMenu(
								expanded = visibilityDropdownOpen,
								onDismissRequest = { visibilityDropdownOpen = !visibilityDropdownOpen }
							) {
								@Composable
								fun VisibilityDropdownItem(vis: String) {
									DropdownMenuItem(
										leadingIcon = { Visibility(vis) },
										text = {
											Column(modifier = Modifier.padding(vertical = 5.dp)) {
												Text(
													when (vis) {
														"public" -> stringResource(Res.string.visibility_public)
														"unlisted" -> stringResource(Res.string.visibility_unlisted)
														"private" -> stringResource(Res.string.visibility_followers)
														else -> stringResource(Res.string.visibility_direct)
													},
													fontWeight = FontWeight.Medium
												)
												Text(
													when (vis) {
														"public" -> stringResource(Res.string.visibility_public_description)
														"unlisted" -> stringResource(Res.string.visibility_unlisted_description)
														"private" -> stringResource(Res.string.visibility_followers_description)
														else -> stringResource(Res.string.visibility_direct_description)
													},
													fontSize = 13.sp
												)
											}
										},
										onClick = {
											visibility = vis
											visibilityDropdownOpen = !visibilityDropdownOpen
										},
										modifier = if (visibility == vis)
											Modifier.background(MaterialTheme.colorScheme.primaryContainer)
										else Modifier,
										colors = if (visibility == vis) MenuDefaults.itemColors(
											leadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
											textColor = MaterialTheme.colorScheme.onPrimaryContainer,
										) else MenuDefaults.itemColors()
									)
								}

								// todo: do minimum visibility based on the view's visibility parameter
								VisibilityDropdownItem("public")
								VisibilityDropdownItem("unlisted")
								VisibilityDropdownItem("private")
								VisibilityDropdownItem("direct")
							}
						}
					}
				}

				if (replyTarget != null)
					Column(
						modifier = Modifier.padding(horizontal = 10.dp)
					) {
						MiniStatus(replyTarget!!, showContentEvenIfCw = true)
					}


				Column(
					modifier = Modifier.fillMaxHeight().weight(1f),
					verticalArrangement = Arrangement.spacedBy(5.dp)
				) {
					AnimatedVisibility(
						visible = showCwField,
						enter = expandVertically(),
						exit = shrinkVertically()
					) {
						TextField(
							value = cw,
							onValueChange = { cw = it },
							placeholder = { Text(stringResource(Res.string.content_warning)) },
							modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp, top = 5.dp)
								.clip(RoundedCornerShape(10.dp)),
							maxLines = 1,
							colors = TextFieldDefaults.colors(
								unfocusedContainerColor = WarningColor25,
								unfocusedIndicatorColor = Color(0x00000000),
								focusedContainerColor = WarningColor25,
								focusedIndicatorColor = Color(0x00000000)
							)
						)
					}

					TextField(
						state = textFieldState,
						placeholder = { Text(stringResource(Res.string.write_your_post_here)) },
						// wtf was this used for?
						//onValueChange = { textFieldState.edit { i } = it },
						modifier = Modifier
							.focusRequester(focusRequester)
							.onFocusChanged {
								if (it.hasFocus) keyboardController?.show()
							}
							.fillMaxWidth()
							.fillMaxHeight(),
						colors = TextFieldDefaults.colors(
							unfocusedContainerColor = Color(0x00000000),
							unfocusedIndicatorColor = Color(0x00000000),
							focusedContainerColor = Color(0x00000000),
							focusedIndicatorColor = Color(0x00000000),
						)
					)
				}

				val altBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
				var altBottomSheetSelection by remember { mutableStateOf<Int?>(null) }
				if (altBottomSheetSelection != null) {
					ModalBottomSheet(
						sheetState = altBottomSheetState,
						onDismissRequest = { altBottomSheetSelection = null }
					) {
						Box(
							modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
						) {
							AttachmentPreview(true, mediaAttachments[altBottomSheetSelection!!])
						}

						OutlinedTextField(
							value = mediaAttachmentsAlt.getOrNull(altBottomSheetSelection!!) ?: "",
							onValueChange = {
								// for some reason [0] for setting doesn't work when list is empty
								if (mediaAttachmentsAlt.getOrNull(altBottomSheetSelection!!) == null)
									mediaAttachmentsAlt.add(altBottomSheetSelection!!, it)
								else mediaAttachmentsAlt[altBottomSheetSelection!!] = it
							},
							label = { Text(stringResource(Res.string.alt_text)) },
							placeholder = { Text(stringResource(Res.string.describe_important_elements_of_your_media)) },
							minLines = 4,
							modifier = Modifier.padding(10.dp).fillMaxWidth()
						)
					}
				}

				/*
				 * Media attachments and stuff
				 * */
				AnimatedVisibility(
					visible = mediaAttachments.isNotEmpty()
				) {
					LazyRow(
						contentPadding = PaddingValues(15.dp),
						horizontalArrangement = Arrangement.spacedBy(10.dp)
					) {
						itemsIndexed(mediaAttachments) { index, item ->
							Box(
								modifier = Modifier.clip(RoundedCornerShape(10.dp)),
								contentAlignment = Alignment.TopEnd
							) {
								Box(
									modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
										.size(250.dp)
								) {
									AttachmentPreview(false, item)
								}

								Row(
									horizontalArrangement = Arrangement.spacedBy(5.dp)
								) {
									IconButton(onClick = { altBottomSheetSelection = index }) {
										Icon(painterResource(Res.drawable.icon_notes_24px), null)
									}

									IconButton(onClick = {
										mediaAttachments.removeAt(index)
										if (mediaAttachmentsAlt.getOrNull(index) != null) mediaAttachmentsAlt.removeAt(index)
									}) {
										Icon(painterResource(Res.drawable.icon_close_24px), null)
									}
								}
							}
						}
					}
				}

				/*
				* Footer
				* */
				Row(
					modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
						.padding(all = 5.dp)
						.fillMaxWidth()
						.imePadding(),
					verticalAlignment = Alignment.CenterVertically
				) {
					// todo: translate contentDescription
					if (showCwField) {
						IconButton(
							onClick = { showCwField = !showCwField },
							modifier = Modifier.semantics { contentDescription = "Show content warning field" }
						) {
							Icon(painterResource(Res.drawable.icon_warning_filled_24px), null)
						}
					} else {
						IconButton(
							onClick = { showCwField = !showCwField },
							modifier = Modifier.semantics { contentDescription = "Hide content warning field" }
						) {
							Icon(painterResource(Res.drawable.icon_warning_24px), null)
						}
					}

					IconButton(
						onClick = { showEmojiPicker = !showEmojiPicker; focusManager.clearFocus() },
						modifier = Modifier.semantics { contentDescription = "Add emoji" }
					) {
						Icon(painterResource(Res.drawable.icon_mood_24px), null)
					}

					DropdownMenu(
						expanded = showAddAttachmentMenu,
						onDismissRequest = { showAddAttachmentMenu = false }
					) {
						DropdownMenuItem(
							leadingIcon = { Icon(painterResource(Res.drawable.icon_image_24), null) },
							text = { Text("Add photo or video") },
							onClick = { galleryLauncher.launch(); showAddAttachmentMenu = false }
						)
						DropdownMenuItem(
							leadingIcon = { Icon(painterResource(Res.drawable.icon_attach_file_24px), null) },
							text = { Text("Add file") },
							onClick = { /* fileLauncher.launch() */; showAddAttachmentMenu = false }
						)
					}

					IconButton(
						onClick = { showAddAttachmentMenu = !showAddAttachmentMenu; focusManager.clearFocus() },
						modifier = Modifier.semantics { contentDescription = "Add attachment" }
					) {
						Icon(painterResource(Res.drawable.icon_add_24px), null)
					}

					// End
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.End
					) {
						Row(
							modifier = Modifier.padding(end = 5.dp),
							horizontalArrangement = Arrangement.spacedBy(5.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								"$remainingChars",
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)
						}
					}
				}
			}
		}

		EmojiPicker(
			visible = showEmojiPicker,
			onDismiss = { showEmojiPicker = !showEmojiPicker },
			onSelectEmoji = { textFieldState.edit { insert(textFieldState.selection.start, ":${it.shortcode}:")} }
		)
	}
}
