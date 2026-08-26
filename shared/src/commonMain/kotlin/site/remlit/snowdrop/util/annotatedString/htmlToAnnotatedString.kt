package site.remlit.snowdrop.util.annotatedString

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import be.digitalia.compose.htmlconverter.htmlToString
import site.remlit.snowdrop.ProfileRoute
import site.remlit.snowdrop.component.defaultEmojiSize
import site.remlit.snowdrop.model.Emoji
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalNavController

/**
 * HTML content to AnnotatedString processor.
 *
 * @param string Source string
 *
 * @param mentions List of mentions from the status model
 * @param emojis List of emojis
 * @param emojiSize Text size of emojis
 * @param simple If the text should be rendered simply (no styling)
 * @param showEmojiTooltips Whether to show emoji tooltips on long-press
 *
 * @since 0.0.5-alpha
 * */
@Composable
fun htmlToAnnotatedString(
	string: String,
	mentions: List<Status.Mention> = emptyList(),
	emojis: List<Emoji> = emptyList(),
	emojiSize: TextUnit = defaultEmojiSize,
	simple: Boolean = false,
	showEmojiTooltips: Boolean = true
): Pair<AnnotatedString, Map<String, InlineTextContent>> {
	val uriHandler = LocalUriHandler.current
	val navHandler = LocalNavController.current
	val theme = MaterialTheme.colorScheme

	// todo: hashtags
	val linkListener = LinkInteractionListener { link ->
		if (link is LinkAnnotation.Url) {
			val mention = mentions.firstOrNull { m -> m.url == link.url }

			if (mention != null) navHandler.navigate(ProfileRoute(mention.id))
			else uriHandler.openUri(link.url)
		}
	}

	val mappedEmojis = mapEmojisToInlineTextContent(emojis, emojiSize, showEmojiTooltips)
	return remember(string, emojis) {
		htmlToAnnotatedString(
			if (simple) htmlToString(string) else string,
			style = HtmlStyle.DEFAULT.copy(
				textLinkStyles = TextLinkStyles(
					style = SpanStyle(color = theme.primary, textDecoration = TextDecoration.Underline)
				)
			),
			linkInteractionListener = linkListener
		).withEmojis(mappedEmojis)
	} to mappedEmojis
}
