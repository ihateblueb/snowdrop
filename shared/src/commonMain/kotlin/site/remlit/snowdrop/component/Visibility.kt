package site.remlit.snowdrop.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_globe_20px
import snowdrop.shared.generated.resources.icon_home_20px
import snowdrop.shared.generated.resources.icon_lock_20px
import snowdrop.shared.generated.resources.icon_mail_20px
import snowdrop.shared.generated.resources.visibility_direct
import snowdrop.shared.generated.resources.visibility_followers
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
fun Visibility(visibility: String, showLabel: Boolean = false) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(5.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		when (visibility) {
			"public" -> Icon(painterResource(Res.drawable.icon_globe_20px) ,null)
			"unlisted" -> Icon(painterResource(Res.drawable.icon_home_20px) ,null)
			"private" -> Icon(painterResource(Res.drawable.icon_lock_20px) ,null)
			"direct" -> Icon(painterResource(Res.drawable.icon_mail_20px) ,null)
		}

		if (showLabel)
			when (visibility) {
				"public" -> Text(stringResource(Res.string.visibility_public))
				"unlisted" -> Text(stringResource(Res.string.visibility_unlisted))
				"private" -> Text(stringResource(Res.string.visibility_followers))
				"direct" -> Text(stringResource(Res.string.visibility_direct))
			}
	}
}
