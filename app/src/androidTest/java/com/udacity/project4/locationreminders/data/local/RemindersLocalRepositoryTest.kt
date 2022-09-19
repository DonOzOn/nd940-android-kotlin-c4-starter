package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.ReminderDao
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.junit.*
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.locationreminders.data.local.RemindersDao
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

//    TODO: Add testing implementation to the RemindersLocalRepository.kt
@get:Rule
var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val reminderList =  listOf(ReminderDTO("title1", "description1",
        "location1",(-360..360).random().toDouble(),(-360..360).random().toDouble()),
        ReminderDTO("title1", "description2",
            "location2",(-360..360).random().toDouble(),(-360..360).random().toDouble()))

    private val reminder1 = reminderList[0]
    private val reminder2 = reminderList[1]

    private lateinit var remindersDao: ReminderDao
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @Before
    fun setup() {
        remindersDao = ReminderDao()
        remindersLocalRepository = RemindersLocalRepository(
            remindersDao, Dispatchers.Unconfined
        )
    }

    @Test
    fun check_save_to_local() = runBlockingTest {
        var tempList = mutableListOf<ReminderDTO>()
        tempList.addAll(remindersDao.remindersServiceData.values)
        assertThat(tempList).doesNotContain(reminder1)
        assertThat((remindersLocalRepository.getReminders() as? Result.Success)?.data).doesNotContain(
            reminder1
        )
        assertThat((remindersLocalRepository.getReminders() as? Result.Success)?.data).doesNotContain(
            reminder2
        )

        // When a reminder is saved to the tasks repository
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)

        tempList = mutableListOf()
        tempList.addAll(remindersDao.remindersServiceData.values)
        // Then the local sources are called and the cache is updated
        assertThat(tempList).contains(reminder1)
        assertThat(tempList).contains(reminder2)

        val result = remindersLocalRepository.getReminders() as? Result.Success
        assertThat(result?.data).contains(reminder1)
        assertThat(result?.data).contains(reminder2)
    }

    @Test
    fun check_delete_all_fetch_empty() = runBlockingTest {
        assertThat((remindersLocalRepository.getReminders() as? Result.Success)?.data).isEmpty()

        remindersDao.remindersServiceData[reminder1.id] = reminder1
        remindersDao.remindersServiceData[reminder2.id] = reminder2

        // When
        assertThat((remindersLocalRepository.getReminders() as? Result.Success)?.data).isNotEmpty()
        remindersLocalRepository.deleteAllReminders()

        // Then
        assertThat((remindersLocalRepository.getReminders() as? Result.Success)?.data).isEmpty()
    }

    @Test
    fun check_existing_id() = runBlockingTest {
        // Make sure newReminder is not in the local cache
        assertThat((remindersLocalRepository.getReminder(reminder1.id) as? Result.Error)?.message).isEqualTo(
            "Reminder not found!")

        remindersDao.remindersServiceData[reminder1.id] = reminder1

        // When
        val loadedReminder = (remindersLocalRepository.getReminder(reminder1.id) as? Result.Success)?.data

        Assert.assertThat<ReminderDTO>(loadedReminder as ReminderDTO, CoreMatchers.notNullValue())
        Assert.assertThat(loadedReminder.id, `is`(reminder1.id))
        Assert.assertThat(loadedReminder.title, `is`(reminder1.title))
        Assert.assertThat(loadedReminder.description, `is`(reminder1.description))
        Assert.assertThat(loadedReminder.location, `is`(reminder1.location))
        Assert.assertThat(loadedReminder.latitude, `is`(reminder1.latitude))
        Assert.assertThat(loadedReminder.longitude, `is`(reminder1.longitude))
    }

    @Test
    fun check_does_not_exist() = runBlockingTest {

        val message = (remindersLocalRepository.getReminder(reminder1.id) as? Result.Error)?.message
        Assert.assertThat<String>(message, CoreMatchers.notNullValue())
        assertThat(message).isEqualTo("Reminder not found!")

    }
}