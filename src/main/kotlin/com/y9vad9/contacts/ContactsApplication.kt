package com.y9vad9.contacts

import android.app.Application
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.y9vad9.contacts.repositories.datasource.db.ContactsDatabase
import kotlin.properties.Delegates

class ContactsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        database = ContactsDatabase(
            AndroidSqliteDriver(
                ContactsDatabase.Schema,
                applicationContext,
                "contacts.db"
            )
        )
    }

    companion object {
        var database: ContactsDatabase by Delegates.notNull()
    }
}