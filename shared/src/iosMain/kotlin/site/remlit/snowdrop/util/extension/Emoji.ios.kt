package site.remlit.snowdrop.util.extension

actual fun String.isUnicodeEmoji(): Boolean {
	return unicodeEmojiProvider.isUnicodeEmoji(this)
}

interface UnicodeEmojiProvider {
	fun isUnicodeEmoji(string: String): Boolean
}

lateinit var unicodeEmojiProvider: UnicodeEmojiProvider
