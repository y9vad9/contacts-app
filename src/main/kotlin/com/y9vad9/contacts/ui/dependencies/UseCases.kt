package com.y9vad9.contacts.ui.dependencies

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.y9vad9.contacts.usecases.*

val LocalEditContactUseCase: ProvidableCompositionLocal<EditContactUseCase> =
    compositionLocalOf { error("EditContactUseCase is not defined") }

val LocalGetContactUseCase: ProvidableCompositionLocal<GetContactUseCase> =
    compositionLocalOf { error("EditContactUseCase is not defined") }

val LocalLoadContactUseCase: ProvidableCompositionLocal<LoadContactsUseCase> =
    compositionLocalOf { error("EditContactUseCase is not defined") }

val LocalReloadContactsUseCase: ProvidableCompositionLocal<ReloadContactsUseCase> =
    compositionLocalOf { error("EditContactUseCase is not defined") }

val LocalRemoveContactUseCase: ProvidableCompositionLocal<RemoveContactUseCase> =
    compositionLocalOf { error("EditContactUseCase is not defined") }

val LocalRestoreOriginalUseCase: ProvidableCompositionLocal<RestoreOriginalUseCase> =
    compositionLocalOf { error("RestoreOriginalUseCase is not defined") }

val LocalRemoveHistoryUseCase: ProvidableCompositionLocal<RemoveHistoryUseCase> =
    compositionLocalOf { error("RemoveHistoryUseCase is not defined") }

val LocalCheckRollbackableUseCase: ProvidableCompositionLocal<CheckRollbackableUseCase> =
    compositionLocalOf { error("CheckRollbackableUseCase is not defined") }