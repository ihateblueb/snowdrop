package site.remlit.snowdrop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import site.remlit.snowdrop.api.statuses.voteInPoll
import site.remlit.snowdrop.model.Status
import site.remlit.snowdrop.util.LocalSnackbarController
import site.remlit.snowdrop.util.annotatedString.simpleAnnotatedString
import site.remlit.snowdrop.util.extension.toRelativeString
import site.remlit.snowdrop.util.translation
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.poll_ended
import snowdrop.shared.generated.resources.ends_in_x
import snowdrop.shared.generated.resources.ends_soon
import snowdrop.shared.generated.resources.hide_results
import snowdrop.shared.generated.resources.show_results
import snowdrop.shared.generated.resources.vote
import snowdrop.shared.generated.resources.x_vote_s_
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@Composable
fun Poll(status: Status) {
	val coroutineScope = rememberCoroutineScope()
	val snackbarController = LocalSnackbarController.current

	var poll by remember { mutableStateOf(status.poll) }
	LaunchedEffect(status.poll) { poll = status.poll }

	if (poll != null) {
		Column(
			verticalArrangement = Arrangement.spacedBy(10.dp)
		) {
			var showResults by remember { mutableStateOf(false) }
			var votersCount = 0L
			poll!!.options.forEach { votersCount += it.votesCount }

			val selection = remember { mutableStateListOf<Int>() }

			val canVote = !poll!!.expired && !poll!!.voted

			Column(
				verticalArrangement = Arrangement.spacedBy(5.dp)
			) {
				poll!!.options.forEachIndexed { index, option ->
					val interactionSource = remember { MutableInteractionSource() }

					Row(
						modifier = Modifier.clip(RoundedCornerShape(100))
							.background(MaterialTheme.colorScheme.surfaceContainerHigh)
							.clickable(
								enabled = canVote,
								interactionSource = interactionSource,
								onClick = {
									if (selection.contains(index)) selection.remove(index)
									else if (poll!!.multiple) selection.add(index)
									else { selection.clear(); selection.add(index) }
								}
							)
							.fillMaxWidth(),
						verticalAlignment = Alignment.CenterVertically
					) {
						Box(
							modifier = Modifier.fillMaxWidth()
						) {
							Row(
								modifier = Modifier.fillMaxSize().zIndex(2f),
								horizontalArrangement = Arrangement.spacedBy(5.dp),
								verticalAlignment = Alignment.CenterVertically
							) {
								RadioButton(
									selected = selection.contains(index) || poll!!.ownVotes.contains(index),
									interactionSource = interactionSource, // inherits everything from the .clickable above
									onClick = {}, 						   // ^^
								)

								Text(option.title)
							}

							if ((showResults || poll!!.voted || poll!!.expired) && option.votesCount > 0) {
								Row(
									modifier = Modifier.matchParentSize().zIndex(1f)
								) {
									Box(
										modifier = Modifier.fillMaxHeight()
											.weight(option.votesCount.toFloat())
											.background(MaterialTheme.colorScheme.primaryContainer)
									)
									val remaining = votersCount - option.votesCount
									if (remaining > 0)
										Box(
											modifier = Modifier.fillMaxHeight()
												.weight(remaining.toFloat())
												.background(Color.Transparent)
										)
								}
							}
						}
					}
				}
			}

			// footer
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(5.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Box(
					modifier = Modifier.weight(1f),
				) {
					FlowRow(
						horizontalArrangement = Arrangement.spacedBy(10.dp)
					) {
						if (poll!!.expired) {
							Text(
								translation(Res.string.poll_ended),
								color = MaterialTheme.colorScheme.onSurfaceVariant,
								fontSize = 14.sp,
							)
						} else if (poll!!.expiresAt != null) {
							var expireTimerKey by remember { mutableStateOf(0) }
							LaunchedEffect(Unit) { expireTimerKey++; delay(10.seconds) }

							key(expireTimerKey) {
								Text(
									translation(
										Res.string.ends_in_x,
										mapOf("time" to Instant.parse(poll!!.expiresAt!!)
											.toRelativeString(inverse = true, nowAlternate = Res.string.ends_soon))
									),
									color = MaterialTheme.colorScheme.onSurfaceVariant,
									fontSize = 14.sp,
								)
							}
						}

						Text(
							translation(
								Res.string.x_vote_s_,
								mapOf("count" to simpleAnnotatedString("$votersCount"))
							),
							color = MaterialTheme.colorScheme.onSurfaceVariant,
							fontSize = 14.sp,
						)
					}
				}

				if (canVote) {
					Row(
						modifier = Modifier.wrapContentWidth(),
						horizontalArrangement = Arrangement.spacedBy(5.dp),
					) {
						OutlinedButton(
							onClick = { showResults = !showResults }
						) {
							if (showResults) Text(translation(Res.string.hide_results))
							else Text(translation(Res.string.show_results))
						}

						Button(
							enabled = selection.isNotEmpty(),
							onClick = {
								coroutineScope.launch {
									val res = voteInPoll(poll!!.id, selection)
									if (res.error || res.response == null) {
										res.handleError(snackbarController)
										return@launch
									}
									// todo: notable thing for timeline state updating
									poll = res.response
								}
							}
						) {
							Text(translation(Res.string.vote))
						}
					}
				}
			}
		}
	}
}
