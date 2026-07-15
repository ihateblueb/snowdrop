package site.remlit.snowdrop.util

import site.remlit.snowdrop.model.Platform

/**
 * Determine what platform the app is running on.
 *
 * @since 0.0.1-alpha
 * */
expect fun getPlatform(): Platform
