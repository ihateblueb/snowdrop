package site.remlit.snowdrop.component.dropdown

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

/**
 * Prepared dropdown menu component. Uses new Material 3 Expressive style menus in a more concise
 * way. Supply [androidx.compose.material3.DropdownMenuItem] in [content] using MenuDefaults for
 * item shapes.
 *
 * @param expanded whether the menu is expanded or not.
 * @param onDismissRequest called when the user requests to dismiss the menu, such as by tapping
 *   outside the menu's bounds.
 * @param offset [DpOffset] from the original position of the menu.
 *
 * @since 0.0.6-alpha
 * */
@Composable
fun PreparedDropdownMenu(
	expanded: Boolean,
	onDismissRequest: () -> Unit,
	offset: DpOffset = DpOffset(0.dp, 0.dp),
	content: @Composable () -> Unit
) {
	DropdownMenu(
		expanded = expanded,
		onDismissRequest = onDismissRequest,
		modifier = Modifier.offset(x = offset.x, y = offset.y)
	) {
		content()
	}
}
