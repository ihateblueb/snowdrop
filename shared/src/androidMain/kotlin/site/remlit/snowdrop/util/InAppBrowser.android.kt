package site.remlit.snowdrop.util

import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

actual suspend fun openInAppBrowser(url: String) {
	// ideally this would be an AuthTab but i couldn't figure that out
	val ctx = AndroidContext.context
	val builder = CustomTabsIntent.Builder().build()
	builder.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
	builder.launchUrl(ctx, url.toUri())
}

// ios only thing
actual fun closeInAppBrowser() {}
