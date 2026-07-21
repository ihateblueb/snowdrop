package site.remlit.snowdrop.model

/**
 * Interface allowing for validation of a request body before
 * the request actually posts.
 * @since 0.0.2-alpha
 * */
interface Validatable {
	/** Ensure instance of class is valid, otherwise throw an exception. */
	fun validate()
}
