package com.rus_euphoria.notes.data.remote

import com.rus_euphoria.notes.data.remote.api.TodoApiService
import com.rus_euphoria.notes.data.remote.api.models.ElementRequest
import com.rus_euphoria.notes.data.remote.api.models.ElementResponse
import com.rus_euphoria.notes.data.remote.api.models.ListResponse
import com.rus_euphoria.notes.data.remote.api.models.PatchListRequest
import com.rus_euphoria.notes.data.remote.api.models.toDto
import com.rus_euphoria.notes.data.remote.api.models.toNote
import com.rus_euphoria.notes.model.Note
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import retrofit2.HttpException
import java.io.IOException

class SyncManager(
    private val api: TodoApiService,
    private val deviceIdProvider: () -> String,
    private val initialDelayMs: Long = 1_000L,
    private val maxDelayMs: Long = 30_000L,
    private val maxRetries: Int = 10,
) {
    private val log = LoggerFactory.getLogger(SyncManager::class.java)
    private val mutex = Mutex()

    @Volatile
    private var revision: Int = 0

    suspend fun fetchAll(): List<Note> = mutex.withLock {
        val response = withRetry("fetchAll") { api.getList() }
        revision = response.revision
        log.info("fetchAll: revision=$revision, items=${response.list.size}")
        response.list.map { it.toNote() }
    }

    suspend fun pushAll(notes: List<Note>): List<Note> = mutex.withLock {
        val deviceId = deviceIdProvider()
        val body = PatchListRequest(list = notes.map { it.toDto(deviceId) })
        val response = executeListMutation("pushAll") { rev ->
            api.patchList(revision = rev, body = body)
        }
        response.list.map { it.toNote() }
    }

    suspend fun addRemote(note: Note): Note = mutex.withLock {
        val deviceId = deviceIdProvider()
        val body = ElementRequest(element = note.toDto(deviceId))
        val response = executeElementMutation("add uid=${note.uid}") { rev ->
            api.addItem(revision = rev, body = body)
        }
        response.element.toNote()
    }

    suspend fun updateRemote(note: Note): Note = mutex.withLock {
        val deviceId = deviceIdProvider()
        val body = ElementRequest(element = note.toDto(deviceId))
        val response = try {
            executeElementMutation("update uid=${note.uid}") { rev ->
                api.updateItem(revision = rev, id = note.uid, body = body)
            }
        } catch (e: HttpException) {
            if (e.code() == 404) {
                log.info("updateRemote: uid=${note.uid} not found on server, falling back to add")
                executeElementMutation("add (after 404) uid=${note.uid}") { rev ->
                    api.addItem(revision = rev, body = body)
                }
            } else throw e
        }
        response.element.toNote()
    }

    suspend fun deleteRemote(uid: String) {
        mutex.withLock {
            try {
                executeElementMutation("delete uid=$uid") { rev ->
                    api.deleteItem(revision = rev, id = uid)
                }
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    log.info("deleteRemote: uid=$uid already gone on server")
                } else throw e
            }
        }
    }

    private suspend fun executeElementMutation(
        tag: String,
        call: suspend (revision: Int) -> ElementResponse,
    ): ElementResponse = withRetry(tag) {
        try {
            val response = call(revision)
            revision = response.revision
            response
        } catch (e: HttpException) {
            if (e.code() == 400) {
                log.warn("$tag: revision mismatch, refreshing revision")
                val refreshed = api.getList()
                revision = refreshed.revision
                val response = call(revision)
                revision = response.revision
                response
            } else throw e
        }
    }

    private suspend fun executeListMutation(
        tag: String,
        call: suspend (revision: Int) -> ListResponse,
    ): ListResponse = withRetry(tag) {
        try {
            val response = call(revision)
            revision = response.revision
            response
        } catch (e: HttpException) {
            if (e.code() == 400) {
                log.warn("$tag: revision mismatch, refreshing revision")
                val refreshed = api.getList()
                revision = refreshed.revision
                val response = call(revision)
                revision = response.revision
                response
            } else throw e
        }
    }

    private suspend fun <T> withRetry(tag: String, block: suspend () -> T): T {
        var attempt = 0
        var delayMs = initialDelayMs
        while (true) {
            try {
                return block()
            } catch (e: HttpException) {
                if (e.code() !in 500..599) {
                    throw e
                }
                if (attempt >= maxRetries) {
                    log.error("$tag: gave up after $attempt retries on http ${e.code()}")
                    throw e
                }
                log.warn("$tag: http ${e.code()}, retry ${attempt + 1}/$maxRetries in ${delayMs}ms")
            } catch (e: IOException) {
                if (attempt >= maxRetries) {
                    log.error("$tag: terminal io after $attempt retries", e)
                    throw e
                }
                log.warn("$tag: io ${e.message}, retry ${attempt + 1}/$maxRetries in ${delayMs}ms")
            }
            delay(delayMs)
            attempt += 1
            delayMs = (delayMs * 2).coerceAtMost(maxDelayMs)
        }
    }
}
