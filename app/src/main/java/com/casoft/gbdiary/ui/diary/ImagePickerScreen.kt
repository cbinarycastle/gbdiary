package com.casoft.gbdiary.ui.diary

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.casoft.gbdiary.R
import com.casoft.gbdiary.model.LocalImage
import com.casoft.gbdiary.ui.GBDiaryAppBar
import com.casoft.gbdiary.ui.extension.toContentUri
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.util.toast

const val SELECTED_IMAGE_URIS_RESULT_KEY = "selectedImages"

@Composable
fun ImagePickerScreen(
    viewModel: ImagePickerViewModel,
    maxSelectionCount: Int,
    onSelect: (List<LocalImage>) -> Unit,
    onClose: () -> Unit,
) {
    val state = rememberImagePickerState(viewModel)
    val context = LocalContext.current
    val images by state.images.collectAsState()
    var numberOfSelectedImages by remember { mutableStateOf(0) }

    LaunchedEffect(viewModel) {
        viewModel.message.collect {
            context.toast(it)
        }
    }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AppBar(
            selectionCount = numberOfSelectedImages,
            onSelect = {
                onSelect(
                    images.flatten()
                        .filter { image -> image.selected }
                        .map { image -> image.localImage }
                )
            },
            onClose = onClose
        )
        LazyColumn(Modifier.fillMaxSize()) {
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
                                onClick = {
                                    if (image.selected || numberOfSelectedImages < maxSelectionCount) {
                                        image.toggleSelect()
                                        if (image.selected) {
                                            numberOfSelectedImages++
                                        } else {
                                            numberOfSelectedImages--
                                        }
                                    }
                                },
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
                Text(
                    text = "선택 $selectionCount",
                    style = GBDiaryTheme.typography.subtitle1
                )
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