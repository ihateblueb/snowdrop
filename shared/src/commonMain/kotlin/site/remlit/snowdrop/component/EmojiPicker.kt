package site.remlit.snowdrop.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.bottomNavEnterAnimation
import site.remlit.snowdrop.bottomNavExitAnimation
import site.remlit.snowdrop.model.Emoji
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.cache.fetchEmojis
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.log.debug
import site.remlit.snowdrop.util.settings
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_keyboard_arrow_down_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_up_24px
import snowdrop.shared.generated.resources.icon_search_24px
import snowdrop.shared.generated.resources.recently_used
import snowdrop.shared.generated.resources.search
import snowdrop.shared.generated.resources.uncategorized
import kotlin.collections.forEach

/**
 * Emoji picker component. Make sure this is lazy loaded, otherwise it will cause
 * extreme lag.
 *
 * @param visible If the bottom sheet should be visible
 * @param onDismiss Action to do after dismiss signal received
 * @param onSelectEmoji Action to do after an emoji is picked
 *
 * @since 0.0.2-alpha
 * */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalSettingsApi::class)
@Composable
fun EmojiPicker(
	visible: Boolean,
	onDismiss: () -> Unit,
	onSelectEmoji: (Emoji) -> Unit
) {
	val coroutineScope = rememberCoroutineScope()
	val sheetState = rememberBottomSheetState(SheetValue.Hidden)

	LaunchedEffect(visible) {
		if (visible) sheetState.show()
		else sheetState.hide()
	}

	AnimatedVisibility(
		visible = visible,
		enter = bottomNavEnterAnimation,
		exit = bottomNavExitAnimation
	) {
		var query by remember { mutableStateOf("") }
		val emojis by remember { fetchEmojis() }.collectAsStateWithLifecycle(emptyList())

		// these contain shortcodes, find them in emojis list and then only if they are found should they be shown
		val recentlyUsedShortcodes by remember { settings.getStringFlow("emojis_recently_used_${getCurrentAccountId()}", "") }
			.collectAsStateWithLifecycle("")

		val categorized = mutableMapOf<String, List<Emoji>>()

		val recentlyUsed = mutableListOf<Emoji>()
		recentlyUsedShortcodes.split(" ").forEach { r ->
			emojis.firstOrNull { it.shortcode == r }?.let { recentlyUsed.add(it) }
		}
		categorized[stringResource(Res.string.recently_used)] = recentlyUsed

		// sorted alphabetically
		emojis.sortedBy { it.category }
			.filter { it.shortcode.contains(query) }
			.forEach {
				val category = it.category ?: stringResource(Res.string.uncategorized)
				categorized[category] = categorized.getOrElse(category) { listOf() }.plus(it)
			}


		// category state nonsense
		val categoryVisibility = mutableStateMapOf<String, Boolean>()
		fun getHiddenKey(category: String) = "emojipicker_category_${category}_hidden"
		categorized.forEach { (key) ->
			categoryVisibility[key] = blockingSettings.getBoolean(getHiddenKey(key), false)
		}

		fun toggleCategory(category: String) {
			fun getCategoryVisibility(category: String): Boolean = categoryVisibility[category] ?: true

			categoryVisibility[category] = !getCategoryVisibility(category)
			blockingSettings.putBoolean(getHiddenKey(category), getCategoryVisibility(category))
		}

		ModalBottomSheet(
			sheetState = sheetState,
			onDismissRequest = onDismiss,
		) {
			val focusManager = LocalFocusManager.current

			if (sheetState.currentValue == SheetValue.PartiallyExpanded)
				focusManager.clearFocus()

			Column(
				modifier = Modifier.fillMaxSize()
			) {
				LazyColumn(
					modifier = Modifier.weight(1f)
				) {
					item {
						TextField(
							value = query,
							onValueChange = { query = it },
							maxLines = 1,

							placeholder = { Text(stringResource(Res.string.search)) },
							leadingIcon = { Icon(painterResource(Res.drawable.icon_search_24px), null) },

							modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 10.dp).fillMaxWidth()
								.onFocusChanged { if (it.hasFocus) coroutineScope.launch { sheetState.expand() } }
								.clip(RoundedCornerShape(100)),

							colors = TextFieldDefaults.colors(
								unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
								unfocusedIndicatorColor = Color(0x00000000),
								focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
								focusedIndicatorColor = Color(0x00000000),
							)
						)
					}

					categorized.forEach { (category, emojis) ->
						item {
							Row(
								modifier = Modifier.clickable(onClick = { toggleCategory(category) })
									.padding(vertical = 10.dp, horizontal = 15.dp)
									.fillMaxWidth(),
								verticalAlignment = Alignment.CenterVertically
							) {
								Text(
									category,
									fontWeight = FontWeight.Medium
								)

								Column(
									horizontalAlignment = Alignment.End,
									modifier = Modifier.fillMaxWidth()
								) {
									Column {
										if (categoryVisibility[category] ?: true) Icon(painterResource(Res.drawable.icon_keyboard_arrow_down_24px), null)
										else Icon(painterResource(Res.drawable.icon_keyboard_arrow_up_24px), null)
									}
								}
							}
						}
						item {
							AnimatedVisibility(
								visible = !(categoryVisibility[category] ?: true) || query.isNotBlank(),
								enter = expandVertically(),
								exit = shrinkVertically()
							) {
								FlowRow(modifier = Modifier.fillMaxWidth()) {
									emojis.forEach {
										Box(
											modifier = Modifier.clickable(onClick = {
												onSelectEmoji(it)

												coroutineScope.launch {
													val newRecentlyUsed = recentlyUsedShortcodes.split(" ")
														.toMutableList()

													newRecentlyUsed.add(0, it.shortcode)

													settings.putString(
														"emojis_recently_used_${getCurrentAccountId()}",
														newRecentlyUsed.distinct().take(20).joinToString(separator = " ")
													)
												}
											})
												.padding(5.dp)
										) {
											Emoji(it, big = true)
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
