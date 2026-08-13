package site.remlit.snowdrop.model.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import site.remlit.snowdrop.model.IdentifiableObject

class TimelineViewModel<T : IdentifiableObject<String>> : ViewModel() {
	val timelineItems = mutableStateListOf<T>()
}

fun <T: IdentifiableObject<String>> timelineViewModelFactory(string: String = "") = viewModelFactory {
	initializer { TimelineViewModel<T>() }
}
