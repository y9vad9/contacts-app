package com.y9vad9.contacts.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.y9vad9.contacts.domain.Contact
import com.y9vad9.contacts.domain.value.Identifier
import com.y9vad9.contacts.usecases.LoadContactsUseCase
import com.y9vad9.contacts.usecases.ReloadContactsUseCase
import com.y9vad9.contacts.usecases.RemoveContactUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val loadContactsUseCase: LoadContactsUseCase,
    private val reloadContactsUseCase: ReloadContactsUseCase,
    private val removeContactUseCase: RemoveContactUseCase
) : ViewModel() {
    private val _contacts = MutableStateFlow(emptyList<Contact>())
    private val _isLoading = MutableStateFlow(true)
    private val _isFailed = MutableStateFlow(false)

    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val isFailed: StateFlow<Boolean> = _isFailed.asStateFlow()

    fun init() {
        viewModelScope.launch {
            loadContactsUseCase.execute().onSuccess {
                _contacts.value = it
            }

            _isLoading.value = false
        }
    }

    fun reinit() {
        _isLoading.value = true
        viewModelScope.launch {
            _contacts.value = reloadContactsUseCase.execute().getOrThrow()
            _isLoading.value = false
        }
    }

    fun remove(identifier: Identifier) {
        viewModelScope.launch {
            removeContactUseCase.execute(identifier)
        }
    }
}