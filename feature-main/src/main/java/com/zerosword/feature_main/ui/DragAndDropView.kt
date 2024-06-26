package com.zerosword.feature_main.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zerosword.domain.model.ListItemModel
import com.zerosword.domain.model.SlideState
import kotlinx.coroutines.delay
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

//    LazyRow(
//
//        state = lazyListState,
//        modifier = modifier
//            .width(screenWidth.dp * 2)
//            .padding(top = 12.dp)
//            .wrapContentHeight(),
//    ) {
//        items(cardList.size) { index ->
//            val card = cardList.getOrNull(index)
//            if (card != null) {
//                key(card) {
//                    val slideState = slideStates[card] ?: SlideState.NONE
//                    DragCard(
//                        card = card,
//                        slideState = slideState,
//                        cardList = cardList,
//                        isEditMode = isEditMode,
//                        updateSlideState = updateSlideState,
//                        updateItemPosition = updateItemPosition
//                    )
//                }
//            }
//        }
//    }

}

@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Home()
}

@Composable
fun Main(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background) {
        content()
    }
}