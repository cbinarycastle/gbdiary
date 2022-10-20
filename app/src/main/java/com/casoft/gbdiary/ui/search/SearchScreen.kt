package com.casoft.gbdiary.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.ui.components.DiaryCard
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.theme.GBDiaryTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onDiaryItemClick: (DiaryItem) -> Unit,
    onBack: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val searchTextFieldFocusRequester = remember { FocusRequester() }

    val inputText by viewModel.inputText.collectAsState()
    val searchKeyword by viewModel.searchKeyword.collectAsState()
    val diaryItems by viewModel.diaryItems.collectAsState()
    val isResultEmpty by viewModel.isResultEmpty.collectAsState()

    LaunchedEffect(true) {
        searchTextFieldFocusRequester.requestFocus()
        keyboardController?.show()
    }

    LaunchedEffect(searchKeyword) {
        if (searchKeyword != null) {
            focusManager.clearFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        AppBar(
            inputText = inputText,
            searchTextFieldFocusRequester = searchTextFieldFocusRequester,
            onInputTextChange = viewModel::changeInputText,
            onSearch = viewModel::search,
            onClearInputText = viewModel::clearInputText,
            onBack = onBack
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .imePadding()
        ) {
            DiaryCards(
                items = diaryItems,
                onDiaryItemClick = onDiaryItemClick,
                searchKeyword = searchKeyword,
                modifier = Modifier.fillMaxSize()
            )
            if (isResultEmpty) {
                Text(
                    text = "검색 결과가 없어요",
                    modifier = Modifier.alpha(0.3f)
                )
            }
        }
    }
}

@Composable
private fun AppBar(
    inputText: String,
    searchTextFieldFocusRequester: FocusRequester,
    onInputTextChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClearInputText: () -> Unit,
    onBack: () -> Unit,
) {
    GBDiaryAppBar {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.back),
                contentDescription = "뒤로"
            )
        }
        Spacer(Modifier.width(4.dp))
        SearchTextField(
            value = inputText,
            onValueChange = onInputTextChange,
            onSearch = onSearch,
            onClear = onClearInputText,
            modifier = Modifier
                .weight(1f)
                .focusRequester(searchTextFieldFocusRequester)
        )
    }
}

@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = "일기 검색",
                    style = GBDiaryTheme.typography.subtitle1,
                    modifier = Modifier.alpha(0.3f)
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = GBDiaryTheme.typography.subtitle1.copy(
                    color = LocalContentColor.current
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions { onSearch() },
                singleLine = true,
                cursorBrush = SolidColor(LocalContentColor.current),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (value.isNotEmpty()) {
            ClearButton(
                onClear = onClear,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}

@Composable
private fun ClearButton(
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClear,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(
                if (GBDiaryTheme.colors.isLight) {
                    R.drawable.search_delete_light
                } else {
                    R.drawable.search_delete_dark
                }
            ),
            contentDescription = "초기화"
        )
    }
}

@Composable
private fun DiaryCards(
    items: List<DiaryItem>,
    onDiaryItemClick: (DiaryItem) -> Unit,
    searchKeyword: String?,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(items) { item ->
            DiaryCard(
                item = item,
                onClick = { onDiaryItemClick(item) },
                wordToHighlight = searchKeyword,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}