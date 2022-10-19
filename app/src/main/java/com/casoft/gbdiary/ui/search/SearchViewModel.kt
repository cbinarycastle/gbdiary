package com.casoft.gbdiary.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.SearchDiaryItemsUseCase
import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val MIN_SEARCH_KEYWORD_LENGTH = 2

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchDiaryItemsUseCase: SearchDiaryItemsUseCase,
) : ViewModel() {

    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()

    private val _searchKeyword = MutableStateFlow<String?>(null)
    val searchKeyword = _searchKeyword.asStateFlow()

    val diaryItems: StateFlow<List<DiaryItem>> = _searchKeyword
        .filterNotNull()
        .map { it.trim() }
        .filter { it.length >= MIN_SEARCH_KEYWORD_LENGTH }
        .flatMapLatest { searchDiaryItemsUseCase(it) }
        .map { result ->
            when (result) {
                is Result.Success -> {
                    result.data.also {
                        _isResultEmpty.value = it.isEmpty()
                    }
                }
                is Result.Error -> {
                    _isResultEmpty.value = true
                    emptyList()
                }
                is Result.Loading -> {
                    _isResultEmpty.value = false
                    emptyList()
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    private val _isResultEmpty = MutableStateFlow(false)
    val isResultEmpty = _isResultEmpty.asStateFlow()

    fun changeInputText(searchText: String) {
        _inputText.value = searchText
    }

    fun clearInputText() {
        _inputText.value = ""
    }

    fun search() {
        val keyword = inputText.value.trim()
        if (keyword.length >= MIN_SEARCH_KEYWORD_LENGTH) {
            _searchKeyword.value = keyword
        }
    }
}