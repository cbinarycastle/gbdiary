package com.casoft.gbdiary.ui.diary

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import com.casoft.gbdiary.R
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.model.TextAlign
import com.casoft.gbdiary.model.imageResId
import com.casoft.gbdiary.util.dpToPx
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd EEEE")

@SuppressLint("ViewConstructor")
class DiaryContent(
    context: Context,
    onAddStickerButtonClick: () -> Unit,
    private val onStickerClick: (Int) -> Unit,
    private val onStickerLongClick: (Int) -> Unit,
    private val onTextChanged: (String) -> Unit,
    private val onImageClick: (Int) -> Unit,
) : ScrollView(context) {

    private val stickersContainer: LinearLayout
    private val dateTextView: TextView
    private val contentTextField: EditText
    private val imagesContainer: LinearLayout

    private val stickers = mutableListOf<Sticker>()
    private val images = mutableListOf<File>()

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.diary_content, this, true)

        findViewById<ImageButton>(R.id.addStickerButton).apply {
            setOnClickListener { onAddStickerButtonClick() }
        }

        stickersContainer = findViewById(R.id.stickersContainer)
        dateTextView = findViewById(R.id.dateText)
        contentTextField = findViewById(R.id.contentTextField)
        imagesContainer = findViewById(R.id.imagesContainer)

        contentTextField.doOnTextChanged { text, _, _, _ ->
            onTextChanged(text.toString())
        }
    }

    fun setStickers(stickers: List<Sticker>) {
        if (this.stickers == stickers) {
            return
        }

        this.stickers.run {
            clear()
            addAll(stickers)
        }

        stickersContainer.removeAllViews()

        stickers.forEachIndexed { index, sticker ->
            ImageView(context)
                .apply {
                    layoutParams = LayoutParams(64.dpToPx, 64.dpToPx)
                    setImageResource(sticker.imageResId)
                    setOnClickListener { onStickerClick(index) }
                    setOnLongClickListener {
                        onStickerLongClick(index)
                        true
                    }
                }
                .let { stickersContainer.addView(it) }
        }
    }

    fun setDate(date: LocalDate) {
        dateTextView.text = date.format(DateFormatter)
    }

    fun setText(text: String) {
        if (contentTextField.text.toString() == text) {
            return
        }

        contentTextField.setText(text)
    }

    @SuppressLint("RtlHardcoded")
    fun setTextAlign(textAlign: TextAlign) {
        contentTextField.gravity = when (textAlign) {
            TextAlign.LEFT -> Gravity.LEFT
            TextAlign.CENTER -> Gravity.CENTER
        }
    }

    fun setImages(images: List<File>) {
        if (this.images == images) {
            return
        }

        this.images.run {
            clear()
            addAll(images)
        }

        imagesContainer.removeAllViews()

        images.forEachIndexed { index, image ->
            ImageLayout(context, image)
                .apply {
                    layoutParams = MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                        if (index > 0) {
                            topMargin = 16.dpToPx
                        }
                    }
                    setOnClickListener { onImageClick(index) }
                }
                .let { imagesContainer.addView(it) }
        }
    }
}

@SuppressLint("ViewConstructor")
private class ImageLayout(
    context: Context,
    image: File,
) : ConstraintLayout(context) {

    init {
        val imageView = ImageView(context).apply {
            id = generateViewId()
            layoutParams = LayoutParams(MATCH_CONSTRAINT, MATCH_CONSTRAINT)
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageURI(image.toUri())
        }
        addView(imageView)

        setConstraintSet(
            ConstraintSet().apply {
                connect(imageView.id, START, PARENT_ID, START)
                connect(imageView.id, END, PARENT_ID, END)
                connect(imageView.id, TOP, PARENT_ID, TOP)
                setDimensionRatio(imageView.id, "1:1")
            }
        )
    }
}