package site.remlit.snowdrop.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp

/**
 * Gets current screen width in pixels.
 * @since 0.0.2-alpha
 * */
@Composable
fun getScreenWidth(): Int  = LocalWindowInfo.current.containerSize.width

/**
 * Gets current screen width in dp.
 * @since 0.0.2-alpha
 * */
@Composable
fun getScreenWidthDp(): Dp = with(LocalDensity.current) { return getScreenWidth().toDp() }

/**
 * Gets current screen height in pixels.
 * @since 0.0.2-alpha
 * */
@Composable
fun getScreenHeight(): Int = LocalWindowInfo.current.containerSize.height

/**
 * Gets current screen height in dp.
 * @since 0.0.2-alpha
 * */
@Composable
fun getScreenHeightDp(): Dp = with(LocalDensity.current) { return getScreenHeight().toDp() }
