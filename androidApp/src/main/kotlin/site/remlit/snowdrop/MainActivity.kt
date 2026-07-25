package site.remlit.snowdrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import site.remlit.snowdrop.util.ExternalUriHandler

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		actionBar?.hide()

		// android 14, sdk 34, is the last version that doesn't enforce edge to edge. for some reason
		// there's massive gaps if we use edge to edge before this version. this works to avoid having
		// to deal with that, but there's black bars at the top and bottom, buuut that's seemingly
		// normal for this version of android.
		if (android.os.Build.VERSION.SDK_INT > 34)
			enableEdgeToEdge()

		if (intent.data != null)
			ExternalUriHandler.onNewUri(intent.data.toString())

		setContent {
			App()
		}
	}
}

@Composable
fun AppAndroidPreview() {
	App()
}
