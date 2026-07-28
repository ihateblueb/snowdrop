package site.remlit.snowdrop.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import site.remlit.snowdrop.model.Platform
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.extension.isUnicodeEmoji
import site.remlit.snowdrop.util.getPlatform

@Composable
fun Reaction(reaction: Status.Reaction, showTooltip: Boolean = true) {
	@Composable
	fun renderContent() {
		val emoji = reaction.toEmoji()
		if (emoji != null) {
			Box(modifier = Modifier.size(20.dp)) {
				Emoji(emoji, fill = true, showTooltip = false)
			}
		} else when (getPlatform()) {
			Platform.ANDROID -> Text(reaction.name)
			Platform.IOS -> Text(
				reaction.name,
				fontSize = 18.sp,
				// todo: make this use apple color emoji explicitly, sometimes it uses a non-emoji glyph when one is available. distracting & ugly & unintended
			)
		}
	}

	if (showTooltip) {
		// yes this other tooltip is needed. reaction is pretty different from emoji
		TooltipBox(
			positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
				TooltipAnchorPosition.Above
			),
			tooltip = {
				if (isUnicodeEmoji(reaction.name)) PlainTooltip { Text(reaction.name) }
				else PlainTooltip { Text(":${reaction.name}:") }
			},
			state = rememberTooltipState()
		) { renderContent() }
	} else renderContent()
}
