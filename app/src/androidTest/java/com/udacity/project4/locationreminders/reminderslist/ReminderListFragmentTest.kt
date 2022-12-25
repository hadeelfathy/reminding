package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.koin.test.get
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest:AutoCloseKoinTest() {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var reminderRepository:ReminderDataSource
    private lateinit var appContext: Application
//  test the navigation of the fragments.
//  test the displayed data on the UI.
//   add testing for the error messages.

    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
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
        reminderRepository = get()

        //clear the data to start fresh
        runBlocking {
            reminderRepository.deleteAllReminders()
        }
    }

   @Test
     fun completeReminderDetails_displayed(): ViewInteraction?= runBlocking {

       val reminder= ReminderDTO("fdt","kiu","hyg",8.654321,5.690321)
        reminderRepository.saveReminder(reminder)

       launchFragmentInContainer<ReminderListFragment>(Bundle(),R.style.AppTheme)
       onView(withId(R.id.reminderssRecyclerView)).perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
           hasDescendant(withText(reminder.title))
       ))




   }

    @Test
    fun noData_OnUI(): ViewInteraction?= runBlocking {

        val reminder= ReminderDTO("fdt","kiu","hyg",8.654321,5.690321)
        reminderRepository.saveReminder(reminder)
        reminderRepository.deleteAllReminders()

        launchFragmentInContainer<ReminderListFragment>(Bundle(),R.style.AppTheme)
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))




    }

    @Test
      fun saveReminder_navigation(){

        // GIVEN - On the home screen
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        // WHEN - Click on the first list item
        onView(withId(R.id.addReminderFAB)).perform(click())


        // THEN - Verify that we navigate to the first detail screen
        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }









      }




































