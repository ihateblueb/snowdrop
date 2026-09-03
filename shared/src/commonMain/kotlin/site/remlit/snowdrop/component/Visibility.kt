package site.remlit.snowdrop.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_globe_20px
import snowdrop.shared.generated.resources.icon_home_20px
import snowdrop.shared.generated.resources.icon_lock_20px
import snowdrop.shared.generated.resources.icon_mail_20px
import snowdrop.shared.generated.resources.icon_groups_20px
import snowdrop.shared.generated.resources.visibility
import snowdrop.shared.generated.resources.visibility_direct
import snowdrop.shared.generated.resources.visibility_followers
import snowdrop.shared.generated.resources.visibility_local
import snowdrop.shared.generated.resources.visibility_local_label
import snowdrop.shared.generated.resources.visibility_public
import snowdrop.shared.generated.resources.visibility_unlisted

/**
 * Visibility icon with optional label.
 *
 * @param visibility Visibility
 * @param showLabel If the name of the visibility should be shown as a label
 * 
 * @since 0.0.1-alpha
 * */
@Composable
fun Visibility(visibility: String, showLabel: Boolean = false, localOnly: Boolean = false) {
	val __translation_visibility = stringResource(Res.string.visibility)
	val label = if (localOnly) stringResource(Res.string.visibility_local_label)
		else when (visibility) {
			"public" -> stringResource(Res.string.visibility_public)
			"unlisted" -> stringResource(Res.string.visibility_unlisted)
			"private" -> stringResource(Res.string.visibility_followers)
			"direct" -> stringResource(Res.string.visibility_direct)
			"local" -> stringResource(Res.string.visibility_local)
			else -> ""
		}

	Row(
		horizontalArrangement = Arrangement.spacedBy(5.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier.semantics { contentDescription = __translation_visibility+", "+label }
	) {
		when (visibility) {
			"public" -> Icon(painterResource(Res.drawable.icon_globe_20px) ,null)
			"unlisted" -> Icon(painterResource(Res.drawable.icon_home_20px) ,null)
			"private" -> Icon(painterResource(Res.drawable.icon_lock_20px) ,null)
			"direct" -> Icon(painterResource(Res.drawable.icon_mail_20px) ,null)
			"local" -> Icon(painterResource(Res.drawable.icon_groups_20px), null)
		}

		if (showLabel) Text(label, modifier = Modifier.semantics { hideFromAccessibility() })
	}
}
