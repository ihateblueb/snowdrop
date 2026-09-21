package site.remlit.snowdrop.model

import io.ktor.util.StringValues
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import site.remlit.snowdrop.util.safeReturnable
import kotlin.time.Instant

@Serializable
data class GroupedNotificationsResults(
	val accounts: List<Account>,
	val statuses: List<Status>,
	// todo: partial accts
	@SerialName("notification_groups")
	val notificationGroups: List<NotificationGroup>
) {
	@Serializable
	data class NotificationGroup(
		@SerialName("group_key")
		override val id: String,

		//@SerialName("group_key")
		//val groupKey: String,
		@SerialName("notifications_count")
		val notificationsCount: Int,
		val type: String,
		@SerialName("most_recent_notification_id")
		val mostRecentNotificationId: String,
		@SerialName("page_min_id")
		val pageMinId: String? = null,
		@SerialName("page_max_id")
		val pageMaxId: String? = null,
		@SerialName("latest_page_notification_at")
		val latestPageNotificationAt: String? = null,
		@SerialName("sample_account_ids")
		val sampleAccountIds: List<String>,
		@SerialName("status_id")
		val statusId: String? = null,
		// todo: report
		// todo: event
		// todo: moderation_warning
		// todo: fallback
		// i cba to do the types for these rn lol
		val reaction: Notification.ChuckyaReaction? = null
	) : IdentifiableObject<String> {
		// Instants
		fun getCreatedAtTimestamp(): Instant? = safeReturnable {
			Instant.parse(this.latestPageNotificationAt!!)
		}
	}
}
