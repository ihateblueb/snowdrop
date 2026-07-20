import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.extraProperties
import java.io.ByteArrayOutputStream

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.androidMultiplatformLibrary)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
	alias(libs.plugins.kotlinSerialization)
	alias(libs.plugins.buildKonfig)
}

kotlin {
	listOf(
		iosArm64(),
		iosSimulatorArm64()
	).forEach { iosTarget ->
		iosTarget.binaries.framework {
			baseName = "Shared"
			isStatic = true
		}
	}

	android {
		namespace = "site.remlit.snowdrop.shared"
		compileSdk = libs.versions.android.compileSdk.get().toInt()
		minSdk = libs.versions.android.minSdk.get().toInt()

		compilerOptions {
			jvmTarget = JvmTarget.JVM_11
		}
		androidResources {
			enable = true
		}
		withHostTest {
			isIncludeAndroidResources = true
		}
	}

	sourceSets {
		androidMain.dependencies {
			implementation(libs.compose.uiToolingPreview)
			implementation(libs.androidx.startup.runtime)

			implementation(libs.ktor.client.okhttp)
			implementation(libs.kotlinx.coroutines.android)

			implementation(libs.kamel.decoder.image.bitmap.resizing)
		}
		iosMain.dependencies {
			implementation(libs.ktor.client.darwin)
		}
		commonMain.dependencies {
			implementation(libs.compose.runtime)
			implementation(libs.compose.foundation)
			implementation(libs.compose.material3)
			implementation(libs.compose.ui)
			implementation(libs.compose.components.resources)
			implementation(libs.compose.uiToolingPreview)
			implementation(libs.androidx.lifecycle.viewmodelCompose)
			implementation(libs.androidx.lifecycle.runtimeCompose)
			implementation(libs.androidx.navigation.compose)

			implementation(libs.multiplatform.settings)
			implementation(libs.multiplatform.settings.coroutines)
			implementation(libs.ktor.client.content.negotiation)
			implementation(libs.ktor.serialization.kotlinx.json)

			implementation(libs.ktor.client.core)
			implementation(libs.kotlinx.coroutines.core)
			implementation(libs.kotlinx.serialization.json)
			implementation(libs.kotlinx.serialization.cbor)

			implementation(libs.kermit)
			implementation(libs.htmlconverter)
			implementation(libs.reorderable)

			// kamel, image handling
			implementation(libs.kamel.image)
			implementation(libs.kamel.image.default)
			implementation(libs.kamel.decoder.animated.image)
			implementation(libs.kamel.decoder.image.bitmap)

			implementation(libs.zoomimage.compose)
		}
		commonTest.dependencies {
			implementation(libs.kotlin.test)
		}
	}
}

dependencies {
	androidRuntimeClasspath(libs.compose.uiTooling)
}

//
// past this point the gradle config is very messy
//

abstract class GitBranchValueSource : ValueSource<String, ValueSourceParameters.None> {
	@get:Inject
	abstract val execOperations: ExecOperations

	override fun obtain(): String {
		val stdout = ByteArrayOutputStream()
		execOperations.exec {
			commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
			standardOutput = stdout
		}
		return stdout.toString().trim()
	}
}

abstract class GitCommitValueSource : ValueSource<String, ValueSourceParameters.None> {
	@get:Inject
	abstract val execOperations: ExecOperations

	override fun obtain(): String {
		val stdout = ByteArrayOutputStream()
		execOperations.exec {
			commandLine("git", "rev-parse", "--short", "HEAD")
			standardOutput = stdout
		}
		return stdout.toString().trim()
	}
}

buildkonfig {
	packageName = "site.remlit.snowdrop"
	objectName = "GradleVariables"

	val gitBranch = providers.of(GitBranchValueSource::class) {}
	val gitCommit = providers.of(GitCommitValueSource::class) {}

	defaultConfigs {
		buildConfigField(STRING, "version", rootProject.version.toString())
		buildConfigField(STRING, "gitBranch", gitBranch.get())
		buildConfigField(STRING, "gitCommit", gitCommit.get())
	}
}
