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

package com.carlosdp.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.*
import com.carlosdp.android.trackmysleepquality.database.SleepDatabaseDao
import com.carlosdp.android.trackmysleepquality.database.SleepNight
import com.carlosdp.android.trackmysleepquality.formatNights
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

    val nights = database.getAllNights()

    val nightsString = Transformations.map(nights){ nigths ->
        formatNights(nigths, application.resources)

    }

    // For enable or desanable button
    val startButtonVisible = Transformations.map(tonigth){
        null == it // Si no existe tonight return True
    }
    val stopButtonVisible = Transformations.map(tonigth){
        null != it
        // Si no existe tonight return false
        // Si se inicial tonight return True
    }
    val clearButtonVisible = Transformations.map(nights){
        it?.isNotEmpty()
        // Si existen nights return True
    }

    // SnackBar
    private  var _showSnackbarEvent = MutableLiveData<Boolean>()
    val  showSnackbarEvent : LiveData<Boolean>
        get() = _showSnackbarEvent

    private val _navigationToSleepQuality = MutableLiveData<SleepNight>()
    val navigationToSleepNight : LiveData<SleepNight>
        get() = _navigationToSleepQuality

    fun doneShowingSnakcbar(){
        _showSnackbarEvent.value = false
    }


    fun doneNavigation(){
        _navigationToSleepQuality.value = null
    }


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
            // InKotlin the return@label syntax is used for specifying wich functions among
            // several nested ones this statement returns from
            // In this case, we are specifying to return from launch)=,
            // not the lamba
            val oldNight = tonigth.value?: return@launch
            // Update the night in the data base to add the end time
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)

            // For Navigation
            _navigationToSleepQuality.value = oldNight
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
            _showSnackbarEvent.value = true
        }

    }

    suspend fun clear(){
        withContext(Dispatchers.IO){
            database.clear()
        }
    }


    // Navigate onClick
    private val _navigateToSleepDataQuality = MutableLiveData<Long>()
    val navigateToSleepDataQuality
        get() = _navigateToSleepDataQuality

    fun onSleepNightClicked( nightClick : SleepNight){
        _navigateToSleepDataQuality.value = nightClick.nightId
    }

    fun onSleepDataQualityNavigated(){
        _navigateToSleepDataQuality.value = null
    }

}



