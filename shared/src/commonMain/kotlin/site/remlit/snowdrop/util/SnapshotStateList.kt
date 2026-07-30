package site.remlit.snowdrop.util

import androidx.compose.runtime.snapshots.SnapshotStateList
import site.remlit.snowdrop.model.IdentifiableObject
import site.remlit.snowdrop.util.log.warn

/**
 * Update a timeline with a new version of an object or remove it if it is null.
 *
 * @param existing Existing timeline item
 * @param new New timeline item, or null to remove existing
 *
 * @since 0.0.6-alpha
 * */
fun <T : IdentifiableObject<String>> SnapshotStateList<T>.update(existing: T, new: T?) {
	if (new == null) this.remove(existing)
	else {
		val index = this.indexOf(existing)
		if (index >= 0) this[index] = new
		else warn { "(SnapshotStateList.update) item ${existing.id} requested an update but couldn't be satisfied, index is $index" }
	}
}
