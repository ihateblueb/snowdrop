package site.remlit.snowdrop.model

/**
 * Interface for objects that must have an ID field.
 * @since 0.0.2-alpha
 * */
interface IdentifiableObject<T : Any> {
	val id: T
}
