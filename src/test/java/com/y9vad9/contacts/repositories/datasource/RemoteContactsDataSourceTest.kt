package com.y9vad9.contacts.repositories.datasource

import com.y9vad9.contacts.domain.value.Count
import com.y9vad9.contacts.repositories.datasources.RemoteContactsDataSource
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class RemoteContactsDataSourceTest {
    private val ds = RemoteContactsDataSource()

    @Test
    fun testSuccess(): Unit = runBlocking {
        val result = ds.getRandomUsers(Count(5))
        assert(result.isSuccess)
        assert(result.getOrThrow().results.size == 5)
    }
}