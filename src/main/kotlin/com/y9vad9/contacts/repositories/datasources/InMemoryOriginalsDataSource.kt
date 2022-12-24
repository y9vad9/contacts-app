package com.y9vad9.contacts.repositories.datasources

import com.y9vad9.contacts.domain.Contact
import com.y9vad9.contacts.domain.value.Identifier

class InMemoryOriginalsDataSource {
    private val list = mutableListOf<Contact>()

    fun getOriginal(identifier: Identifier) =
        list.firstOrNull { it.identifier == identifier }

    fun removeHistory(identifier: Identifier) =
        list.removeIf { it.identifier == identifier }

    fun saveOriginal(contact: Contact) {
        list.removeIf { it.identifier == contact.identifier }
        list += contact
    }

    fun reset() = list.clear()
}