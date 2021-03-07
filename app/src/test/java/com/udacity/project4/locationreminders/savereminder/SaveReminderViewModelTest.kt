package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowPackageManager.resources

@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var saveReminderViewModel: SaveReminderViewModel

    private lateinit var fakeDataSource: FakeDataSource

    @Before
    fun setupSaveReminderViewModel() {
        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @Test
    fun validateEnteredData_reminderValid() {
        // GIVEN
        val reminder = ReminderDataItem("Title1", "Description", "Location", 0.0, 0.0)

        // WHEN
        val result = saveReminderViewModel.validateEnteredData(reminder)

        // THEN
        assertThat(result, `is`(true))
    }

    @Test
    fun validateReminder_titleEmpty() {
        // GIVEN
        val reminder = ReminderDataItem(null, "Description", "Location", 0.0, 0.0)

        // WHEN
        val result = saveReminderViewModel.validateEnteredData(reminder)

        // THEN
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_enter_title))
        assertThat(result, `is`(false))

    }

    @Test
    fun validateReminder_locationEmpty() {
        // GIVEN
        val reminder = ReminderDataItem("title1", "Description", null, 0.0, 0.0)

        // WHEN
        val result =saveReminderViewModel.validateEnteredData(reminder)

        // THEN
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_select_location))
        assertThat(result, `is`(false))

    }

    @Test
    fun saveReminder() = mainCoroutineRule.runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        // GIVEN
        val reminder = ReminderDataItem("title1", "Description", "Location1", 0.0, 0.0)

        // WHEN
        val result = saveReminderViewModel.saveReminder(reminder)
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()
        // THEN

//        val toastText: SingleLiveEvent<String> = saveReminderViewModel.showToast.getOrAwaitValue()
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(fakeDataSource.reminders?.get(0)?.description, `is`(reminder.description))
//        assertEquals(saveReminderViewModel.showToast.getOrAwaitValue(), `is`(R.string.reminder_saved))

    }

}