package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var remindersListViewModel: RemindersListViewModel

    private lateinit var fakeDataSource: FakeDataSource

    @Before
    fun setupReminderListViewModel() {
        fakeDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @Test
    fun loadReminders_checkReminderIsNotEmpty() = mainCoroutineRule.runBlockingTest {

        mainCoroutineRule.pauseDispatcher()

        // GIVEN the reminders are

        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        val reminder = ReminderDTO("Reminder1", "Description", "Location", 0.0,0.0)

        // WHEN
        fakeDataSource.saveReminder(reminder)

        mainCoroutineRule.resumeDispatcher()

        assertThat(remindersListViewModel.remindersList.getOrAwaitValue().isNotEmpty(), `is`(true))

        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadReminders_checkReminderIsEmpty() = mainCoroutineRule.runBlockingTest {

        mainCoroutineRule.pauseDispatcher()

        // GIVEN there are no reminders saved

        // WHEN the empty list of reminders is called
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()

        // THEN the remindersList is empty and the showNoData is true
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue().isEmpty(), `is`(true))
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(true))
    }
}