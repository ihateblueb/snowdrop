package site.remlit.snowdrop.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemShapes
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetState
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import io.github.vinceglb.filekit.mimeType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.api.media.uploadMedia
import site.remlit.snowdrop.api.statuses.createStatus
import site.remlit.snowdrop.api.statuses.editStatus
import site.remlit.snowdrop.component.Avatar
import site.remlit.snowdrop.component.DatePickerModal
import site.remlit.snowdrop.component.EmojiPicker
import site.remlit.snowdrop.component.HtmlContent
import site.remlit.snowdrop.component.MiniStatus
import site.remlit.snowdrop.component.NavigationBackButton
import site.remlit.snowdrop.component.TimePickerModal
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.component.Visibility
import site.remlit.snowdrop.component.dropdown.PreparedDropdownMenu
import site.remlit.snowdrop.model.ApiResponse
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.model.request.CreateStatusRequest
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.LocalSnackbarController
import site.remlit.snowdrop.util.WarningColor25
import site.remlit.snowdrop.util.cache.fetchInstance
import site.remlit.snowdrop.util.cache.fetchStatusOrNull
import site.remlit.snowdrop.util.extension.getPreparedDropdownMenuItemShapes
import site.remlit.snowdrop.util.getCurrentAccountObjectFlow
import site.remlit.snowdrop.util.getDefaultVisibilityBlocking
import site.remlit.snowdrop.util.getFeature
import site.remlit.snowdrop.util.settings
import site.remlit.snowdrop.util.translation
import site.remlit.snowdrop.util.vibrateConfirm
import site.remlit.snowdrop.util.vibrateError
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.add_attachment
import snowdrop.shared.generated.resources.add_emoji
import snowdrop.shared.generated.resources.add_file
import snowdrop.shared.generated.resources.add_photo_or_video
import snowdrop.shared.generated.resources.alt_text
import snowdrop.shared.generated.resources.compose
import snowdrop.shared.generated.resources.content_warning_field_hide
import snowdrop.shared.generated.resources.content_warning_field_show
import snowdrop.shared.generated.resources.content_warning
import snowdrop.shared.generated.resources.describe_important_elements_of_your_media
import snowdrop.shared.generated.resources.edit
import snowdrop.shared.generated.resources.icon_access_time_24px
import snowdrop.shared.generated.resources.icon_access_time_filled_24px
import snowdrop.shared.generated.resources.icon_add_24px
import snowdrop.shared.generated.resources.icon_attach_file_24px
import snowdrop.shared.generated.resources.icon_close_24px
import snowdrop.shared.generated.resources.icon_image_24
import snowdrop.shared.generated.resources.icon_mood_24px
import snowdrop.shared.generated.resources.icon_notes_24px
import snowdrop.shared.generated.resources.icon_send_24px
import snowdrop.shared.generated.resources.icon_warning_24px
import snowdrop.shared.generated.resources.icon_warning_filled_24px
import snowdrop.shared.generated.resources.ok
import snowdrop.shared.generated.resources.post_verb
import snowdrop.shared.generated.resources.remaining_characters
import snowdrop.shared.generated.resources.reply
import snowdrop.shared.generated.resources.schedule_post
import snowdrop.shared.generated.resources.submit_scheduled_post
import snowdrop.shared.generated.resources.unknown_media_type_x
import snowdrop.shared.generated.resources.visibility_direct
import snowdrop.shared.generated.resources.visibility_direct_description
import snowdrop.shared.generated.resources.visibility_followers
import snowdrop.shared.generated.resources.visibility_followers_description
import snowdrop.shared.generated.resources.visibility_local
import snowdrop.shared.generated.resources.visibility_local_description
import snowdrop.shared.generated.resources.visibility_picker
import snowdrop.shared.generated.resources.visibility_public
import snowdrop.shared.generated.resources.visibility_public_description
import snowdrop.shared.generated.resources.visibility_unlisted
import snowdrop.shared.generated.resources.visibility_unlisted_description
import snowdrop.shared.generated.resources.write_your_post_here
import snowdrop.shared.generated.resources.you_cannot_schedule_a_post_in_the_past
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalSettingsApi::class)
@Composable
fun ComposeView(
	inReplyToId: String? = null,
	editingId: String? = null,
	initialCw: String = "",
	initialContent: String = "",
	visibility: String? = null,
	localOnly: Boolean? = null
) = ViewSurface {
	val navHandler = LocalNavController.current
	val snackbarHandler = LocalSnackbarController.current
	val haptics = LocalHapticFeedback.current
	val focusManager = LocalFocusManager.current
	val keyboardController = LocalSoftwareKeyboardController.current

	val instance by remember { fetchInstance(snackbarHandler) }
		.collectAsStateWithLifecycle(null)

	val textFocusRequester = remember { FocusRequester() }
	val cwFocusRequester = remember { FocusRequester() }
	val coroutineScope = rememberCoroutineScope()

	val mediaAttachments = remember { mutableStateListOf<PlatformFile>() }
	val mediaAttachmentsAlt = remember { mutableStateListOf<String?>() }

	val altBottomSheetState = rememberBottomSheetState(SheetValue.Hidden)
	var altBottomSheetSelection by remember { mutableStateOf<Int?>(null) }

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
	var showDatePicker by remember { mutableStateOf(false) }
	var showTimePicker by remember { mutableStateOf(false) }
	var showInvalidTimeAlert by remember { mutableStateOf(false) }
	val textFieldState = rememberTextFieldState(initialContent)
	val cwFieldState = rememberTextFieldState(initialCw)

	var scheduledDate: Long by remember { mutableStateOf(-1) }
	var scheduledTimeHour by remember { mutableStateOf(-1) }
	var scheduledTimeMinute by remember { mutableStateOf(-1) }
	var scheduledDateTimeIsSet by remember { mutableStateOf(false) }
	var scheduledDateTimeParsed by remember { mutableStateOf("") }

	if (initialCw.isNotBlank()) showCwField = true

	var visibility by remember { mutableStateOf(visibility ?: getDefaultVisibilityBlocking()) }
	var visibilityEnabled by remember { mutableStateOf(true) }

	var localOnly by remember { mutableStateOf(localOnly == true) }

	val replyTarget by remember { fetchStatusOrNull(inReplyToId, snackbarHandler) }
		.collectAsStateWithLifecycle(null)
	val editTarget by remember { fetchStatusOrNull(editingId, snackbarHandler) }
		.collectAsStateWithLifecycle(null)

	LaunchedEffect(editTarget) {
		if (editTarget != null) {
			visibility = editTarget!!.visibility ?: "direct"
			visibilityEnabled = false

			cwFieldState.clearText()
			cwFieldState.setTextAndPlaceCursorAtEnd(editTarget!!.spoilerText ?: "")

			textFieldState.clearText()
			textFieldState.setTextAndPlaceCursorAtEnd(editTarget!!.text ?: "")
		}
	}

	val maxChars = (instance?.maxTootChars ?: instance?.configuration?.statuses?.maxCharacters ?: 500)
	val remainingChars = maxChars - (textFieldState.text.length + cwFieldState.text.length)

	val swapPostButtonAndCharLimit by settings.getBooleanFlow("swap_post_button_and_char_limit", false)
		.collectAsStateWithLifecycle(false)

	var sendingDone by remember { mutableStateOf(false) }
	var isSending by remember { mutableStateOf(false) }

	LaunchedEffect(sendingDone) { if (sendingDone) navHandler.popBackStack() }

	// can submit stuff
	canSubmit = !isSending && (textFieldState.text.isNotBlank() || mediaAttachments.isNotEmpty()) && remainingChars >= 0

	suspend fun sendPost() {
		isSending = true

		fun <T> handleError(res: ApiResponse<T>) {
			res.handleError(snackbarHandler)
			vibrateError(haptics)
			isSending = false
		}

		val uploadedMedia = mutableListOf<Status.MediaAttachment>()

		mediaAttachments.forEachIndexed { index, file ->
			val uploadRes = uploadMedia(file, mediaAttachmentsAlt.getOrNull(index))
			if (uploadRes.error || uploadRes.response == null) {
				handleError(uploadRes)
				return
			}
			uploadedMedia.add(uploadRes.response)
		}

		val res = if (editingId != null) editStatus(editingId, CreateStatusRequest(
			status = textFieldState.text as String?,
			spoilerText = cwFieldState.text as String?
		)) else createStatus(CreateStatusRequest(
			inReplyToId = inReplyToId,
			status = textFieldState.text as String?,
			spoilerText = cwFieldState.text as String?,
			visibility = visibility,
			mediaIds = uploadedMedia.map { it.id },
			localOnly = localOnly,
			scheduledAt = if (!scheduledDateTimeIsSet) null else scheduledDateTimeParsed
		))

		if (res.error || res.response == null) {
			handleError(res)
			return
		}

		sendingDone = true
		vibrateConfirm(haptics)
	}

	@Composable
	fun PostButton() {
		val __translation = stringResource(if (scheduledDateTimeIsSet) Res.string.submit_scheduled_post else Res.string.post_verb)
		FilledTonalIconButton(
			onClick = { coroutineScope.launch { sendPost() } },
			enabled = canSubmit,
			modifier = Modifier.semantics { contentDescription =  __translation }
		) {
			if (isSending) {
				LoadingIndicator(
					modifier = Modifier.padding(2.dp)
				)
			} else if (!scheduledDateTimeIsSet) Icon(painterResource(Res.drawable.icon_send_24px), null)
			else Icon(painterResource(Res.drawable.icon_access_time_filled_24px), null)
		}
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
			modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
		)
		if (bitmap != null) {
			when (val type = file.mimeType()?.primaryType) {
				"image" -> {
					Image(
						bitmap = bitmap!!,
						contentDescription = null,
						modifier = Modifier.fillMaxWidth().let {
							if (detailedView) it.heightIn(min = 100.dp, max = 200.dp)
							else it.fillMaxHeight()
						}
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
							mapOf("type" to AnnotatedString(type ?: "unknown"))
						))
					}
				}
			}
		}
	}


	LaunchedEffect(Unit) {
		// kinda jank but there's no good way around this
		delay(50.milliseconds)
		textFocusRequester.requestFocus()
	}

	LaunchedEffect(showCwField) {
		if (showCwField) cwFocusRequester.requestFocus()
		else textFocusRequester.requestFocus()
	}

	Scaffold(
		topBar = {
			TopAppBar(
				navigationIcon = { NavigationBackButton(close = true) },
				title = {
					if (inReplyToId != null) Text(stringResource(Res.string.reply))
					else if (editingId != null) Text(stringResource(Res.string.edit))
					else Text(stringResource(Res.string.compose))
				},
				actions = {
					if (swapPostButtonAndCharLimit) {
						Row(
							modifier = Modifier.padding(end = 10.dp),
							horizontalArrangement = Arrangement.spacedBy(5.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							val __translation_remaining_characters = stringResource(Res.string.remaining_characters)
							Text(
								"$remainingChars",
								color = MaterialTheme.colorScheme.onSurfaceVariant,
								modifier = Modifier.semantics { contentDescription = __translation_remaining_characters }
							)
						}
					} else {
						PostButton()
					}
				}
			)
		},
		bottomBar = {
			HorizontalFloatingToolbar(
				expanded = false,
				modifier = Modifier.fillMaxWidth()
					.windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))
					.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically
				) {
					// can we make it so we can change the order of the actions?
					PreparedDropdownMenu(
						expanded = showAddAttachmentMenu,
						onDismissRequest = { showAddAttachmentMenu = false }
					) {
						DropdownMenuItem(
							leadingIcon = { Icon(painterResource(Res.drawable.icon_image_24), null) },
							text = { Text(stringResource(Res.string.add_photo_or_video)) },
							shape = MenuDefaults.leadingItemShape,
							onClick = { galleryLauncher.launch(); showAddAttachmentMenu = false }
						)
						DropdownMenuItem(
							leadingIcon = { Icon(painterResource(Res.drawable.icon_attach_file_24px), null) },
							text = { Text(stringResource(Res.string.add_file)) },
							shape = MenuDefaults.trailingItemShape,
							onClick = { /* fileLauncher.launch() */; coroutineScope.launch { snackbarHandler.showSnackbar("todo") }; showAddAttachmentMenu = false }
						)
					}

					val addAttachmentDescription = stringResource(Res.string.add_attachment)

					IconButton(
						onClick = { showAddAttachmentMenu = !showAddAttachmentMenu; focusManager.clearFocus() },
						modifier = Modifier.semantics { contentDescription = addAttachmentDescription }
					) {
						Icon(painterResource(Res.drawable.icon_add_24px), null)
					}

					val addEmojiDescription = stringResource(Res.string.add_emoji)

					IconButton(
						onClick = { showEmojiPicker = !showEmojiPicker; focusManager.clearFocus() },
						modifier = Modifier.semantics { contentDescription = addEmojiDescription }
					) {
						Icon(painterResource(Res.drawable.icon_mood_24px), null)
					}

					val __translate_showContentWarningFieldDescription = stringResource(Res.string.content_warning_field_show)
					val __translate_hideContentWarningFieldDescription = stringResource(Res.string.content_warning_field_hide)
					val __translation_schedulePost = stringResource(Res.string.schedule_post)

					// todo: translate contentDescription
					if (showCwField) {
						IconButton(
							onClick = { showCwField = !showCwField },
							modifier = Modifier.semantics { contentDescription = __translate_showContentWarningFieldDescription }
						) {
							Icon(painterResource(Res.drawable.icon_warning_filled_24px), null)
						}
					} else {
						IconButton(
							onClick = { showCwField = !showCwField },
							modifier = Modifier.semantics { contentDescription = __translate_hideContentWarningFieldDescription }
						) {
							Icon(painterResource(Res.drawable.icon_warning_24px), null)
						}
					}

					IconButton(
						onClick = { showDatePicker = true },
						modifier = Modifier.semantics { contentDescription = __translation_schedulePost }
					) {
						if (!scheduledDateTimeIsSet) {
							Icon(painterResource(Res.drawable.icon_access_time_24px), null)
						} else {
							Icon(painterResource(Res.drawable.icon_access_time_filled_24px), null)
						}
					}

					// End
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.End
					) {
						Row(
							modifier = if (!swapPostButtonAndCharLimit) Modifier.padding(end = 10.dp)
								else Modifier,
							horizontalArrangement = Arrangement.spacedBy(5.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							if (swapPostButtonAndCharLimit) {
								PostButton()
							} else {
								Text(
									"$remainingChars",
									color = MaterialTheme.colorScheme.onSurfaceVariant
								)
							}
						}
					}
				}
			}
		}
	) { paddingValues ->
		Box(
			modifier = Modifier.fillMaxSize()
				.padding(paddingValues)
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
							HtmlContent(
								currentAccount!!.displayName(),
								emojis = currentAccount!!.emojis,
								fontWeight = FontWeight.Medium,
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
								val __translation_visibility_picker = stringResource(Res.string.visibility_picker)
								TextButton(
									onClick = {
										visibilityDropdownOpen = !visibilityDropdownOpen
									},
									enabled = visibilityEnabled,
									modifier = Modifier.semantics { contentDescription = __translation_visibility_picker }
								) {
									Visibility(visibility, true, localOnly)
								}

								// Visibility picker
								PreparedDropdownMenu(
									expanded = visibilityDropdownOpen,
									onDismissRequest = { visibilityDropdownOpen = !visibilityDropdownOpen }
								) {
									var visibilities = listOf("public", "unlisted", "private", "direct")

									if (getFeature("local_visibility")) visibilities = visibilities.plus("local")

									@Composable
									fun VisibilityDropdownItem(vis: String, index: Int) {
										DropdownMenuItem(
											enabled = true,
											selected = visibility == vis,
											onClick = {
												visibility = vis
												visibilityDropdownOpen = !visibilityDropdownOpen
											},
											leadingIcon = { Visibility(vis) },
											colors = MenuDefaults.selectableItemColors(),
											shapes = visibilities.getPreparedDropdownMenuItemShapes(index),
											contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
											text = {
												Column(modifier = Modifier.padding(vertical = 5.dp)) {
													Text(
														when (vis) {
															"public" -> stringResource(Res.string.visibility_public)
															"unlisted" -> stringResource(Res.string.visibility_unlisted)
															"private" -> stringResource(Res.string.visibility_followers)
															"local" -> stringResource(Res.string.visibility_local)
															else -> stringResource(Res.string.visibility_direct)
														},
														fontWeight = FontWeight.Medium
													)
													Text(
														when (vis) {
															"public" -> stringResource(Res.string.visibility_public_description)
															"unlisted" -> stringResource(Res.string.visibility_unlisted_description)
															"private" -> stringResource(Res.string.visibility_followers_description)
															"local" -> stringResource(Res.string.visibility_local_description)
															else -> stringResource(Res.string.visibility_direct_description)
														},
														fontSize = 13.sp
													)
												}
											}
										)
									}

									// todo: do minimum visibility based on the view's visibility parameter
									visibilities.forEachIndexed { index, string -> VisibilityDropdownItem(string, index) }


									if (getFeature("local_only_toggle")) {
										HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))

										DropdownMenuItem(
											checked = false,
											onCheckedChange = { localOnly = !localOnly },
											leadingIcon = { Visibility("local") },
											colors = MenuDefaults.selectableItemColors(),
											shapes = MenuItemShapes(MenuDefaults.standaloneGroupShape, MenuDefaults.standaloneGroupShape),
											contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
											text = {
												Text(
													stringResource(Res.string.visibility_local),
													fontWeight = FontWeight.Medium
												)
											},
											trailingIcon = {
												Switch(checked = localOnly, onCheckedChange = {
													localOnly = !localOnly
												})
											}
										)
									}
								}
							}
						}
					}


					//<editor-fold name="Content">
					val contentScrollState = rememberScrollState()
					Column(
						modifier = Modifier.fillMaxHeight().weight(1f)
							.verticalScroll(contentScrollState),
						verticalArrangement = Arrangement.spacedBy(5.dp)
					) {
						if (replyTarget != null)
							Column(
								modifier = Modifier.padding(horizontal = 10.dp)
							) {
								MiniStatus(replyTarget!!, showContentEvenIfCw = true)
							}

						if (editTarget != null)
							Column(
								modifier = Modifier.padding(horizontal = 10.dp)
							) {
								MiniStatus(editTarget!!, showContentEvenIfCw = true)
							}

						AnimatedVisibility(
							visible = showCwField,
							enter = expandVertically(),
							exit = shrinkVertically()
						) {
							TextField(
								state = cwFieldState,
								placeholder = { Text(stringResource(Res.string.content_warning)) },
								modifier = Modifier
									.focusRequester(cwFocusRequester)
									.onFocusChanged {
										if (it.hasFocus) keyboardController?.show()
									}
									.fillMaxWidth()
									.padding(start = 10.dp, end = 10.dp, top = 5.dp)
									.clip(RoundedCornerShape(10.dp)),
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
							modifier = Modifier
								.focusRequester(textFocusRequester)
								.onFocusChanged {
									if (it.hasFocus) keyboardController?.show()
								}
								.fillMaxWidth()
								.fillMaxHeight()
								.heightIn(min = 100.dp),
							colors = TextFieldDefaults.colors(
								unfocusedContainerColor = Color(0x00000000),
								unfocusedIndicatorColor = Color(0x00000000),
								focusedContainerColor = Color(0x00000000),
								focusedIndicatorColor = Color(0x00000000),
							)
						)

						//<editor-fold name="Media, Attachments, and Alt Text Sheet">
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

										val iconButtonBg = if (isSystemInDarkTheme()) Color(0x40000000)
											else Color(0x40FFFFFF)

										Row(
											horizontalArrangement = Arrangement.spacedBy(5.dp)
										) {
											IconButton(
												onClick = { altBottomSheetSelection = index },
												colors = IconButtonDefaults.filledIconButtonColors(containerColor = iconButtonBg)
											) {
												Icon(painterResource(Res.drawable.icon_notes_24px), null)
											}

											IconButton(
												onClick = {
													mediaAttachments.removeAt(index)
													if (mediaAttachmentsAlt.getOrNull(index) != null) mediaAttachmentsAlt.removeAt(
														index
													)
												},
												colors = IconButtonDefaults.filledIconButtonColors(containerColor = iconButtonBg)
											) {
												Icon(painterResource(Res.drawable.icon_close_24px), null)
											}
										}
									}
								}
							}
						}
						//</editor-fold>
					}
					//</editor-fold>
				}
			}

			EmojiPicker(
				visible = showEmojiPicker,
				onDismiss = { showEmojiPicker = !showEmojiPicker },
				onSelectEmoji = { textFieldState.edit { insert(textFieldState.selection.start, ":${it.shortcode}:") } },
				onEnterUnicodeEmoji = {}
			)

			if (showDatePicker) {
				DatePickerModal(
					onConfirm = {
						if (it == null) return@DatePickerModal

						val currentTimeInstant = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
						if (it < currentTimeInstant.date.toEpochDays() * 86400000) {
							showInvalidTimeAlert = !showInvalidTimeAlert
							return@DatePickerModal
						}

						showDatePicker = false

						scheduledDate = it

						showTimePicker = true
					},
					onDismiss = {
						showDatePicker = false
					}
				)
			}

			if (showTimePicker) {
				TimePickerModal(
					onConfirm = { hour, minute ->
						//val currentTimeInstant = Clock.System.now()
						val scheduledTimeInstant = Instant.fromEpochMilliseconds(scheduledDate)

						// i can't figure out how to fix this so that's a todo
						//
						//if (scheduledDate < currentTimeInstant.toEpochMilliseconds()) {
						//	showInvalidTimeAlert = !showInvalidTimeAlert
						//	return@TimePickerModal
						//}

						showTimePicker = false

						// fuck you google. and jetbrains too honestly this library fucking sucks
						val offset = scheduledTimeInstant.offsetIn(TimeZone.currentSystemDefault())

						scheduledDateTimeParsed = scheduledTimeInstant
							.plus(hour, DateTimeUnit.HOUR)
							.plus(minute, DateTimeUnit.MINUTE)
							.plus(offset.totalSeconds * -1, DateTimeUnit.SECOND)
							.toString()

						scheduledTimeHour = hour
						scheduledTimeMinute = minute

						scheduledDateTimeIsSet = scheduledDate != (-1).toLong() &&
							scheduledTimeHour != -1 &&
							scheduledTimeMinute != -1
					},
					onDismiss = {
						showTimePicker = false
					}
				)
			}

			if (showInvalidTimeAlert) {
				AlertDialog(
					text = {
						Text(stringResource(Res.string.you_cannot_schedule_a_post_in_the_past))
					},
					onDismissRequest = {
						showInvalidTimeAlert = !showInvalidTimeAlert
					},
					confirmButton = {
						TextButton(
							onClick = { showInvalidTimeAlert = !showInvalidTimeAlert }
						) {
							Text(stringResource(Res.string.ok))
						}
					},
					properties = DialogProperties(
						dismissOnBackPress = true,
						dismissOnClickOutside = true
					)
				)
			}
		}

		if (altBottomSheetSelection != null) {
			ModalBottomSheet(
				sheetState = altBottomSheetState,
				onDismissRequest = {
					coroutineScope.launch {
						altBottomSheetState.hide()
						altBottomSheetSelection = null
					}
				}
			) {
				val selection = altBottomSheetSelection

				if (selection != null) {
					Box(
						modifier = Modifier.padding(
							start = 10.dp,
							end = 10.dp,
							bottom = 10.dp
						)
					) {
						AttachmentPreview(
							true,
							mediaAttachments[selection]
						)
					}

					OutlinedTextField(
						value = mediaAttachmentsAlt.getOrNull(selection) ?: "",
						onValueChange = {
							// for some reason [0] for setting doesn't work when list is empty
							if (mediaAttachmentsAlt.getOrNull(selection) == null)
								mediaAttachmentsAlt.add(selection, it)
							else mediaAttachmentsAlt[selection] = it
						},
						label = { Text(stringResource(Res.string.alt_text)) },
						placeholder = { Text(stringResource(Res.string.describe_important_elements_of_your_media)) },
						minLines = 4,
						modifier = Modifier.padding(10.dp).fillMaxWidth()
					)
				}
			}
		}
	}
}
