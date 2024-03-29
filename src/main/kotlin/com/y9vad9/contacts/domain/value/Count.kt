package com.y9vad9.contacts.domain.value

@JvmInline
value class Count(val int: Int) {
    init {
        require(int >= 0) { "Count cannot be negative" }
    }
}