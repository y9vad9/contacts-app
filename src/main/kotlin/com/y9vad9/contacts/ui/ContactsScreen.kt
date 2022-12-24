package com.y9vad9.contacts.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.material.SwipeToDismissKeys
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.y9vad9.contacts.domain.Contact
import com.y9vad9.contacts.domain.value.Identifier
import com.y9vad9.contacts.ui.dependencies.LoadContactUseCase
import com.y9vad9.contacts.ui.dependencies.ReloadContactsUseCase
import com.y9vad9.contacts.ui.dependencies.RemoveContactUseCase
import com.y9vad9.contacts.usecases.LoadContactsUseCase
import com.y9vad9.contacts.usecases.ReloadContactsUseCase
import com.y9vad9.contacts.usecases.RemoveContactUseCase
import com.y9vad9.contacts.viewmodels.ContactsViewModel

@Composable
fun ContactsScreen(
    loadContactsUseCase: LoadContactsUseCase = LoadContactUseCase.current,
    reloadContactsUseCase: ReloadContactsUseCase = ReloadContactsUseCase.current,
    removeContactUseCase: RemoveContactUseCase = RemoveContactUseCase.current,
    viewModel: ContactsViewModel = viewModel {
        ContactsViewModel(loadContactsUseCase, reloadContactsUseCase, removeContactUseCase)
    },
    onNavigateToDetails: (Identifier) -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val isFailed by viewModel.isFailed.collectAsState()
    val contacts by viewModel.contacts.collectAsState()

    LaunchedEffect(viewModel.contacts) {
        viewModel.init()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(8.dp),
                title = { Text("Contacts") },
                actions = {
                    IconButton(
                        onClick = { viewModel.reinit() }
                    ) {
                        Icon(Icons.Rounded.Refresh, "Refresh list")
                    }
                }
            )
        },
    ) { scaffoldPadding ->
        Box(Modifier.fillMaxSize().padding(scaffoldPadding)) {
            when {
                isLoading -> LoadingView()
                isFailed -> FailView()
                else -> {
                    val contacts = contacts.toMutableStateList()
                    ListView(
                        list = contacts,
                        onRemove = { id ->
                            viewModel.remove(id)
                            contacts.removeIf { it.identifier == id }
                        },
                        onItemClicked = { onNavigateToDetails(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ListView(
    list: SnapshotStateList<Contact>,
    onItemClicked: (Identifier) -> Unit,
    onRemove: (Identifier) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(list) { contact ->
            ContactItem(
                contact = contact,
                onItemClicked = { onItemClicked(contact.identifier) },
                onRemove = { onRemove(contact.identifier) }
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun ContactItem(
    contact: Contact,
    onItemClicked: () -> Unit,
    onRemove: () -> Unit
) {
    SwipeToDismissBox(
        onDismissed = { onRemove() },
        backgroundScrimColor = Color.Transparent,
    ) { isBackground ->
        if (!isBackground) {
            ListItem(
                modifier = Modifier.selectable(false) { onItemClicked() },
                leadingContent = {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(contact.profilePictureLink.string)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Picture of ${contact.profilePictureLink}",
                        modifier = Modifier.defaultMinSize(36.dp, 36.dp).clip(CircleShape).align(Alignment.Center)
                    )
                },
                headlineText = { Text("${contact.name.string} ${contact.surname.string}") },
                supportingText = { Text(contact.email.string) }
            )
        }
    }
}

@Composable
private fun BoxScope.LoadingView() {
    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
}

@Composable
private fun BoxScope.FailView() {
    Column(Modifier.align(Alignment.Center)) {
        Icon(Icons.Rounded.Error, "Failure icon")
        Text("Failure", fontWeight = FontWeight.Bold)
        Text("We're unable to get list of your contacts.")
    }
}