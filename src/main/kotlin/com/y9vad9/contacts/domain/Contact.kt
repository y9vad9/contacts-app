package com.y9vad9.contacts.domain

import com.y9vad9.contacts.domain.value.*

data class Contact(
    val identifier: Identifier,
    val email: Email,
    val name: Name,
    val surname: Surname,
    val profilePictureLink: Link
)