package site.remlit.snowdrop.component.native

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun NativeTextField(
	value: String,
	onValueChange: (String) -> Unit,
	maxLines: Int,
	label: String?,
	placeholder: String?,
	modifier: Modifier,

	keyboardOptions: KeyboardOptions,
	keyboardActions: KeyboardActions
) = TextField(
	value = value,
	onValueChange = onValueChange,
	label = { if (label != null) Text(label) },
	placeholder = { if (placeholder != null) Text(placeholder) },
	maxLines = maxLines,
	modifier = modifier,
	keyboardOptions = keyboardOptions,
	keyboardActions = keyboardActions
)
