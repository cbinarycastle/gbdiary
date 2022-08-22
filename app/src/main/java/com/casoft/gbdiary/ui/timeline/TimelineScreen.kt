package com.casoft.gbdiary.ui.timeline

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.casoft.gbdiary.R
import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.model.imageResId
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd EEEE")

@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel,
    onBack: () -> Unit,
) {
    val yearMonth by viewModel.yearMonth.collectAsState()
    val diaryItems by viewModel.diaryItems.collectAsState()

    TimelineScreen(
        yearMonth = yearMonth,
        diaryItems = diaryItems,
        onBeforeMonth = viewModel::moveToBeforeMonth,
        onNextMonth = viewModel::moveToNextMonth,
        onBack = onBack
    )
}

@Composable
private fun TimelineScreen(
    yearMonth: YearMonth,
    diaryItems: List<DiaryItem>,
    onBeforeMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            AppBar(
                yearMonth = yearMonth,
                onBack = onBack,
                onBefore = onBeforeMonth,
                onNext = onNextMonth
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(diaryItems) { item ->
                    DiaryCard(
                        item = item,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        if (diaryItems.isEmpty()) {
            Text(
                text = "작성된 일기가 없어요",
                modifier = Modifier
                    .align(Alignment.Center)
                    .alpha(0.3f)
            )
        }
    }
}

@Composable
private fun AppBar(
    yearMonth: YearMonth,
    onBefore: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    GBDiaryAppBar {
        Box(Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "뒤로"
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.Center)
            ) {
                IconButton(onClick = onBefore) {
                    Icon(
                        painter = painterResource(R.drawable.chevron_circle_left),
                        contentDescription = "이전"
                    )
                }
                Text("${yearMonth.year}년 ${yearMonth.monthValue}월")
                IconButton(onClick = onNext) {
                    Icon(
                        painter = painterResource(R.drawable.chevron_circle_right),
                        contentDescription = "다음"
                    )
                }
            }
        }
    }
}

@Composable
private fun DiaryCard(
    item: DiaryItem,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        elevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(Modifier.fillMaxWidth()) {
                if (item.stickers.isNotEmpty()) {
                    Stickers(
                        stickers = item.stickers,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    text = item.date.format(dateFormatter),
                    style = GBDiaryTheme.typography.caption,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .alpha(0.4f)
                )
            }
            if (item.content.isNotEmpty()) {
                Text(item.content)
            }
            if (item.images.isNotEmpty()) {
                Images(item.images)
            }
        }
    }
}

@Composable
private fun Stickers(
    stickers: List<Sticker>,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        stickers.forEach { sticker ->
            Image(
                painter = painterResource(sticker.imageResId),
                contentDescription = sticker.name,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

@Composable
private fun Images(images: List<String>) {
    Box(Modifier.clip(RoundedCornerShape(6.dp))) {
        when (images.size) {
            1 -> SingleImage(images[0])
            2 -> DoubleImage(images[0], images[1])
            3 -> TripleImage(images[0], images[1], images[2])
        }
    }
}

@Composable
private fun SingleImage(image: String) {
    Image(
        painter = rememberAsyncImagePainter(image),
        contentDescription = "이미지",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
    )
}

@Composable
private fun DoubleImage(image1: String, image2: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        listOf(image1, image2).forEachIndexed { index, path ->
            Image(
                painter = rememberAsyncImagePainter(path),
                contentDescription = "이미지 ${index + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.9f)
            )
        }
    }
}

@Composable
private fun TripleImage(image1: String, image2: String, image3: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = rememberAsyncImagePainter(image1),
            contentDescription = "이미지 1",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2.4f)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(image2, image3).forEachIndexed { index, image ->
                Image(
                    painter = rememberAsyncImagePainter(image),
                    contentDescription = "이미지 ${index + 2}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1.2f)
                )
            }
        }
    }
}