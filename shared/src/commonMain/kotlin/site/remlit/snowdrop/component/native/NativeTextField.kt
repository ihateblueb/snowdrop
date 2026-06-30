package site.remlit.snowdrop.component.native

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun NativeTextField(
	value: String,
	onValueChange: (String) -> Unit = {},
	maxLines: Int = 1,
	label: String? = null,
	placeholder: String? = null,
	modifier: Modifier = Modifier,

	keyboardOptions: KeyboardOptions = KeyboardOptions(),
	keyboardActions: KeyboardActions = KeyboardActions()
)
