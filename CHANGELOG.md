# Snowdrop Changelog

## [Unreleased (0.0.8-alpha)]

### Added
- Support for "local" visibility (Akkoma, Pleroma, and Iceshrimp.NET)
- Ability to react with Unicode emojis (by typing them into the emoji picker's search box)
- Loading indicator to "Liked by" / "Boosted by" / "Reacted by" pages

### Fixed
- Notifications marked as read when you accept a follow request/bite back
- Bite back status shown properly
- Boost button now filled when boosted
- Use proper backstack (i.e. fix being able to go back to login and other buggy-looking behavior)
- Fixed error shown on snackbar when not logged in
- Fixed emojis not being detected properly via regex
- Fixed Brazillian Portuguese and Simplified Chinese not showing up in Android's app-specific language picker
- Disabled boosting posts with direct visibility
- Android debug builds now have a unique name, icon, and package id

## [0.0.7-alpha]

### Added
- Unread notifications badge in navigation bar
- Marking notifications as read on notifications page
- Option to hide notifications badge under "Wellness"
- Ability to swap positions of "send post" button and character limit indicator (Moshidon-style)
- Ability to set a specific language for Snowdrop on Android (in system settings)
- "Likes" tab to current user profile, and, if supported (on Pleroma), others' profiles when permitted

### Fixed
- iOS race condition crash with alt text bottom sheet
- Login with snac2
- Startup crash on Android 7-11
- Dropped minsdk version so it runs on Android 6
- Rule numbering no longer uses IDs because they aren't ints starting at 1 for every software
- Post dropdowns remaining on screen when navigating to "Show likes/boosts/reactions" pages
- Filters with "hide" action are now hidden
- Use auth when fetching emojis
- Don't make timeline requests with max_id of previous timeline
- Alt text button is now disabled if there is no alt
- iOS target reduced to 15.6 (may require workaround to get working though)
- Boost button is now disabled if you can't boost
- CW field is now focused when the field is shown in the composer
- Visibility is now properly shown on boosted posts
- Search box now uses field state and horizontally scrolls
- Fixed keyboard opening if explore tab was long-pressed and you're returning from a post

## [0.0.6-alpha]

### Added
- New logging, including a log viewing page and copy button
- List timelines
- Hide media viewer decorations when you zoom in
- Emoji tooltips with their names
- Thread view has a button to toggle all content warnings for all posts in a thread
- Error handling and feedback for accepting and rejecting follow requests
- Timeline locking, posts on a timeline cannot be interacted with while the timeline is locked
- Pinning and unpinning posts
- Pinned posts on profile page
- Post editing
- Edited post indicator (* next to timestamp)
- M3 Expressive menus, animations
- Reply indicators showing who's being replied to
- Post filtering
- Reordering of logged in accounts
- Video attachment viewing, controls in the detailed view
- Search in emoji picker
- Support feature detection with net.iceshrimp.bites
- Button to open media in browser, even when it's supported in-app
- Reply bar at the bottom of the thread viewer

### Fixed
- Timelines not updating properly
- Emojis missing from places where they belong
- Weird padding when having content warning field in focus in compose page
- Content warning state persists when posts go off-screen
- Timestamps on notifications are properly aligned and update like post timestamps do
- Large media uploads no longer cause an out of memory crash
- Plural translation strings now done properly
- Relative time translation strings are now also done properly
- Predictive back is enabled again
- iOS crash in Compose view
- Thread view title bar emoji rendering
- Use smaller broken image icon for small and smaller avatars
- AMOLED dark theme border and divider contrast
- Spacing and padding in follow request and bite notifications
- Content warning field in the compose view no longer limited to one line
- Crash related to the alt text bottom sheet

## [0.0.5-alpha]

### Added
- Search for posts and accounts
- Subtle haptics on clicking navigation bar tabs
- Success and error haptics for sending a post
- AccountRow component for consistency between places that share the simple account row with avatar, display name, and handle
- Long press on Explore to automatically focus search bar
- Broken avatars now show an icon to indicate they are broken rather than taking a long time to load
- Setting to always show compose button regardless of scroll direction
- Setting to hide navigation bar labels
- Adopt AccountRow in more places for consistency
- Mutuals/Follows you indicator on profiles
- /api/v2/instance support for improved feature detection
- Attachment, poll, and quote post indicator on mini status
- Poll viewing and voting on statuses
- Translations for relative time
- Account picker list component for use across the app
- Account picker on login page, so if you add an account and change your mind or log out you can open your other accounts easily
- Existing account check & handling to login
- Redesigned settings with subpages
- Uploading images and videos to statuses
- Post button now shows a progress indicator while post is sending
- Emojis in post bodies, display names, notifications, and profiles

### Fixed
- Awkward naming of SnackbarController, renamed it LocalSnackbarController
- Spacing of timeline header icon to be more consistent with navigation icons
- Hide account switcher after selecting an account
- Only show reactions notification filter when that feature is available
- Remember explore page state
- Make debug page easier to get to, it now toggles a persistent button after the five clicks
- Layout of compose view, items will no longer be crushed when the keyboard is up and media is added, and the bottom bar now floats
- Large gap at the top of screens on Android versions before edge to edge enforcement

## [0.0.4-alpha] - 2026/07/19

### Added
- App icon
- "About Snowdrop" page in settings
- Ability to reorder the items in the navigation bar
- Lots of translations!
- Recently Used category at the top of the emoji picker
- An expanded media view which shows alt text at the bottom as a card
- Better handling of unsupported media types, allowing users to open them in their browsers
- StatusMediaAttachment component for reusing the same logic across status media previews and the full viewer
- transitionedComposable function which automatically applies correct transitions to the page
- Notification filtering selection in notifications page (potentially going to be redesigned, not set in stone yet!)
- Remove debug button in settings, instead open debug after clicking the version information on the About Snowdrop page 5 times
- Soft vibration on starting a refresh of a timeline
- Vibrate on click of certain actions (bite action button, post menu delete, bookmark, etc.) and vibrate and show snackbar on error

### Fixed
- AMOLED theme has better contrast on some surfaces
- Translation system uses named parameters now
- Misc documentation and other small codebase improvements
- Long pressing the Profile button doing nothing instead of opening the account switcher
- Made animations for dropdown parts of the settings view less awkward
- Improved page animations
- Made fetchers run on IO dispatcher
- getCurrentAccountObjectFlow not re-emitting when current account changes
- Certain mutable states not being wrapped in remember

## [0.0.3-alpha] - 2026/07/05

### Added
- AMOLED dark theme
- Haptics for copying links, bookmarking, and selecting timelines
- CW state shown with filled/unfilled icon in composer
- Option to swap order of notifications and explore tabs
- Compose view now focuses on the text field when opened

### Fixed
- Crash when app is put into the background
- Don't allow biting your own posts
- Emojis now get inserted wherever the cursor is

## [0.0.2-alpha] - 2026/07/04

### Added
- Confirmation dialogs to follow/unfollow
- Ability to bite posts (Iceshrimp.NET)
- Ability to bite users (Iceshrimp.NET)
- Ability to bite back (Iceshrimp.NET)
- Timelines to profile page
- Images in posts
- Bookmarks timeline
- About instance page
- Ability to edit your profile
- Ability to delete your posts
- "Reset" button on oauth callback page in case something goes wrong
- Persistent timeline state
- Post timestamps now update every 10 seconds
- Make default visibility a per-user setting
- Add host to account switcher

### Fixed
- Don't include dependency metadata in built APKs
- Contrast on like button in light mode
- Bubble timeline on iceshrimp-js
- State performance bugs

## [0.0.1-alpha] - 2026/07/01
Initial alpha release, too much to mention here. Check out the [feature matrix](https://github.com/ihateblueb/snowdrop/wiki/Feature-Matrix/672b7003dd142f90466358e19831a45a5dfeaba0) as it was when this release was published to see what was supported :3
