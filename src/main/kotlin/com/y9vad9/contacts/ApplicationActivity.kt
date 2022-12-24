package com.y9vad9.contacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.y9vad9.contacts.domain.value.Identifier
import com.y9vad9.contacts.repositories.ContactsRepository
import com.y9vad9.contacts.repositories.datasources.InMemoryOriginalsDataSource
import com.y9vad9.contacts.repositories.datasources.LocalContactsDataSource
import com.y9vad9.contacts.repositories.datasources.RemoteContactsDataSource
import com.y9vad9.contacts.ui.ContactScreen
import com.y9vad9.contacts.ui.ContactsScreen
import com.y9vad9.contacts.ui.EditContactScreen
import com.y9vad9.contacts.ui.dependencies.*
import com.y9vad9.contacts.ui.resources.ContactsTheme
import com.y9vad9.contacts.usecases.*

class ApplicationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ContactsTheme {
                val navigation = rememberNavController()
                val contactsRepository: ContactsRepository = remember {
                    ContactsRepository(
                        LocalContactsDataSource(ContactsApplication.database),
                        RemoteContactsDataSource(),
                        InMemoryOriginalsDataSource()
                    )
                }

                CompositionLocalProvider(
                    LocalEditContactUseCase provides EditContactUseCase(contactsRepository),
                    LocalGetContactUseCase provides GetContactUseCase(contactsRepository),
                    LocalLoadContactUseCase provides LoadContactsUseCase(contactsRepository),
                    LocalReloadContactsUseCase provides ReloadContactsUseCase(contactsRepository),
                    LocalRemoveContactUseCase provides RemoveContactUseCase(contactsRepository),
                    LocalRestoreOriginalUseCase provides RestoreOriginalUseCase(contactsRepository),
                    LocalRemoveHistoryUseCase provides RemoveHistoryUseCase(contactsRepository),
                    LocalCheckRollbackableUseCase provides CheckRollbackableUseCase(contactsRepository)
                ) {
                    NavHost(
                        navigation,
                        "contacts"
                    ) {
                        composable("contacts") {
                            ContactsScreen(
                                onNavigateToDetails = {
                                    navigation.navigate(
                                        "contacts/${it.int}"
                                    )
                                }
                            )
                        }
                        composable(
                            "contacts/{id}",
                            arguments = listOf(
                                navArgument("id") {
                                    type = NavType.IntType
                                }
                            )
                        ) {
                            val id = Identifier(it.arguments!!.getInt("id"))

                            ContactScreen(
                                id,
                                onBackPressed = { navigation.navigateUp() },
                                onEdit = { navigation.navigate("contacts/${id.int}/edit") }
                            )
                        }
                        composable(
                            "contacts/{id}/edit",
                            arguments = listOf(
                                navArgument("id") {
                                    type = NavType.IntType
                                }
                            )
                        ) {
                            EditContactScreen(
                                Identifier(it.arguments!!.getInt("id")),
                                onBackPressed = { navigation.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }
}