package site.remlit.snowdrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import site.remlit.snowdrop.util.ExternalUriHandler

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		actionBar?.hide()

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
