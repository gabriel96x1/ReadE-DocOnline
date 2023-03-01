package com.mutablestate.readonline.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mutablestate.readonline.domain.models.UserChipInfo

class ReaderViewModel : ViewModel() {
    private val _isReadNFC = MutableLiveData<Boolean>()
    val isReadNFC : LiveData<Boolean> = _isReadNFC
    private val _userChipInfo = MutableLiveData<UserChipInfo>()
    val userChipInfo : LiveData<UserChipInfo> = _userChipInfo

    init {
        _isReadNFC.value = false
    }

    fun updateUserChipInfo(chipInfo: UserChipInfo) {
        _userChipInfo.value = chipInfo
    }

    fun finishReadNfc() {
        _isReadNFC.value = true
    }
}