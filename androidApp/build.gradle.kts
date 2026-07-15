import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.extraProperties

plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_11
	}
}
dependencies {
	implementation(projects.shared)

	implementation(libs.androidx.activity.compose)

	implementation(libs.compose.uiToolingPreview)
	debugImplementation(libs.compose.uiTooling)
}

android {
	namespace = "site.remlit.snowdrop"
	compileSdk = libs.versions.android.compileSdk.get().toInt()

	defaultConfig {
		applicationId = "site.remlit.snowdrop"
		minSdk = libs.versions.android.minSdk.get().toInt()
		targetSdk = libs.versions.android.targetSdk.get().toInt()
		versionCode = rootProject.extraProperties.get("versionCode").toString().toInt()
		versionName = rootProject.version.toString()
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
	buildTypes {
		getByName("release") {
			isMinifyEnabled = true
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	// so f-droid is happy
	dependenciesInfo {
		includeInApk = false
		includeInBundle = false
	}
}
