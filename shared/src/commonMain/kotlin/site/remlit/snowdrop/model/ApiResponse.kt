package site.remlit.snowdrop.model

import androidx.compose.material3.SnackbarHostState
import kotlinx.serialization.Serializable
import site.remlit.snowdrop.util.bg

/**
 * Response object for an API request.
 *
 * @param error If an error occurred
 * @param message Error message, if any error
 * @param response Returns specified T, but Unit if no response body
 *
 * @since 0.0.1-alpha
 * */
@Serializable
data class ApiResponse<T>(
	val error: Boolean = false,
	val message: String? = null,
	val response: T? = null
) {
	/**
	 * Handle showing something went wrong to a user.
	 *
	 * @param snackbarController Snackbar controller to show snackbar alert
	 * */
	fun handleError(snackbarController: SnackbarHostState) {
		bg {
			// todo: pass route to this somehow
			if (error) snackbarController.showSnackbar("Error: $message")
			else if (response == null) snackbarController.showSnackbar("Error: Response was null")
		}
	}
}
