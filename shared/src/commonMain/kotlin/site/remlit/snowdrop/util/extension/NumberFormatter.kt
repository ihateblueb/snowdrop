package site.remlit.snowdrop.util.extension

import kotlin.math.round

expect fun formatNumber(number: Long): String

fun Long.toFormatShort(): String {
	return if (this < 1000) "$this"
	else if (this < 1000000) "${(this/1000.0).toSingleDecimal()}k"
	else "${(this/1000000.0).toSingleDecimal()}m"
}

fun Double.toSingleDecimal(): Double =
	round(this * 10.0) / 10.0
