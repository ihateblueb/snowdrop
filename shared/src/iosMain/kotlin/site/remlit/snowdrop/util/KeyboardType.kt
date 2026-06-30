package site.remlit.snowdrop.util

import androidx.compose.ui.text.input.KeyboardType
import platform.UIKit.UIKeyboardType
import platform.UIKit.UIKeyboardTypeASCIICapable
import platform.UIKit.UIKeyboardTypeDecimalPad
import platform.UIKit.UIKeyboardTypeDefault
import platform.UIKit.UIKeyboardTypeEmailAddress
import platform.UIKit.UIKeyboardTypeNumberPad
import platform.UIKit.UIKeyboardTypePhonePad
import platform.UIKit.UIKeyboardTypeURL

fun KeyboardType.toUIKeyboardType(): UIKeyboardType =
	when (this) {
		KeyboardType.Uri -> UIKeyboardTypeURL
		KeyboardType.Ascii -> UIKeyboardTypeASCIICapable
		KeyboardType.Decimal -> UIKeyboardTypeDecimalPad
		KeyboardType.Email -> UIKeyboardTypeEmailAddress
		KeyboardType.Number -> UIKeyboardTypeNumberPad
		KeyboardType.NumberPassword -> UIKeyboardTypeNumberPad
		KeyboardType.Phone -> UIKeyboardTypePhonePad
		else -> UIKeyboardTypeDefault
	}
