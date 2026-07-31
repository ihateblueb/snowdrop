package site.remlit.snowdrop.util.extension

import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape

@Composable
fun List<*>?.getPreparedDropdownMenuItemShape(index: Int): Shape {
	val isOnly = this?.size == 1
	val isFirst = 0 == index
	val isLast = this?.size == (index + 1)

	return if (isOnly) MenuDefaults.standaloneItemShape
	else if (isFirst) MenuDefaults.leadingItemShape
	else if (isLast) MenuDefaults.trailingItemShape
	else MenuDefaults.middleItemShape
}
