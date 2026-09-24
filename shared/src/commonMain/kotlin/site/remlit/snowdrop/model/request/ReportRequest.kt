package site.remlit.snowdrop.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportRequest(
	@SerialName("account_id")
	val accountId: String,
	@SerialName("status_ids")
	val statusIds: List<String>? = listOf(),
	@SerialName("collection_ids") // who fucking cares
	val collectionIds: List<String>? = listOf(),
	val comment: String? = "",
	val forward: Boolean? = false,
	val category: String? = "other",
	@SerialName("rule_ids")
	val ruleIds: List<String>? = listOf()
)
