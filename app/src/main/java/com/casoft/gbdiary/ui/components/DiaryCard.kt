package com.casoft.gbdiary.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.casoft.gbdiary.model.DiaryFontSize
import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.model.imageResId
import com.casoft.gbdiary.ui.theme.DarkTextIcon
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.ui.theme.LightDimmingOverlay
import com.casoft.gbdiary.ui.theme.LightTextIcon
import com.casoft.gbdiary.util.style
import java.time.format.DateTimeFormatter

private const val MAX_VISIBLE_IMAGES = 3

private val DateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd EEEE")

@Composable
fun DiaryCard(
    item: DiaryItem,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentTextStyle: TextStyle = DiaryFontSize.Default.style,
    wordToHighlight: String? = null,
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
                .then(
                    if (onClick == null) {
                        Modifier
                    } else {
                        Modifier.clickable { onClick() }
                    }
                )
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
                if (wordToHighlight == null) {
                    Text(
                        text = item.content,
                        maxLines = 8,
                        overflow = TextOverflow.Ellipsis,
                        style = contentTextStyle
                    )
                } else {
                    Text(
                        text = item.buildHighlightedContent(wordToHighlight),
                        maxLines = 8,
                        overflow = TextOverflow.Ellipsis,
                        style = contentTextStyle
                    )
                }
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

@Composable
private fun DiaryItem.buildHighlightedContent(wordToHighlight: String) = buildAnnotatedString {
    var text = content

    while (text.isNotEmpty()) {
        val indexToHighlight = text.indexOf(
            string = wordToHighlight,
            ignoreCase = true
        )

        text = if (indexToHighlight >= 0) {
            val nextStartIndex = indexToHighlight + wordToHighlight.length

            append(text.slice(0 until indexToHighlight))
            withStyle(
                style = SpanStyle(
                    color = if (GBDiaryTheme.colors.isLight) DarkTextIcon else LightTextIcon,
                    background = LocalContentColor.current
                )
            ) {
                append(text.slice(indexToHighlight until nextStartIndex))
            }
            text.slice(nextStartIndex until text.length)
        } else {
            append(text)
            ""
        }
    }
}