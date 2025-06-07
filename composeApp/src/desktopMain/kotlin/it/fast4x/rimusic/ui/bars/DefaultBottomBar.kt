package it.fast4x.rimusic.ui.bars

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeDown
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import extension.formatTimestamp
import it.fast4x.rimusic.enums.ThumbnailRoundness
import player.PlayerController
import kotlin.math.roundToLong

@Composable
fun DefaultBottomBar(modifier: Modifier = Modifier, controller: PlayerController) {

    val state by controller.state.collectAsState()

    val animatedTimestamp by animateFloatAsState(state.timestamp.toFloat())

    Column(
        modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            //Modifier.border(BorderStroke(1.dp, Color.White)),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (state.isPlaying) {
                IconButton(controller::pause) {
                    Icon(
                        Icons.Filled.Pause, "pause media",
                        tint = Color.Green.copy(alpha = 0.6f),
                        modifier = Modifier.size(36.dp)
                    )
                }
            } else {
                IconButton(controller::play) {
                    Icon(
                        Icons.Rounded.PlayArrow, "play media",
                        tint = Color.Green.copy(alpha = 0.6f),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

            Row(
                //Modifier.border(BorderStroke(1.dp, Color.Yellow)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Slider(
                    value = animatedTimestamp,
                    onValueChange = { controller.seekTo(it.roundToLong()) },
                    valueRange = 0f..state.duration.toFloat(),
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color.White,
                        thumbColor = Color.Green.copy(alpha = 0.6f),
                        inactiveTrackColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth(0.8f).padding(horizontal = 16.dp)
                )

                if (state.isMuted || state.volume == 0f) IconButton(controller::toggleSound) {
                    Icon(Icons.AutoMirrored.Rounded.VolumeOff, "volume off",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp))
                }
                else {
                    if (state.volume < .5f) IconButton(controller::toggleSound) {
                        Icon(Icons.AutoMirrored.Rounded.VolumeDown, "volume low",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp))
                    } else IconButton(controller::toggleSound) {
                        Icon(Icons.AutoMirrored.Rounded.VolumeUp, "volume high",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp))
                    }
                }
                Slider(
                    value = state.volume,
                    onValueChange = controller::setVolume,
                    modifier = Modifier.width(300.dp),
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color.White,
                        thumbColor = Color.Green.copy(alpha = 0.6f),
                        inactiveTrackColor = Color.Gray
                    )
                )
            }

        Row(
            Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                state.timestamp.formatTimestamp(),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                state.duration.formatTimestamp(),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

    }

}