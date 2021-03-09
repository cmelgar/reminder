package com.udacity.project4

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = ApplicationProvider.getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                        appContext,
                        get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                        appContext,
                        get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun loginWithEmail() = runBlockingTest {
        val activityScenario = ActivityScenario.launch(AuthenticationActivity::class.java)

        dataBindingIdlingResource.monitorActivity(activityScenario)

        val randomString = (1..5)
            .map { i -> kotlin.random.Random.nextInt(0, 10) }
            .joinToString("");

        onView(withId(R.id.login_button)).perform(click())
        onView(withId(R.id.email_button)).perform(click())

        onView(withId(R.id.email)).perform(typeText(randomString + "@gmail.com"))
        onView(withId(R.id.button_next)).perform(click())
        Thread.sleep(3000)
//        onView(withId(R.id.name)).check { view, noViewFoundException ->
//            matches(isDisplayed()).apply {
//                onView(withId(R.id.name)).perform(typeText("Carlos"))
//                onView(withId(R.id.password)).perform(typeText("123456"))
//                onView(withId(R.id.button_create)).perform(click())
//            }
////
////            onView(withId(R.id.name)).check(matches(isDisplayed())).perform(typeText("Carlos"))
////            onView(withId(R.id.password)).check(matches(isDisplayed())).perform(typeText("123456"))
////            onView(withId(R.id.button_create)).check(matches(isDisplayed())).perform(click())
//        }
        onView(withId(R.id.name)).check(matches(isDisplayed()))
        onView(withId(R.id.name)).perform(typeText("Carlos"))
        onView(withId(R.id.password)).check(matches(isDisplayed())).perform(typeText("123456"))
        onView(withId(R.id.button_create)).check(matches(isDisplayed())).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.addReminderFAB)).check(matches(isDisplayed()))
        onView(withId(R.id.logout)).check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun noLocationSaved() = runBlocking {

        repository.deleteAllReminders()

        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))

//        onView(withId(R.id.addReminderFAB)).perform(click())

//        onView(withId(R.id.reminderTitle)).perform(typeText("New location"))
//        onView(withId(R.id.reminderDescription)).perform(typeText("Description"))
//        closeSoftKeyboard()
////        Thread.sleep(2000)
//        onView(withId(R.id.saveReminder)).perform(click())

        activityScenario.close()

    }


    @Test
    fun saveReminder() = runBlocking {

        repository.deleteAllReminders()
        repository.saveReminder(ReminderDTO("Title1", "Description1", "Location", 0.0, 0.0))


        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withText("Title1")).check(matches(isDisplayed()))
//
//        onView(withId(R.id.addReminderFAB)).perform(click())
//
//        onView(withId(R.id.selectLocation)).perform(click())
//
//        onView(withId(R.id.save_button)).perform(click())
//
//        onView(withId(R.id.reminderTitle)).perform(typeText("New location"))
//        onView(withId(R.id.reminderDescription)).perform(typeText("Description"))
//
////        val _viewModel: SaveReminderViewModel by inject()
//
////        SaveReminderViewModel()
////        closeSoftKeyboard()
////        Thread.sleep(2000)
//        onView(withId(R.id.saveReminder)).perform(click())

        activityScenario.close()

    }

}
