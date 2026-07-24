package site.remlit.snowdrop.util

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

private val max = 20.dp
private val min = 5.dp

fun ListItemShape(index: Int, size: Int): Shape {
	return when (index) {
		0 -> RoundedCornerShape(max, max, min, min)
		(size - 1) -> RoundedCornerShape(min, min, max, max)
		else -> RoundedCornerShape(min)
	}
}

fun Modifier.listItemClip(index: Int, size: Int): Modifier =
	this.then(Modifier.clip(ListItemShape(index, size)))
