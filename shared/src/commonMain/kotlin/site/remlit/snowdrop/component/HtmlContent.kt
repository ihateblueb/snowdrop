package site.remlit.snowdrop.component

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import co.touchlab.kermit.Logger
import site.remlit.snowdrop.ProfileRoute
import site.remlit.snowdrop.model.Emoji
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalNavController

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
	maxLines: Int = Int.MAX_VALUE
) {
	val uriHandler = LocalUriHandler.current
	val navHandler = LocalNavController.current

	val mappedEmojis = mutableMapOf<String, InlineTextContent>()
	emojis.forEach { emoji ->
		mappedEmojis[":${emoji.shortcode}:"] = InlineTextContent(
			placeholder = Placeholder(
				width = 20.sp,
				height = 20.sp,
				placeholderVerticalAlign = PlaceholderVerticalAlign.Center
			)
		) {
			Emoji(emoji)
		}
	}

	val linkListener = LinkInteractionListener { link ->
		if (link is LinkAnnotation.Url) {
			val mention = mentions.firstOrNull { m -> m.url == link.url }

			if (mention != null) navHandler.navigate(ProfileRoute(mention.id))
			else uriHandler.openUri(link.url)
		}
	}

	Logger.d { "mappedEmojis $mappedEmojis" }

	Text(
		maxLines = maxLines,
		overflow = TextOverflow.Ellipsis,
		modifier = modifier,
		text = remember(string) {
			htmlToAnnotatedString(
				string,
				linkInteractionListener = linkListener
			) // todo: add emoji thing
		},
		inlineContent = mappedEmojis
	)
}
