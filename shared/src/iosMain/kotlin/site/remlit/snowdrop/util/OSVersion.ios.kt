package site.remlit.snowdrop.util

import platform.UIKit.UIDevice

actual fun getOSVersion(): Int = UIDevice.currentDevice.systemVersion.substring(0, 2).toInt()
