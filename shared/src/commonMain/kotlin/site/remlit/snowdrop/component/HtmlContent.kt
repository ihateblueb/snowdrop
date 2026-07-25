package site.remlit.snowdrop.component

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import be.digitalia.compose.htmlconverter.htmlToString
import site.remlit.snowdrop.ProfileRoute
import site.remlit.snowdrop.model.Emoji
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.annotatedString.replaceAnnotated

/**
 * HTML content element. Will render HTML, handle mention links, emojis,
 * and other rich content.
 *
 * @param string Source string
 * @param modifier Modifier for text
 * @param mentions List of mentions from the status model
 * @param emojis List of emojis
 * @param maxLines Maximum amount of lines that the text element should allow
 *
 * @since 0.0.1-alpha
 * */
@Composable
fun HtmlContent(
	string: String,
	modifier: Modifier = Modifier,
	mentions: List<Status.Mention> = emptyList(),
	emojis: List<Emoji> = emptyList(),
	emojiSize: TextUnit = 1.2.em,
	fontWeight: FontWeight = FontWeight.Normal,
	fontSize:  TextUnit = TextUnit.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
	simple: Boolean = false
) {
	// todo: rewrite some of this to be prettier

	val uriHandler = LocalUriHandler.current
	val navHandler = LocalNavController.current

	val mappedEmojis = mutableMapOf<String, InlineTextContent>()
	emojis.forEach { emoji ->
		mappedEmojis[":${emoji.shortcode}:"] = InlineTextContent(
			placeholder = Placeholder(
				width = emojiSize,
				height = emojiSize,
				placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
			)
		) {
			Emoji(emoji, fill = true)
		}
	}

	val linkListener = LinkInteractionListener { link ->
		if (link is LinkAnnotation.Url) {
			val mention = mentions.firstOrNull { m -> m.url == link.url }

			if (mention != null) navHandler.navigate(ProfileRoute(mention.id))
			else uriHandler.openUri(link.url)
		}
	}

	val emojiRegex = remember { mappedEmojis.keys.joinToString("|") }
	val text = remember(string, emojis) {
		htmlToAnnotatedString(
			if (simple) htmlToString(string) else string,
			linkInteractionListener = linkListener
		).let {
			if (emojiRegex.isNotBlank()) it
				.replaceAnnotated(emojiRegex.toRegex()) { match ->
					appendInlineContent(match.value, match.value)
				}
			else it
		}
	}

	Text(
		text = text,
		modifier = modifier,
		fontWeight = fontWeight,
		fontSize = fontSize,
		overflow = TextOverflow.Ellipsis,
		maxLines = maxLines,
		inlineContent = mappedEmojis
	)
}
