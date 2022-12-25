package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

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
}




















}