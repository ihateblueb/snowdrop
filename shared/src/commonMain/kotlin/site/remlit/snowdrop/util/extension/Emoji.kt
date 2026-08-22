package site.remlit.snowdrop.util.extension

private val unicodeEmoji = Regex("^\\p{Emoji_Presentation}$")

fun isUnicodeEmoji(emoji: String): Boolean {
	return unicodeEmoji.matches(emoji)
}
