package com.xinto.overlappingpanels

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Possible values for [OverlappingPanels]
 */
enum class OverlappingPanelsValue {

    /**
     * Start panel is opened.
     */
    OpenStart,

    /**
     * End panel is opened.
     */
    OpenEnd,

    /**
     * Both panels are closed.
     */
    Closed

}

/**
 * @param initialValue Initial state of the panels, can only be one of [OverlappingPanelsValue]
 * @param confirmStateChange Whether to consume the change.
 */
@ExperimentalMaterialApi
class OverlappingPanelsState(
    initialValue: OverlappingPanelsValue,
    confirmStateChange: (OverlappingPanelsValue) -> Boolean = { true },
) {

    val swipeableState = SwipeableState(
        initialValue = initialValue,
        animationSpec = spring(),
        confirmStateChange = confirmStateChange
    )

    /**
     * Current [value][OverlappingPanelsValue]
     */
    val currentValue
        get() = swipeableState.currentValue

    /**
     * Center panel offset
     */
    val offset
        get() = swipeableState.offset

    val isPanelsClosed
        get() = currentValue == OverlappingPanelsValue.Closed

    val isEndPanelOpen
        get() = currentValue == OverlappingPanelsValue.OpenStart

    val isStartPanelOpen
        get() = currentValue == OverlappingPanelsValue.OpenEnd

    /**
     * Open End Panel with animation.
     */
    suspend fun openStartPanel() {
        swipeableState.animateTo(OverlappingPanelsValue.OpenEnd)
    }

    /**
     * Open End Panel with animation.
     */
    suspend fun openEndPanel() {
        swipeableState.animateTo(OverlappingPanelsValue.OpenStart)
    }

    /**
     * Close panels with animation.
     */
    suspend fun closePanels() {
        swipeableState.animateTo(OverlappingPanelsValue.Closed)
    }

    companion object {

        fun Saver(confirmStateChange: (OverlappingPanelsValue) -> Boolean) =
            Saver<OverlappingPanelsState, OverlappingPanelsValue>(
                save = { it.currentValue },
                restore = { OverlappingPanelsState(it, confirmStateChange) }
            )

    }
}

/**
 * @param initialValue Initial state of the panels, can only be one of [OverlappingPanelsValue]
 * @param confirmStateChange Whether to consume the change.
 */
@ExperimentalMaterialApi
@Composable
fun rememberOverlappingPanelsState(
    initialValue: OverlappingPanelsValue = OverlappingPanelsValue.Closed,
    confirmStateChange: (OverlappingPanelsValue) -> Boolean = { true },
): OverlappingPanelsState {
    return rememberSaveable(saver = OverlappingPanelsState.Saver(confirmStateChange)) {
        OverlappingPanelsState(initialValue, confirmStateChange)
    }
}

/**
 * @param modifier [Modifier]
 * @param panelsState [Panel Controller][OverlappingPanelsState]
 * @param panelStart Content for the start panel (replaced with `panelEnd` for the LTR layout).
 * @param panelCenter Content for the center panel.
 * @param panelEnd Content for the center panel (replaced with `panelStart` for the LTR layout).
 * @param gesturesEnabled Whether to enable swipe gestures.
 * @param velocityThreshold Minimum swipe speed required to open/close side panels.
 * @param resistance Controls how much resistance will be applied when swiping past the bounds.
 * @param sidePanelWidthFraction Maximum width in fractions for side panels to occupy when opened.
 * @param centerPanelAlpha Opacity of the center panel when side panels are closed and opened.
 * @param centerPanelElevation Elevation of the center panel
 */
@ExperimentalMaterialApi
@Composable
fun OverlappingPanels(
    panelStart: @Composable BoxScope.() -> Unit,
    panelCenter: @Composable BoxScope.() -> Unit,
    panelEnd: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    panelsState: OverlappingPanelsState = rememberOverlappingPanelsState(initialValue = OverlappingPanelsValue.Closed),
    gesturesEnabled: Boolean = true,
    velocityThreshold: Dp = 400.dp,
    resistance: (anchors: Set<Float>) -> ResistanceConfig? = { null },
    sidePanelWidthFraction: SidePanelWidthFraction = PanelDefaults.sidePanelWidthFraction(),
    centerPanelAlpha: CenterPanelAlpha = PanelDefaults.centerPanelAlpha(),
    centerPanelElevation: Dp = 8.dp,
) {
    val resources = LocalContext.current.resources

    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr

    BoxWithConstraints(modifier.fillMaxSize()) {
        val fraction =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                sidePanelWidthFraction.portrait()
            } else {
                sidePanelWidthFraction.landscape()
            }

        val offsetValue = (constraints.maxWidth * fraction) + PanelDefaults.MarginBetweenPanels.value

        //TODO make animation configurable
        val animatedCenterPanelAlpha by animateFloatAsState(
            targetValue = if (
                panelsState.offset.value == offsetValue ||
                panelsState.offset.value == -offsetValue
            ) centerPanelAlpha.sidesOpened() else centerPanelAlpha.sidesClosed(),
        )

        val anchors = mapOf(
            offsetValue to OverlappingPanelsValue.OpenEnd,
            0f to OverlappingPanelsValue.Closed,
            -offsetValue to OverlappingPanelsValue.OpenStart
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .swipeable(
                    state = panelsState.swipeableState,
                    orientation = Orientation.Horizontal,
                    velocityThreshold = velocityThreshold,
                    anchors = anchors,
                    enabled = gesturesEnabled,
                    reverseDirection = !isLtr,
                    resistance = resistance(anchors.keys),
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .align(if (isLtr) Alignment.CenterStart else Alignment.CenterEnd)
                    .alpha(if ((isLtr && panelsState.offset.value > 0f) || (!isLtr && panelsState.offset.value < 0f)) 1f else 0f),
                content = if (isLtr) panelStart else panelEnd
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .align(if (isLtr) Alignment.CenterEnd else Alignment.CenterStart)
                    .alpha(if ((isLtr && panelsState.offset.value < 0f) || (!isLtr && panelsState.offset.value > 0f)) 1f else 0f),
                content = if (isLtr) panelEnd else panelStart
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .alpha(animatedCenterPanelAlpha)
                    .offset {
                        IntOffset(
                            x = panelsState.offset.value.roundToInt(),
                            y = 0
                        )
                    }
                    .shadow(centerPanelElevation),
                content = panelCenter
            )
        }
    }
}

interface SidePanelWidthFraction {

    @Composable
    fun portrait(): Float

    @Composable
    fun landscape(): Float

}

interface CenterPanelAlpha {

    @Composable
    fun sidesOpened(): Float

    @Composable
    fun sidesClosed(): Float

}

object PanelDefaults {

    val MarginBetweenPanels = 16.dp

    /**
     * @param portrait Fraction to use when the device is in portrait mode.
     * @param landscape Fraction to use when the device is in landscape mode.
     */
    @Composable
    fun sidePanelWidthFraction(
        portrait: Float = 0.85f,
        landscape: Float = 0.45f,
    ): SidePanelWidthFraction = DefaultSidePanelWidthFraction(
        portrait = portrait,
        landscape = landscape,
    )

    /**
     * @param sidesOpened Alpha to use when any of the side panels are opened
     * @param sidesClosed Alpha to use when any of the side panels are closed
     */
    @Composable
    fun centerPanelAlpha(
        sidesOpened: Float = 0.7f,
        sidesClosed: Float = 1f
    ): CenterPanelAlpha = DefaultCenterPanelAlpha(
        sidesOpened = sidesOpened,
        sidesClosed = sidesClosed,
    )

}

private class DefaultSidePanelWidthFraction(
    private val portrait: Float,
    private val landscape: Float,
) : SidePanelWidthFraction {

    @Composable
    override fun portrait() = portrait

    @Composable
    override fun landscape() = landscape

}

private class DefaultCenterPanelAlpha(
    private val sidesOpened: Float,
    private val sidesClosed: Float,
) : CenterPanelAlpha {

    @Composable
    override fun sidesOpened() = sidesOpened

    @Composable
    override fun sidesClosed() = sidesClosed

}

