package site.remlit.snowdrop.util.annotatedString

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

/**
 * Creates a simple annotated string, unstyled, with your string input.
 *
 * @param string Text to show
 *
 * @since 0.0.3-alpha
 * */
@Deprecated(
	message = "Deprecated since 0.0.6-alpha. Better pre-existing solution was available.",
	replaceWith = ReplaceWith("AnnotatedString(string)")
)
fun simpleAnnotatedString(string: String): AnnotatedString =
	buildAnnotatedString {
		append(string)
		toAnnotatedString()
	}
