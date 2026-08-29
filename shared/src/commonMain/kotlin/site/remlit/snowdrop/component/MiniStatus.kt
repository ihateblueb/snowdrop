package site.remlit.snowdrop.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import site.remlit.snowdrop.ThreadRoute
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.LocalStatusStateController
import site.remlit.snowdrop.util.extension.toRelativeString
import site.remlit.snowdrop.util.translation
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources._1_poll
import snowdrop.shared.generated.resources._1_quoted_post
import snowdrop.shared.generated.resources.filtered_by_x
import snowdrop.shared.generated.resources.icon_attachment_20px
import snowdrop.shared.generated.resources.icon_filter_alt_24px
import snowdrop.shared.generated.resources.icon_warning_20px
import snowdrop.shared.generated.resources.show_content
import snowdrop.shared.generated.resources.x_attachments

/**
 * Mini status component.
 *
 * @param status Status to show
 * @param showContentEvenIfCw If content should be shown even if a CW is present
 *
 * @since 0.0.1-alpha
 * */
@Composable
fun MiniStatus(
	status: Status,
	showContentEvenIfCw: Boolean = false,
	filterContext: String? = null
) {
	val navHandler = LocalNavController.current
	val statusStateController = LocalStatusStateController.current
	val applicableFilters = status.filtered?.filter {
		filterContext != null && it.filter.context.contains(filterContext)
	}.orEmpty()
	val filterStateKey = "${filterContext ?: "none"}:${status.id}"
	val isHiddenFilter = applicableFilters.any { it.filter.filterAction == "hide" }
	var isFilterVisible by remember(filterStateKey, applicableFilters) {
		mutableStateOf(
			applicableFilters.isEmpty() || statusStateController.filtered.getOrElse(filterStateKey) {
				statusStateController.defaultFilteredValue
			}
		)
	}

	if (!statusStateController.filtered.containsKey(filterStateKey))
		statusStateController.filtered[filterStateKey] = statusStateController.defaultFilteredValue

	if (isHiddenFilter) return

	Column(
		modifier = Modifier.fillMaxWidth()
			.clip(RoundedCornerShape(10.dp))
			.border(1.dp, borderOnBackgroundColor(), RoundedCornerShape(10.dp))
			.clickable(onClick = {
				navHandler.navigate(ThreadRoute(status.id))
			})
	) {
	if (!isVisible && filtered && !isHiddenFilter) {
		Column(
			modifier = Modifier.fillMaxWidth()
				.clickable { filteredState[filterStateKey] = true }
		) {
			Row(
				modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)
					.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(painterResource(Res.drawable.icon_filter_alt_24px), null)

				Column {
					Text(
						translation(
							Res.string.filtered_by_x,
							mapOf("filters" to AnnotatedString(
								applicableFilters.joinToString { "${it.filter.title}" }
							))
						)
					)
					Text(
						translation(Res.string.show_content),
						fontSize = 13.sp
					)
				}
			} else Column(modifier = Modifier.padding(10.dp)) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(5.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Avatar(status.account!!, smaller = true)
				HtmlContent(
					status.account.displayName(),
					emojis = status.account.emojis,
					fontWeight = FontWeight.Bold,
					simple = true,
					maxLines = 1,
					modifier = Modifier.weight(1f)
				)

				Row(
					horizontalArrangement = Arrangement.spacedBy(5.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						"${status.getCreatedAtTimestamp()?.toRelativeString(short = true)}",
						fontSize = 13.sp
					)
					Visibility(status.visibility!!)
				}
			}

			@Composable
			fun Content() {
				Column(
					modifier = Modifier.padding(top = 5.dp),
					verticalArrangement = Arrangement.spacedBy(5.dp)
				) {
					HtmlContent(
						string = status.content ?: "",
						emojis = status.emojis,
						mentions = status.mentions,
						maxLines = 3,
						simple = true
					)


					val attachmentStrings = mutableListOf<AnnotatedString>()

					if (status.mediaAttachments.isNotEmpty())
						attachmentStrings.add(translation(
							Res.plurals.x_attachments,
							quantity = status.mediaAttachments.size,
							mapOf("count" to AnnotatedString("${status.mediaAttachments.size}"))
						))

					if (status.poll != null)
						attachmentStrings.add(translation(Res.string._1_poll))

					if (status.quote != null || status.quotedStatus != null)
						attachmentStrings.add(translation(Res.string._1_quoted_post))

					if (attachmentStrings.isNotEmpty())
						Row(
							horizontalArrangement = Arrangement.spacedBy(5.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							Icon(
								painterResource(Res.drawable.icon_attachment_20px), null,
								tint = MaterialTheme.colorScheme.onSurfaceVariant
							)

							Text(
								buildAnnotatedString {
									withStyle(style = SpanStyle(
										fontSize = 14.sp,
										color = MaterialTheme.colorScheme.onSurfaceVariant
									)) {
										attachmentStrings.forEach {
											append(it)
											if (attachmentStrings.indexOf(it) != (attachmentStrings.size - 1))
												append(", ")
										}
									}

									toAnnotatedString()
								}
							)
						}
				}
			}

			if (!status.spoilerText.isNullOrBlank()) {
				Row(
					modifier = Modifier.padding(top = 5.dp),
					horizontalArrangement = Arrangement.spacedBy(5.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Icon(painterResource(Res.drawable.icon_warning_20px), null)
					Text(
						status.spoilerText,
						fontWeight = FontWeight.Medium
					)
				}

				if (showContentEvenIfCw && status.content != null) Content()
			} else if (status.content != null) Content()
		}
	}
}
