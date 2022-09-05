package com.casoft.gbdiary.ui.diary

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ui.components.AppBarHeight
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import java.io.File

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageViewerScreen(
    images: List<File>,
    onClose: () -> Unit,
    state: ImageViewerState = rememberImageViewerState(),
    initialPage: Int = 0,
) {
    val pagerState = rememberPagerState(initialPage)

    BackHandler {
        state.onClose()
        onClose()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) {
            AppBar(
                currentPageNumber = pagerState.currentPage + 1,
                pageCount = pagerState.pageCount,
                onClose = {
                    state.onClose()
                    onClose()
                }
            )
            HorizontalPager(
                count = images.size,
                state = pagerState,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(top = AppBarHeight)
            ) { page ->
                val image = images[page]
                Image(
                    painter = rememberAsyncImagePainter(image),
                    contentDescription = "이미지 ${page + 1}",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun AppBar(
    currentPageNumber: Int,
    pageCount: Int,
    onClose: () -> Unit,
) {
    GBDiaryAppBar(backgroundColor = Color.Black) {
        Box(Modifier.fillMaxWidth()) {
            Text(
                text = "$currentPageNumber/$pageCount",
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "닫기"
                )
            }
        }
    }
}

@Preview(name = "Image viewer screen")
@Preview(name = "Image viewer screen (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ImageViewerScreenPreview() {
    GBDiaryTheme {
        ImageViewerScreen(
            images = listOf(),
            onClose = {}
        )
    }
}