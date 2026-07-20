package site.remlit.snowdrop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.model.Account
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_broken_image_24px

const val bigAvatarSize = 84
const val avatarSize = 48
const val smallAvatarSize = 36
const val smallerAvatarSize = 24

const val bigAvatarRadius = 20
const val avatarRadius = 15
const val smallAvatarRadius = 10
const val smallerAvatarRadius = 8

/**
 * Avatar component.
 *
 * Only use one of big/small/smaller, not more than one.
 *
 * @param account Account data
 * @param big If avatar should be big
 * @param small If avatar should be small
 * @param smaller If avatar should be smaller
 * */
@Composable
fun Avatar(
	account: Account,
	big: Boolean = false,
	small: Boolean = false,
	smaller: Boolean = false
) {
	val size = if (big) bigAvatarSize.dp
		else if (small) smallAvatarSize.dp
		else if (smaller) smallerAvatarSize.dp
		else avatarSize.dp
	val radius = if (big) bigAvatarRadius.dp
		else if (small) smallAvatarRadius.dp
		else if (smaller) smallerAvatarRadius.dp
		else avatarRadius.dp

	@Composable
	fun fallback(fail: Boolean = false) {
		Box(
			modifier = Modifier.clip(RoundedCornerShape(radius))
				.background(MaterialTheme.colorScheme.surfaceContainerHigh)
				.height(size)
				.width(size),
			contentAlignment = Alignment.Center
		) {
			if (fail)
				Icon(
					painterResource(Res.drawable.icon_broken_image_24px),
					null,
					tint = MaterialTheme.colorScheme.onSurface,
					modifier = Modifier.alpha(0.5f)
				)
		}
	}

	if (account.avatar != null) {
		KamelImage(
			resource = { asyncPainterResource(account.avatarStatic ?: account.avatar) },
			contentDescription = account.avatarDescription,
			contentScale = ContentScale.Crop,
			onLoading = { fallback() },
			onFailure = { fallback(fail = true) },
			modifier = Modifier.clip(RoundedCornerShape(radius))
				.height(size)
				.width(size),
		)
	} else fallback()
}
