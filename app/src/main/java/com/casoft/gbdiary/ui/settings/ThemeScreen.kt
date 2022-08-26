package com.casoft.gbdiary.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.model.Theme
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.components.GBDiaryRadioButton

private val Themes = listOf(Theme.SYSTEM, Theme.LIGHT, Theme.DARK)

@Composable
fun ThemeScreen(
    viewModel: ThemeViewModel,
    onBack: () -> Unit,
) {
    val theme by viewModel.theme.collectAsState()

    ThemeScreen(
        selectedTheme = theme,
        onThemeSelect = viewModel::setTheme,
        onBack = onBack
    )
}

@Composable
private fun ThemeScreen(
    selectedTheme: Theme,
    onThemeSelect: (Theme) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AppBar(onBack)
        Themes.forEach { theme ->
            RadioItem(
                name = theme.text,
                selected = theme == selectedTheme,
                onClick = { onThemeSelect(theme) }
            )
        }
    }
}

@Composable
private fun AppBar(onBack: () -> Unit) {
    GBDiaryAppBar {
        Box(Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "뒤로"
                )
            }
            Text(
                text = Settings.THEME.text,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun RadioItem(
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    SettingsItem(
        name = name,
        contentPadding = PaddingValues(start = 24.dp, end = 10.dp)
    ) {
        GBDiaryRadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}

val Theme.text
    get() = when (this) {
        Theme.SYSTEM -> "시스템 설정"
        Theme.LIGHT -> "라이트 모드"
        Theme.DARK -> "다크 모드"
    }