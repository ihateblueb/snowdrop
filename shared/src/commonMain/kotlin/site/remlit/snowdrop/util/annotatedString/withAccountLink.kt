package site.remlit.snowdrop.util.annotatedString

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString.Builder
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import site.remlit.snowdrop.ProfileRoute
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.util.LocalNavController

/**
 * Creates a text link which navigates to the profile view of an account
 * and shows the account display name. Inherits color, but has weight set medium
 * and text decoration set to none.
 *
 * @param account Account to create link for
 *
 * @since 0.0.3-alpha
 * */
@Composable
fun Builder.withAccountLink(account: Account) {
	val navHandler = LocalNavController.current

	val linkListener = LinkInteractionListener { link ->
		if (link is LinkAnnotation.Clickable) {
			navHandler.navigate(ProfileRoute(link.tag))
		}
	}

	withLink(
		LinkAnnotation.Clickable(
			tag = account.id,
			linkInteractionListener = linkListener,
			styles = TextLinkStyles(
				style = SpanStyle(
					textDecoration = TextDecoration.None,
				)
			)
		)
	) {
		withStyle(
			style = SpanStyle(
				fontWeight = FontWeight.Medium
			)
		) {
			append(account.displayName())
		}
	}
}
