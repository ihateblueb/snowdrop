package site.remlit.snowdrop.component

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.cancel
import snowdrop.shared.generated.resources.ok
import kotlin.time.Clock

@Composable
fun TimePickerModal(
	onConfirm: (hour: Int, minute: Int) -> Unit,
	onDismiss: () -> Unit
) {
	val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
	val timePickerState = rememberTimePickerState(initialHour = currentTime.hour, initialMinute = currentTime.minute)

	TimePickerDialog(
		title = {},
		onDismissRequest = {
			onDismiss()
		},
		confirmButton = {
			TextButton(onClick = {
				onConfirm(timePickerState.hour, timePickerState.minute)
			}) {
				Text(stringResource(Res.string.ok))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(stringResource(Res.string.cancel))
			}
		},
		content = {
			TimePicker(
				state = timePickerState
			)
		}
	)
}
