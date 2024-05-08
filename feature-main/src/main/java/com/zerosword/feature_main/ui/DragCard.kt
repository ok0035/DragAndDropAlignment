package com.zerosword.feature_main.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.zerosword.domain.model.ListItemModel
import com.zerosword.domain.model.SlideState
import com.zerosword.resources.R
import kotlinx.coroutines.launch

private val particlesStreamRadii = mutableListOf<Float>()
private var itemHeight = 0
private var itemWidth = 0
private var particleRadius = 0f
private var slotItemDifference = 0f

@ExperimentalAnimationApi
@Composable
fun DragCard(
    card: ListItemModel,
    slideState: SlideState,
    cardList: MutableList<ListItemModel>,
    isEditMode: Boolean,
    updateSlideState: (shoesArticle: ListItemModel, slideState: SlideState) -> Unit,
    updateItemPosition: (currentIndex: Int, destinationIndex: Int) -> Unit
) {

    val density = LocalDensity.current

    val itemWidthDp = 200.dp
    val itemHeightDp = 300.dp

    val draggingScale = 0.7f

    val itemWidthPx =
        with(density) {
            itemWidthDp.toPx()
        }

    val itemHeightPx =
        with(density) {
            itemHeightDp.toPx()
        }

    val heightAnim = remember {
        Animatable(with(density) {
            if (isEditMode) itemHeightDp.toPx() * draggingScale else itemHeightDp.toPx()
        })
    }

    val widthAnim = remember {
        Animatable(with(density) {
            if (isEditMode) itemWidthDp.toPx() * draggingScale else itemWidthDp.toPx()
        })
    }

    with(density) {

        particleRadius = 3.dp.toPx()
        if (particlesStreamRadii.isEmpty())
            particlesStreamRadii.addAll(arrayOf(6.dp.toPx(), 10.dp.toPx(), 14.dp.toPx()))
        slotItemDifference = 18.dp.toPx()
    }

    val verticalTranslation by animateIntAsState(
        targetValue = when (slideState) {
            SlideState.UP -> -heightAnim.value.toInt()
            SlideState.DOWN -> heightAnim.value.toInt()
            else -> 0
        },
        label = "",
    )

    val horizontalTranslation by animateIntAsState(
        targetValue = when (slideState) {
            SlideState.UP -> -widthAnim.value.toInt()
            SlideState.DOWN -> widthAnim.value.toInt()
            else -> 0
        },
        label = "",
    )

    val isDragged = remember { mutableStateOf(false) }
    val zIndex = if (isDragged.value) 1.0f else 0.0f
    val scale = if (isDragged.value) 1.05f else 1.0f
    val elevation = if (isDragged.value) 8.dp else 0.dp

    val currentIndex = remember { mutableIntStateOf(0) }
    val destinationIndex = remember { mutableIntStateOf(0) }

    val isPlaced = remember { mutableStateOf(false) }
    val isHorizontal = true

    val editModeOffsetX = -(with(density) { 60.dp.toPx() } * cardList.indexOf(card))
    val editModeAnim = remember { Animatable(if (isEditMode) 0f else editModeOffsetX) }

    LaunchedEffect(isPlaced.value) {
        if (isPlaced.value) {

            launch {
                if (currentIndex.intValue != destinationIndex.intValue) {
                    updateItemPosition(currentIndex.intValue, destinationIndex.intValue)
                }
                isPlaced.value = false
            }
        }
    }

    LaunchedEffect(key1 = isEditMode) {
        println("edit mode $isEditMode")
        if (isEditMode) {
            launch {
                if (editModeAnim.value == 0f) return@launch
                editModeAnim.animateTo(0f, tween(1000))
            }
            launch {
                val target = with(density) { itemWidthDp.toPx() * draggingScale }
                if (widthAnim.value == target) return@launch
                widthAnim.animateTo(
                    target,
                    tween(1000)
                )
            }
            launch {
                val target = with(density) {
                    itemHeightDp.toPx() * draggingScale
                }
                if (heightAnim.value == target) return@launch
                heightAnim.animateTo(
                    target, tween(1000)
                )
            }
        } else {
            launch {
                if (editModeAnim.value == editModeOffsetX) return@launch
                editModeAnim.animateTo(editModeOffsetX, tween(1000))
            }

            launch {
                val target = with(density) {
                    itemWidthDp.toPx()
                }

                if (widthAnim.value == target) return@launch
                widthAnim.animateTo(
                    target,
                    tween(1000)
                )
            }
            launch {
                val target = with(density) {
                    itemHeightDp.toPx()
                }
                if (heightAnim.value == target) return@launch

                heightAnim.animateTo(
                    target,
                    tween(1000)
                )
            }
        }
    }


    Box(
        Modifier
            .width(with(density) { widthAnim.value.toDp() })
            .height(with(density) { heightAnim.value.toDp() })
            .padding(16.dp)
            .graphicsLayer {
                translationX = editModeAnim.value
            }
            .dragAndDrop(
                card,
                cardList,
                itemLength = if (isHorizontal) itemWidthPx else itemHeightPx,
                draggingScale = draggingScale,
                updateSlideState = updateSlideState,
                isDraggedAfterLongPress = true,
                isHorizontal = isHorizontal,
                onStartDrag = { isDragged.value = true },
                onStopDrag = { cIndex, dIndex ->
                    isDragged.value = false
                    isPlaced.value = true
                    currentIndex.intValue = cIndex
                    destinationIndex.intValue = dIndex
                }
            )
            .offset {
                IntOffset(
                    if (isHorizontal) horizontalTranslation else 0,
                    if (!isHorizontal) verticalTranslation else 0
                )
            }
            .zIndex(zIndex)
            .scale(scaleX = scale, scaleY = scale)
    ) {

        ListItemView(
            modifier = Modifier
                .fillMaxSize(),
            with(density) { widthAnim.value.toDp() } - 32.dp,
            with(density) { heightAnim.value.toDp() } - 32.dp,
            item = card
        )

    }
}


@ExperimentalAnimationApi
@Preview
@Composable
fun ShoesCardPreview() {

    DragCard(
        ListItemModel("asdfasdf", R.drawable.test_image),
        SlideState.NONE,
        mutableListOf(),
        false,
        { _, _ -> },
        { _, _ -> }
    )

}