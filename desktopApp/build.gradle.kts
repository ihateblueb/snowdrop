import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
    application {
        mainClass = "site.remlit.snowdrop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "site.remlit.snowdrop"
            packageVersion = "0.0.8"
			macOS {
				bundleID = "site.remlit.snowdrop"
				infoPlist {
					extraKeysRawXml = macExtraPlistKeys
				}
			}
        }
    }
}

val macExtraPlistKeys: String
	get() = """
    <key>CFBundleURLTypes</key>
    <array>
      <dict>
        <key>CFBundleURLName</key>
		<string>site.remlit.snowdrop</string>
		<key>CFBundleURLSchemes</key>
		<array>
			<string>snowdrop</string>
		</array>
      </dict>
    </array>
  """
