package com.y9vad9.contacts.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.y9vad9.contacts.domain.value.Email
import com.y9vad9.contacts.domain.value.Identifier
import com.y9vad9.contacts.domain.value.Name
import com.y9vad9.contacts.domain.value.Surname
import com.y9vad9.contacts.ui.dependencies.EditContactUseCase
import com.y9vad9.contacts.ui.dependencies.GetContactUseCase
import com.y9vad9.contacts.usecases.EditContactUseCase
import com.y9vad9.contacts.usecases.GetContactUseCase
import com.y9vad9.contacts.viewmodels.EditContactViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun EditContactScreen(
    identifier: Identifier,
    getContactUseCase: GetContactUseCase = GetContactUseCase.current,
    editContactUseCase: EditContactUseCase = EditContactUseCase.current,
    viewModel: EditContactViewModel = viewModel {
        EditContactViewModel(
            identifier, getContactUseCase, editContactUseCase
        )
    },
    onBackPressed: () -> Unit
) {
    val (isSavingAvailable, setSavingAvailable) = remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()

    val (name, setName) = remember { mutableStateOf<Name?>(null) }
    val (surname, setSurname) = remember { mutableStateOf<Surname?>(null) }
    val (email, setEmail) = remember { mutableStateOf<Email?>(null) }


    LaunchedEffect(true) {
        viewModel.init()
        viewModel.contact.filterNotNull().collectLatest { contact ->
            setName(contact.name)
            setSurname(contact.surname)
            setEmail(contact.email)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(8.dp),
                title = { Text("Editor") },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.save(
                                name!!, surname!!, email!!, onFinish = onBackPressed
                            )
                        },
                        enabled = isSavingAvailable
                    ) {
                        Icon(Icons.Rounded.Done, "Apply changes")
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed
                    ) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Go back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name?.string ?: "",
                onValueChange = {
                    setName(Name(it))
                    setSavingAvailable(true)
                },
                label = { Text("Name") }
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = surname?.string ?: "",
                onValueChange = {
                    setSurname(Surname(it))
                    setSavingAvailable(true)
                },
                label = {
                    Text("Surname")
                }
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email?.string ?: "",
                onValueChange = {
                    setEmail(Email(it))
                    setSavingAvailable(true)
                },
                label = { Text("Email") }
            )
        }
    }
}