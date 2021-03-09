package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.android.inject

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private val reminder1 = ReminderDTO("Title1", "Description1", "Location", 0.0, 0.0)
    private val reminder2 = ReminderDTO("Title1", "Description1", "Location", 0.0, 0.0)
    private val reminder3 = ReminderDTO("Title1", "Description1", "Location", 0.0, 0.0)
//    private val remindersList = listOf(reminder1, reminder2, reminder3).sortedBy { it.id }

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var reminderLocalRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase


    @Before
    fun createRepository() = runBlocking() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        reminderLocalRepository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminder_getReminderById() = runBlocking {
        reminderLocalRepository.saveReminder(reminder1)

        val reminderRetrieved = reminderLocalRepository.getReminder(reminder1.id) as Result.Success

        assertThat(reminderRetrieved.data.id, `is`(reminder1.id))
        assertThat(reminderRetrieved.data.title, `is`(reminder1.title))
        assertThat(reminderRetrieved.data.description, `is`(reminder1.description))
        assertThat(reminderRetrieved.data.location, `is`(reminder1.location))
        assertThat(reminderRetrieved.data.latitude, `is`(reminder1.latitude))
        assertThat(reminderRetrieved.data.longitude, `is`(reminder1.longitude))
    }

    @Test
    fun deleteAllReminders_checkReminders() = runBlocking {
        reminderLocalRepository.saveReminder(reminder1)
        reminderLocalRepository.saveReminder(reminder2)
        reminderLocalRepository.saveReminder(reminder3)
        reminderLocalRepository.deleteAllReminders()
        val reminders = reminderLocalRepository.getReminders()  as Result.Success

        val remindersList = reminders.data

        assertThat(remindersList.size, `is`(0))
//        assertThat(reminders.data[1].id, `is`(reminder2.id))
//        assertThat(reminders.data[2].id, `is`(reminder3.id))
    }

    @Test
    fun getReminder_handleError() = runBlocking {
        val result = reminderLocalRepository.getReminder("test") as Result.Error

        assertThat(result.message, `is`("Reminder not found!"))
    }

}