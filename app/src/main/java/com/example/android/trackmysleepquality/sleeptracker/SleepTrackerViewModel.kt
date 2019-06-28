/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.provider.SyncStateContract.Helpers.insert
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {
         private val viewModelJob = Job()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private  val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob) // UIScope will run on the main thread
    private var tonigth = MutableLiveData <SleepNight?>()
    private val nigths = database.getAllNights()

    val nightsString = Transformations.map(nigths){ nigths ->
        formatNights(nigths , application.resources)

    }

    private val _navigationToSleepQuality = MutableLiveData<SleepNight>()
    val navigationToSleepNight : LiveData<SleepNight>
        get() = _navigationToSleepQuality


    init {
        initializeTonigth()
    }

    private fun initializeTonigth(){
        uiScope.launch { // Create th Coroutine without blocking the current thread in the context defined by the scope
            tonigth.value = getTonightFromDatabase() // return sleepNigth or Null
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight?{ // private call it from inside the Coroutine and not block
        return withContext(Dispatchers.IO){// create another Coroutine in I/O context using the i/o dispatcher
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli){
                night = null
            }
            night
        }
    }
    /* START */
    fun onStartTracking(){
        uiScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonigth.value = getTonightFromDatabase()
        }
    }

    private suspend fun insert(night: SleepNight){
        withContext(Dispatchers.IO){
            database.insert(night)
        }
    }

    /* STOP */
    fun onStopTracking(){
        uiScope.launch {
            val oldNight = tonigth.value?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
        }
    }

    private suspend fun update(nightUpdate: SleepNight){
        withContext(Dispatchers.IO){
            database.upDate(nightUpdate)
        }
    }
    /* CLEAR */
    fun onClear(){
        uiScope.launch {
            clear()
            tonigth.value = null
        }

    }

    suspend fun clear(){
        withContext(Dispatchers.IO){
            database.clear()
        }
    }

}



