package site.remlit.snowdrop.util

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

val bgScope = CoroutineScope(Dispatchers.Default + CoroutineName("Background"))
val bgIOScope = CoroutineScope(Dispatchers.IO + CoroutineName("BackgroundIO"))

/**
 * Run block in the background on a default dispatcher thread.
 * */
fun bg(block: suspend () -> Unit) = bgScope.launch { block() }

/**
 * Run block in the background on an IO dispatcher thread.
 * */
fun bgIO(block: suspend () -> Unit) = bgIOScope.launch { block() }
