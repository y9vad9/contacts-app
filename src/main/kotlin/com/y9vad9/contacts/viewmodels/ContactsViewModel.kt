package com.y9vad9.contacts.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.y9vad9.contacts.domain.Contact
import com.y9vad9.contacts.domain.value.Identifier
import com.y9vad9.contacts.usecases.LoadContactsUseCase
import com.y9vad9.contacts.usecases.ReloadContactsUseCase
import com.y9vad9.contacts.usecases.RemoveContactUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val loadContactsUseCase: LoadContactsUseCase,
    private val reloadContactsUseCase: ReloadContactsUseCase,
    private val removeContactUseCase: RemoveContactUseCase
) : ViewModel() {
    private val _contacts = MutableStateFlow(emptyList<Contact>().toImmutableList())
    private val _isLoading = MutableStateFlow(true)
    private val _isFailed = MutableStateFlow(false)

    val contacts: StateFlow<ImmutableList<Contact>> = _contacts.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val isFailed: StateFlow<Boolean> = _isFailed.asStateFlow()

    fun init() {
        viewModelScope.launch {
            loadContactsUseCase.execute().onSuccess {
                _contacts.value = it.toImmutableList()
            }.onFailure {
                _isFailed.value = true
            }

            _isLoading.value = false
        }
    }

    fun reinit() {
        _isFailed.value = false
        _isLoading.value = true
        viewModelScope.launch {
            reloadContactsUseCase.execute().onSuccess {
                _contacts.value = it.toImmutableList()
            }.onFailure {
                _isFailed.value = true
            }
            _isLoading.value = false
        }
    }

    fun remove(identifier: Identifier) {
        viewModelScope.launch {
            removeContactUseCase.execute(identifier)
            _contacts.value = _contacts.value.toMutableList().apply {
                removeIf { it.identifier == identifier }
            }.toImmutableList()
        }
    }
}