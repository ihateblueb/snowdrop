package site.remlit.snowdrop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import site.remlit.snowdrop.util.ListItemShape
import site.remlit.snowdrop.util.listItemClip
import site.remlit.snowdrop.util.listItemSpacing

@Composable
fun SettingsCard(
	position: Int,
	size: Int,

	icon: @Composable (Color, Modifier) -> Unit,
	iconBackground: Color = MaterialTheme.colorScheme.primaryContainer,
	iconColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,

	headlineContent: String,
	supportingContent: String? = null,

	trailingContent: (@Composable () -> Unit)? = null,
	onClick: () -> Unit = {}
) {
	Box(
		modifier = Modifier
			.let {
				if (position + 1 != size) it.padding(bottom = listItemSpacing)
				else it.padding(bottom = 20.dp)
			}
	) {
		Card(
			modifier = Modifier.listItemClip(position, size)
				.fillMaxWidth()
				.clickable { onClick() },
			shape = ListItemShape(position, size),
			colors = CardDefaults.cardColors()
		) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(15.dp),
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.padding(10.dp)
			) {
				Box(
					modifier = Modifier.clip(RoundedCornerShape(100))
						.background(iconBackground)
						.padding(5.dp)
				) {
					icon(iconColor, Modifier.padding(5.dp))
				}

				Column(
					modifier = Modifier.weight(1f),
				) {
					Text(
						headlineContent,
						color = MaterialTheme.colorScheme.onSurface
					)
					if (supportingContent != null)
						Text(
							headlineContent,
							fontSize = 13.sp
						)
				}

				if (trailingContent != null) trailingContent()
			}
		}
	}
}
