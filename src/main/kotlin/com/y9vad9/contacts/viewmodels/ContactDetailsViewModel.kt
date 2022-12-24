package com.y9vad9.contacts.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.y9vad9.contacts.domain.Contact
import com.y9vad9.contacts.domain.value.Identifier
import com.y9vad9.contacts.usecases.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContactDetailsViewModel(
    private val identifier: Identifier,
    private val getContactUseCase: GetContactUseCase,
    private val removeContactUseCase: RemoveContactUseCase,
    private val restoreOriginalUseCase: RestoreOriginalUseCase,
    private val checkRollbackableUseCase: CheckRollbackableUseCase,
    private val removeHistoryUseCase: RemoveHistoryUseCase
) : ViewModel() {
    private val _contact: MutableStateFlow<Contact?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)
    private val _isPendingChanges = MutableStateFlow(false)

    val contact = _contact.asStateFlow()
    val isLoading = _isLoading.asStateFlow()
    val isPendingChanges = _isPendingChanges.asStateFlow()

    fun removeRollback(onDeleted: () -> Unit) {
        removeHistoryUseCase.execute(identifier)
        onDeleted()
    }

    fun init() {
        _isLoading.value = true
        viewModelScope.launch {
            _contact.value = getContactUseCase.execute(identifier).getOrThrow()
            _isLoading.value = false
            if (checkRollbackableUseCase.execute(identifier)) {
                _isPendingChanges.value = true
                delay(3000L)
                _isPendingChanges.value = false
                removeHistoryUseCase.execute(contact.value!!.identifier)
            }
        }
    }

    fun restore(onCancel: () -> Unit = {}) {
        _isPendingChanges.value = false
        viewModelScope.launch {
            restoreOriginalUseCase.execute(identifier)
            onCancel()
            _contact.value = getContactUseCase.execute(identifier).getOrThrow()
        }
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            removeContactUseCase.execute(identifier)
            removeHistoryUseCase.execute(identifier)
            onDeleted()
        }
    }
}