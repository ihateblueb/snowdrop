package site.remlit.snowdrop.util

/**
 * Handler for snowdrop:// URIs being forwarded
 * to the app.
 *
 * https://kotlinlang.org/docs/multiplatform/compose-navigation-deep-links.html#handle-received-deep-links
 * */
object ExternalUriHandler {
	private var cached: String? = null

	var listener: ((uri: String) -> Unit)? = null
		set(value) {
			field = value
			if (value != null) {
				cached?.let { value.invoke(it) }
				cached = null
			}
		}

	fun onNewUri(uri: String) {
		cached = uri
		listener?.let {
			it.invoke(uri)
			cached = null
		}
	}
}
