package com.zerosword.feature_main.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zerosword.domain.model.ListItemModel
import com.zerosword.domain.model.SlideState
import kotlinx.coroutines.launch

val allCharacters = arrayOf(
    ListItemModel(
        title = "그라운드시소 서촌",
        res = com.zerosword.resources.R.drawable.test_image,
    ),
    ListItemModel(
        title = "스태픽스",
        res = com.zerosword.resources.R.drawable.test_image2,
    ),
    ListItemModel(
        title = "더마틴",
        res = com.zerosword.resources.R.drawable.test_image3,
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun Home() {

    val scope = rememberCoroutineScope()

    var isEditMode by remember { mutableStateOf(true) }
    val cards = remember { mutableStateListOf(*allCharacters) }
    val slideStates = remember {
        mutableStateMapOf<ListItemModel, SlideState>()
            .apply {
                cards.associateWith { _ ->
                    SlideState.NONE
                }.also {
                    putAll(it)
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Drag and Drop")
                },
                actions = {
                    IconButton(onClick = {
                        isEditMode = !isEditMode
                    }) {
                        Icon(Icons.Filled.Edit, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->

        DragItemList(
            modifier = Modifier.padding(innerPadding),
            cardList = cards,
            slideStates = slideStates,
            isEditMode = isEditMode,
            updateSlideState = { shoesArticle, slideState ->
                slideStates[shoesArticle] = slideState
            },
            updateItemPosition = { currentIndex, destinationIndex ->
                val card = cards[currentIndex]
                cards.removeAt(currentIndex)
                cards.add(destinationIndex, card)

                slideStates.apply {
                    cards.map { card ->
                        card to SlideState.NONE
                    }.toMap().also {
                        putAll(it)
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DraggableDrawer() {
    var isEditMode by remember { mutableStateOf(false) }
    val cards = remember { mutableStateListOf(*allCharacters) }
    val slideStates = remember {
        mutableStateMapOf<ListItemModel, SlideState>()
            .apply {
                cards.associateWith { _ ->
                    SlideState.NONE
                }.also {
                    putAll(it)
                }
            }
    }

    val scope = rememberCoroutineScope()

    val density = LocalDensity.current
    val context = LocalContext.current
    val configuration = context.resources.configuration

    val width = configuration.screenWidthDp
    val height = configuration.screenHeightDp

    val endPadding = width.dp * 32f / 360

    val initialWidth = width.dp * 52f / 360f
    val initialHeight = width.dp * 52f / 360f

    val maxWidth = width.dp - endPadding
    val maxHeight = width.dp * 120f / 360f

    val animatableWidth = remember { Animatable(initialWidth.value) }
    val animatableHeight = remember { Animatable(initialHeight.value) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {

        RightRoundedDrawerBox(
            modifier = Modifier
                .width(animatableWidth.value.dp)
                .height(animatableHeight.value.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            val progress = animatableWidth.value / maxWidth.value
                            if (animatableWidth.value.dp > maxWidth * 0.5f) {
                                scope.launch {

                                    launch {
                                        animatableWidth.animateTo(
                                            targetValue = maxWidth.value,
                                            animationSpec = tween(
                                                durationMillis = (400 + 600 * (1f - progress)).toInt(),
                                                //                                                durationMillis = 600,
                                                //                                                delayMillis = 800,
                                                //                                                easing = CubicBezierEasing(0.65f, 0f, 0.23f, 1.13f)
                                            )
                                        ) {
                                            isEditMode = true
                                        }
                                    }

                                    launch {
                                        animatableHeight.animateTo(
                                            targetValue = maxHeight.value,
                                            animationSpec = tween(
                                                durationMillis = (400 + 600 * (1f - progress)).toInt(),
                                                //                                                durationMillis = 1000,
                                                delayMillis = 200,
                                                //                                                easing = CubicBezierEasing(0.65f, 0f, 0.23f, 1.13f)
                                            )
                                        )
                                    }
                                }
                            } else {
                                scope.launch {

                                    launch {
                                        animatableWidth.animateTo(
                                            targetValue = initialWidth.value,
                                            animationSpec = tween(
                                                durationMillis = (1000 * progress).toInt(),
                                            )
                                        )
                                    }

                                    launch {
                                        animatableHeight.animateTo(
                                            targetValue = initialHeight.value,
                                            animationSpec = tween(
                                                durationMillis = (1000 * progress).toInt(),
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    ) { change, dragAmount ->
                        isEditMode = false
                        val newWidth =
                            (animatableWidth.value.dp + dragAmount.x.toDp()).coerceIn(
                                initialWidth,
                                maxWidth
                            )
                        val newHeight =
                            (animatableHeight.value.dp + (dragAmount.x.toDp() * width.dp.value / height.dp.value)).coerceIn(
                                initialHeight,
                                maxHeight
                            )
                        scope.launch {
                            launch { animatableWidth.snapTo(newWidth.value) }
                            launch {
                                if (dragAmount.x < 0)
                                    animatableHeight.snapTo(newHeight.value)
                            }
                            change.consume()
                        }
                    }
                },
            contentAlignment = Alignment.CenterEnd
        ) {

            ConstraintLayout(
                modifier = Modifier.fillMaxSize(),
            ) {

                val (itemList, drawer) = createRefs()

                DragItemList(
                    modifier = Modifier
                        .constrainAs(itemList) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(drawer.start)
                            this.width = Dimension.fillToConstraints
                        },
                    cardList = cards,
                    slideStates = slideStates,
                    isEditMode = true,
                    updateSlideState = { shoesArticle, slideState ->
                        slideStates[shoesArticle] = slideState
                    },
                    updateItemPosition = { currentIndex, destinationIndex ->
                        val card = cards[currentIndex]
                        cards.removeAt(currentIndex)
                        cards.add(destinationIndex, card)

                        slideStates.apply {
                            cards.map { card ->
                                card to SlideState.NONE
                            }.toMap().also {
                                putAll(it)
                            }
                        }
                    }
                )

                Box(
                    modifier = Modifier
                        .width(initialWidth)
                        .fillMaxHeight()
                        .background(Color.Transparent)
                        .constrainAs(drawer) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                            this.width = Dimension.fillToConstraints
                            //                            this.height = androidx.constraintlayout.compose.Dimension.fillToConstraints
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("asdf")
                }
            }

        }
    }
}

@ExperimentalAnimationApi
@Composable
fun DragItemList(
    modifier: Modifier,
    cardList: MutableList<ListItemModel>,
    slideStates: Map<ListItemModel, SlideState>,
    isEditMode: Boolean,
    updateSlideState: (model: ListItemModel, slideState: SlideState) -> Unit,
    updateItemPosition: (currentIndex: Int, destinationIndex: Int) -> Unit
) {
//    val lazyListState = rememberLazyListState()
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val configuration = context.resources.configuration
    val screenWidth = configuration.screenWidthDp

    Row(
        modifier = modifier
            .width(screenWidth.dp)
            .fillMaxHeight()// 수평 스크롤 가능하게 설정
            .padding(top = 12.dp)
            .horizontalScroll(scrollState),
    ) {
        cardList.forEachIndexed() { index, item ->
            val card = cardList.getOrNull(index)
            if (card != null) {
                key(card) {
                    val slideState = slideStates[card] ?: SlideState.NONE
                    DragCard(
                        card = card,
                        slideState = slideState,
                        cardList = cardList,
                        isEditMode = isEditMode,
                        updateSlideState = updateSlideState,
                        updateItemPosition = updateItemPosition
                    )
                }
            }
        }
    }

}

@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DraggableDrawer()
}

@Composable
fun Main(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background) {
        content()
    }
}