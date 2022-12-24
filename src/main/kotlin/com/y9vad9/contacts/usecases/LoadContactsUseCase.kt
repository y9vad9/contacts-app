package com.y9vad9.contacts.usecases

import com.y9vad9.contacts.domain.Contact
import com.y9vad9.contacts.domain.value.Count
import com.y9vad9.contacts.repositories.ContactsRepository

class LoadContactsUseCase(
    private val contacts: ContactsRepository
) {
    suspend fun execute(): Result<List<Contact>> {
        return contacts.getContacts(Count(20))
    }
}