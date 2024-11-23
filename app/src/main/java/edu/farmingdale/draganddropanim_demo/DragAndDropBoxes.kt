@file:OptIn(ExperimentalFoundationApi::class)

package edu.farmingdale.draganddropanim_demo

import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Offset.Companion.Infinite
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.farmingdale.draganddropanim_demo.ui.theme.DragAndDropAnim_DemoTheme


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragAndDropBoxes(modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(true) }
    var rectOffset by remember { mutableStateOf(IntOffset(130, 300)) }
    var dragBoxIndex by remember { mutableIntStateOf(0) }
    var prevDragIndex by remember { mutableIntStateOf(0)}
    var hasMoved by remember { mutableStateOf(false)}
    var flipRotate by remember { mutableStateOf(false) }
    var parentSize by remember { mutableStateOf(Size.Zero) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Row(
            modifier = modifier
                .fillMaxWidth()
                .weight(0.35f)
        ) {
            val boxCount = 4

            repeat(boxCount) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(10.dp)
                        .border(1.dp, Color.Black)
                        .dragAndDropTarget(
                            shouldStartDragAndDrop = { event ->
                                event
                                    .mimeTypes()
                                    .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                            },
                            target = remember {
                                object : DragAndDropTarget {
                                    override fun onDrop(event: DragAndDropEvent): Boolean {
                                        isPlaying = !isPlaying
                                        prevDragIndex = dragBoxIndex
                                        dragBoxIndex = index
                                        hasMoved = true
                                        flipRotate = !flipRotate
                                        return true
                                    }
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    this@Row.AnimatedVisibility(
                        visible = index == dragBoxIndex,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Icon",
                            tint = Color.Blue,
                            modifier = Modifier
                                .fillMaxSize()
                                .dragAndDropSource {
                                    detectTapGestures(
                                        onLongPress = { offset ->
                                            startTransfer(
                                                transferData = DragAndDropTransferData(
                                                    clipData = ClipData.newPlainText(
                                                        "drag-icon",
                                                        "ArrowForward"
                                                    )
                                                )
                                            )
                                        }
                                    )
                                }
                        )


                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .background(Color.Red)
                .onSizeChanged { size ->
                    // Track parent container's size
                    parentSize = Size(size.width.toFloat(), size.height.toFloat())
                },
        ) {


            if(hasMoved){
                val moveDistance = 200
                rectOffset = when {
                    dragBoxIndex < prevDragIndex -> IntOffset( rectOffset.x - moveDistance, rectOffset.y)
                    dragBoxIndex > prevDragIndex -> IntOffset(rectOffset.x + moveDistance , rectOffset.y)
                    else -> rectOffset
                }
                hasMoved = false

            }

            val rectRotation by animateFloatAsState(
                targetValue = if (flipRotate) 360f else 0f,
                animationSpec = repeatable(
                    iterations =  1,
                    animation = tween(durationMillis = 3000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = ""
            )




            println("New rectOffset $rectOffset")

            Box(
                modifier = Modifier
                    .offset { rectOffset }
                    .rotate(rectRotation)
                    .size(55.dp, 100.dp)
                    .background(Color.Green)
            )
        }

        // Reset button
        Button(
            onClick = {
                isPlaying = false
                rectOffset = IntOffset(
                    x = (parentSize.width / 2 - 55/ 2).toInt(),
                    y = (parentSize.height / 2 - 100 / 2).toInt()
                )
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Reset")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DragAndDropPreview() {
    DragAndDropAnim_DemoTheme {
        DragAndDropBoxes(modifier = Modifier)
    }
}

