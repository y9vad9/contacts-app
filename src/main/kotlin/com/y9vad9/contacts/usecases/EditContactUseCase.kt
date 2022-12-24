package com.y9vad9.contacts.usecases

import com.y9vad9.contacts.domain.value.Identifier
import com.y9vad9.contacts.repositories.ContactsRepository

class EditContactUseCase(
    private val contactsRepository: ContactsRepository
) {
    suspend fun execute(identifier: Identifier, contactPatch: ContactsRepository.ContactPatch): Result<Unit> {
        return Result.success(
            contactsRepository.edit(
                identifier,
                contactPatch
            )
        )
    }
}