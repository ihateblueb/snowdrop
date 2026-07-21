package site.remlit.snowdrop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import site.remlit.snowdrop.model.Emoji

const val emojiSize = 20
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
	big: Boolean = false
) {
	val size = if (big) bigEmojiSize.dp else emojiSize.dp

	@Composable
	fun fallback() {
		Box(
			modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
				.height(size)
				.width(size)
		)
	}

	KamelImage(
		resource = { asyncPainterResource(emoji.staticUrl ?: emoji.url) },
		contentDescription = emoji.shortcode,
		contentScale = ContentScale.Fit,
		onLoading = { fallback() },
		modifier = Modifier.height(size)
			.width(size),
	)
}
