package site.remlit.snowdrop.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp

@Composable
fun getScreenWidth(): Int  = LocalWindowInfo.current.containerSize.width

@Composable
fun getScreenWidthDp(): Dp = with(LocalDensity.current) { return getScreenWidth().toDp() }

@Composable
fun getScreenHeight(): Int = LocalWindowInfo.current.containerSize.height

@Composable
fun getScreenHeightDp(): Dp = with(LocalDensity.current) { return getScreenHeight().toDp() }
