package com.zerosword.feature_main.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.ktx.Status
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.zerosword.domain.model.ListItemModel
import java.security.MessageDigest

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ListItemView(modifier: Modifier, item: ListItemModel) {

    val res  = remember { mutableIntStateOf(item.res) }

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(res.intValue)
            .size(coil.size.Size(400, 400)) // 이미지의 원래 크기 사용
            .allowHardware(true) // 소프트웨어 디코딩을 사용하여 고품질 이미지 디코딩
            .build()
    )

    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .aspectRatio(1f)
                .background(Color.Transparent, CircleShape)
                .clip(CircleShape),
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
        )

        Text(item.title)
    }
}