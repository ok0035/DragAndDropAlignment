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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.zerosword.domain.model.ListItemModel
import com.zerosword.domain.model.SlideState
import com.zerosword.resources.R
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val configuration = context.resources.configuration
    val screenWidth = configuration.screenWidthDp

    val itemWidthDp = (screenWidth * 0.538f).dp
    val itemHeightDp = (screenWidth * 0.7f).dp

    val draggingScale = 0.515f

    val dragScaleAnim = remember {
        Animatable(if (isEditMode) draggingScale else 1f)
    }

    val curIndex = cardList.indexOf(card)
    val startPadding = remember { mutableStateOf(if (curIndex == 0) 40.dp else 0.dp) }

    val dragHeight =
        with(density) { (itemHeightDp * (if (isEditMode) draggingScale else 1f)).toPx() }.toInt()
    val dragWidth =
        with(density) { (itemWidthDp * (if (isEditMode) draggingScale else 1f)).toPx() }.toInt()

    val verticalTranslation by animateIntAsState(
        targetValue = when (slideState) {
            SlideState.UP -> -dragHeight
            SlideState.DOWN -> dragHeight
            else -> 0
        },
        label = "",
    )

    val horizontalTranslation by animateIntAsState(
        targetValue = when (slideState) {
            SlideState.UP -> -dragWidth
            SlideState.DOWN -> dragWidth
            else -> 0
        },
        label = "",
    )

    val isDragged = remember { mutableStateOf(false) }
    val zIndex = if (isDragged.value) 1.0f else 0.0f
    val pressedScale = if (isDragged.value) 1.05f else 1.0f

    val paddingAnimTarget = with(density) {
        if (isEditMode) 12.dp.toPx()
        else 0f
    }

    val paddingAnim = remember {
        Animatable(
            paddingAnimTarget
        )
    }

    val currentIndex = remember { mutableIntStateOf(0) }
    val destinationIndex = remember { mutableIntStateOf(0) }

    val isPlaced = remember { mutableStateOf(false) }
    val isHorizontal = true

    val itemOffsetX = -(with(density) { 52.dp.toPx() } * cardList.indexOf(card))
    val itemOffsetY = (with(density) { 52.dp.toPx() } * cardList.indexOf(card))
    val itemOffsetXAnim = remember { Animatable(if (isEditMode) 0f else itemOffsetX) }
    val itemOffsetYAnim = remember {
        Animatable(
            if (isEditMode) 0f
            else {
                if (cardList.indexOf(card) % 2 == 1)
                    itemOffsetY
                else 0f
            }
        )
    }

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

//    LaunchedEffect(key1 = isEditMode) {
//        println("edit mode $isEditMode")
//        if (isEditMode) {
//
//            launch {
//                paddingAnim.animateTo(paddingAnimTarget, tween(1000))
//            }
//
//            launch {
//                if (dragScaleAnim.value == draggingScale) return@launch
//                dragScaleAnim.animateTo(draggingScale, tween(1000))
//            }
//
//            launch {
//                if (itemOffsetXAnim.value == 0f) return@launch
//                itemOffsetXAnim.animateTo(0f, tween(1000))
//            }
//
//            launch {
//                if (itemOffsetYAnim.value == 0f) return@launch
//                itemOffsetYAnim.animateTo(0f, tween(1000))
//            }
//
//        } else {
//
//            launch { paddingAnim.animateTo(0f, tween(1000)) }
//
//
//            launch {
//                if (dragScaleAnim.value == 1f) return@launch
//                dragScaleAnim.animateTo(1f, tween(1000))
//            }
//
//            launch {
//                if (itemOffsetXAnim.value == itemOffsetX) return@launch
//                itemOffsetXAnim.animateTo(itemOffsetX, tween(1000))
//            }
//
//            launch {
//                if (cardList.indexOf(card) % 2 == 0 || itemOffsetYAnim.value == itemOffsetY) return@launch
//                itemOffsetYAnim.animateTo(itemOffsetY, tween(1000))
//            }
//
//        }
//    }

    val padding = with(density) { paddingAnim.value.toDp() }
    Box(
        Modifier
            .width(itemWidthDp * dragScaleAnim.value + startPadding.value)
            .height(itemHeightDp * dragScaleAnim.value)
            .padding(
                start = padding + startPadding.value,
                end = padding,
                top = padding,
                bottom = padding
            )
            .dragAndDrop(
                card,
                cardList,
                itemSize = ((
                        if (isHorizontal)
                            with(density) { (itemWidthDp * draggingScale).toPx() }
                        else with(density) { (itemHeightDp * draggingScale).toPx() })).toInt(),
                updateSlideState = updateSlideState,
                isDraggedAfterLongPress = true,
                isHorizontal = isHorizontal,
                disabled = !isEditMode,
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
                    (if (isHorizontal) horizontalTranslation else 0) + itemOffsetXAnim.value.toInt(),
                    if (!isHorizontal) verticalTranslation else 0 + itemOffsetYAnim.value.toInt()
                )
            }
            .zIndex(zIndex)
//            .scale(dragScaleAnim.value)
            .scale(scaleX = pressedScale, scaleY = pressedScale)
    ) {

        ListItemView(
            modifier = Modifier
                .fillMaxSize(),
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