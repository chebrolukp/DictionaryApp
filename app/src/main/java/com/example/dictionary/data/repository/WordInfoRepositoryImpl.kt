package com.example.dictionary.data.repository

import com.example.dictionary.core.util.DictionaryException
import com.example.dictionary.core.util.NetworkHelper
import com.example.dictionary.core.util.Resource
import com.example.dictionary.core.util.safeNetworkCall
import com.example.dictionary.data.local.WordInfoDao
import com.example.dictionary.data.remote.DictionaryApi
import com.example.dictionary.domain.model.WordInfo
import com.example.dictionary.domain.repository.WordInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.net.UnknownHostException

class WordInfoRepositoryImpl(
    private val api: DictionaryApi,
    private val dao: WordInfoDao,
    private val networkHelper: NetworkHelper
) : WordInfoRepository {

    override fun getWordInfo(word: String) = safeNetworkCall(
        cacheLoader = {
            dao.getWordInfo(word).map { it.toWordInfo() }
        },
        networkCall = {
            if (!networkHelper.isNetworkAvailable()) {
                throw UnknownHostException()
            }
            api.getWordInfo(word)
        },
        cacheUpdater = { remote ->
            dao.deleteWordInfos(remote.map { it.word })
            dao.insertWordInfos(remote.map { it.toWordInfoEntity() })
        },
        emptyNetworkCheck = { it.isEmpty() },
        fallbackValue = emptyList()
    )

    override fun getSearchHistory(): Flow<Resource<List<String>>> =
        dao.getHistory()
            .map<List<String>, Resource<List<String>>> { words ->
                Resource.Success(words)
            }
            .onStart {
                emit(Resource.Loading())
            }
            .catch {
                emit(Resource.Error(exception = DictionaryException.UnknownException))
            }

    override suspend fun getLocalWordInfo(word: String): List<WordInfo>{
        return dao.getWordInfo(word).map { it.toWordInfo() }
    }
}
