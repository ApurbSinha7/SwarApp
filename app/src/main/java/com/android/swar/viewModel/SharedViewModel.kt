package com.android.swar.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _caption = MutableLiveData<String>()
    val caption: LiveData<String> get() = _caption

    private val _audioUri = MutableLiveData<String>()
    val audioUri: LiveData<String> get() = _audioUri

    fun setCaption(caption: String) {
        _caption.value = caption
    }

    fun setAudioUri(uri: String) {
        _audioUri.value = uri
    }
}