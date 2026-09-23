package site.remlit.snowdrop.util.config

import androidx.compose.animation.core.tween
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpUrlFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.animatedImageDecoder
import io.kamel.image.config.Default
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import site.remlit.snowdrop.GradleVariables
import site.remlit.snowdrop.util.getVersionString

val kamelTweenAnimation = tween<Float>(durationMillis = 250)

val kamelConfig = KamelConfig {
	takeFrom(KamelConfig.Default)

	animatedImageDecoder()

	httpUrlFetcher {
		// 250 MiB
		httpCache(250 * 1024 * 1024)

		val userAgent = "Snowdrop/${getVersionString(userAgent = true)}"

		defaultRequest {
			header("User-Agent", userAgent)
		}
	}
}
