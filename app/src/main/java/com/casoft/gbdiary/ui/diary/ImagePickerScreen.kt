package com.casoft.gbdiary.ui.diary

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
import com.casoft.gbdiary.model.LocalImage
import com.casoft.gbdiary.ui.components.AlertDialogLayout
import com.casoft.gbdiary.ui.components.AlertDialogState
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.components.rememberAlertDialogState
import com.casoft.gbdiary.ui.extension.toContentUri
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.util.collectMessage

const val SELECTED_IMAGE_URIS_RESULT_KEY = "selectedImages"

@Composable
fun ImagePickerScreen(
    viewModel: ImagePickerViewModel,
    preSelectionCount: Int,
    onFinishSelect: (List<LocalImage>) -> Unit,
    onClose: () -> Unit,
) {
    val alertDialogState = rememberAlertDialogState()
    val context = LocalContext.current

    val images by viewModel.images.collectAsState()
    val numberOfSelectedImages by viewModel.numberOfSelectedImages.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.message.collectMessage(context, alertDialogState)
    }

    LaunchedEffect(preSelectionCount) {
        viewModel.preSelectionCount = preSelectionCount
    }

    ImagePickerScreen(
        alertDialogState = alertDialogState,
        images = images,
        numberOfSelectedImages = numberOfSelectedImages,
        onImageSelect = { viewModel.toggleImage(it) },
        onFinishSelect = onFinishSelect,
        onClose = onClose
    )
}

@Composable
private fun ImagePickerScreen(
    alertDialogState: AlertDialogState,
    images: List<List<ImageUiState>>,
    numberOfSelectedImages: Int,
    onImageSelect: (ImageUiState) -> Unit,
    onFinishSelect: (List<LocalImage>) -> Unit,
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
                onSelect = {
                    onFinishSelect(
                        images.flatten()
                            .filter { image -> image.selected }
                            .map { image -> image.localImage }
                    )
                },
                onClose = onClose
            )
            LazyColumn(Modifier.weight(1f)) {
                items(images) { row ->
                    Row {
                        repeat(NUMBER_OF_IMAGES_BY_ROW) { index ->
                            val image = row.getOrNull(index)
                            if (image == null) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                )
                            } else {
                                SelectableImage(
                                    uri = image.localImage.toContentUri(),
                                    selected = image.selected,
                                    onClick = { onImageSelect(image) },
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
            onFinishSelect = {},
            onClose = {}
        )
    }
}