package eu.depau.etchdroid.utils

import android.os.Looper
import android.util.Log
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.io.OutputStream

private const val TAG = "AsyncStreams"

/**
 * Safe runBlocking wrapper that warns (but does not deadlock) when called
 * from the main thread.  This should never happen in production, but if it
 * does the log message will make debugging straightforward.
 */
private fun <T> safeRunBlocking(block: suspend () -> T): T {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        Log.w(TAG, "runBlocking called on main thread — potential deadlock!", Throwable("stack trace"))
    }
    return runBlocking { block() }
}

abstract class AsyncOutputStream : OutputStream() {
    override fun write(b: Int) {
        safeRunBlocking { writeAsync(b) }
    }

    open suspend fun writeAsync(b: Int) {
        write(b)
    }

    override fun write(b: ByteArray) {
        safeRunBlocking { writeAsync(b) }
    }

    open suspend fun writeAsync(b: ByteArray) {
        write(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        safeRunBlocking { writeAsync(b, off, len) }
    }

    open suspend fun writeAsync(b: ByteArray, off: Int, len: Int) {
        write(b, off, len)
    }

    override fun close() {
        safeRunBlocking { closeAsync() }
    }

    open suspend fun closeAsync() {
        close()
    }

    override fun flush() {
        safeRunBlocking { flushAsync() }
    }

    open suspend fun flushAsync() {
        flush()
    }
}

abstract class AsyncInputStream : InputStream() {
    override fun read(): Int {
        return safeRunBlocking { readAsync() }
    }

    open suspend fun readAsync(): Int {
        return read()
    }

    override fun read(b: ByteArray): Int {
        return safeRunBlocking { readAsync(b) }
    }

    open suspend fun readAsync(b: ByteArray): Int {
        return read(b)
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return safeRunBlocking { readAsync(b, off, len) }
    }

    open suspend fun readAsync(b: ByteArray, off: Int, len: Int): Int {
        return read(b, off, len)
    }

    override fun close() {
        safeRunBlocking { closeAsync() }
    }

    open suspend fun closeAsync() {
        close()
    }

    override fun available(): Int {
        return safeRunBlocking { availableAsync() }
    }

    open suspend fun availableAsync(): Int {
        return available()
    }

    override fun mark(readlimit: Int) {
        safeRunBlocking { markAsync(readlimit) }
    }

    open suspend fun markAsync(readlimit: Int) {
        mark(readlimit)
    }

    override fun markSupported(): Boolean {
        return safeRunBlocking { markSupportedAsync() }
    }

    open suspend fun markSupportedAsync(): Boolean {
        return markSupported()
    }

    override fun reset() {
        safeRunBlocking { resetAsync() }
    }

    open suspend fun resetAsync() {
        reset()
    }

    override fun skip(n: Long): Long {
        return safeRunBlocking { skipAsync(n) }
    }

    open suspend fun skipAsync(n: Long): Long {
        return skip(n)
    }
}