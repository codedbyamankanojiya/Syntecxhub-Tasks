package com.novachat.app.domain.usecase

import com.novachat.app.domain.model.Message
import com.novachat.app.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case that exposes a real-time [Flow] of messages for a given chat room.
 *
 * Delegates directly to [ChatRepository.observeMessages], which fuses
 * Firestore snapshot listeners with the local Room cache to provide
 * instant first-frame data followed by live remote updates.
 *
 * Wrapping in a use case keeps the ViewModel decoupled from repository
 * internals and makes this interactor independently testable.
 *
 * @property repository The injected [ChatRepository] implementation.
 */
class ObserveMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    /**
     * Invokes the use case for [chatId].
     *
     * @param chatId Firestore chat document ID.
     * @return A [Flow] emitting ordered lists of [Message] objects, updated in real time.
     */
    operator fun invoke(chatId: String): Flow<List<Message>> =
        repository.observeMessages(chatId)
}
