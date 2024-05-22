package com.zerosword.feature_main.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainView() {
//    val viewModel: MainViewModel = hiltViewModel()

    DraggableDrawer()

}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MainView()
}

