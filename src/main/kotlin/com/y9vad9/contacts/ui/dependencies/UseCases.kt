package com.y9vad9.contacts.ui.dependencies

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import com.y9vad9.contacts.usecases.*

val EditContactUseCase: ProvidableCompositionLocal<EditContactUseCase> =
    compositionLocalOf { error("EditContactUseCase is not defined") }

val GetContactUseCase: ProvidableCompositionLocal<GetContactUseCase> =
    compositionLocalOf { error("EditContactUseCase is not defined") }

val LoadContactUseCase: ProvidableCompositionLocal<LoadContactsUseCase> =
    compositionLocalOf { error("EditContactUseCase is not defined") }

val ReloadContactsUseCase: ProvidableCompositionLocal<ReloadContactsUseCase> =
    compositionLocalOf { error("EditContactUseCase is not defined") }

val RemoveContactUseCase: ProvidableCompositionLocal<RemoveContactUseCase> =
    compositionLocalOf { error("EditContactUseCase is not defined") }

val RestoreOriginalUseCase: ProvidableCompositionLocal<RestoreOriginalUseCase> =
    compositionLocalOf { error("RestoreOriginalUseCase is not defined") }

val RemoveHistoryUseCase: ProvidableCompositionLocal<RemoveHistoryUseCase> =
    compositionLocalOf { error("RemoveHistoryUseCase is not defined") }

val CheckRollbackableUseCase: ProvidableCompositionLocal<CheckRollbackableUseCase> =
    compositionLocalOf { error("CheckRollbackableUseCase is not defined") }