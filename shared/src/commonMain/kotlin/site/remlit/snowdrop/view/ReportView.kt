package site.remlit.snowdrop.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.ktor.util.reflect.instanceOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import site.remlit.snowdrop.api.accounts.updateCredentials
import site.remlit.snowdrop.api.report
import site.remlit.snowdrop.api.statuses.getStatusFavouritedBy
import site.remlit.snowdrop.api.statuses.getStatusReactions
import site.remlit.snowdrop.api.statuses.getStatusRebloggedBy
import site.remlit.snowdrop.component.AccountRow
import site.remlit.snowdrop.component.NavigationBackButton
import site.remlit.snowdrop.component.ViewSurface
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.model.request.ReportRequest
import site.remlit.snowdrop.model.request.UpdateCredentialsRequest
import site.remlit.snowdrop.util.LocalNavController
import site.remlit.snowdrop.util.LocalSnackbarController
import site.remlit.snowdrop.util.cache.fetchAccount
import site.remlit.snowdrop.util.cache.fetchInstance
import site.remlit.snowdrop.util.cache.fetchStatus
import site.remlit.snowdrop.util.cache.fetchStatusOrNull
import site.remlit.snowdrop.util.getFeature
import site.remlit.snowdrop.util.translation
import site.remlit.snowdrop.util.updateCurrentAccountObject
import site.remlit.snowdrop.util.vibrateError
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.any_info
import snowdrop.shared.generated.resources.category
import snowdrop.shared.generated.resources.comment
import snowdrop.shared.generated.resources.forward_to_remote_instance
import snowdrop.shared.generated.resources.illegal_content
import snowdrop.shared.generated.resources.other
import snowdrop.shared.generated.resources.reporting_x
import snowdrop.shared.generated.resources.rule_violation
import snowdrop.shared.generated.resources.rules
import snowdrop.shared.generated.resources.save
import snowdrop.shared.generated.resources.send_report
import snowdrop.shared.generated.resources.spam
import snowdrop.shared.generated.resources.which_rules
import kotlin.collections.mutableListOf

@Composable
fun ReportView(
	accountId: String,
	statusId: String? = null
) = ViewSurface {
	val navHandler = LocalNavController.current
	val snackbarHandler = LocalSnackbarController.current
	val coroutineScope = rememberCoroutineScope()

	val status by remember { fetchStatusOrNull(statusId, snackbarHandler) }
		.collectAsStateWithLifecycle(null)
	val account by remember { fetchAccount(accountId, snackbarHandler) }
		.collectAsStateWithLifecycle(null)
	val instance by remember { fetchInstance() }
		.collectAsStateWithLifecycle(null)


	var comment by remember { mutableStateOf("") }
	var shouldForward by remember { mutableStateOf(false) }
	var category by remember { mutableStateOf("other") }
	val rules = mutableListOf<String>()

	var ruleKey by remember { mutableStateOf(0) }

	TopAppBar(
		navigationIcon = { NavigationBackButton() },
		title = {
			Text(translation(Res.string.reporting_x, mapOf("handle" to AnnotatedString("@${account?.acct}"))))
		},
		actions = {
			FilledTonalButton(
				onClick = {
					coroutineScope.launch {
						navHandler.popBackStack()

						report(ReportRequest(
							accountId = accountId,
							statusIds = if (statusId != null) listOf(statusId) else listOf(), // todo: make it so we can pick more posts. too lazy rn
							forward = shouldForward,
							comment = comment,
							ruleIds = rules.ifEmpty { null }
						))
					}
				},
				enabled = (statusId == null || status != null) && account != null
			) {
				Text(stringResource(Res.string.send_report))
			}
		}
	)

	if ((statusId != null && status == null) || account == null || instance == null) {
		Column(
			modifier = Modifier.fillMaxHeight().fillMaxWidth(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			CircularProgressIndicator()
		}
	} else {
		LazyColumn(
			modifier = Modifier.padding(10.dp),
			verticalArrangement = Arrangement.spacedBy(10.dp)
		) {
			item {
				OutlinedTextField(
					value = comment,
					onValueChange = { comment = it },
					label = { Text(stringResource(Res.string.comment)) },
					placeholder = { Text(stringResource(Res.string.any_info)) },
					modifier = Modifier.fillMaxWidth().height(250.dp)
				)
			}

			item {
				Row(
					horizontalArrangement = Arrangement.spacedBy(5.dp),
					verticalAlignment = Alignment.CenterVertically,
					modifier = Modifier
						.fillMaxWidth()
						.clickable(onClick = { shouldForward = !shouldForward })
				) {
					IconButton(
						onClick = { shouldForward = !shouldForward }
					) {
						Checkbox(
							checked = shouldForward,
							onCheckedChange = { shouldForward = !shouldForward }
						)
					}

					Text(stringResource(Res.string.forward_to_remote_instance))
				}
			}

			if (getFeature("report_categories")) {
				item {
					Text(
						text = stringResource(Res.string.category),
						fontSize = 13.sp
					)
				}

				item {
					Row(
						horizontalArrangement = Arrangement.spacedBy(5.dp),
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier
							.fillMaxWidth()
							.clickable(onClick = { category = "spam" })
					) {
						IconButton(
							onClick = { category = "spam" }
						) {
							RadioButton(
								selected = category == "spam",
								onClick = { category = "spam" }
							)
						}

						Text(stringResource(Res.string.spam))
					}
				}

				item {
					Row(
						horizontalArrangement = Arrangement.spacedBy(5.dp),
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier
							.fillMaxWidth()
							.clickable(onClick = { category = "legal" })
					) {
						IconButton(
							onClick = { category = "legal" }
						) {
							RadioButton(
								selected = category == "legal",
								onClick = { category = "legal" }
							)
						}

						Text(stringResource(Res.string.illegal_content))
					}
				}

				if (instance != null && instance!!.rules.isNotEmpty()) {
					item {
						Row(
							horizontalArrangement = Arrangement.spacedBy(5.dp),
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier
								.fillMaxWidth()
								.clickable(onClick = { category = "violation" })
						) {
							IconButton(
								onClick = { category = "violation" }
							) {
								RadioButton(
									selected = category == "violation",
									onClick = { category = "violation" }
								)
							}

							Text(stringResource(Res.string.rule_violation))
						}
					}
				}

				item {
					Row(
						horizontalArrangement = Arrangement.spacedBy(5.dp),
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier
							.fillMaxWidth()
							.clickable(onClick = { category = "other" })
					) {
						IconButton(
							onClick = { category = "other" }
						) {
							RadioButton(
								selected = category == "other",
								onClick = { category = "other" }
							)
						}

						Text(stringResource(Res.string.other))
					}
				}

				if (category == "violation" && instance!!.rules.isNotEmpty()) {
					item {
						Text(
							stringResource(Res.string.which_rules),
							fontWeight = FontWeight.Medium,
							fontSize = 13.sp,
							modifier = Modifier.padding(10.dp)
						)
					}

					instance!!.rules.forEachIndexed { i, rule ->
						item {
							// this is annoying and breaks the animation but idk how else to do this
							key(ruleKey) {
								Row(
									horizontalArrangement = Arrangement.spacedBy(5.dp),
									verticalAlignment = Alignment.CenterVertically,
									modifier = Modifier
										.fillMaxWidth()
										.clickable(onClick = {
											if (rules.contains(rule.id)) rules.remove(rule.id)
											else rules.add(rule.id)
											ruleKey++
										})
								) {
									IconButton(
										onClick = {
											if (rules.contains(rule.id)) rules.remove(rule.id)
											else rules.add(rule.id)
											ruleKey++
										}
									) {
										Checkbox(
											checked = rules.contains(rule.id),
											onCheckedChange = {
												if (rules.contains(rule.id)) rules.remove(rule.id)
												else rules.add(rule.id)
												ruleKey++
											}
										)
									}

									Column(
										verticalArrangement = Arrangement.spacedBy(5.dp)
									) {
										Text("${i+1}. ${rule.text}")
										if (!rule.hint.isNullOrBlank()) Text(rule.hint, color = MaterialTheme.colorScheme.onSurfaceVariant)
									}
								}
							}

						}
					}
				}
			}
		}
	}
}
