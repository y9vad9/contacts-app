package com.y9vad9.contacts.repositories.datasources

import com.y9vad9.contacts.domain.value.Count
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class RemoteContactsDataSource {
    private val client = HttpClient(CIO) {
        defaultRequest {
            url("https://randomuser.me/api")
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getRandomUsers(count: Count): Result<GetRandomUsersResult> = runCatching {
        val result = client.get {
            parameter("inc", "email,name,picture")
            parameter("results", count.int)
        }

        return if (result.status.isSuccess())
            Result.success(result.body())
        else Result.failure(IllegalStateException(result.bodyAsText()))
    }

    @Serializable
    class GetRandomUsersResult(
        val results: List<Contact>
    ) {
        @Serializable
        class Contact(
            val email: String,
            val name: Name,
            val picture: Picture
        ) {
            @Serializable
            class Name(
                val first: String,
                val last: String
            )

            @Serializable
            class Picture(
                val medium: String
            )
        }
    }
}