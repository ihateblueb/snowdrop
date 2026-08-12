package site.remlit.snowdrop.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.github.kdroidfilter.composemediaplayer.InitialPlayerState
import io.github.kdroidfilter.composemediaplayer.VideoPlayerState
import io.github.kdroidfilter.composemediaplayer.VideoPlayerSurface
import io.github.kdroidfilter.composemediaplayer.rememberVideoPlayerState
import org.jetbrains.compose.resources.painterResource
import snowdrop.shared.generated.resources.Res
import snowdrop.shared.generated.resources.icon_play_arrow_24px
import snowdrop.shared.generated.resources.icon_replay_24px

@Composable
fun VideoPlayer(
	url: String,
	initialPlayerState: InitialPlayerState = InitialPlayerState.PAUSE,
	showProgress: Boolean = false,
	clickToPause: Boolean = false,
	onPlayerStateChange: (VideoPlayerState) -> Unit = {}
) {
	/*
	val playerState = rememberVideoPlayerState(cacheConfig = CacheConfig(enabled = true))
	playerState.openUri(url, initializeplayerState = initialPlayerState)

	var playbackEnded by remember { mutableStateOf(false) }
	playerState.onPlaybackEnded = { playbackEnded = true }

	fun togglePlayer() {
		if (playerState.isPlaying) playerState.pause()
		else if (playbackEnded) {
			playerState.restart()
			playerState.play()
			playbackEnded = false
		} else playerState.play()

		onPlayerStateChange(playerState)
	}

	Column(
		verticalArrangement = Arrangement.spacedBy(10.dp)
	) {
		Box(
			modifier = Modifier.fillMaxWidth(),
			contentAlignment = Alignment.Center
		) {
			VideoPlayerSurface(
				playerState = playerState,
				modifier = Modifier.fillMaxSize().let {
						if (clickToPause) it.clickable { togglePlayer() }
						else it
					},
				contentScale = ContentScale.Fit,
			) {
				Column {
					Box(
						modifier = Modifier.fillMaxSize().weight(1f),
						contentAlignment = Alignment.Center
					) {
						if (playerState.isLoading) {
							CircularProgressIndicator()
						} else {
							if (!playerState.isPlaying && playbackEnded) {
								FilledTonalIconButton(onClick = { togglePlayer() }) {
									Icon(painterResource(Res.drawable.icon_replay_24px), null)
								}
							} else if (!playerState.isPlaying) {
								FilledTonalIconButton(onClick = { togglePlayer() }) {
									Icon(painterResource(Res.drawable.icon_play_arrow_24px), null)
								}
							}
						}
					}

					if (showProgress)
						Row(
							modifier = Modifier.padding(all = 10.dp)
						) {
							Slider(
								value = playerState.sliderPos,
								onValueChange = { playerState.seekStart(it) },
								onValueChangeFinished = { playerState.seekFinished() },
								valueRange = 0f..1000f
							)
						}
				}
			}
		}
	}
	*/
}
