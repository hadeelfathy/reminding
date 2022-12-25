package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//  Add testing implementation to the RemindersLocalRepository.kt
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun setUp() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)

    }

    @After
    fun cleanUp() = database.close()


    @Test
    fun deleteReminders() = runBlocking {

        val reminder = ReminderDTO("fdt", "kiu", "hyg", 8.654321, 5.690321)
        repository.saveReminder(reminder)
        repository.deleteAllReminders()

        val result = repository.getReminders()
        assertThat(result is Result.Success, `is`(true))
        result as Result.Success

        assertThat(result.data, `is`(emptyList()))


    }

    @Test
    fun retrieveReminders_AfterDelete() = runBlocking {

        val reminder = ReminderDTO("fdt", "kiu", "hyg", 8.654321, 5.690321)
        repository.saveReminder(reminder)
        repository.deleteAllReminders()

        val result = repository.getReminder(reminder.id)
        assertThat(result is Result.Error, `is`(true))
        result as Result.Error

        assertThat(result.message, `is`("reminders not found"))


    }


    @Test
    fun saveReminders() = runBlocking {

        val reminder = ReminderDTO("fdt", "kiu", "hyg", 8.654321, 5.690321)
        repository.saveReminder(reminder)
        val result = repository.getReminder(reminder.id) as? Result.Success

        assertThat(result is Result.Success, `is`(true))
        result as Result.Success

        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))
        assertThat(result.data.location, `is`(reminder.location))



    }


































}

























