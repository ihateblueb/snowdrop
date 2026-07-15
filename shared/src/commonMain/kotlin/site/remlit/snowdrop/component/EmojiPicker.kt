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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.bottomNavEnterAnimation
import site.remlit.snowdrop.bottomNavExitAnimation
import site.remlit.snowdrop.model.Emoji
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.cache.fetchEmojis
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_keyboard_arrow_down_24px
import snowdrop.shared.generated.resources.icon_keyboard_arrow_up_24px
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
	AnimatedVisibility(
		visible = visible,
		enter = bottomNavEnterAnimation,
		exit = bottomNavExitAnimation
	) {
		val emojis by remember { fetchEmojis() }.collectAsStateWithLifecycle(emptyList())

		val categorized = mutableMapOf<String, List<Emoji>>()

		emojis.forEach {
			val category = it.category ?: stringResource(Res.string.uncategorized)
			categorized[category] = categorized.getOrElse(category) { mutableListOf() }.plus(it)
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
			onDismissRequest = onDismiss
		) {
			Column(
				modifier = Modifier.fillMaxSize()
			) {
				LazyColumn(
					modifier = Modifier.weight(1f)
				) {
					categorized.forEach { (category, emojis) ->
						item {
							Row(
								modifier = Modifier.clickable(onClick = { toggleCategory(category) })
									.padding(10.dp)
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
								visible = !(categoryVisibility[category] ?: true),
								enter = expandVertically(),
								exit = shrinkVertically()
							) {
								FlowRow(modifier = Modifier.fillMaxWidth()) {
									emojis.forEach {
										Box(
											modifier = Modifier.clickable(onClick = { onSelectEmoji(it) })
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
