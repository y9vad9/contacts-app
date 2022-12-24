package com.y9vad9.contacts.usecases

import com.y9vad9.contacts.domain.Contact
import com.y9vad9.contacts.domain.value.Count
import com.y9vad9.contacts.repositories.ContactsRepository

class ReloadContactsUseCase(
    private val contactsRepository: ContactsRepository
) {
    suspend fun execute(): Result<List<Contact>> {
        contactsRepository.reset()
        return contactsRepository.getContacts(Count(20))
    }
}