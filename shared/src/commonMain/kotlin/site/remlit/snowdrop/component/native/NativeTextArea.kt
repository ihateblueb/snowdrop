package site.remlit.snowdrop.component.native

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * NativeTextArea is a fully background free and borderless text field
 * */
@Composable
expect fun NativeTextArea(
	value: String,
	onValueChange: (String) -> Unit = {},
	maxLines: Int = 1,
	label: String? = null,
	placeholder: String? = null,
	modifier: Modifier = Modifier,

	keyboardOptions: KeyboardOptions = KeyboardOptions(),
	keyboardActions: KeyboardActions = KeyboardActions()
)
