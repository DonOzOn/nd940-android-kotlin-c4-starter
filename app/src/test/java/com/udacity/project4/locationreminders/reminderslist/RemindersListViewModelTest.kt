package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects

    private lateinit var reminderListViewModel: RemindersListViewModel

    private lateinit var fakeDataSource: FakeDataSource

    // provide testing to the RemindersListViewModel and its live data objects
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val reminderList = listOf(
        ReminderDTO("title 1", "description 1", "location 1", 0.0, 0.0),
        ReminderDTO(
            "title 2",
            "description 2",
            "location 2",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        ),
        ReminderDTO(
            "title 3",
            "description 3",
            "location 3",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        ),
        ReminderDTO(
            "title 4",
            "description 4",
            "location 4",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        ),
        ReminderDTO(
            "title 5",
            "description 5",
            "location 5",
            (-360..360).random().toDouble(),
            (-360..360).random().toDouble()
        )
    )
    private val reminder1 = reminderList[0]
    private val reminder2 = reminderList[1]
    private val reminder3 = reminderList[2]
    private val reminder4 = reminderList[3]
    private val reminder5 = reminderList[4]



    @After
    fun tearDown() {
        stopKoin()
    }

    // FIX: added test with error
    @Test
    fun check_load_reminders_when_error() = runBlockingTest {
        fakeDataSource = FakeDataSource(null)
        reminderListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource)

        fakeDataSource.setShouldReturnError(true)
        reminderListViewModel.loadReminders()
        MatcherAssert.assertThat(reminderListViewModel.showSnackBar.getOrAwaitValue(), CoreMatchers.`is`("No reminders to return"))

    }

    @Test
    fun check_list_reminders() {
        val remindersList = mutableListOf(reminder1, reminder2, reminder3, reminder4, reminder5)
        fakeDataSource = FakeDataSource(remindersList)
        reminderListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource)
        reminderListViewModel.loadReminders()
        MatcherAssert.assertThat(
            reminderListViewModel.remindersList.getOrAwaitValue(),
            (CoreMatchers.not(emptyList()))
        )
        MatcherAssert.assertThat(
            reminderListViewModel.remindersList.getOrAwaitValue().size,
            `is`(remindersList.size)
        )
    }

    @Test
    fun check_loading() = runBlockingTest {
        fakeDataSource = FakeDataSource(mutableListOf())
        reminderListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource)
        mainCoroutineRule.pauseDispatcher()
        reminderListViewModel.loadReminders()
        MatcherAssert.assertThat(
            reminderListViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
    }

    @Test
    fun checkShowLoadingloadReminders() {
        fakeDataSource = FakeDataSource(mutableListOf(reminder1, reminder2, reminder3, reminder4, reminder5))
        reminderListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource)
        reminderListViewModel.loadReminders()
        MatcherAssert.assertThat(
            reminderListViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
    }

    @Test
    fun returnError() {
        fakeDataSource = FakeDataSource(null)
        reminderListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource)
        reminderListViewModel.loadReminders()
        MatcherAssert.assertThat(
            reminderListViewModel.showSnackBar.getOrAwaitValue(),
            CoreMatchers.`is`("No reminders to return")
        )
    }
}