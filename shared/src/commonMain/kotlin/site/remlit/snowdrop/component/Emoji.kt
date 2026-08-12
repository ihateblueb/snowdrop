package site.remlit.snowdrop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import site.remlit.snowdrop.model.Emoji

const val emojiSize = 24
const val mediumEmojiSize = 36
const val bigEmojiSize = 40

/**
 * Emoji component.
 *
 * @param emoji Emoji data
 * @param big If the emoji should be big
 *
 * @since 0.0.2-alpha
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Emoji(
	emoji: Emoji,
	fill: Boolean = false,
	big: Boolean = false,

	showTooltip: Boolean = true
) {
	val size = if (big) bigEmojiSize.dp else emojiSize.dp

	fun Modifier.emojiSize() = this.let {
		if (fill) it.fillMaxSize()
		else it.size(size)
	}

	@Composable
	fun fallback() {
		Box(
			modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
				.emojiSize()
		)
	}

	@Composable
	fun renderContent() {
		// todo: reduced motion should make this use emoji.staticUrl
		KamelImage(
			resource = { asyncPainterResource(emoji.url) },
			contentDescription = emoji.shortcode,
			contentScale = ContentScale.Fit,
			onLoading = { fallback() },
			onFailure = { fallback() },
			modifier = Modifier.emojiSize(),
		)
	}

	if (showTooltip) {
		val tooltipState = rememberTooltipState()
		val positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
			TooltipAnchorPosition.Above
		)

		TooltipBox(
			modifier = Modifier,
			positionProvider = positionProvider,
			state = tooltipState,
			tooltip = { PlainTooltip { Text(":${emoji.shortcode}:") } },
		) { renderContent() }
	} else renderContent()
}
