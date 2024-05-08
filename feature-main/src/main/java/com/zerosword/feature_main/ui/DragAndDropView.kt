package com.zerosword.feature_main.ui

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zerosword.domain.model.ListItemModel
import com.zerosword.domain.model.ShoesArticle
import com.zerosword.domain.model.SlideState

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
                    Text(text = "Drag and Drop In Compose")
                },
                actions = {
                    IconButton(onClick = {
                        isEditMode = !isEditMode
                    }) {
                        Icon(Icons.Filled.AddCircle, contentDescription = null)
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
                val shoesArticle = cards[currentIndex]
                cards.removeAt(currentIndex)
                cards.add(destinationIndex, shoesArticle)
                slideStates.apply {
                    cards.map { shoesArticle ->
                        shoesArticle to SlideState.NONE
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
    val lazyListState = rememberLazyListState()
    LazyRow(
        state = lazyListState,
        modifier = modifier.padding(top = 12.dp).fillMaxWidth()
    ) {
        items(cardList.size) { index ->
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
    Home()
}

@Composable
fun Main(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background) {
        content()
    }
}