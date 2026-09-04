package com.novachat.app.domain.usecase

import com.novachat.app.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * Use case that validates and dispatches a plain-text message to a chat room.
 *
 * Encapsulates the business rule: an empty or blank message must never
 * be sent. This validation lives here rather than in the ViewModel so that
 * any future alternative entry point (e.g., a widget or notification reply)
 * automatically benefits from the same guard.
 *
 * @property repository The injected [ChatRepository] implementation.
 */
class SendTextMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    /**
     * Sends [content] to [chatId] after stripping leading/trailing whitespace.
     *
     * @param chatId  Firestore chat document ID.
     * @param content Raw text typed by the user.
     * @return [Result.success] with the generated message ID, or
     *         [Result.failure] with a descriptive [IllegalArgumentException]
     *         if the content is blank, or a backend [Exception] on network error.
     */
    suspend operator fun invoke(chatId: String, content: String): Result<String> {
        val trimmed = content.trim()
        if (trimmed.isBlank()) {
            return Result.failure(IllegalArgumentException("Message content cannot be empty"))
        }
        return repository.sendTextMessage(chatId = chatId, content = trimmed)
    }
}
