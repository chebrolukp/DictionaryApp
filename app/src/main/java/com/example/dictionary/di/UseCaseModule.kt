package com.example.dictionary.di

import com.example.dictionary.domain.repository.WordInfoRepository
import com.example.dictionary.domain.usecase.GetSearchHistoryUseCase
import com.example.dictionary.domain.usecase.GetWordInfoUsecase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetWordInfoUseCase(repository: WordInfoRepository): GetWordInfoUsecase {
        return GetWordInfoUsecase(repository)
    }

    @Provides
    fun provideGetSearchHistoryUseCase(repository: WordInfoRepository): GetSearchHistoryUseCase {
        return GetSearchHistoryUseCase(repository)
    }
}