package site.remlit.snowdrop.util.annotatedString

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.TextUnit
import site.remlit.snowdrop.component.Emoji
import site.remlit.snowdrop.component.defaultEmojiSize
import site.remlit.snowdrop.model.Emoji
import site.remlit.snowdrop.util.log.debug

/**
 * Map a list of emojis to InlineTextContent for use with [AnnotatedString.withEmojis].
 *
 * @param emojis List of emojis to map
 * @param emojiSize Size of emojis
 *
 * @return Mapped emojis
 * @since 0.0.5-alpha
 * */
fun mapEmojisToInlineTextContent(
	emojis: List<Emoji> = emptyList(),
	emojiSize: TextUnit = defaultEmojiSize,
): Map<String, InlineTextContent> {
	val mappedEmojis = mutableMapOf<String, InlineTextContent>()
	emojis.forEach { emoji ->
		mappedEmojis[":${emoji.shortcode}:"] = InlineTextContent(
			placeholder = Placeholder(
				width = emojiSize,
				height = emojiSize,
				placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
			)
		) { Emoji(emoji, fill = true) }
	}
	debug { "(mapEmojisToInlineTextContent) mappedEmojis $mappedEmojis" }
	return mappedEmojis
}

/**
 * Replace emoji shortcodes with Emoji component.
 *
 * @param mappedEmojis Emojis mapped with [mapEmojisToInlineTextContent]
 *
 * @return AnnotatedString decorated with emojis
 * @since 0.0.5-alpha
 * */
fun AnnotatedString.withEmojis(
	mappedEmojis: Map<String, InlineTextContent>
): AnnotatedString {
	val emojiRegex = mappedEmojis.keys.joinToString("|")
	debug { "(withEmojis) emojiRegex $emojiRegex" }

	return this.let {
		if (emojiRegex.isNotBlank()) {
			debug { "(withEmojis) let applying" }
			it.replaceAnnotated(emojiRegex.toRegex()) { match ->
				appendInlineContent(match.value, match.value)
			}
		}
		else it
	}
}
