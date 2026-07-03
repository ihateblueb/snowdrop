# Contributing

## AI contributions

In no way are AI generated pull requests, issues, or code welcome in this code base. You will have your issue or pull
request closed, and we will ignore you if you break this rule.

## What API should I use as a reference?
Always up-to-date Iceshrimp.NET with all non-essential values nullable. Most Iceshrimp.NET instances will have an API
reference at `/swagger`. If you don't know any, you can use https://next.iceshrimp.dev/.

## Shared solutions before platform-specific solutions
Too many platform-specific solutions can cause problems with either platform's implementation
to go unnoticed if a change is only tested on one platform. Always prefer a shared solution over one that
requires platform-specific implementations (`expect`/`actual` functions).

## Adding icons
We use Material Icons, which you can find here: https://fonts.google.com/icons. Icons can be two sizes: 20px or 24px.
To add an icon, click on it, swap the tab to "Android" in the opened sidebar, and download the XML.
Prefix that file with `icon_`, then drop it in `shared/commonMain/composeResources/drawable`.
Change the color from `@android/*` to `#000000`, otherwise the app will instantly crash.

## Using Flow<*> and collecting emissions as a state

Use the collector with lifecycle over the one without. Collectors with lifecycles are better for performance long term.

| Do                                                                                          | Don't                                                                          |
|:--------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------|
| ```val currentAccount by getCurrentAccountObjectFlow().collectAsStateWithLifecycle(null)``` | ```val currentAccount by getCurrentAccountObjectFlow().collectAsState(null)``` |
