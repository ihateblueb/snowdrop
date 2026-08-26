import SwiftUI
import Shared

@main
struct iOSApp: App {
	init() {
		Emoji_iosKt.unicodeEmojiProvider = iOSUnicodeEmojiProvider()
	}
	
    var body: some Scene {
        WindowGroup {
            ContentView().onOpenURL { uri in handleUri(uri) }
        }
    }
	
	private func handleUri(_ uri: URL) {
		ExternalUriHandler.shared.onNewUri(uri: uri.absoluteString)
	}
}
