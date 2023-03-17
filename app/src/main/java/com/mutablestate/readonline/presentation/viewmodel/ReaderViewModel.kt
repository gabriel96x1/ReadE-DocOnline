package com.mutablestate.readonline.presentation.viewmodel

import android.content.Context
import android.nfc.tech.IsoDep
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutablestate.readonline.domain.models.UserChipInfo
import com.mutablestate.readonline.domain.nfcreading.NFCReadingProcess
import com.mutablestate.readonline.presentation.stateflows.ReadingNFCEvent
import com.mutablestate.readonline.presentation.stateflows.ReadingNFCState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jmrtd.BACKeySpec

class ReaderViewModel : ViewModel() {
    private val _readingEvent = MutableLiveData<ReadingNFCEvent>()
    val readingEvent : LiveData<ReadingNFCEvent> = _readingEvent
    private val _readingState = MutableLiveData<ReadingNFCState>()
    val readingState : LiveData<ReadingNFCState> = _readingState
    private val _tagLost = MutableLiveData<Boolean>()
    val tagLost : LiveData<Boolean> = _tagLost

    init {
        _readingEvent.value = ReadingNFCEvent.PREREAD
        _readingState.value = ReadingNFCState.PREREAD
    }

    private fun runNFCReadingTask(context: Context, isoDep: IsoDep?, bacKey: BACKeySpec) {
        val readingProcess = NFCReadingProcess(context)
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val data = readingProcess.readTask(isoDep, bacKey)
                withContext(Dispatchers.Main) {
                    _readingEvent.value = ReadingNFCEvent.ENDREAD
                    _readingState.value = ReadingNFCState.ENDREAD(data)
                }
            } catch (e: Exception) {
                Log.e("ReadingNFC", e.message.toString())
                withContext(Dispatchers.Main) {
                    _tagLost.value = true
                }
            }
        }
    }

    fun startReadNfc(context: Context, isoDep: IsoDep?, bacKey: BACKeySpec) {
        _readingEvent.value = ReadingNFCEvent.READING
        runNFCReadingTask(context, isoDep, bacKey)
    }

    fun resetTagLostState() {
        _tagLost.value = false
        _readingEvent.value = ReadingNFCEvent.PREREAD
        _readingState.value = ReadingNFCState.PREREAD
    }
}