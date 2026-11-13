package site.remlit.snowdrop.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemShapes
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.component.dropdown.PreparedDropdownMenu
import site.remlit.snowdrop.util.extension.getPreparedDropdownMenuItemShapes
import site.remlit.snowdrop.util.getFeature
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.visibility_direct
import snowdrop.shared.generated.resources.visibility_direct_description
import snowdrop.shared.generated.resources.visibility_followers
import snowdrop.shared.generated.resources.visibility_followers_description
import snowdrop.shared.generated.resources.visibility_local
import snowdrop.shared.generated.resources.visibility_local_description
import snowdrop.shared.generated.resources.visibility_public
import snowdrop.shared.generated.resources.visibility_public_description
import snowdrop.shared.generated.resources.visibility_unlisted
import snowdrop.shared.generated.resources.visibility_unlisted_description

@Composable
fun VisibilityPicker(
	expanded: Boolean = false,
	onSetExpanded: (Boolean) -> Unit,

	visibility: String,
	onSetVisibility: (String) -> Unit,

	showLocal: Boolean = false,
	local: Boolean = false,
	onSetLocal: (Boolean) -> Unit = {}
) {
	PreparedDropdownMenu(
		expanded = expanded,
		onDismissRequest = { onSetExpanded(!expanded) }
	) {
		var visibilities = listOf("public", "unlisted", "private", "direct")

		if (getFeature("local_visibility")) visibilities = visibilities.plus("local")

		@Composable
		fun VisibilityDropdownItem(vis: String, index: Int) {
			DropdownMenuItem(
				enabled = true,
				selected = visibility == vis,
				onClick = { onSetVisibility(vis); onSetExpanded(!expanded) },
				leadingIcon = { Visibility(vis) },
				colors = MenuDefaults.selectableItemColors(),
				shapes = visibilities.getPreparedDropdownMenuItemShapes(index),
				contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
				text = {
					Column(modifier = Modifier.padding(vertical = 5.dp)) {
						Text(
							when (vis) {
								"public" -> stringResource(Res.string.visibility_public)
								"unlisted" -> stringResource(Res.string.visibility_unlisted)
								"private" -> stringResource(Res.string.visibility_followers)
								"local" -> stringResource(Res.string.visibility_local)
								else -> stringResource(Res.string.visibility_direct)
							},
							fontWeight = FontWeight.Medium
						)
						Text(
							when (vis) {
								"public" -> stringResource(Res.string.visibility_public_description)
								"unlisted" -> stringResource(Res.string.visibility_unlisted_description)
								"private" -> stringResource(Res.string.visibility_followers_description)
								"local" -> stringResource(Res.string.visibility_local_description)
								else -> stringResource(Res.string.visibility_direct_description)
							},
							fontSize = 13.sp
						)
					}
				}
			)
		}

		// todo: do minimum visibility based on the view's visibility parameter
		visibilities.forEachIndexed { index, string -> VisibilityDropdownItem(string, index) }


		if (getFeature("local_only_toggle") && showLocal) {
			HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))

			DropdownMenuItem(
				checked = false,
				onCheckedChange = { onSetLocal(!local) },
				leadingIcon = { Visibility("local") },
				colors = MenuDefaults.selectableItemColors(),
				shapes = MenuItemShapes(MenuDefaults.standaloneGroupShape, MenuDefaults.standaloneGroupShape),
				contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
				text = {
					Text(
						stringResource(Res.string.visibility_local),
						fontWeight = FontWeight.Medium
					)
				},
				trailingIcon = {
					Switch(checked = local, onCheckedChange = { onSetLocal(!local) })
				}
			)
		}
	}
}
