package site.remlit.snowdrop.util

import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

var safariViewController: SFSafariViewController? = null

actual suspend fun openInAppBrowser(url: String) {
	val nsurl = NSURL(string = url)
	safariViewController = SFSafariViewController(uRL = nsurl)
	val rvc = UIApplication.sharedApplication.keyWindow?.rootViewController

	withContext(Dispatchers.Main) {
		rvc?.presentViewController(safariViewController!!, true, null)
	}
}

// this is obnoxious and honestly i'm surprised it works
actual fun closeInAppBrowser() {
	if (safariViewController == null) return
	safariViewController!!.dismissViewControllerAnimated(true, null)
}
