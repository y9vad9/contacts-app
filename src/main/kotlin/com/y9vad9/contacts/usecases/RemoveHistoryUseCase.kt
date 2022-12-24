package com.y9vad9.contacts.usecases

import com.y9vad9.contacts.domain.value.Identifier
import com.y9vad9.contacts.repositories.ContactsRepository

class RemoveHistoryUseCase(
    private val contacts: ContactsRepository
) {
    fun execute(identifier: Identifier) {
        contacts.removeHistory(identifier)
    }
}