package site.remlit.snowdrop

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		actionBar?.hide()

		val oauthCallbackCode: String? = intent.data?.getQueryParameter("code")

		setContent {
			App(oauthCallback = oauthCallbackCode)
		}
	}
}

@Preview
@Composable
fun AppAndroidPreview() {
	App()
}
