package com.casoft.gbdiary.ui.diary

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import coil.compose.rememberAsyncImagePainter
import com.casoft.gbdiary.R
import com.casoft.gbdiary.model.*
import com.casoft.gbdiary.ui.components.AppBarHeight
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.extension.CollectOnce
import com.casoft.gbdiary.ui.extension.border
import com.casoft.gbdiary.ui.extension.navigateToAppSettings
import com.casoft.gbdiary.ui.extension.statusBarHeight
import com.casoft.gbdiary.ui.modifier.alignTopToCenterOfParent
import com.casoft.gbdiary.ui.modifier.noRippleClickable
import com.casoft.gbdiary.ui.theme.Dark1
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.ui.theme.markerPainter
import com.casoft.gbdiary.util.toast
import java.io.File
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private const val NUMBER_OF_STICKERS_BY_ROW = 3

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd EEEE")

private val AddStickerButtonSize = 32.dp
private val SelectedStickerSize = 64.dp
private val SuggestionBarHeight = 44.dp

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun DiaryScreen(
    viewModel: DiaryViewModel,
    savedStateHandle: SavedStateHandle?,
    onImageClick: (Int) -> Unit,
    onAlbumClick: (Int) -> Unit,
    onBack: () -> Unit,
) {
    val state = rememberDiaryState()
    val context = LocalContext.current

    val date by viewModel.date.collectAsState()
    val isPremiumUser by viewModel.isPremiumUser.collectAsState()
    val existsDiary by viewModel.existsDiary.collectAsState()
    val initialStickerBottomSheetShown by viewModel.initialStickerBottomSheetShown.collectAsState()
    val stickers by viewModel.stickers.collectAsState()
    val content by viewModel.content.collectAsState()
    val images by viewModel.images.collectAsState()
    val textAlign = viewModel.textAlign.collectAsState().value.toUiModel()

    BackHandler {
        if (state.isBottomSheetVisible) {
            state.hideBottomSheet()
        } else {
            viewModel.saveDiary(shouldShowMessage = false)
            onBack()
        }
    }

    savedStateHandle?.CollectOnce<List<LocalImage>>(
        key = SELECTED_IMAGE_URIS_RESULT_KEY,
        defaultValue = listOf()
    ) { selectedImages -> viewModel.addImages(selectedImages) }

    LaunchedEffect(viewModel) {
        viewModel.message.collect {
            context.toast(it)
        }
    }

    DiaryScreen(
        state = state,
        date = date,
        isPremiumUser = isPremiumUser,
        existsDiary = existsDiary,
        initialStickerBottomSheetShown = initialStickerBottomSheetShown,
        stickers = stickers,
        content = content,
        images = images,
        textAlign = textAlign,
        addSticker = viewModel::addSticker,
        changeSticker = viewModel::changeSticker,
        onShowStickerBottomSheetInitially = viewModel::onShowStickerBottomSheetInitially,
        onRemoveSticker = viewModel::removeSticker,
        onContentChange = viewModel::inputText,
        onRemoveImage = viewModel::removeImage,
        onImageClick = onImageClick,
        onAlbumClick = onAlbumClick,
        onAlignClick = viewModel::toggleTextAlign,
        onDoneClick = viewModel::saveDiary,
        onDeleteDiary = {
            viewModel.deleteDiary()
            onBack()
        },
        onBack = {
            viewModel.saveDiary(shouldShowMessage = false)
            onBack()
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DiaryScreen(
    state: DiaryState,
    date: LocalDate,
    isPremiumUser: Boolean,
    existsDiary: Boolean?,
    initialStickerBottomSheetShown: Boolean,
    stickers: List<Sticker>,
    content: String,
    images: List<File>,
    textAlign: TextAlign,
    addSticker: (Sticker) -> Unit,
    changeSticker: (Int, Sticker) -> Unit,
    onShowStickerBottomSheetInitially: () -> Unit,
    onRemoveSticker: (Int) -> Unit,
    onContentChange: (String) -> Unit,
    onRemoveImage: (Int) -> Unit,
    onImageClick: (Int) -> Unit,
    onAlbumClick: (Int) -> Unit,
    onAlignClick: () -> Unit,
    onDoneClick: () -> Unit,
    onDeleteDiary: () -> Unit,
    onBack: () -> Unit,
    scrollState: ScrollState = rememberScrollState(),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val shouldShowAppBarDivider by remember { derivedStateOf { scrollState.value > 0 } }

    val permissionLauncher = rememberLauncherForActivityResult(RequestPermission()) { granted ->
        if (granted) {
            onAlbumClick(MAX_NUMBER_OF_IMAGES - images.size)
        } else {
            state.showPermissionDeniedDialog()
        }
    }

    LaunchedEffect(existsDiary) {
        if (existsDiary != null && existsDiary.not() && initialStickerBottomSheetShown.not()) {
            state.showBottomSheet(DiaryBottomSheet.STICKER)
            onShowStickerBottomSheetInitially()
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            when (state.currentBottomSheet) {
                DiaryBottomSheet.STICKER -> StickerBottomSheet(
                    date = date,
                    isPremiumUser = isPremiumUser,
                    onStickerSelected = { sticker ->
                        val index = state.selectedStickerIndex
                        if (index == null) {
                            addSticker(sticker)
                        } else {
                            changeSticker(index, sticker)
                        }

                        state.run {
                            hideBottomSheet()
                            showKeyboard()
                        }
                    }
                )
                DiaryBottomSheet.MORE -> MoreBottomSheet(
                    onDelete = {
                        state.run {
                            hideBottomSheet()
                            showConfirmDeleteDialog()
                        }
                    }
                )
            }
        },
        sheetState = state.bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetElevation = 0.dp,
        scrimColor = GBDiaryTheme.gbDiaryColors.dimmingOverlay
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
        ) {
            Column(Modifier.fillMaxSize()) {
                AppBar(
                    showDivider = shouldShowAppBarDivider,
                    onBack = onBack,
                    onMoreClick = { state.showBottomSheet(DiaryBottomSheet.MORE) },
                    moreButtonVisible = existsDiary ?: false
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(scrollState)
                ) {
                    Spacer(Modifier.height(8.dp))
                    if (stickers.size < MAX_STICKERS) {
                        AddStickerButton(
                            onClick = { state.showBottomSheet(DiaryBottomSheet.STICKER) }
                        )
                    } else {
                        Spacer(Modifier.height(AddStickerButtonSize))
                    }
                    Spacer(Modifier.height(8.dp))
                    SelectedStickers(
                        stickers = stickers,
                        onStickerClick = state::selectSticker,
                        onStickerLongPress = state::startRemovingSticker
                    )
                    Spacer(Modifier.height(4.dp))
                    DateText(date = date)
                    Spacer(Modifier.height(24.dp))
                    Box(Modifier.fillMaxWidth()) {
                        if (content.isEmpty()) {
                            TextInputPlaceholder(textAlign = textAlign)
                        }
                        ContentTextField(
                            text = content,
                            textAlign = textAlign,
                            onValueChange = onContentChange,
                            modifier = Modifier.focusRequester(state.textFieldFocusRequester)
                        )
                    }
                    if (images.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .noRippleClickable { state.showKeyboard() }
                        )
                    } else {
                        Spacer(Modifier.height(24.dp))
                        SelectedImages(
                            images = images,
                            visibleRemoveButtonIndex = state.visibleRemoveImageButtonIndex,
                            onImageClick = { index -> onImageClick(index) },
                            onImageLongPress = state::startRemovingImage,
                            onCancelRemove = state::cancelRemovingImage,
                            onRemove = { index ->
                                state.cancelRemovingImage()
                                onRemoveImage(index)
                            }
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }
                SuggestionBar(
                    onAlbumClick = {
                        val permissionGranted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                        if (permissionGranted) {
                            onAlbumClick(MAX_NUMBER_OF_IMAGES - images.size)
                        } else {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    },
                    onAlignClick = onAlignClick,
                    onDoneClick = {
                        focusManager.clearFocus()
                        onDoneClick()
                    },
                    textAlign = textAlign,
                )
            }

            if (state.isRemovingSticker) {
                TouchBlock { state.cancelRemovingSticker() }
            }

            RemoveStickerButtons(
                count = stickers.size,
                visibleIndex = state.visibleRemoveStickerButtonIndex,
                onClick = { index ->
                    state.cancelRemovingSticker()
                    onRemoveSticker(index)
                },
                modifier = Modifier
                    .padding(top = 176.dp)
                    .align(Alignment.TopCenter)
            )

            if (state.shouldShowPermissionDeniedDialog) {
                PermissionDeniedDialog(
                    onConfirm = {
                        state.hidePermissionDeniedDialog()
                        context.navigateToAppSettings()
                    },
                    onDismiss = state::hidePermissionDeniedDialog
                )
            }

            if (state.shouldShowConfirmDeleteDialog) {
                ConfirmDeleteDialog(
                    onConfirm = {
                        state.hideConfirmDeleteDialog()
                        onDeleteDiary()
                    },
                    onDismiss = state::hideConfirmDeleteDialog
                )
            }
        }
    }
}

@Composable
private fun AppBar(
    showDivider: Boolean,
    onBack: () -> Unit,
    onMoreClick: () -> Unit,
    moreButtonVisible: Boolean = false,
) {
    GBDiaryAppBar(showDivider = showDivider) {
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
            if (moreButtonVisible) {
                IconButton(onClick = onMoreClick) {
                    Icon(
                        painter = painterResource(R.drawable.more),
                        contentDescription = "더보기"
                    )
                }
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
        modifier = modifier.size(AddStickerButtonSize),
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
    onStickerClick: (Int) -> Unit,
    onStickerLongPress: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        stickers.forEachIndexed { index, sticker ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SelectedSticker(
                    sticker = sticker,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onStickerClick(index) },
                            onLongPress = { onStickerLongPress(index) }
                        )
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
private fun TextInputPlaceholder(textAlign: TextAlign) {
    Text(
        text = "오늘 하루를 기록해보세요",
        modifier = Modifier
            .fillMaxWidth()
            .alpha(0.3f),
        style = GBDiaryTheme.typography.body1,
        textAlign = textAlign
    )
}

@Composable
private fun ContentTextField(
    text: String,
    textAlign: TextAlign,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = text,
        onValueChange = onValueChange,
        textStyle = GBDiaryTheme.typography.body1.copy(
            color = LocalContentColor.current,
            textAlign = textAlign
        ),
        cursorBrush = SolidColor(LocalContentColor.current),
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun SelectedImages(
    images: List<File>,
    visibleRemoveButtonIndex: Int?,
    onImageClick: (Int) -> Unit,
    onImageLongPress: (Int) -> Unit,
    onCancelRemove: () -> Unit,
    onRemove: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        images.forEachIndexed { index, image ->
            SelectedImage(
                image = image,
                removeButtonVisible = index == visibleRemoveButtonIndex,
                onClick = { onImageClick(index) },
                onLongPress = { onImageLongPress(index) },
                onCancelRemove = onCancelRemove,
                onRemove = { onRemove(index) }
            )
        }
    }
}

@Composable
private fun SelectedImage(
    image: File,
    removeButtonVisible: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    onCancelRemove: () -> Unit,
    onRemove: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Image(
            painter = rememberAsyncImagePainter(image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onLongPress() },
                        onTap = { onClick() }
                    )
                }
        )
        AnimatedVisibility(
            visible = removeButtonVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(GBDiaryTheme.gbDiaryColors.dimmingOverlay)
                    .noRippleClickable { onCancelRemove() }
            ) {
                RemoveButton(onClick = onRemove)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SuggestionBar(
    onAlbumClick: () -> Unit,
    onAlignClick: () -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    val borderColor = GBDiaryTheme.colors.onBackground

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(SuggestionBarHeight)
            .border(
                color = borderColor,
                top = 1.dp,
                alpha = 0.05f
            )
            .padding(horizontal = 16.dp)
    ) {
        Row {
            SuggestionBarIcon(onClick = onAlbumClick) {
                Icon(
                    painter = painterResource(R.drawable.album),
                    contentDescription = "앨범"
                )
            }
            Spacer(Modifier.width(8.dp))
            SuggestionBarIcon(onClick = onAlignClick) {
                when (textAlign) {
                    TextAlign.Left -> {
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
        if (WindowInsets.isImeVisible) {
            SuggestionBarIcon(onClick = onDoneClick) {
                Icon(
                    painter = painterResource(R.drawable.done),
                    contentDescription = "완료"
                )
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
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(count) { index ->
            Box(
                modifier = Modifier.width(SelectedStickerSize),
                contentAlignment = Alignment.Center
            ) {
                if (index == visibleIndex) {
                    RemoveButton(onClick = { onClick(index) })
                }
            }
        }
    }
}

@Composable
private fun RemoveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(width = 54.dp, height = 56.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.delete),
            contentDescription = "제거"
        )
    }
}

@Composable
private fun TouchBlock(onClick: () -> Unit) {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable { onClick() }
    )
}

@Composable
private fun StickerBottomSheet(
    date: LocalDate,
    isPremiumUser: Boolean,
    onStickerSelected: (Sticker) -> Unit,
) {
    var selectedStickerType by remember { mutableStateOf(StickerType.MOOD) }
    val stickerEnabled = remember(selectedStickerType, isPremiumUser) {
        selectedStickerType == StickerType.MOOD || isPremiumUser
    }

    BoxWithConstraints {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxHeight - statusBarHeight - AppBarHeight),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    selected = selectedStickerType == StickerType.MOOD,
                    onClick = { selectedStickerType = StickerType.MOOD }
                )
                StickerTypeTab(
                    text = StickerType.DAILY.text,
                    selected = selectedStickerType == StickerType.DAILY,
                    onClick = { selectedStickerType = StickerType.DAILY }
                )
            }
            Spacer(Modifier.height(24.dp))
            SelectableStickers(
                stickerType = selectedStickerType,
                onClick = onStickerSelected,
                enabled = stickerEnabled
            )
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
            .height(27.dp)
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
                .align(Alignment.Center)
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
private fun SelectableStickers(
    stickerType: StickerType,
    onClick: (Sticker) -> Unit,
    enabled: Boolean = true,
) {
    Box {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Sticker.values()
                .filter { it.type == stickerType }
                .toList()
                .chunked(NUMBER_OF_STICKERS_BY_ROW)
                .forEach { stickers ->
                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        stickers.forEach { sticker ->
                            Image(
                                painter = painterResource(sticker.imageResId),
                                contentDescription = sticker.name,
                                modifier = Modifier
                                    .size(72.dp)
                                    .then(
                                        if (enabled) {
                                            Modifier.noRippleClickable { onClick(sticker) }
                                        } else {
                                            Modifier.alpha(0.3f)
                                        }
                                    )
                            )
                        }
                    }
                }
        }
        if (enabled.not()) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Dark1,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = "프리미엄 이용권 구매 후 사용할 수 있어요",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun MoreBottomSheet(onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(top = 24.dp, bottom = 48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(43.dp)
                .clickable { onDelete() }
                .padding(horizontal = 24.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.delete),
                contentDescription = "삭제"
            )
            Text(
                text = "삭제",
                style = GBDiaryTheme.typography.subtitle1
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Preview(name = "Diary screen")
@Preview(name = "Diary screen (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DiaryScreenPreview() {
    GBDiaryTheme {
        DiaryScreen(
            state = rememberDiaryState(),
            date = LocalDate.now(ZoneOffset.UTC),
            isPremiumUser = false,
            existsDiary = true,
            initialStickerBottomSheetShown = false,
            stickers = listOf(Sticker.HOPEFUL, Sticker.CONFUSION),
            content = "내용",
            images = listOf(),
            textAlign = TextAlign.Left,
            addSticker = {},
            changeSticker = { _, _ -> },
            onShowStickerBottomSheetInitially = {},
            onRemoveSticker = {},
            onContentChange = {},
            onRemoveImage = {},
            onImageClick = {},
            onAlbumClick = {},
            onAlignClick = {},
            onDoneClick = {},
            onDeleteDiary = {},
            onBack = {},
            scrollState = rememberScrollState()
        )
    }
}

private fun com.casoft.gbdiary.model.TextAlign.toUiModel(): TextAlign {
    return when (this) {
        com.casoft.gbdiary.model.TextAlign.LEFT -> TextAlign.Left
        com.casoft.gbdiary.model.TextAlign.CENTER -> TextAlign.Center
    }
}