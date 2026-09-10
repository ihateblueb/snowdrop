package site.remlit.snowdrop.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.ProfileRoute
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.util.LocalNavController
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_lock_20px
import snowdrop.shared.generated.resources.icon_smart_toy_20px

/**
 * Account row showing avatar, display name, and username.
 *
 * @param modifier Modifier for row container
 * @param account Account to show information for
 * @param navigateToProfileOnClick If clicking should navigate to the account's profile
 * @param includeHorizontalDivider Show divider at the bottom of the component
 * @param onClick Action to run when clicking component
 * @param leadingContent Content to show before the row
 * @param trailingContent Content to show after the row
 *
 * @since 0.0.5-alpha
 * */
@Composable
fun AccountRow(
	modifier: Modifier = Modifier,
	account: Account,
	navigateToProfileOnClick: Boolean = true,
	includeHorizontalDivider: Boolean = true,

	leadingContent: @Composable () -> Unit = {},
	trailingContent: @Composable () -> Unit = {},
	onClick: () -> Unit = {},
) {
	val navHandler = LocalNavController.current

	Row(
		modifier = modifier.let {
			if (navigateToProfileOnClick) it.clickable {
				if (navigateToProfileOnClick) navHandler.navigate(ProfileRoute(account.id))
				onClick()
			} else it
		}.padding(10.dp)
			.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(10.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		leadingContent()

		Avatar(account)

		Column {
			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(5.dp),
				verticalArrangement = Arrangement.Center,
				itemVerticalAlignment = Alignment.CenterVertically
			) {
				HtmlContent(
					account.displayName(),
					fontWeight = FontWeight.Medium,
					maxLines = 1,
					emojis = account.emojis
				)

				if (account.locked)
					Icon(
						painterResource(Res.drawable.icon_lock_20px),
						null,
						tint = MaterialTheme.colorScheme.onSurfaceVariant
					)

				if (account.bot)
					Icon(
						painterResource(Res.drawable.icon_smart_toy_20px),
						null,
						tint = MaterialTheme.colorScheme.onSurfaceVariant
					)
			}
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
