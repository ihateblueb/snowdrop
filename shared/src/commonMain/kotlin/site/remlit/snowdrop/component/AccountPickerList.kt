package site.remlit.snowdrop.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import site.remlit.snowdrop.util.ListItemShape
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.getAccountHost
import site.remlit.snowdrop.util.getAccountObjectFlow
import site.remlit.snowdrop.util.getAccounts
import site.remlit.snowdrop.util.getCurrentAccountId
import site.remlit.snowdrop.util.listItemClip
import site.remlit.snowdrop.util.switchAccount
import site.remlit.snowdrop.util.vibrateSoft

@Composable
fun AccountPickerList(
	modifier: Modifier = Modifier,
	onSelect: () -> Unit = {}
) {
	val navController = LocalNavController.current
	val haptics = LocalHapticFeedback.current

	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(5.dp)
	) {
		val accounts = getAccounts()
		accounts.forEachIndexed { index, it ->
			val account by getAccountObjectFlow(it)
				.collectAsStateWithLifecycle(null)

			fun switchToThisAccount() {
				if (it != getCurrentAccountId()) {
					onSelect()
					switchAccount(it, navController)
					vibrateSoft(haptics)
				}
			}

			if (account != null) {
				// todo: long press to log out
				Card(
					modifier = Modifier.fillMaxWidth()
						.listItemClip(index, accounts.size)
						.clickable { switchToThisAccount() },
					shape = ListItemShape(index, accounts.size),
					colors = if (getCurrentAccountId() == it)
						CardDefaults.cardColors(
							containerColor = MaterialTheme.colorScheme.primaryContainer,
							contentColor = MaterialTheme.colorScheme.onPrimaryContainer
						)
					else CardDefaults.cardColors()
				) {
					Row(
						modifier = Modifier.padding(10.dp),
						horizontalArrangement = Arrangement.spacedBy(10.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						Avatar(account!!)

						Column(
							modifier = Modifier.weight(1f)
						) {
							Text(
								account!!.displayName(),
								fontWeight = FontWeight.Medium,
								color = MaterialTheme.colorScheme.onSurface
							)
							Text(
								"@${account!!.username}@${getAccountHost(it)}",
								fontSize = 13.sp
							)
						}

						RadioButton(
							selected = getCurrentAccountId() == it,
							onClick = { switchToThisAccount() }
						)
					}
				}
			}
		}
	}
}
