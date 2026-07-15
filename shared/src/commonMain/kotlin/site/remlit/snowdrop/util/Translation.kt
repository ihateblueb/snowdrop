package site.remlit.snowdrop.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Preferred method for using translation strings. Supports rich text replacements with
 * annotated strings.
 *
 * Pass a placeholder for each "x" in the translation string key, otherwise the placeholder
 * will remail visible to the user.
 *
 * @param res Translation string resource
 * @param replacements Map of placeholder key to annotated string replacement, use
 * 					   simpleAnnotatedString method for simple text replacements.
 *
 * @return AnnotatedString with translation resolved and replacements processed
 * @since 0.0.3-alpha
 * */
@Composable
fun translation(
	res: StringResource,
	replacements: Map<String, AnnotatedString> = emptyMap()
): AnnotatedString {
	val placeholderRegex = """\{[a-zA-Z_-]*?\}""".toRegex()
	val source = stringResource(res)

	return buildAnnotatedString {
		// iterate over matches, get content before (if any), put content before (if any),
		// then put replacement. after loop: get remaining text
		var lastIndex = 0
		placeholderRegex.findAll(source).forEach { match ->
			if (lastIndex < match.range.first) append(source.substring(lastIndex, match.range.first))

			val key = match.value.removePrefix("{").removeSuffix("}")

			append(if (replacements.contains(key)) replacements[key] else "{$key}")

			lastIndex = match.range.last + 1
		}

		if (lastIndex < source.length) append(source.substring(lastIndex))

		toAnnotatedString()
	}
}
