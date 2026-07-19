import org.jetbrains.kotlin.gradle.plugin.extraProperties

plugins {
	// this is necessary to avoid the plugins to be loaded multiple times
	// in each subproject's classloader
	alias(libs.plugins.androidApplication) apply false
	alias(libs.plugins.androidMultiplatformLibrary) apply false
	alias(libs.plugins.composeMultiplatform) apply false
	alias(libs.plugins.composeCompiler) apply false
	alias(libs.plugins.kotlinMultiplatform) apply false
}

version = "0.0.4-alpha"
extraProperties.set("versionCode", 4)
// todo: sync version with iOS app
