# Snowdrop Changelog

## [Unreleased (0.0.5-alpha)]

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

### Fixed
- Awkward naming of SnackbarController, renamed it LocalSnackbarController
- Spacing of timeline header icon to be more consistent with navigation icons
- Hide account switcher after selecting an account
- Only show reactions notification filter when that feature is available
- Remember explore page state

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
