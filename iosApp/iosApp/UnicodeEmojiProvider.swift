import Shared

class iOSUnicodeEmojiProvider: UnicodeEmojiProvider {
	// this is not fully accurate. for example it will match the char "©" (where the real emoji is "©️") as well as any numbers.
	// isEmojiPresentation is more accurate with respect to this but it doesn't match some emojis like ❤️.
	// so, for a "good enough" implementation we're just going to check if it's ascii and assume it's not an emoji in that case.
	//
	// it does still break in other ways, like, if you were to put "hello❤️" in the search box it will submit that whole string as an emoji.
	// unfortunately without using regex this is kind of where we're at. and the huge regex just crashes the app on ios.
    func isUnicodeEmoji(string: String) -> Bool {
		if (string.unicodeScalars.contains(where: { $0.isASCII } )) {
			return false
		}
		return string.unicodeScalars.contains(where: { $0.properties.isEmoji })
    }
}
