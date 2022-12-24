package com.y9vad9.contacts.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.y9vad9.contacts.domain.Contact
import com.y9vad9.contacts.domain.value.Email
import com.y9vad9.contacts.domain.value.Identifier
import com.y9vad9.contacts.domain.value.Name
import com.y9vad9.contacts.domain.value.Surname
import com.y9vad9.contacts.repositories.ContactsRepository
import com.y9vad9.contacts.usecases.EditContactUseCase
import com.y9vad9.contacts.usecases.GetContactUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditContactViewModel(
    private val identifier: Identifier,
    private val getContactUseCase: GetContactUseCase,
    private val editContactUseCase: EditContactUseCase
) : ViewModel() {
    private val _contact: MutableStateFlow<Contact?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)

    val contact = _contact.asStateFlow()
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        viewModelScope.launch {
            _contact.value = getContactUseCase.execute(identifier).getOrThrow()
            _isLoading.value = false
        }
    }

    fun save(name: Name, surname: Surname, email: Email, onFinish: () -> Unit) {
        viewModelScope.launch {
            editContactUseCase.execute(
                identifier,
                ContactsRepository.ContactPatch(
                    name, surname, email
                )
            )
            onFinish()
        }
    }
}