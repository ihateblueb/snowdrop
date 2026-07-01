package site.remlit.snowdrop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import site.remlit.snowdrop.model.Emoji
import site.remlit.snowdrop.util.extension.toPixelsRounded

val emojiSize = 20
val bigEmojiSize = 40

@Composable
fun Emoji(
	emoji: Emoji,
	big: Boolean = false
) {
	val context = LocalPlatformContext.current

	var isLoading by remember { mutableStateOf(true) }

	val size = if (big) bigEmojiSize.dp else emojiSize.dp

	@Composable
	fun fallback() {
		Box(
			modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
				.height(size)
				.width(size)
		)
	}

	Box {
		AsyncImage(
			model = ImageRequest.Builder(context).data(emoji.staticUrl ?: emoji.url)
				.size(size.toPixelsRounded())
				.build(),
			contentDescription = emoji.shortcode,
			contentScale = ContentScale.Fit,
			onSuccess = { isLoading = false },
			modifier = Modifier.height(size)
				.width(size),
		)
		if (isLoading) fallback()
	}
}
