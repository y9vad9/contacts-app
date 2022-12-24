package com.y9vad9.contacts.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.CircleCropTransformation
import com.y9vad9.contacts.domain.value.Email
import com.y9vad9.contacts.domain.value.Identifier
import com.y9vad9.contacts.ui.components.OnBackPressed
import com.y9vad9.contacts.ui.components.shimmerBackground
import com.y9vad9.contacts.ui.dependencies.*
import com.y9vad9.contacts.usecases.*
import com.y9vad9.contacts.viewmodels.ContactDetailsViewModel

@Composable
fun ContactScreen(
    identifier: Identifier,
    getContactUseCase: GetContactUseCase = GetContactUseCase.current,
    restoreOriginalUseCase: RestoreOriginalUseCase = RestoreOriginalUseCase.current,
    removeContactUseCase: RemoveContactUseCase = RemoveContactUseCase.current,
    checkRollbackableUseCase: CheckRollbackableUseCase = CheckRollbackableUseCase.current,
    removeHistoryUseCase: RemoveHistoryUseCase = RemoveHistoryUseCase.current,
    viewModel: ContactDetailsViewModel = viewModel {
        ContactDetailsViewModel(
            identifier,
            getContactUseCase,
            removeContactUseCase,
            restoreOriginalUseCase,
            checkRollbackableUseCase,
            removeHistoryUseCase
        )
    },
    onEdit: () -> Unit,
    onBackPressed: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val isPendingState by viewModel.isPendingChanges.collectAsState()
    val contact by viewModel.contact.collectAsState()
    val onBackPressed = remember {
        {
            viewModel.removeRollback(onBackPressed)
        }
    }
    OnBackPressed(onBack = onBackPressed)

    LaunchedEffect(false) {
        viewModel.init()
    }

    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(appBarState)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                modifier = Modifier.shadow(8.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (contact?.profilePictureLink?.string.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .heightIn(min = 40.dp, max = 72.dp)
                                    .widthIn(40.dp, 72.dp)
                                    .shimmerBackground(CircleShape)
                            )
                        } else {
                            val model = ImageRequest.Builder(LocalContext.current)
                                .data(contact!!.profilePictureLink.string)
                                .size(Size.ORIGINAL)
                                .crossfade(true)
                                .transformations(CircleCropTransformation())
                                .build()

                            val painter = rememberAsyncImagePainter(
                                model = model
                            )

                            Image(
                                modifier = Modifier.defaultMinSize(36.dp, 36.dp)
                                    .heightIn(min = 40.dp, max = 72.dp)
                                    .widthIn(40.dp, 72.dp)
                                    .align(Alignment.CenterVertically),
                                painter = painter,
                                contentDescription = null,
                                contentScale = ContentScale.FillWidth
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "${contact?.name?.string.orEmpty()} ${contact?.surname?.string.orEmpty()}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
                actions = {
                    IconButton(
                        onClick = { viewModel.delete(onBackPressed) }
                    ) {
                        Icon(Icons.Outlined.DeleteOutline, "Delete contact")
                    }

                    IconButton(
                        onClick = onEdit
                    ) {
                        Icon(Icons.Rounded.Edit, "Edit contact")
                    }
                }
            )
        }
    ) { padding ->
        BoxWithConstraints(Modifier.padding(padding)) {
            ContactDetails(contact?.email ?: Email(""))
            if (isPendingState) {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    action = {
                        TextButton(
                            onClick = { viewModel.restore() }
                        ) {
                            Icon(Icons.Rounded.Undo, "Undo icon")
                            Spacer(Modifier.width(8.dp))
                            Text("Cancel")
                        }
                    }
                ) {
                    Text("You've successfully changed the contact.")
                }
            }
        }
    }
}

@Composable
private fun ContactDetails(email: Email) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp)
    ) {
        item {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Details",
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            PropertyItem(
                "Email",
                Icons.Rounded.Email,
                email.string
            )
        }
    }
}

@Composable
private fun PropertyItem(title: String, icon: ImageVector, value: String) {
    ListItem(
        headlineText = {
            Row {
                Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                Text(title)
            }
        },
        supportingText = {
            Text(value)
        }
    )
}