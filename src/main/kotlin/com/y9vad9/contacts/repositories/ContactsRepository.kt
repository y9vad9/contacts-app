package com.y9vad9.contacts.repositories

import com.y9vad9.contacts.domain.Contact
import com.y9vad9.contacts.domain.value.*
import com.y9vad9.contacts.repositories.datasources.InMemoryOriginalsDataSource
import com.y9vad9.contacts.repositories.datasources.LocalContactsDataSource
import com.y9vad9.contacts.repositories.datasources.RemoteContactsDataSource
import com.y9vad9.contacts.repositories.datasources.db.Contact as DbContact

class ContactsRepository(
    private val localContacts: LocalContactsDataSource,
    private val remoteContacts: RemoteContactsDataSource,
    private val originals: InMemoryOriginalsDataSource
) {
    suspend fun getContacts(count: Count): Result<List<Contact>> {
        val localUsers = localContacts.getContacts()

        return if (localUsers.isEmpty()) {
            val randomUsers = remoteContacts.getRandomUsers(count)

            if (randomUsers.isFailure)
                Result.failure(
                    randomUsers.exceptionOrNull()!!
                )
            else {
                val mappedContacts = randomUsers.getOrThrow().results.map { it.toDbVersion() }
                localContacts.saveContacts(mappedContacts)

                Result.success(
                    localContacts.getContacts().map { it.external() }
                )
            }
        } else {
            Result.success(localUsers.map { it.external() })
        }
    }

    suspend fun getContact(identifier: Identifier): Result<Contact> {
        return Result.success(localContacts.getContact(identifier).external())
    }

    fun hasHistory(identifier: Identifier): Boolean {
        return originals.getOriginal(identifier) != null
    }

    fun removeHistory(identifier: Identifier) {
        originals.removeHistory(identifier)
    }

    suspend fun rollback(identifier: Identifier): Boolean {
        val original = originals.getOriginal(identifier)
        return original?.let {
            localContacts.patch(
                identifier.int,
                ContactPatch(it.name, it.surname, it.email, it.profilePictureLink)
            )
            true
        } ?: false
    }

    suspend fun edit(identifier: Identifier, patch: ContactPatch) {
        originals.saveOriginal(localContacts.getContact(identifier).external())
        localContacts.patch(identifier.int, patch)
    }

    suspend fun removeContact(identifier: Identifier) {
        localContacts.remove(identifier.int)
    }

    suspend fun reset() {
        localContacts.removeAll()
        originals.reset()
    }

    data class ContactPatch(
        val name: Name? = null,
        val surname: Surname? = null,
        val email: Email? = null,
        val profilePictureLink: Link? = null
    )

    private fun RemoteContactsDataSource.GetRandomUsersResult.Contact.toDbVersion(): LocalContactsDataSource.ContactWithoutId {
        return LocalContactsDataSource.ContactWithoutId(
            name.first, name.last, email, picture.medium
        )
    }

    private fun DbContact.external(): Contact {
        return Contact(Identifier(id), Email(email), Name(name), Surname(surname), Link(photoLink))
    }
}