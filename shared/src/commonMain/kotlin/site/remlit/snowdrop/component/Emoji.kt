package site.remlit.snowdrop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
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
@Composable
fun Emoji(
	emoji: Emoji,
	fill: Boolean = false,
	big: Boolean = false
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

	// todo: reduced motion should make this use emoji.staticUrl
	KamelImage(
		resource = { asyncPainterResource(emoji.url) },
		contentDescription = emoji.shortcode,
		contentScale = ContentScale.Fit,
		onLoading = { fallback() },
		modifier = Modifier.emojiSize(),
	)
}
