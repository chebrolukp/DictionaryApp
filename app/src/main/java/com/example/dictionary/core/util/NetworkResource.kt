package com.example.dictionary.core.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private const val TAG = "SafeNetworkCall"

fun <T, R> safeNetworkCall(
     cacheLoader: suspend () -> T,
     networkCall: suspend () -> R,
     cacheUpdater: suspend (R) -> Unit = {},
     emptyNetworkCheck: (R) -> Boolean = { false },
    fallbackValue: T
): Flow<Resource<T>> = flow {

    emit(Resource.Loading())

    val cached: T = runCatching { cacheLoader() }.getOrElse { fallbackValue }

    emit(Resource.Loading(data = cached))

    try {
        val remote = networkCall()

        if (emptyNetworkCheck(remote)) {
            Timber.tag(TAG).d("Remote returned empty result")
            emit(Resource.Error(data = fallbackValue, exception = DictionaryException.WordNotFoundException))
            return@flow
        }

        runCatching { cacheUpdater(remote) }

        val fresh: T = runCatching { cacheLoader() }.getOrElse { cached }

        emit(Resource.Success(fresh))

    } catch (e: UnknownHostException) {
        Timber.tag(TAG).d("No internet: ${e.message}")
        emit(Resource.Error(data = cached, exception = DictionaryException.NoInternetException))
    } catch (e: SocketTimeoutException) {
        Timber.tag(TAG).d("Timeout: ${e.message}")
        emit(Resource.Error(data = cached, exception = DictionaryException.NetworkException))
    } catch (e: HttpException) {//400, 401, 403, 404, 500
        val exception = if(e.code() == 404) DictionaryException.WordNotFoundException else DictionaryException.ServerException
        val fallback = if(e.code() == 404) fallbackValue else cached
        Timber.tag(TAG).d("HTTP error: ${e.message}")
        emit(Resource.Error(data = fallback, exception = exception))
    } catch (e: IOException) {
        Timber.tag(TAG).d("IO Error: ${e.message}")
        emit(Resource.Error(data = cached, exception = DictionaryException.NetworkException))
    } catch (e: Exception) {
        Timber.tag(TAG).d("Unknown error: ${e.message}")
        emit(Resource.Error(data = cached, exception = DictionaryException.UnknownException))
    }
}