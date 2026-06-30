package site.remlit.snowdrop.component.native

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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
) = TextField(
	value = value,
	onValueChange = onValueChange,
	label = { if (label != null) Text(label) },
	placeholder = { if (placeholder != null) Text(placeholder) },
	maxLines = maxLines,
	modifier = modifier,
	keyboardOptions = keyboardOptions,
	keyboardActions = keyboardActions,
	colors = TextFieldDefaults.colors(
		unfocusedContainerColor = Color(0x00000000),
		unfocusedIndicatorColor = Color(0x00000000),
		focusedContainerColor = Color(0x00000000),
		focusedIndicatorColor = Color(0x00000000),
	)
)
