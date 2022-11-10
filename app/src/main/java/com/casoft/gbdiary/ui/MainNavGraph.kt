package com.casoft.gbdiary.ui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.casoft.gbdiary.ui.calendar.CalendarScreen
import com.casoft.gbdiary.ui.calendar.CalendarViewModel
import com.casoft.gbdiary.ui.calendar.rememberCalendarState
import com.casoft.gbdiary.ui.diary.DiaryActions
import com.casoft.gbdiary.ui.diary.DiaryDestinations
import com.casoft.gbdiary.ui.diary.diaryNavGraph
import com.casoft.gbdiary.ui.search.SearchScreen
import com.casoft.gbdiary.ui.search.SearchViewModel
import com.casoft.gbdiary.ui.settings.SettingsActions
import com.casoft.gbdiary.ui.settings.SettingsDestination
import com.casoft.gbdiary.ui.settings.settingsNavGraph
import com.casoft.gbdiary.ui.timeline.TimelineScreen
import com.casoft.gbdiary.ui.timeline.TimelineViewModel
import java.time.LocalDate
import java.time.YearMonth

fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController,
    actions: MainActions,
    diaryActions: DiaryActions,
    screenUnlocked: State<Boolean?>, // Boolean으로 선언 시 recomposition되지 않음.
) {
    composable(MainDestinations.HOME_ROUTE) {
        val isScreenUnlocked = screenUnlocked.value

        if (isScreenUnlocked != null) {
            LaunchedEffect(isScreenUnlocked) {
                if (!isScreenUnlocked) {
                    navController.navigate(GBDiaryDestinations.SCREEN_LOCK_ROUTE)
                }
            }

            if (isScreenUnlocked) {
                val viewModel = hiltViewModel<CalendarViewModel>()
                CalendarScreen(
                    viewModel = viewModel,
                    onDayClick = actions::navigateToDiary,
                    onTimelineClick = { actions.navigateToTimeline(viewModel.currentYearMonth) },
                    onSearchClick = actions::navigateToSearch,
                    onSettingsClick = actions::navigateToSettings,
                    onWriteClick = actions::navigateToDiary,
                    state = rememberCalendarState(viewModel.currentYearMonth)
                )
            }
        }
    }
    navigation(
        route = MainDestinations.DIARY_ROUTE,
        startDestination = DiaryDestinations.HOME_ROUTE
    ) {
        diaryNavGraph(diaryActions)
    }
    composable(
        route = "${MainDestinations.TIMELINE_ROUTE}/{${MainDestinations.TIMELINE_YEAR_KEY}}/{${MainDestinations.TIMELINE_MONTH_KEY}}",
        arguments = listOf(
            navArgument(MainDestinations.TIMELINE_YEAR_KEY) { type = NavType.IntType },
            navArgument(MainDestinations.TIMELINE_MONTH_KEY) { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        val year = arguments.getInt(MainDestinations.TIMELINE_YEAR_KEY)
        val month = arguments.getInt(MainDestinations.TIMELINE_MONTH_KEY)
        val viewModel = hiltViewModel<TimelineViewModel>()

        LaunchedEffect(year, month) {
            viewModel.moveToYearMonth(YearMonth.of(year, month))
        }

        TimelineScreen(
            viewModel = viewModel,
            onDiaryItemClick = { item -> actions.navigateToDiary(item.date) },
            onBack = actions::navigateUp
        )
    }
    composable(MainDestinations.SEARCH_ROUTE) {
        val viewModel = hiltViewModel<SearchViewModel>()
        SearchScreen(
            viewModel = viewModel,
            onDiaryItemClick = { item -> actions.navigateToDiary(item.date) },
            onBack = actions::navigateUp
        )
    }
    navigation(
        route = MainDestinations.SETTINGS_ROUTE,
        startDestination = SettingsDestination.HOME_ROUTE
    ) {
        settingsNavGraph(actions = SettingsActions(navController))
    }
}

object MainDestinations {
    private const val PREFIX = "main"
    const val HOME_ROUTE = "$PREFIX/home"
    const val DIARY_ROUTE = "$PREFIX/diary"
    const val TIMELINE_ROUTE = "$PREFIX/timeline"
    const val TIMELINE_YEAR_KEY = "$PREFIX/year"
    const val TIMELINE_MONTH_KEY = "$PREFIX/month"
    const val SEARCH_ROUTE = "$PREFIX/search"
    const val SETTINGS_ROUTE = "$PREFIX/settings"
}

class MainActions(private val navController: NavHostController) {

    fun navigateToDiary(date: LocalDate) {
        navController.navigate(
            "${DiaryDestinations.HOME_ROUTE}/${date.year}/${date.monthValue}/${date.dayOfMonth}"
        )
    }

    fun navigateToTimeline(initialYearMonth: YearMonth) {
        navController.navigate("${MainDestinations.TIMELINE_ROUTE}/${initialYearMonth.year}/${initialYearMonth.monthValue}")
    }

    fun navigateToSearch() {
        navController.navigate(MainDestinations.SEARCH_ROUTE)
    }

    fun navigateToSettings() {
        navController.navigate(MainDestinations.SETTINGS_ROUTE)
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}