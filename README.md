# Snowdrop

An attempt to make a Mastodon client that is multiplatform (iOS and Android)
and supports extensions brought by Mastodon API compatible software
like Iceshrimp.NET.

Uses Material 3 (supporting dynamic color schemes) for UI and icons.

## Screenshots

<div style="display: flex; align-content: center; gap: 10px;">
	<img src=".github/assets/timeline.png" height="250px">
	<img src=".github/assets/settings.png" height="250px">
	<img src=".github/assets/account_switcher.png" height="250px">
	<img src=".github/assets/profile_top.png" height="250px">
	<img src=".github/assets/profile_bottom.png" height="250px">
</div>

## Contributing

You can see instructions and helpful tips on contributing in `./CONTRIBUTING.md`.
Pull requests are welcome!

## Acknowledgments

[outadoc modified](https://github.com/coil-kt/coil/issues/2347#issuecomment-2408047810) a 
[multiplatform decoder written by colinrtwhite](https://github.com/coil-kt/coil/issues/2347#issuecomment-2392764750) which
Snowdrop uses to handle GIFs on iOS. This code is used in `shared/src/iosMain/kotlin/site/remlit/snowdrop/util/config/MediaDecoders.ios.kt`.
