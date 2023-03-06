package com.mutablestate.readonline.presentation.stateflows

import com.mutablestate.readonline.domain.models.UserChipInfo

sealed class ReadingNFCState {
    object PREREAD : ReadingNFCState()

    data class ENDREAD(val userChipInfo: UserChipInfo) : ReadingNFCState()
}

sealed class ReadingNFCEvent {
    object PREREAD : ReadingNFCEvent()

    object READING : ReadingNFCEvent()

    object ENDREAD : ReadingNFCEvent()
}