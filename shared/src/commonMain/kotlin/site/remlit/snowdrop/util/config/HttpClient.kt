package site.remlit.snowdrop.util.config

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.header
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import site.remlit.snowdrop.GradleVariables
import site.remlit.snowdrop.exception.ApiException
import site.remlit.snowdrop.util.log.debug

val httpClient = HttpClient {
	val userAgent = "Snowdrop/${GradleVariables.version}+${GradleVariables.gitCommit}@${GradleVariables.gitBranch}"

	defaultRequest {
		header("User-Agent", userAgent)
	}

	install(ContentNegotiation) {
		json(json)
	}

	debug { "(HttpClient) created ktor client with ua $userAgent" }
}

suspend inline fun <reified T> endOfRequest(req: HttpResponse): T {
	if (!req.status.isSuccess())
		throw ApiException(
			"${req.status.value} - ${req.request.url}" +
				"\nBody: ${req.bodyAsText()}"
		)

	val body = req.body<T>()
	debug {
		"${req.status.value} - ${req.request.url}" +
			"\nBody: $body"
	}
	return body
}

suspend fun endOfRequestNoBody(req: HttpResponse) {
	if (!req.status.isSuccess())
		throw ApiException(
			"${req.status.value} - ${req.request.url}" +
				"\nBody: ${req.bodyAsText()}"
		)
}

