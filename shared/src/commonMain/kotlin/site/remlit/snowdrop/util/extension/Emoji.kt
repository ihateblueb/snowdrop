package site.remlit.snowdrop.util.extension

private val customEmoji = Regex("^[A-z0-9-_]\\S+$")

fun isUnicodeEmoji(emoji: String): Boolean {
	return !emoji.contains("@") && (!customEmoji.matches(emoji))
}
