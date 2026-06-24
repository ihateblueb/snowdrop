package site.remlit.snowdrop.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import co.touchlab.kermit.Logger
import site.remlit.snowdrop.Profile
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalNavController

@Composable
fun HtmlContent(
	string: String,
	mentions: List<Status.Mention> = emptyList()
) {
	val uriHandler = LocalUriHandler.current
	val navHandler = LocalNavController.current

	val linkListener = LinkInteractionListener { link ->
		if (link is LinkAnnotation.Url) {
			val mention = mentions.firstOrNull { m -> m.url == link.url }

			if (mention != null) navHandler.navigate(Profile(mention.id))
			else uriHandler.openUri(link.url)
		}
	}

	Text(text = remember(string) {
		htmlToAnnotatedString(string, linkInteractionListener = linkListener)
	})
}