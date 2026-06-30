package site.remlit.snowdrop.component.native

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventEditingChanged
import platform.UIKit.UITextBorderStyle
import platform.UIKit.UITextField
import site.remlit.snowdrop.util.toUIKeyboardType

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun NativeTextArea(
	value: String,
	onValueChange: (String) -> Unit,
	maxLines: Int,
	label: String?,
	placeholder: String?,
	modifier: Modifier,
	keyboardOptions: KeyboardOptions,
	keyboardActions: KeyboardActions
) {
	// https://github.com/JetBrains/compose-multiplatform-core/blob/f9dfd04e830b08d6e81463daa78551b0b8c28e96/compose/ui/ui/src/uikitMain/kotlin/androidx/compose/ui/interop/ComposeUITextField.uikit.kt#L19
	UIKitView(
		factory = f@{
			val textField = object : UITextField(CGRectMake(0.0, 0.0, 0.0, 0.0)) {
				@ObjCAction
				fun editingChanged() = onValueChange(text ?: "")
			}

			textField.setBackgroundColor(UIColor.clearColor)
			textField.setBorderStyle(UITextBorderStyle.UITextBorderStyleNone)

			textField.setPlaceholder(placeholder)
			textField.setKeyboardType(keyboardOptions.keyboardType.toUIKeyboardType())

			var isPassword = false
			when (keyboardOptions.keyboardType) {
				KeyboardType.Password, KeyboardType.NumberPassword -> isPassword = true
			}
			textField.setSecureTextEntry(isPassword)

			// goto @g
			textField.addTarget(
				target = textField,
				action = NSSelectorFromString(textField::editingChanged.name),
				forControlEvents = UIControlEventEditingChanged
			)
			return@f textField
		},
		modifier = modifier,
		// g@
		update = { textField ->
			textField.text = value
		},
		onRelease = { textField ->
			textField.removeTarget(
				target = textField,
				action = NSSelectorFromString(textField::editingChanged.name),
				forControlEvents = UIControlEventEditingChanged
			)
		}
	)
}
