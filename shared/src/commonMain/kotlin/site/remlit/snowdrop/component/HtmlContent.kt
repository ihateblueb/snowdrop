package site.remlit.snowdrop.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import site.remlit.snowdrop.model.Emoji
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.annotatedString.htmlToAnnotatedString

/**
 * Default emoji size
 * @since 0.0.5-alpha
 * */
val defaultEmojiSize = 1.2.em

/**
 * HTML content element. Will render HTML, handle mention links, emojis,
 * and other rich content.
 *
 * @param string Source string
 * @param modifier Modifier for text
 *
 * @param mentions List of mentions from the status model
 * @param emojis List of emojis
 * @param emojiSize Text size of emojis
 * @param simple If the text should be rendered simply (no styling)
 * @param showEmojiTooltips Whether to show emoji tooltips on long-press
 *
 * @param fontWeight Font weight of text
 * @param fontSize Font size of text
 * @param color Color of text
 * @param maxLines Maximum amount of lines that the text should allow
 *
 * @since 0.0.1-alpha
 * */
@Composable
fun HtmlContent(
	string: String,
	modifier: Modifier = Modifier,

	mentions: List<Status.Mention> = emptyList(),
	emojis: List<Emoji> = emptyList(),
	emojiSize: TextUnit = defaultEmojiSize,
	simple: Boolean = false,
	showEmojiTooltips: Boolean = true,

	fontWeight: FontWeight = FontWeight.Normal,
	fontSize: TextUnit = TextUnit.Unspecified,
	color: Color = Color.Unspecified,
	maxLines: Int = Int.MAX_VALUE,
) {
	val (annotatedString, mappedEmojis) = htmlToAnnotatedString(
		string,
		mentions,
		emojis,
		emojiSize,
		simple,
		showEmojiTooltips
	)

	Text(
		text = annotatedString,
		modifier = modifier,
		fontWeight = fontWeight,
		fontSize = fontSize,
		color = color,
		overflow = TextOverflow.Ellipsis,
		maxLines = maxLines,
		inlineContent = mappedEmojis
	)
}
