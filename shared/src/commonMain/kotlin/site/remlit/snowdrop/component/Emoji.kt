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

val emojiSize = 20.dp

@Composable
fun Emoji(emoji: Emoji) {
	@Composable
	fun fallback() {
		Box(
			modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
				.height(emojiSize)
				.width(emojiSize)
		)
	}

	KamelImage(
		{ asyncPainterResource(emoji.staticUrl) },
		emoji.shortcode,
		onLoading = { fallback() },
		modifier = Modifier
			.height(emojiSize)
			.width(emojiSize),
		contentScale = ContentScale.Fit
	)
}
