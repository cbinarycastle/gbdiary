package com.casoft.gbdiary.ui.timeline

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ad.TIMELINE_BANNER_AD_UNIT_ID
import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.model.imageResId
import com.casoft.gbdiary.ui.components.AdBanner
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.components.MonthPickerDialog
import com.casoft.gbdiary.ui.modifier.noRippleClickable
import com.casoft.gbdiary.ui.theme.DarkTextIcon
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.ui.theme.LightDimmingOverlay
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private const val MAX_VISIBLE_IMAGES = 3

private val DateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd EEEE")

@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel,
    onDiaryClick: (LocalDate) -> Unit,
    onBack: () -> Unit,
    today: YearMonth = YearMonth.now(),
) {
    val yearMonth by viewModel.yearMonth.collectAsState()
    val diaryItems by viewModel.diaryItems.collectAsState()
    val isPremiumUser by viewModel.isPremiumUser.collectAsState()

    TimelineScreen(
        yearMonth = yearMonth,
        today = today,
        diaryItems = diaryItems,
        isPremiumUser = isPremiumUser,
        moveToYearMonth = viewModel::moveToYearMonth,
        onBeforeMonth = viewModel::moveToBeforeMonth,
        onNextMonth = viewModel::moveToNextMonth,
        onDiaryClick = onDiaryClick,
        onBack = onBack
    )
}

@Composable
private fun TimelineScreen(
    yearMonth: YearMonth,
    today: YearMonth,
    diaryItems: List<DiaryItem>,
    isPremiumUser: Boolean,
    moveToYearMonth: (YearMonth) -> Unit,
    onBeforeMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDiaryClick: (LocalDate) -> Unit,
    onBack: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    var showMonthPickerDialog by remember { mutableStateOf(false) }

    val shouldShowAppBarDivider by remember {
        derivedStateOf { lazyListState.firstVisibleItemScrollOffset > 0 }
    }

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
                showDivider = shouldShowAppBarDivider,
                onYearMonthClick = { showMonthPickerDialog = true },
                onBefore = onBeforeMonth,
                onNext = onNextMonth,
                onBack = onBack
            )
            LazyColumn(
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(diaryItems) { item ->
                    DiaryCard(
                        item = item,
                        onClick = { onDiaryClick(item.date) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (isPremiumUser.not()) {
                AdBanner(TIMELINE_BANNER_AD_UNIT_ID)
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

        if (showMonthPickerDialog) {
            MonthPickerDialog(
                initialYear = yearMonth.year,
                today = today,
                isEnabled = { it <= today },
                onSelect = {
                    showMonthPickerDialog = false
                    moveToYearMonth(it)
                },
                onDismiss = { showMonthPickerDialog = false }
            )
        }
    }
}

@Composable
private fun AppBar(
    yearMonth: YearMonth,
    showDivider: Boolean,
    onYearMonthClick: () -> Unit,
    onBefore: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    GBDiaryAppBar(showDivider = showDivider) {
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
                Text(
                    text = "${yearMonth.year}년 ${yearMonth.monthValue}월",
                    modifier = Modifier.noRippleClickable { onYearMonthClick() }
                )
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DiaryCard(
    item: DiaryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
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
                    text = item.date.format(DateFormatter),
                    style = GBDiaryTheme.typography.caption,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .alpha(0.4f)
                )
            }
            if (item.content.isNotEmpty()) {
                Text(
                    text = item.content,
                    maxLines = 8,
                    overflow = TextOverflow.Ellipsis
                )
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
            else -> TripleOrMoreImage(images[0], images[1], images[2], images.size)
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
private fun TripleOrMoreImage(image1: String, image2: String, image3: String, size: Int) {
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
            Image(
                painter = rememberAsyncImagePainter(image2),
                contentDescription = "이미지 2",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1.2f)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1.2f)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(image3),
                    contentDescription = "이미지 3",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                if (size > MAX_VISIBLE_IMAGES) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = LightDimmingOverlay)
                    ) {
                        Text(
                            text = "+${size - MAX_VISIBLE_IMAGES}",
                            color = DarkTextIcon,
                            style = GBDiaryTheme.typography.h6
                        )
                    }
                }
            }
        }
    }
}