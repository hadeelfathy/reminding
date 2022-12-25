package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

//provide testing to the RemindersListViewModel and its live data objects
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {


    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var viewModel: RemindersListViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var repository: FakeDataSource

    @Before
    fun setupStatisticsViewModel() {
        // Initialise the repository with no tasks.
        repository = FakeDataSource()

        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),repository)
    }

    @After
      fun tearDown(){ stopKoin()  }


    @Test
      fun loadReminders_loading(){

        // Pause dispatcher so you can verify initial values.
        mainCoroutineRule.pauseDispatcher()

        // Load the task in the view model.
          viewModel.loadReminders()
          assertThat(viewModel.showLoading.getOrAwaitValue()).isTrue()

        // Execute pending coroutines actions.
        mainCoroutineRule.resumeDispatcher()

     // Then assert that the progress indicator is hidden.
        assertThat(viewModel.showLoading.getOrAwaitValue()).isFalse()



      }
     @Test
       fun loadreminders_snackbar(){
         mainCoroutineRule.pauseDispatcher()
         repository.setError(true)
         viewModel.loadReminders()

         mainCoroutineRule.pauseDispatcher()
         assertThat(viewModel.showSnackBar.getOrAwaitValue()).isEqualTo("error with getting reminders")



     }


    @Test
      fun loadreminders()= mainCoroutineRule.runBlockingTest {

          val reminder=ReminderDTO("jhg","kju","poi",6.097654,5.765432)
          repository.saveReminder(reminder)

          viewModel.loadReminders()

           assertThat(viewModel.remindersList.getOrAwaitValue()).isEmpty()

    }
































}




















