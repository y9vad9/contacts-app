package com.y9vad9.contacts.repositories.datasources

import com.y9vad9.contacts.domain.value.Identifier
import com.y9vad9.contacts.repositories.ContactsRepository
import com.y9vad9.contacts.repositories.datasource.db.ContactsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.y9vad9.contacts.repositories.datasources.db.Contact as DbContact

class LocalContactsDataSource(private val database: ContactsDatabase) {
    suspend fun getContacts(): List<DbContact> {
        return withContext(Dispatchers.IO) {
            database.contactsQueries.getAll(Long.MAX_VALUE, 0)
                .executeAsList()
        }
    }

    suspend fun getContact(identifier: Identifier): com.y9vad9.contacts.repositories.datasources.db.Contact {
        return withContext(Dispatchers.IO) {
            database.contactsQueries.get(identifier.int)
                .executeAsOne()
        }
    }

    /**
     * Saves contacts to database.
     *
     * @return [Int] with last inserted id.
     */
    suspend fun saveContacts(collection: Collection<ContactWithoutId>): Int {
        return withContext(Dispatchers.IO) {
            database.contactsQueries.transactionWithResult {
                collection.forEach {
                    database.contactsQueries.addContact(
                        it.name,
                        it.surname,
                        it.email,
                        it.profilePictureUrl
                    )
                }
                database.contactsQueries.getLastId().executeAsOne().toInt()
            }
        }
    }

    suspend fun patch(id: Int, patch: ContactsRepository.ContactPatch) {
        return withContext(Dispatchers.IO) {
            patch.name?.let { database.contactsQueries.setName(it.string, id) }
            patch.email?.let { database.contactsQueries.setEmail(it.string, id) }
            patch.surname?.let { database.contactsQueries.setSurname(it.string, id) }
            patch.profilePictureLink?.let {
                database.contactsQueries.setProfileLink(it.string, id)
            }
        }
    }

    suspend fun remove(id: Int) = withContext(Dispatchers.IO) {
        database.contactsQueries.remove(id)
    }

    suspend fun removeAll() = withContext(Dispatchers.IO) {
        database.contactsQueries.deleteAll()
    }

    class ContactWithoutId(
        val name: String,
        val surname: String,
        val email: String,
        val profilePictureUrl: String
    )
}