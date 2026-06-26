package site.remlit.snowdrop.util.extension

import java.text.NumberFormat

actual fun formatNumber(number: Long): String =
	NumberFormat.getInstance().format(number)
