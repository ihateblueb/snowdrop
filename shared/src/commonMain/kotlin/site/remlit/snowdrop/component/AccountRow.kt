package site.remlit.snowdrop.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import site.remlit.snowdrop.ProfileRoute
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.util.LocalNavController

@Composable
fun AccountRow(
	account: Account,
	navigateToProfileOnClick: Boolean = true,
	includeHorizontalDivider: Boolean = true,
	onClick: (() -> Unit)? = null,
	modifier: Modifier = Modifier,
	leadingContent: @Composable () -> Unit = {},
	trailingContent: @Composable () -> Unit = {},
) {
	val navHandler = LocalNavController.current

	Row(
		modifier = modifier.let {
			if (onClick != null || navigateToProfileOnClick) it.clickable {
				if (navigateToProfileOnClick) navHandler.navigate(ProfileRoute(account.id))
				if (onClick != null) onClick()
			} else it
		}.padding(10.dp)
			.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(10.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		leadingContent()

		Avatar(account)

		Column {
			Text(
				account.displayName(),
				fontWeight = FontWeight.Medium
			)
			Text(
				"@${account.acct}",
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				fontSize = 13.sp,
			)
		}

		trailingContent()
	}

	if (includeHorizontalDivider)
		HorizontalDivider(
			thickness = 1.dp,
			color = MaterialTheme.colorScheme.surfaceContainer
		)
}
