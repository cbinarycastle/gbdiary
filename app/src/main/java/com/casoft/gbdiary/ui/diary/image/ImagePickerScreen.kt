package com.casoft.gbdiary.ui.diary.image

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ui.components.AlertDialogLayout
import com.casoft.gbdiary.ui.components.AlertDialogState
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.components.rememberAlertDialogState
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.util.collectMessage
import kotlinx.coroutines.launch
import java.io.File

const val SELECTED_IMAGES_RESULT_KEY = "selectedImages"

@Composable
fun ImagePickerScreen(
    viewModel: ImagePickerViewModel,
    onFinishSelection: (List<File>) -> Unit,
    onClose: () -> Unit,
) {
    val alertDialogState = rememberAlertDialogState()
    val context = LocalContext.current

    val images by viewModel.images.collectAsState()
    val numberOfSelectedImages by viewModel.numberOfSelectedImages.collectAsState()

    LaunchedEffect(viewModel) {
        launch {
            viewModel.message.collectMessage(context, alertDialogState)
        }

        launch {
            viewModel.selectionFinished.collect { selectedImages ->
                onFinishSelection(selectedImages)
            }
        }
    }

    ImagePickerScreen(
        alertDialogState = alertDialogState,
        images = images,
        numberOfSelectedImages = numberOfSelectedImages,
        onImageSelect = { viewModel.toggleSelected(it) },
        onFinishSelection = viewModel::finishSelection,
        onClose = onClose
    )
}

@Composable
private fun ImagePickerScreen(
    alertDialogState: AlertDialogState,
    images: List<List<ImageUiState>>,
    numberOfSelectedImages: Int,
    onImageSelect: (ImageUiState) -> Unit,
    onFinishSelection: () -> Unit,
    onClose: () -> Unit,
) {
    AlertDialogLayout(state = alertDialogState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            AppBar(
                selectionCount = numberOfSelectedImages,
                onSelect = onFinishSelection,
                onClose = onClose
            )
            LazyColumn(Modifier.weight(1f)) {
                items(images) { row ->
                    Row {
                        repeat(NUMBER_OF_IMAGES_BY_ROW) { index ->
                            val imageUiState = row.getOrNull(index)
                            if (imageUiState == null) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                )
                            } else {
                                SelectableImage(
                                    uri = imageUiState.image.contentUri,
                                    selected = imageUiState.selected,
                                    onClick = { onImageSelect(imageUiState) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppBar(
    selectionCount: Int,
    onSelect: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GBDiaryAppBar {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "닫기"
                )
            }
            TextButton(
                onClick = onSelect,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = GBDiaryTheme.colors.onBackground
                )
            ) {
                Text("$selectionCount 추가")
            }
        }
    }
}

@Composable
private fun SelectableImage(
    uri: Uri,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .border(width = 1.dp, color = Color.Black)
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp)
        ) {
            if (selected) {
                Image(
                    painter = painterResource(R.drawable.check_on),
                    contentDescription = "선택"
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.check_off),
                    contentDescription = "선택 해제"
                )
            }
        }
    }
}

@Preview(name = "Image picker screen")
@Preview(name = "Image picker screen (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ImagePickerScreenPreview() {
    GBDiaryTheme {
        ImagePickerScreen(
            alertDialogState = rememberAlertDialogState(),
            images = listOf(),
            numberOfSelectedImages = 0,
            onImageSelect = {},
            onFinishSelection = {},
            onClose = {}
        )
    }
}