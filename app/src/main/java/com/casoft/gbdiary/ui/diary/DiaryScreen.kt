package com.casoft.gbdiary.ui.diary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.extensions.toast
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.model.StickerType
import com.casoft.gbdiary.model.imageResId
import com.casoft.gbdiary.ui.AppBarHeight
import com.casoft.gbdiary.ui.GBDiaryAppBar
import com.casoft.gbdiary.ui.modifier.alignTopToCenterOfParent
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.ui.theme.markerPainter
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

private const val NUMBER_OF_STICKERS_BY_ROW = 3

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd EEEE")

private val SelectedStickerSize = 64.dp
private val SuggestionBarHeight = 44.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DiaryScreen(
    viewModel: DiaryViewModel,
    date: LocalDate,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()
    val stickers by viewModel.stickers.collectAsState()
    val text by viewModel.text.collectAsState()
    var visibleRemoveButtonIndex by remember { mutableStateOf<Int?>(null) }
    val isRemoveMode = remember(visibleRemoveButtonIndex) { visibleRemoveButtonIndex != null }
    var textAlign by remember { mutableStateOf(TextAlign.Start) }

    LaunchedEffect(viewModel) {
        launch {
            viewModel.message.collect {
                context.toast(it)
            }
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            StickerSelectionBottomSheet(
                date = date,
                onStickerSelected = {
                    viewModel.addSticker(it)
                    coroutineScope.launch { bottomSheetState.hide() }
                }
            )
        },
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetElevation = 0.dp,
        scrimColor = GBDiaryTheme.gbDiaryColors.dimmingOverlay
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            Column(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = CenterHorizontally
                ) {
                    AppBar(
                        onBack = onBack,
                        onMoreClick = { /*TODO*/ }
                    )
                    Spacer(Modifier.height(8.dp))
                    AddStickerButton(
                        onClick = {
                            coroutineScope.launch { bottomSheetState.show() }
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    SelectedStickers(
                        stickers = stickers,
                        onStickerLongPress = { index -> visibleRemoveButtonIndex = index }
                    )
                    Spacer(Modifier.height(4.dp))
                    DateText(date = date)
                    Spacer(Modifier.height(24.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        BasicTextField(
                            value = text,
                            onValueChange = { viewModel.inputText(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp),
                            textStyle = GBDiaryTheme.typography.body1.copy(
                                color = LocalContentColor.current,
                                textAlign = textAlign
                            ),
                            cursorBrush = SolidColor(LocalContentColor.current)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                SuggestionBar(
                    onAlbumClick = { /*TODO*/ },
                    onAlignClick = { textAlign = textAlign.toggle() },
                    modifier = Modifier
                        .navigationBarsPadding()
                        .imePadding(),
                    textAlign = textAlign
                )
            }

            if (isRemoveMode) {
                TouchBlock { visibleRemoveButtonIndex = null }
            }

            RemoveStickerButtons(
                count = stickers.size,
                visibleIndex = visibleRemoveButtonIndex,
                onClick = { index ->
                    visibleRemoveButtonIndex = null
                    viewModel.removeSticker(index)
                },
                modifier = Modifier
                    .padding(top = 176.dp)
                    .align(TopCenter)
            )
        }
    }
}

@Composable
private fun AppBar(
    onBack: () -> Unit,
    onMoreClick: () -> Unit,
) {
    GBDiaryAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "뒤로"
                )
            }
            IconButton(onClick = onMoreClick) {
                Icon(
                    painter = painterResource(R.drawable.more),
                    contentDescription = "더보기"
                )
            }
        }
    }
}

@Composable
private fun AddStickerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        elevation = 1.dp
    ) {
        Box(
            modifier = Modifier
                .clickable { onClick() }
                .padding(4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.add_sticker),
                contentDescription = "스티커 추가"
            )
        }
    }
}

@Composable
private fun SelectedStickers(
    stickers: List<Sticker>,
    onStickerLongPress: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = spacedBy(4.dp)
    ) {
        stickers.forEachIndexed { index, sticker ->
            Column(horizontalAlignment = CenterHorizontally) {
                SelectedSticker(
                    sticker = sticker,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(onLongPress = { onStickerLongPress(index) })
                    }
                )
            }
        }
    }
}

@Composable
private fun SelectedSticker(
    sticker: Sticker,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(sticker.imageResId),
        contentDescription = sticker.name,
        modifier = modifier.size(64.dp)
    )
}

@Composable
private fun DateText(date: LocalDate, modifier: Modifier = Modifier) {
    Text(
        text = date.format(dateFormatter),
        modifier = modifier.alpha(0.4f),
        style = GBDiaryTheme.typography.caption
    )
}

@Composable
private fun SuggestionBar(
    onAlbumClick: () -> Unit,
    onAlignClick: () -> Unit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    val borderColor = GBDiaryTheme.colors.onBackground

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(SuggestionBarHeight)
            .drawBehind {
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx(),
                    alpha = 0.05f
                )
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = CenterVertically
    ) {
        SuggestionBarIcon(onClick = onAlbumClick) {
            Icon(
                painter = painterResource(R.drawable.album),
                contentDescription = "앨범"
            )
        }
        Spacer(Modifier.width(8.dp))
        SuggestionBarIcon(onClick = onAlignClick) {
            when (textAlign) {
                TextAlign.Start -> {
                    Icon(
                        painter = painterResource(R.drawable.align_left),
                        contentDescription = "왼쪽 정렬"
                    )
                }
                TextAlign.Center -> {
                    Icon(
                        painter = painterResource(R.drawable.align_center),
                        contentDescription = "가운데 정렬"
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionBarIcon(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(Modifier.clickable { onClick() }) {
        content()
    }
}

@Composable
private fun RemoveStickerButtons(
    count: Int,
    visibleIndex: Int?,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = spacedBy(4.dp)
    ) {
        repeat(count) { index ->
            Box(
                modifier = Modifier.width(SelectedStickerSize),
                contentAlignment = Center
            ) {
                RemoveStickerButton(
                    visible = index == visibleIndex,
                    onClick = { onClick(index) }
                )
            }
        }
    }
}

@Composable
private fun RemoveStickerButton(
    visible: Boolean,
    onClick: () -> Unit,
) {
    AnimatedVisibility(visible) {
        Button(
            onClick = onClick,
            modifier = Modifier.size(width = 54.dp, height = 56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.delete),
                contentDescription = "제거"
            )
        }
    }
}

@Composable
private fun TouchBlock(onClick: () -> Unit) {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = onClick
            )
    )
}

@Composable
private fun StickerSelectionBottomSheet(
    date: LocalDate,
    onStickerSelected: (Sticker) -> Unit,
) {
    var selectedType by remember { mutableStateOf(StickerType.MOOD) }

    BoxWithConstraints {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxHeight - AppBarHeight),
            horizontalAlignment = CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Spacer(
                modifier = Modifier
                    .width(40.dp)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(GBDiaryTheme.gbDiaryColors.border)
            )
            Spacer(Modifier.height(24.dp))
            DateText(date)
            Spacer(Modifier.height(6.dp))
            Text(
                text = "오늘은 어떤 하루였나요?",
                style = GBDiaryTheme.typography.subtitle1
            )
            Spacer(Modifier.height(24.dp))
            Row {
                StickerTypeTab(
                    text = StickerType.MOOD.text,
                    selected = selectedType == StickerType.MOOD,
                    onClick = { selectedType = StickerType.MOOD }
                )
                StickerTypeTab(
                    text = StickerType.DAILY.text,
                    selected = selectedType == StickerType.DAILY,
                    onClick = { selectedType = StickerType.DAILY }
                )
            }
            Spacer(Modifier.height(24.dp))
            SelectableStickers(onClick = onStickerSelected)
        }
    }
}

@Composable
private fun StickerTypeTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(IntrinsicSize.Min)
            .clickable { onClick() }
    ) {
        if (selected) {
            Image(
                painter = markerPainter(),
                contentDescription = null,
                modifier = Modifier.alignTopToCenterOfParent()
            )
        }
        Text(
            text = text,
            style = GBDiaryTheme.typography.body1,
            modifier = Modifier
                .align(Center)
                .then(
                    if (selected) {
                        Modifier
                    } else {
                        Modifier.alpha(0.4f)
                    }
                )
        )
    }
}

@Composable
private fun SelectableStickers(onClick: (Sticker) -> Unit) {
    Column(verticalArrangement = spacedBy(24.dp)) {
        Sticker.values().toList()
            .chunked(NUMBER_OF_STICKERS_BY_ROW)
            .forEach { stickers ->
                Row(horizontalArrangement = spacedBy(32.dp)) {
                    stickers.forEach { sticker ->
                        Image(
                            painter = painterResource(sticker.imageResId),
                            contentDescription = sticker.name,
                            modifier = Modifier
                                .size(72.dp)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null
                                ) { onClick(sticker) }
                        )
                    }
                }
            }
    }
}

private fun TextAlign.toggle(): TextAlign {
    return if (this == TextAlign.Start) {
        TextAlign.Center
    } else {
        TextAlign.Start
    }
}