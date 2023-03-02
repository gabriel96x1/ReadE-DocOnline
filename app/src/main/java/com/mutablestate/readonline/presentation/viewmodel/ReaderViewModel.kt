package com.mutablestate.readonline.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mutablestate.readonline.domain.models.UserChipInfo
import com.mutablestate.readonline.presentation.stateflows.ReadingNFCState

class ReaderViewModel : ViewModel() {
    private val _userChipInfo = MutableLiveData<UserChipInfo>()
    val userChipInfo : LiveData<UserChipInfo> = _userChipInfo
    private val _readingState = MutableLiveData<ReadingNFCState>()
    val readingState : LiveData<ReadingNFCState> = _readingState

    init {
        _readingState.value = ReadingNFCState.PREREAD
    }

    fun updateUserChipInfo(chipInfo: UserChipInfo) {
        _userChipInfo.value = chipInfo
    }

    fun startReadNfc() {
        _readingState.value = ReadingNFCState.READING
    }

    fun finishReadNfc() {
        _readingState.value = ReadingNFCState.ENDREAD
    }
}