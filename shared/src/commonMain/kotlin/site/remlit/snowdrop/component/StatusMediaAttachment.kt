package site.remlit.snowdrop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.github.panpf.zoomimage.ZoomImage
import com.github.panpf.zoomimage.compose.rememberZoomState
import com.github.panpf.zoomimage.compose.zoom.ScrollBarSpec
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.annotatedString.simpleAnnotatedString
import site.remlit.snowdrop.util.translation
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_open_in_new_24px
import snowdrop.shared.generated.resources.open_in_browser
import snowdrop.shared.generated.resources.unknown_media_type_x

/**
 * Component to handle rendering of multiple types of media
 * attachments that may come on a status. For use in the Status
 * component and also the StatusMediaAttachmentView.
 *
 * Supports a card-like style with [includeFallback] or a minimally
 * styled version without.
 *
 * @param attachment Media attachment to render
 * @param includeFallback If fallback/background card should be shown
 * @param modifier Modifier for the wrapping box of the media
 * @param onClick Action to occur when clicking attachment
 *
 * @since 0.0.4-alpha
 * */
@Composable
fun StatusMediaAttachment(
	attachment: Status.MediaAttachment,
	includeFallback: Boolean,
	supportZoomGestures: Boolean = false, // todo: implement
	modifier: Modifier = Modifier,
	onClick: () -> Unit = {},
	onZoom: () -> Unit = {},
) {
	val uriHandler = LocalUriHandler.current
	val interactionSource = remember { MutableInteractionSource() }

	var itemModifier: Modifier = Modifier

	Box(
		modifier = modifier.let {
			if (includeFallback) it.clip(RoundedCornerShape(10.dp))
			else it
		}.clickable(
			onClick = onClick,
			interactionSource = interactionSource,
			indication = if (includeFallback) ripple() else null
		)
	) {
		if (includeFallback) {
			// so it doesn't overflow the fallback/background card
			itemModifier = itemModifier.clip(RoundedCornerShape(10.dp))

			Box(
				modifier = Modifier.clip(RoundedCornerShape(10.dp))
					.background(MaterialTheme.colorScheme.surfaceContainerHigh)
					.height(200.dp)
					.fillMaxWidth()
			)
		}

		when (val type = attachment.type.split("/").first()) {
			"image" -> if (supportZoomGestures) {
				val zoomState = rememberZoomState()

				when (val res = asyncPainterResource(attachment.url)) {
					is Resource.Success -> {
						ZoomImage(
							painter = res.value,
							contentDescription = attachment.description,
							modifier = Modifier.fillMaxSize(),
							zoomState = zoomState,
							scrollBar = ScrollBarSpec(size = 0.dp)
						)
					}
					else -> {}
				}
			} else {
				KamelImage(
					resource = { asyncPainterResource(attachment.url) },
					contentDescription = attachment.description,
					contentScale = ContentScale.Fit,
					modifier = itemModifier.fillMaxWidth(),
				)
			}

			else -> {
				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center,
					modifier = Modifier.fillMaxSize()
				) {
					Text(translation(
						Res.string.unknown_media_type_x,
						mapOf("type" to simpleAnnotatedString(type))
					))
					TextButton(onClick = { uriHandler.openUri(attachment.url) }) {
						Icon(painterResource(Res.drawable.icon_open_in_new_24px), null)
						Spacer(Modifier.size(ButtonDefaults.IconSpacing))
						Text(translation(Res.string.open_in_browser))
					}
				}
			}
		}
	}
}
