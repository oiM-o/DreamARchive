package com.example.dreamarchive.ui.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {

    // MutableStateFlowでトグルボタンの状態を保持
    private val _isGoodDreamMode = MutableStateFlow(false)
    val isGoodDreamMode: StateFlow<Boolean> = _isGoodDreamMode

    // トグルボタンの状態を変更する関数
    fun toggleGoodDreamMode(enabled: Boolean) {
        viewModelScope.launch {
            _isGoodDreamMode.value = enabled
        }
    }
}