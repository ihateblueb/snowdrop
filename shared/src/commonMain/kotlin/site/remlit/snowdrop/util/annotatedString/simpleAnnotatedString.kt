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
fun simpleAnnotatedString(string: String): AnnotatedString =
	buildAnnotatedString {
		append(string)
		toAnnotatedString()
	}
