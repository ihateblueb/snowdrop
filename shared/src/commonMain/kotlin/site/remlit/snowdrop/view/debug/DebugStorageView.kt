package site.remlit.snowdrop.view.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.russhwolf.settings.ExperimentalSettingsApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.cache.blockingCache
import site.remlit.snowdrop.util.blockingSettings
import site.remlit.snowdrop.util.cache.getCacheEntry
import site.remlit.snowdrop.util.cache.getCacheManifest
import site.remlit.snowdrop.util.settings
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.account
import snowdrop.shared.generated.resources.cache
import snowdrop.shared.generated.resources.icon_arrow_back_24
import snowdrop.shared.generated.resources.storage

@OptIn(ExperimentalSettingsApi::class)
@Composable
fun DebugStorageView(
	storage: Int = 0
) = ViewSurface {
	val navHandler = LocalNavController.current

	@Composable
	fun renderKeyVal(key: String, value: String?) {
		Column(
			modifier = Modifier.padding(bottom = 20.dp),
			verticalArrangement = Arrangement.spacedBy(5.dp)
		) {
			Text(key, fontWeight = FontWeight.Bold, softWrap = true)
			Text(value?.ifBlank { "n/a" } ?: "n/a", softWrap = true)
		}
	}

	TopAppBar(
		navigationIcon = {
			IconButton(onClick = { navHandler.popBackStack() }) {
				Icon(painterResource(Res.drawable.icon_arrow_back_24), null)
			}
		},
		title = {
			when (storage) {
				0 -> Text(stringResource(Res.string.storage))
				1 -> Text(stringResource(Res.string.cache))
			}
		}
	)

	val scrollState = rememberScrollState()
	val accounts by settings.getStringFlow("accounts", "")
		.collectAsStateWithLifecycle("")

	Column(
		modifier = Modifier.verticalScroll(scrollState)
	) {
		when (storage) {
			0 -> {
				@Composable
				fun renderString(key: String) {
					val d = blockingSettings.getString(key, "n/a")
					renderKeyVal(key, d)
				}

				Text("Key/val pair count: ${blockingSettings.size}")

				HorizontalDivider()

				renderString("accounts")
				renderString("current_account")

				accounts.split(" ").forEach { id ->
					if (id.isBlank()) return@forEach

					renderString("account_${id}_host")
					renderString("account_${id}_token")
					renderString("account_${id}_client_id")
					renderString("account_${id}_client_secret")
				}
			}
			1 -> {
				Text("Key/val pair count: ${blockingCache.size}")

				HorizontalDivider()

				renderKeyVal("manifest", getCacheManifest().toString())

				HorizontalDivider()

				blockingCache.keys.forEach {
					if (!it.startsWith("entry_")) return@forEach
					renderKeyVal(
						it,
						getCacheEntry(it.replace("entry_", ""))
							.toString()
					)
				}
			}
		}
	}
}
