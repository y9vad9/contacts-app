package com.y9vad9.contacts.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import kotlinx.collections.immutable.ImmutableList

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
        Box(Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)) {
            when {
                isLoading -> LoadingView()
                isFailed -> FailView()
                else -> {
                    ListView(
                            list = contacts,
                            onRemove = { id ->
                                viewModel.remove(id)
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
        list: ImmutableList<Contact>,
        onItemClicked: (Identifier) -> Unit,
        onRemove: (Identifier) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(list, key = { it.identifier.int }) { contact ->
            ContactItem(
                    contact = contact,
                    onItemClicked = { onItemClicked(contact.identifier) },
                    onRemove = { onRemove(contact.identifier) }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
private fun LazyItemScope.ContactItem(
        contact: Contact,
        onItemClicked: () -> Unit,
        onRemove: () -> Unit
) {
    val dismissState = rememberDismissState(
            initialValue = DismissValue.Default,
            confirmStateChange = {
                if(it == DismissValue.DismissedToStart) {
                    onRemove()
                }
                true
            }
    )

    SwipeToDismiss(
            modifier = Modifier.animateItemPlacement(),
            state = dismissState,
            background = {
                val color = when (dismissState.dismissDirection) {
                    DismissDirection.EndToStart -> Color.Red
                    else -> Color.Transparent
                }

                Box(
                        modifier = Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(8.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.CenterEnd)) {
                        Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.heightIn(8.dp))
                        Text(
                                text = "Move to Bin",
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray
                        )

                    }
                }
            },
            directions = setOf(DismissDirection.EndToStart)
    ) {
        ListItem(
                modifier = Modifier.selectable(false) { onItemClicked() },
                leadingContent = {
                    AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                    .data(contact.profilePictureLink.string)
                                    .crossfade(true)
                                    .build(),
                            contentDescription = "Picture of ${contact.profilePictureLink}",
                            modifier = Modifier
                                    .defaultMinSize(36.dp, 36.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.CenterVertically)
                    )
                },
                headlineText = { Text("${contact.name.string} ${contact.surname.string}") },
                supportingText = { Text(contact.email.string) }
        )
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