package site.remlit.snowdrop.model.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import site.remlit.snowdrop.model.IdentifiableObject

class TimelineViewModel<T : IdentifiableObject<String>> : ViewModel() {
	val timelineItems = mutableStateListOf<T>()
}
