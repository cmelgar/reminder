package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        val reminder = ReminderDTO("Title1", "Description1", "Location", 0.0, 0.0)

        database.reminderDao().saveReminder(reminder)

        val result = database.reminderDao().getReminderById(reminder.id)

        assertThat<ReminderDTO>(result as ReminderDTO, notNullValue())
        assertThat(result.id, `is`(reminder.id))
        assertThat(result.title, `is`(reminder.title))
        assertThat(result.description, `is`(reminder.description))
        assertThat(result.location, `is`(reminder.location))
        assertThat(result.latitude, `is`(reminder.latitude))
        assertThat(result.longitude, `is`(reminder.longitude))
    }

    @Test
    fun getAllReminders() = runBlockingTest {

        val reminders = mutableListOf<ReminderDTO>(
                ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0),
                ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0),
                ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0),
                ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0)
        )

        for (reminder in reminders) {
            database.reminderDao().saveReminder(reminder)
        }

        val result = database.reminderDao().getReminders()

        assertThat(result.size, `is`(reminders.size))
    }

    @Test
    fun deleteReminders() = runBlockingTest {

        val reminders = mutableListOf<ReminderDTO>(
                ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0),
                ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0),
                ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0),
                ReminderDTO("Title1", "Description1", "Location1", 0.0, 0.0)
        )

        for (reminder in reminders) {
            database.reminderDao().saveReminder(reminder)
        }

        database.reminderDao().deleteAllReminders()

        val result = database.reminderDao().getReminders()

        assertThat(result.isEmpty(), `is`(true))
    }

}