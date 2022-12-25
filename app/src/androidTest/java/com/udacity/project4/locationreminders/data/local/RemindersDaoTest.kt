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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

//   Add testing implementation to the RemindersDao.kt
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
           ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()


    @Test
      fun insertReminderAndGetByID()= runBlocking {

        val reminder= ReminderDTO("fdt","kiu","hyg",8.654321,5.690321)
         database.reminderDao().saveReminder(reminder)

         val loaded=database.reminderDao().getReminderById(reminder.id)

        assertThat<RemindersDao>(loaded as RemindersDao, notNullValue())

        assertThat(loaded.id, `is` (reminder.id))
        assertThat(loaded.title, `is` (reminder.title))
        assertThat(loaded.description, `is` (reminder.description))



    }

    @Test
    fun deleteAllReminders()= runBlocking {

        val reminder = ReminderDTO("fdt", "kiu", "hyg", 8.654321, 5.690321)
        database.reminderDao().saveReminder(reminder)
        database.reminderDao().deleteAllReminders()

        val result= database.reminderDao().getReminderById(reminder.id)
         assertThat(reminder, `is` (nullValue()))




    }







































}