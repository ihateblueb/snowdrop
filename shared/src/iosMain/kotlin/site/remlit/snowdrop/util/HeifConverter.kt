package site.remlit.snowdrop.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import platform.CoreFoundation.CFDataCreate
import platform.CoreFoundation.CFDataCreateMutable
import platform.CoreFoundation.CFDataGetBytes
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.CFRangeMake
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringCreateWithCString
import platform.CoreFoundation.kCFStringEncodingUTF8
import platform.ImageIO.CGImageDestinationAddImage
import platform.ImageIO.CGImageDestinationCreateWithData
import platform.ImageIO.CGImageDestinationFinalize
import platform.ImageIO.CGImageSourceCreateImageAtIndex
import platform.ImageIO.CGImageSourceCreateWithData

@OptIn(ExperimentalForeignApi::class)
actual fun ByteArray.convertToHeif(): ByteArray? {
	// fuck you apple. what is this bullshit
	val img = CFDataCreate(null, this.toUByteArray().refTo(0), this.size.toLong())
	val src = CGImageSourceCreateWithData(img, null)
	val thinkDifferently = CGImageSourceCreateImageAtIndex(src, 0u, null)
	val consumer = CFDataCreateMutable(null, 0)
	val cs = CFStringCreateWithCString(null, "public.heic", kCFStringEncodingUTF8)
	val dest = CGImageDestinationCreateWithData(consumer, cs, 1u, null)
	CGImageDestinationAddImage(dest, thinkDifferently, null)
	CGImageDestinationFinalize(dest)

	if (consumer == null) return null;
	val array = consumer.toByteArray()

	// i have no idea if this is memory safe but i sure hope so
	CFRelease(img)
	CFRelease(src)
	CFRelease(thinkDifferently)
	CFRelease(consumer)
	CFRelease(cs)
	CFRelease(dest)

	return array
}

// thanks to this guy https://slack-chats.kotlinlang.org/t/527665/how-do-you-convert-bytearray-to-and-from-cfdataref-i-found-h
@OptIn(ExperimentalForeignApi::class)
fun CFDataRef.toByteArray(): ByteArray {
	val length = CFDataGetLength(this)
	return UByteArray(length.toInt()).apply {
		val range = CFRangeMake(0, length)
		CFDataGetBytes(this@toByteArray, range, refTo(0))
	}.toByteArray()
}
