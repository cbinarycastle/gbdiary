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
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import coil.compose.rememberAsyncImagePainter
import com.casoft.gbdiary.R
import com.casoft.gbdiary.model.*
import com.casoft.gbdiary.ui.AppBarHeight
import com.casoft.gbdiary.ui.GBDiaryAppBar
import com.casoft.gbdiary.ui.extension.CollectOnce
import com.casoft.gbdiary.ui.extension.border
import com.casoft.gbdiary.ui.extension.navigateToAppSettings
import com.casoft.gbdiary.ui.extension.statusBarHeight
import com.casoft.gbdiary.ui.modifier.alignTopToCenterOfParent
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.ui.theme.markerPainter
import com.casoft.gbdiary.util.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import java.io.File

private const val NUMBER_OF_STICKERS_BY_ROW = 3

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd EEEE")

private val AddStickerButtonSize = 32.dp
private val SelectedStickerSize = 64.dp
private val SuggestionBarHeight = 44.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DiaryScreen(
    viewModel: DiaryViewModel,
    savedStateHandle: SavedStateHandle?,
    onImageClick: (Int) -> Unit,
    onAlbumClick: (Int) -> Unit,
    onBack: () -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    bottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    ),
    scrollState: ScrollState = rememberScrollState(),
) {
    val context = LocalContext.current

    val date by viewModel.date.collectAsState()
    val existingDiary by viewModel.existingDiary.collectAsState()
    val stickers by viewModel.stickers.collectAsState()
    val content by viewModel.content.collectAsState()
    val images by viewModel.images.collectAsState()
    val isValidToSave by viewModel.isValidToSave.collectAsState()
    val textAlign = viewModel.textAlign.collectAsState().value.toUiModel()

    BackHandler {
        onBack()
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
        date = date,
        existsDiary = existingDiary != null,
        stickers = stickers,
        content = content,
        images = images,
        isValidToSave = isValidToSave,
        textAlign = textAlign,
        onStickerSelected = {
            viewModel.addSticker(it)
            coroutineScope.launch { bottomSheetState.hide() }
        },
        onRemoveSticker = { index -> viewModel.removeSticker(index) },
        onContentChange = { viewModel.inputText(it) },
        onRemoveImage = { index -> viewModel.removeImage(index) },
        onImageClick = onImageClick,
        onAlbumClick = onAlbumClick,
        onAlignClick = { viewModel.toggleTextAlign() },
        onDoneClick = { viewModel.saveDiary() },
        onDeleteDiary = {
            viewModel.deleteDiary()
            onBack()
        },
        onBack = {
            viewModel.saveDiary(shouldShowMessage = false)
            onBack()
        },
        coroutineScope = coroutineScope,
        bottomSheetState = bottomSheetState,
        scrollState = scrollState,
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun DiaryScreen(
    date: LocalDate,
    existsDiary: Boolean,
    stickers: List<Sticker>,
    content: String,
    images: List<File>,
    isValidToSave: Boolean,
    textAlign: TextAlign,
    onStickerSelected: (Sticker) -> Unit,
    onRemoveSticker: (Int) -> Unit,
    onContentChange: (String) -> Unit,
    onRemoveImage: (Int) -> Unit,
    onImageClick: (Int) -> Unit,
    onAlbumClick: (Int) -> Unit,
    onAlignClick: () -> Unit,
    onDoneClick: () -> Unit,
    onDeleteDiary: () -> Unit,
    onBack: () -> Unit,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    scrollState: ScrollState,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var diaryBottomSheet by remember { mutableStateOf(DiaryBottomSheet.STICKER_SELECTION) }
    var visibleRemoveStickerButtonIndex by remember { mutableStateOf<Int?>(null) }
    var visibleRemoveImageButtonIndex by remember { mutableStateOf<Int?>(null) }
    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(RequestPermission()) { granted ->
        if (granted) {
            onAlbumClick(MAX_NUMBER_OF_IMAGES - images.size)
        } else {
            showPermissionDeniedDialog = true
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            when (diaryBottomSheet) {
                DiaryBottomSheet.STICKER_SELECTION -> StickerSelectionBottomSheet(
                    date = date,
                    onStickerSelected = onStickerSelected
                )
                DiaryBottomSheet.MORE -> MoreBottomSheet(
                    onDelete = {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                            showConfirmDeleteDialog = true
                        }
                    }
                )
            }
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
                AppBar(
                    onBack = {
                        onBack()
                    },
                    onMoreClick = {
                        coroutineScope.launch {
                            diaryBottomSheet = DiaryBottomSheet.MORE
                            bottomSheetState.show()
                        }
                    },
                    moreButtonVisible = existsDiary
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
                            onClick = {
                                coroutineScope.launch {
                                    diaryBottomSheet = DiaryBottomSheet.STICKER_SELECTION
                                    bottomSheetState.show()
                                }
                            }
                        )
                    } else {
                        Spacer(Modifier.height(AddStickerButtonSize))
                    }
                    Spacer(Modifier.height(8.dp))
                    SelectedStickers(
                        stickers = stickers,
                        onStickerLongPress = { index -> visibleRemoveStickerButtonIndex = index }
                    )
                    Spacer(Modifier.height(4.dp))
                    DateText(date = date)
                    Spacer(Modifier.height(24.dp))
                    Box(Modifier.fillMaxWidth()) {
                        if (content.isEmpty()) {
                            TextInputPlaceholder(textAlign = textAlign)
                        }
                        TextInput(
                            text = content,
                            textAlign = textAlign,
                            onValueChange = onContentChange
                        )
                    }
                    if (images.isNotEmpty()) {
                        Spacer(Modifier.height(24.dp))
                        SelectedImages(
                            images = images,
                            visibleRemoveButtonIndex = visibleRemoveImageButtonIndex,
                            onImageClick = { index -> onImageClick(index) },
                            onImageLongPress = { index -> visibleRemoveImageButtonIndex = index },
                            onCancelRemove = { visibleRemoveImageButtonIndex = null },
                            onRemove = { index ->
                                onRemoveImage(index)
                                visibleRemoveImageButtonIndex = null
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
                        keyboardController?.hide()
                        onDoneClick()
                    },
                    textAlign = textAlign,
                    doneButtonVisible = isValidToSave
                )
            }

            if (visibleRemoveStickerButtonIndex != null) {
                TouchBlock { visibleRemoveStickerButtonIndex = null }
            }

            RemoveStickerButtons(
                count = stickers.size,
                visibleIndex = visibleRemoveStickerButtonIndex,
                onClick = { index ->
                    visibleRemoveStickerButtonIndex = null
                    onRemoveSticker(index)
                },
                modifier = Modifier
                    .padding(top = 176.dp)
                    .align(Alignment.TopCenter)
            )

            if (showPermissionDeniedDialog) {
                PermissionDeniedDialog(
                    onConfirm = {
                        showPermissionDeniedDialog = false
                        context.navigateToAppSettings()
                    },
                    onDismiss = { showPermissionDeniedDialog = false }
                )
            }

            if (showConfirmDeleteDialog) {
                ConfirmDeleteDialog(
                    onConfirm = {
                        showConfirmDeleteDialog = false
                        onDeleteDiary()
                    },
                    onDismiss = { showConfirmDeleteDialog = false }
                )
            }
        }
    }
}

@Composable
private fun AppBar(
    onBack: () -> Unit,
    onMoreClick: () -> Unit,
    moreButtonVisible: Boolean = false,
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
private fun TextInput(
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
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
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
            painter = rememberAsyncImagePainter(model = image),
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
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onCancelRemove() }
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
    doneButtonVisible: Boolean = false,
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
        if (doneButtonVisible && WindowInsets.isImeVisible) {
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
                RemoveButton(
                    visible = index == visibleIndex,
                    onClick = { onClick(index) }
                )
            }
        }
    }
}

@Composable
private fun RemoveButton(
    onClick: () -> Unit,
    visible: Boolean = true,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
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
private fun SelectableStickers(onClick: (Sticker) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Sticker.values().toList()
            .chunked(NUMBER_OF_STICKERS_BY_ROW)
            .forEach { stickers ->
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
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

@OptIn(ExperimentalMaterialApi::class)
@Preview(name = "Diary screen")
@Preview(name = "Diary screen (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DiaryScreenPreview() {
    GBDiaryTheme {
        DiaryScreen(
            date = LocalDate.now(ZoneOffset.UTC),
            existsDiary = true,
            stickers = listOf(Sticker.HOPEFUL, Sticker.CONFUSION),
            content = "내용",
            images = listOf(),
            isValidToSave = false,
            textAlign = TextAlign.Left,
            onStickerSelected = {},
            onRemoveSticker = {},
            onContentChange = {},
            onRemoveImage = {},
            onImageClick = {},
            onAlbumClick = {},
            onAlignClick = {},
            onDoneClick = {},
            onDeleteDiary = {},
            onBack = {},
            coroutineScope = rememberCoroutineScope(),
            bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
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

private enum class DiaryBottomSheet {
    STICKER_SELECTION, MORE
}