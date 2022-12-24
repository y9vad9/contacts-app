package com.y9vad9.contacts.usecases

import com.y9vad9.contacts.domain.Contact
import com.y9vad9.contacts.domain.value.Identifier
import com.y9vad9.contacts.repositories.ContactsRepository

class GetContactUseCase(
    private val contacts: ContactsRepository
) {
    suspend fun execute(identifier: Identifier): Result<Contact> {
        return contacts.getContact(identifier)
    }
}