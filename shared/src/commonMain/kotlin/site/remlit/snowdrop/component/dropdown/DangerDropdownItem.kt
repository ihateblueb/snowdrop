package site.remlit.snowdrop.component.dropdown

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable

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