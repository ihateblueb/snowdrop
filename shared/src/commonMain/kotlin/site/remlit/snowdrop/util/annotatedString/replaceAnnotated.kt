package site.remlit.snowdrop.util.annotatedString

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

fun AnnotatedString.replaceAnnotated(
	regex: Regex,
	transform: AnnotatedString.Builder.(MatchResult) -> Unit,
): AnnotatedString {
	val source = this
	var offset = 0

	// \u2060 is for word join, adding the inline text component causes newlines if you don't join
	// it together. text glue.
	return buildAnnotatedString {
		regex.findAll(source.text).forEach { match ->
			if (match.range.first > offset) {
				append("${source.subSequence(offset, match.range.first)}\u2060")
			}

			transform(match)
			append("\u2060")
			offset = match.range.last + 1
		}

		if (offset < source.text.length) {
			append("\u2060${source.subSequence(offset, source.text.length)}")
		}
	}
}
