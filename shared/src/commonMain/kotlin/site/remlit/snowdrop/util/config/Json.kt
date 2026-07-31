package site.remlit.snowdrop.util.config

import kotlinx.serialization.json.Json

val json = Json {
	prettyPrint = true
	isLenient = true
	ignoreUnknownKeys = true
	coerceInputValues = true
}
