package site.remlit.snowdrop.component

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.cancel
import snowdrop.shared.generated.resources.ok
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
	onConfirm: (Long?) -> Unit,
	onDismiss: () -> Unit
) {
	val currentTime = Clock.System.now()
	val datePickerState = rememberDatePickerState(currentTime.toEpochMilliseconds())

	DatePickerDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(onClick = {
				onConfirm(datePickerState.selectedDateMillis)
			}) {
				Text(stringResource(Res.string.ok))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(stringResource(Res.string.cancel))
			}
		}
	) {
		DatePicker(state = datePickerState)
	}
}
