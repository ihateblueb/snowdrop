package site.remlit.snowdrop.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.style.TextOverflow
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import site.remlit.snowdrop.ProfileRoute
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalNavController

@Composable
fun HtmlContent(
	string: String,
	mentions: List<Status.Mention> = emptyList(),
	maxLines: Int = Int.MAX_VALUE
) {
	val uriHandler = LocalUriHandler.current
	val navHandler = LocalNavController.current

	val linkListener = LinkInteractionListener { link ->
		if (link is LinkAnnotation.Url) {
			val mention = mentions.firstOrNull { m -> m.url == link.url }

			if (mention != null) navHandler.navigate(ProfileRoute(mention.id))
			else uriHandler.openUri(link.url)
		}
	}

	Text(
		maxLines = maxLines,
		overflow = TextOverflow.Ellipsis,
		text = remember(string) {
			htmlToAnnotatedString(string, linkInteractionListener = linkListener)
		}
	)
}