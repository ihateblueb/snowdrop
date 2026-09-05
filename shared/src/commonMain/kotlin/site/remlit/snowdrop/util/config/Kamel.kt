package site.remlit.snowdrop.util.config

import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpUrlFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.animatedImageDecoder
import io.kamel.image.config.Default
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import site.remlit.snowdrop.GradleVariables

val kamelConfig = KamelConfig {
	takeFrom(KamelConfig.Default)

	animatedImageDecoder()

	httpUrlFetcher {
		// 250 MiB
		httpCache(250 * 1024 * 1024)

		val userAgent = "Snowdrop/${GradleVariables.version}+${GradleVariables.gitCommit}@${GradleVariables.gitBranch}"

		defaultRequest {
			header("User-Agent", userAgent)
		}
	}
}
