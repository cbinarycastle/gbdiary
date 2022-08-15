package com.casoft.gbdiary.ui.diary

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import coil.compose.rememberAsyncImagePainter
import com.casoft.gbdiary.R
import com.casoft.gbdiary.model.MAX_NUMBER_OF_IMAGES
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.model.StickerType
import com.casoft.gbdiary.model.imageResId
import com.casoft.gbdiary.ui.AppBarHeight
import com.casoft.gbdiary.ui.GBDiaryAppBar
import com.casoft.gbdiary.ui.GBDiaryDialog
import com.casoft.gbdiary.ui.extension.border
import com.casoft.gbdiary.ui.extension.navigateToAppSettings
import com.casoft.gbdiary.ui.extension.statusBarHeight
import com.casoft.gbdiary.ui.modifier.alignTopToCenterOfParent
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.ui.theme.markerPainter
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

private const val NUMBER_OF_STICKERS_BY_ROW = 3

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd EEEE")

private val AddStickerButtonSize = 32.dp
private val SelectedStickerSize = 64.dp
private val SuggestionBarHeight = 44.dp

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMaterialApi::class, ExperimentalLayoutApi::class,
)
@Composable
fun DiaryScreen(
    viewModel: DiaryViewModel,
    savedStateHandle: SavedStateHandle?,
    date: LocalDate,
    onAlbumClick: (Int) -> Unit,
    onBack: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val stickers by viewModel.stickers.collectAsState()
    val text by viewModel.text.collectAsState()
    val imageUris by viewModel.images.collectAsState()

    var visibleRemoveButtonIndex by remember { mutableStateOf<Int?>(null) }
    var textAlign by remember { mutableStateOf(TextAlign.Start) }
    var permissionDeniedDialogVisible by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(RequestPermission()) { granted ->
        if (granted) {
            onAlbumClick(MAX_NUMBER_OF_IMAGES - imageUris.size)
        } else {
            permissionDeniedDialogVisible = true
        }
    }

    if (savedStateHandle != null) {
        val selectedImages = savedStateHandle
            .getStateFlow<List<Uri>>(SELECTED_IMAGE_URIS_RESULT_KEY, listOf())
            .collectAsState()

        LaunchedEffect(selectedImages) {
            viewModel.addImages(selectedImages.value)
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
                AppBar(
                    onBack = onBack,
                    onMoreClick = { /*TODO*/ }
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
                                coroutineScope.launch { bottomSheetState.show() }
                            }
                        )
                    } else {
                        Spacer(Modifier.height(AddStickerButtonSize))
                    }
                    Spacer(Modifier.height(8.dp))
                    SelectedStickers(
                        stickers = stickers,
                        onStickerLongPress = { index -> visibleRemoveButtonIndex = index }
                    )
                    Spacer(Modifier.height(4.dp))
                    DateText(date = date)
                    Spacer(Modifier.height(24.dp))
                    Box(Modifier.fillMaxWidth()) {
                        if (text.isEmpty()) {
                            TextInputPlaceholder(textAlign = textAlign)
                        }
                        TextInput(
                            text = text,
                            textAlign = textAlign,
                            onValueChange = { viewModel.inputText(it) }
                        )
                    }
                    if (imageUris.isNotEmpty()) {
                        Spacer(Modifier.height(24.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            imageUris.forEach { uri ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = uri),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
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
                            onAlbumClick(MAX_NUMBER_OF_IMAGES - imageUris.size)
                        } else {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    },
                    onAlignClick = { textAlign = textAlign.toggleSelect() },
                    onHideKeyboardClick = { keyboardController?.hide() },
                    textAlign = textAlign
                )
            }

            if (visibleRemoveButtonIndex != null) {
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
                    .align(Alignment.TopCenter)
            )

            if (permissionDeniedDialogVisible) {
                PermissionDeniedDialog(
                    onConfirm = {
                        permissionDeniedDialogVisible = false
                        context.navigateToAppSettings()
                    },
                    onDismiss = { permissionDeniedDialogVisible = false }
                )
            }
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SuggestionBar(
    onAlbumClick: () -> Unit,
    onAlignClick: () -> Unit,
    onHideKeyboardClick: () -> Unit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    val borderColor = GBDiaryTheme.colors.onBackground

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(SuggestionBarHeight)
            .border(
                color = borderColor,
                top = 1.dp,
                alpha = 0.05f
            )
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
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
        if (WindowInsets.isImeVisible) {
            SuggestionBarIcon(onClick = onHideKeyboardClick) {
                Icon(
                    painter = painterResource(R.drawable.keyboard_down),
                    contentDescription = "키보드 닫기"
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
private fun PermissionDeniedDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    GBDiaryDialog(
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        message = {
            Text(
                text = "사진 업로드를 위한 접근 권한 변경이 필요합니다.",
                textAlign = TextAlign.Center
            )
        },
        confirmText = { Text("설정") },
        dismissText = { Text("취소") }
    )
}

private fun TextAlign.toggleSelect(): TextAlign {
    return if (this == TextAlign.Start) {
        TextAlign.Center
    } else {
        TextAlign.Start
    }
}