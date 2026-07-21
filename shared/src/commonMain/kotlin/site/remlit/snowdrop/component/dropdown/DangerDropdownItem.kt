package site.remlit.snowdrop.component.dropdown

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable

/**
 * Danger dropdown item component. Will set text color and icons to be
 * the MaterialTheme error color.
 *
 * @param text Text of dropdown item
 * @param leadingIcon Leading dropdown icon
 * @param onClick Action on click of dropdown item
 *
 * @since 0.0.1-alpha
 * */
@Composable
fun DangerDropdownItem(
	text: @Composable () -> Unit,
	leadingIcon: (@Composable () -> Unit)? = null,
	onClick: () -> Unit
) {
	DropdownMenuItem(
		text = text,
		leadingIcon = leadingIcon,
		onClick = onClick,
		colors = MenuDefaults.itemColors(
			MaterialTheme.colorScheme.error,
			MaterialTheme.colorScheme.error,
			MaterialTheme.colorScheme.error,
		)
	)
}
