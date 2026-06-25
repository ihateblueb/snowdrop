package site.remlit.snowdrop.util

import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpUrlFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default

val kamelConfig = KamelConfig {
	takeFrom(KamelConfig.Default)

	httpUrlFetcher {
		/* 200 MiB */
		httpCache(200 * 1024 * 1024)
	}
}