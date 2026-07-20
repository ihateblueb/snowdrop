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
import site.remlit.snowdrop.ProfileRoute
import site.remlit.snowdrop.model.Account
import site.remlit.snowdrop.util.LocalNavController

@Composable
fun AccountRow(account: Account) {
	val navHandler = LocalNavController.current

	Row(
		modifier = Modifier.clickable(onClick = { navHandler.navigate(ProfileRoute(account.id)) })
			.padding(10.dp)
			.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(10.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		Avatar(account)

		Column {
			Text(account.displayName(), fontWeight = FontWeight.Medium)
			Text("@${account.acct}")
		}
	}
	HorizontalDivider(
		thickness = 1.dp,
		color = MaterialTheme.colorScheme.surfaceContainer
	)
}
