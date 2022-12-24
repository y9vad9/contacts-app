package com.y9vad9.contacts.usecases

import com.y9vad9.contacts.domain.value.Identifier
import com.y9vad9.contacts.repositories.ContactsRepository

class RestoreOriginalUseCase(
    private val contacts: ContactsRepository
) {
    suspend fun execute(identifier: Identifier) {
        contacts.rollback(identifier)
    }
}