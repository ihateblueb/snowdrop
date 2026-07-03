package site.remlit.snowdrop.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import site.remlit.snowdrop.model.Account

@Serializable
data class PatchProfileRequest(
	@SerialName("display_name")
	val displayName: String? = null,
	val note: String? = null,

	val avatar: String? = null,
	@SerialName("avatar_description")
	val avatarDescription: String? = null,
	val header: String? = null,
	@SerialName("header_description")
	val headerDescription: String? = null,

	val locked: Boolean? = null,
	val bot: Boolean? = null,
	val discoverable: Boolean? = null,
	@SerialName("hide_collections")
	val hideCollections: Boolean? = null,
	val indexable: Boolean? = null,
	@SerialName("permit_followback")
	val permitFollowback: Boolean? = null,

	@SerialName("attribution_domains")
	val attributionDomains: List<String>? = null,
	@SerialName("fields_attributes")
	val fieldsAttributes: List<Account.Field>? = null,

	val source: Source = Source()
) {
	@Serializable
	data class Source(
		val privacy: String? = null,
		val language: String? = null,
		val sensitive: Boolean = false
	)
}
