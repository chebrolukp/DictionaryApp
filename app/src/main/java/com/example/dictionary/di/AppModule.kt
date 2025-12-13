package com.example.dictionary.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.dictionary.core.util.NetworkHelper
import com.example.dictionary.data.local.Converters
import com.example.dictionary.data.local.WordInfoDatabase
import com.example.dictionary.data.remote.DictionaryApi
import com.example.dictionary.data.repository.WordInfoRepositoryImpl
import com.example.dictionary.domain.repository.WordInfoRepository
import com.example.dictionary.domain.usecase.GetWordInfoUsecase
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okio.IOException
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWordInfoRepository(
        db: WordInfoDatabase,
        api: DictionaryApi,
        networkHelper: NetworkHelper
    ): WordInfoRepository {
        return WordInfoRepositoryImpl(api, db.dao, networkHelper)
    }

    @Provides
    @Singleton
    fun provideNetworkHelper(
        @ApplicationContext context: Context
    ): NetworkHelper {
        return NetworkHelper(context)
    }

    @Provides
    @Singleton
    fun provideGetWordInfoDatabase(app: Application, parser: Json): WordInfoDatabase {
        return Room.databaseBuilder(
            app, WordInfoDatabase::class.java, "word_db"
        )
            .addTypeConverter(Converters(parser))
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun providesDictionaryApi(json: Json): DictionaryApi {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                try {
                    chain.proceed(chain.request())
                } catch (e: Exception) {
                    throw IOException("Network error: ${e.message}")
                }
            }.build()
        return Retrofit.Builder()
            .baseUrl(DictionaryApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(DictionaryApi::class.java)
    }

    @Provides
    @Singleton
    fun providesJson(): Json {
        return Json {
            ignoreUnknownKeys = true
        }
    }
}