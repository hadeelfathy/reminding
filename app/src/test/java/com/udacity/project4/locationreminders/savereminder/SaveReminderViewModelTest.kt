package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

// provide testing to the SaveReminderView and its live data objects
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var viewModel: SaveReminderViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var repository: FakeDataSource

    @Before
    fun setupStatisticsViewModel() {
        // Initialise the repository with no tasks.
        repository = FakeDataSource()

        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),repository)
    }

    @After
    fun tearDown(){ stopKoin()  }


    @Test
    fun loadReminders_loading(){

        val reminder= ReminderDataItem("fdt","kiu","hyg",8.654321,5.690321)
        // Pause dispatcher so you can verify initial values.
        mainCoroutineRule.pauseDispatcher()

        // Load the task in the view model.
        viewModel.saveReminder(reminder)
        Truth.assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()

        // Execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

        // Then assert that the progress indicator is hidden.
        Truth.assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()



    }

    @Test
      fun validateTitle_snackbar(){

        val reminder= ReminderDataItem("fdt","kiu","hyg",8.654321,5.690321)

         assertThat(viewModel.validateEnteredData(reminder)).isFalse()

         assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_enter_title)


    }


    @Test
    fun validateLocation_snackbar(){

        val reminder= ReminderDataItem("fdt","kiu","hyg",8.654321,5.690321)

        assertThat(viewModel.validateEnteredData(reminder)).isFalse()

        assertThat(viewModel.showSnackBarInt.getOrAwaitValue()).isEqualTo(R.string.err_select_location)


    }










}