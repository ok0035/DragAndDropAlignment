package com.zerosword.feature_main.ui

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.IntOffset
import com.zerosword.domain.model.SlideState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.IndexOutOfBoundsException
import kotlin.math.roundToInt
import kotlin.math.sign

fun <T>Modifier.dragAndDrop(
    card: T,
    cardList: MutableList<T>,
    itemLength: Float,
    draggingScale: Float = 0.7f,
    updateSlideState: (card: T, slideState: SlideState) -> Unit,
    isDraggedAfterLongPress: Boolean,
    isHorizontal: Boolean = false,
    onStartDrag: () -> Unit,
    onStopDrag: (currentIndex: Int, destinationIndex: Int) -> Unit,
): Modifier = composed {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    pointerInput(Unit) {
        // Wrap in a coroutine scope to use suspend functions for touch events and animation.
        coroutineScope {
            val itemSize = (itemLength * draggingScale).toInt()
            val itemIndex = cardList.indexOf(card)
            val offsetToSlide = itemSize / 4
            var numberOfItems = 0
            var previousNumberOfItems: Int
            var listOffset = 0

            val onDragStart = {
                // Interrupt any ongoing animation of other items.
                launch {
                    offsetX.stop()
                    offsetY.stop()
                }
                onStartDrag()
            }
            val onDrag = { change: PointerInputChange ->
                val horizontalDragOffset = offsetX.value + change.positionChange().x
                launch {
                    offsetX.snapTo(horizontalDragOffset)
                    if(!isHorizontal) return@launch

                    val offsetSign = offsetX.value.sign.toInt()
                    previousNumberOfItems = numberOfItems
                    numberOfItems = calculateNumberOfSlidItemsForHorizontal(
                        offsetX.value * offsetSign,
                        (itemSize * draggingScale).toInt(),
                        offsetToSlide,
                        previousNumberOfItems
                    )

                    if (previousNumberOfItems > numberOfItems) {
                        val nextIndex = itemIndex + previousNumberOfItems * offsetSign
                        val item =
                            if (cardList.size <= nextIndex) cardList.last()
                            else cardList[itemIndex + previousNumberOfItems * offsetSign]
                        updateSlideState(
                            item,
                            SlideState.NONE
                        )
                    } else if (numberOfItems != 0) {
                        try {
                            updateSlideState(
                                cardList[itemIndex + numberOfItems * offsetSign],
                                if (offsetSign == 1) SlideState.UP else SlideState.DOWN
                            )
                        } catch (e: IndexOutOfBoundsException) {
                            numberOfItems = previousNumberOfItems
                            Log.i("DragToReorderHorizontal", "Item is outside or at the edge")
                        }
                    }
                    listOffset = numberOfItems * offsetSign
                }
                val verticalDragOffset = offsetY.value + change.positionChange().y
                launch {
                    offsetY.snapTo(verticalDragOffset)

                    if(isHorizontal) return@launch

                    val offsetSign = offsetY.value.sign.toInt()
                    previousNumberOfItems = numberOfItems
                    numberOfItems = calculateNumberOfSlidItems(
                        offsetY.value * offsetSign,
                        itemSize,
                        offsetToSlide,
                        previousNumberOfItems
                    )

                    if (previousNumberOfItems > numberOfItems) {
                        val nextIndex = itemIndex + previousNumberOfItems * offsetSign
                        val item =
                            if (cardList.size <= nextIndex) cardList.last()
                            else cardList[itemIndex + previousNumberOfItems * offsetSign]
                        updateSlideState(
                            item,
                            SlideState.NONE
                        )
                    } else if (numberOfItems != 0) {
                        try {
                            updateSlideState(
                                cardList[itemIndex + numberOfItems * offsetSign],
                                if (offsetSign == 1) SlideState.UP else SlideState.DOWN
                            )
                        } catch (e: IndexOutOfBoundsException) {
                            numberOfItems = previousNumberOfItems
                            Log.i("DragToReorderVertical", "Item is outside or at the edge")
                        }
                    }
                    listOffset = numberOfItems * offsetSign
                }
                // Consume the gesture event, not passed to external
                if (change.positionChange() != Offset.Zero) change.consume()
            }
            val onDragEnd = {
                launch {
                    if(isHorizontal) offsetY.animateTo(0f) else offsetX.animateTo(0f)
                }
                launch {
                    println("drag end item size -> $itemSize")
                    if(isHorizontal) offsetX.animateTo(itemSize * numberOfItems * offsetX.value.sign)
                    else offsetY.animateTo(itemSize * numberOfItems * offsetY.value.sign)
                    onStopDrag(itemIndex, itemIndex + listOffset)
                }
            }
            if (isDraggedAfterLongPress)
                detectDragGesturesAfterLongPress(
                    onDragStart = { onDragStart() },
                    onDrag = { change, _ -> onDrag(change) },
                    onDragEnd = { onDragEnd() }
                ) else
                while (true) {
                    val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                    awaitPointerEventScope {
                        drag(pointerId) { change ->
                            onDragStart()
                            onDrag(change)
                        }
                    }
                    onDragEnd()
                }
        }
    }
        .offset {
            // Use the animating offset value here.
            IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt())
        }
}

private fun calculateNumberOfSlidItems(
    offsetY: Float,
    itemHeight: Int,
    offsetToSlide: Int,
    previousNumberOfItems: Int
): Int {
    val numberOfItemsInOffset = (offsetY / itemHeight).toInt()
    val numberOfItemsPlusOffset = ((offsetY + offsetToSlide) / itemHeight).toInt()
    val numberOfItemsMinusOffset = ((offsetY - offsetToSlide - 1) / itemHeight).toInt()

    return when {
        (offsetY - offsetToSlide - 1) < 0 -> 0
        (numberOfItemsPlusOffset > numberOfItemsInOffset) -> numberOfItemsPlusOffset
        (numberOfItemsMinusOffset < numberOfItemsInOffset) -> numberOfItemsInOffset
        else -> previousNumberOfItems
    }
}

private fun calculateNumberOfSlidItemsForHorizontal(
    offsetX: Float,
    itemWidth: Int,
    offsetToSlide: Int,
    previousNumberOfItems: Int
): Int {
    val numberOfItemsInOffset = (offsetX / itemWidth).toInt()
    val numberOfItemsPlusOffset = ((offsetX + offsetToSlide) / itemWidth).toInt()
    val numberOfItemsMinusOffset = ((offsetX - offsetToSlide - 1) / itemWidth).toInt()

    return when {
        (offsetX - offsetToSlide - 1) < 0 -> 0
        (numberOfItemsPlusOffset > numberOfItemsInOffset) -> numberOfItemsPlusOffset
        (numberOfItemsMinusOffset < numberOfItemsInOffset) -> numberOfItemsInOffset
        else -> previousNumberOfItems
    }
}