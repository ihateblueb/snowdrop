package site.remlit.snowdrop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.util.extension.toPixels
import site.remlit.snowdrop.util.extension.toPixelsRounded

const val bigAvatarSize = 84
const val avatarSize = 48
const val smallAvatarSize = 36
const val smallerAvatarSize = 24

const val bigAvatarRadius = 20
const val avatarRadius = 15
const val smallAvatarRadius = 10
const val smallerAvatarRadius = 8

@Composable
fun Avatar(
	account: Account,
	big: Boolean = false,
	small: Boolean = false,
	smaller: Boolean = false
) {
	val context = LocalPlatformContext.current

	val size = if (big) bigAvatarSize.dp
		else if (small) smallAvatarSize.dp
		else if (smaller) smallerAvatarSize.dp
		else avatarSize.dp
	val radius = if (big) bigAvatarRadius.dp
		else if (small) smallAvatarRadius.dp
		else if (smaller) smallerAvatarRadius.dp
		else avatarRadius.dp

	var isLoading by remember { mutableStateOf(true) }

	@Composable
	fun fallback() {
		Box(
			modifier = Modifier.clip(RoundedCornerShape(radius))
				.background(MaterialTheme.colorScheme.surfaceContainerHigh)
				.height(size)
				.width(size)
		)
	}

	if (account.avatar != null) {
		Box {
			AsyncImage(
				model = ImageRequest.Builder(context).data(account.avatarStatic ?: account.avatar)
					.size(size.toPixelsRounded())
					.build(),
				contentDescription = account.avatarDescription,
				contentScale = ContentScale.Crop,
				onSuccess = { isLoading = false },
				modifier = Modifier.clip(RoundedCornerShape(radius))
					.height(size)
					.width(size),
			)
			if (isLoading) fallback()
		}
	} else fallback()
}
