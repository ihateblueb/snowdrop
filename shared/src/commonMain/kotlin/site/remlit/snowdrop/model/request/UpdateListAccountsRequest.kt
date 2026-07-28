package site.remlit.snowdrop.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateListAccountsRequest(
	@SerialName("account_ids")
	val accountIds: List<String>
)
