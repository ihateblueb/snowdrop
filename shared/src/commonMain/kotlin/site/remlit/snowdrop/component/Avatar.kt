package site.remlit.snowdrop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import site.remlit.snowdrop.model.User

const val bigAvatarSize = 84
const val avatarSize = 48

@Composable
fun Avatar(user: User, big: Boolean = false) {

	val size = if (big) bigAvatarSize.dp else avatarSize.dp
	val radius = if (big) 20.dp else 15.dp

	@Composable
	fun fallback() {
		Box(
			modifier = Modifier.clip(RoundedCornerShape(radius))
				.background(MaterialTheme.colorScheme.surfaceContainerHigh)
				.height(size)
				.width(size)
		)
	}

	if (user.avatar != null) {
		KamelImage(
			{ asyncPainterResource(user.avatarStatic!!) },
			"Profile",
			onLoading = { fallback() },
			modifier = Modifier.clip(RoundedCornerShape(radius))
				.height(size)
				.width(size)
		)
	} else fallback()
}