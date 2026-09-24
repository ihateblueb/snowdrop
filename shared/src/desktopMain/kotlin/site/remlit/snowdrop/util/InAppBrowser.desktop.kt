package site.remlit.snowdrop.util

import java.awt.Desktop
import java.net.URI

actual suspend fun openInAppBrowser(url: String) {
	if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
		Desktop.getDesktop().browse(URI(url))
	}
}

actual fun closeInAppBrowser() {}
