package com.mutablestate.readonline.presentation.viewmodel

import android.content.Context
import android.nfc.tech.IsoDep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutablestate.readonline.domain.models.UserChipInfo
import com.mutablestate.readonline.domain.nfcreading.NFCReadingProcess
import com.mutablestate.readonline.presentation.stateflows.ReadingNFCState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jmrtd.BACKeySpec

class ReaderViewModel : ViewModel() {
    private val _userChipInfo = MutableLiveData<UserChipInfo>()
    val userChipInfo : LiveData<UserChipInfo> = _userChipInfo
    private val _readingState = MutableLiveData<ReadingNFCState>()
    val readingState : LiveData<ReadingNFCState> = _readingState

    init {
        _readingState.value = ReadingNFCState.PREREAD
    }

    private fun runNFCReadingTask(context: Context, isoDep: IsoDep?, bacKey: BACKeySpec) {
        val readingProcess = NFCReadingProcess(context)

        viewModelScope.launch(Dispatchers.Default) {
            readingProcess.readTask(isoDep, bacKey, _userChipInfo)
            withContext(Dispatchers.Main) {
                _readingState.value = ReadingNFCState.ENDREAD
            }
        }
    }

    fun startReadNfc(context: Context, isoDep: IsoDep?, bacKey: BACKeySpec) {
        _readingState.value = ReadingNFCState.READING
        runNFCReadingTask(context, isoDep, bacKey)
    }
}