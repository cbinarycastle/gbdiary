package com.casoft.gbdiary.ui.diary

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.casoft.gbdiary.R
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
    initialPage: Int = 0,
) {
    val pagerState = rememberPagerState(initialPage)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AppBar(
            currentPageNumber = pagerState.currentPage + 1,
            pageCount = pagerState.pageCount,
            onClose = onClose
        )
        HorizontalPager(
            count = images.size,
            state = pagerState,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) { page ->
            val image = images[page]
            Image(
                painter = rememberAsyncImagePainter(model = image),
                contentDescription = "이미지 ${page + 1}",
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

@Composable
private fun AppBar(
    currentPageNumber: Int,
    pageCount: Int,
    onClose: () -> Unit,
) {
    GBDiaryAppBar {
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