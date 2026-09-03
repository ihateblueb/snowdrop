package site.remlit.snowdrop.util

import kotlin.uuid.Uuid

/**
 * Generate a Snowdrop format ID ("_S-" appended before a random UUID).
 * Use for IDs for Snowdrop specific things, like drafts or logged in accounts.
 *
 * Useful for quickly determining if an ID is internal, because sometimes they
 * get confused for API IDs.
 *
 * @since 0.0.8-alpha
 * */
fun generateSnowdropId() = "_S-${Uuid.random()}"
