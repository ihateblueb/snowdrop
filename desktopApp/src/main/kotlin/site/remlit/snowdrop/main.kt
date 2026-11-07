package site.remlit.snowdrop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.Desktop
import site.remlit.snowdrop.util.ExternalUriHandler

fun main(args: Array<String>) {
	if (System.getProperty("os.name").indexOf("Mac") > -1) {
		Desktop.getDesktop().setOpenURIHandler { uri ->
			ExternalUriHandler.onNewUri(uri.uri.toString())
		}
	} else {
		ExternalUriHandler.onNewUri(args.getOrNull(0).toString())
	}

	application {
		Window(
			onCloseRequest = ::exitApplication,
			title = "Snowdrop",
		) {
			App()
		}
	}
}
