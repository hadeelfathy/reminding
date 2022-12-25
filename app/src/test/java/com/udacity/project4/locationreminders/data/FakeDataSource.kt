package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//   Create a fake data source to act as a double to the real data source
//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders:MutableList<ReminderDTO>?= mutableListOf()) : ReminderDataSource {

    private var returnError= false
    fun setError(value:Boolean){
        returnError=value
    }





    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (returnError)return Result.Error("There is an error")
         else{reminders?.let {  return Result.Success(it) }
             return Result.Error("There is no reminders")
         }

    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (returnError)return Result.Error("exceptional error")
        else{reminders?.let{
            for (reminder in it){
                if (reminder.id==id) return Result.Success(reminder)
            }

        }
          return Result.Error("no reminders")
        }


    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }


}