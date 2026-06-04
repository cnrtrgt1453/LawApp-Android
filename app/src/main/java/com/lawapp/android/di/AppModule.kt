package com.lawapp.android.di

import com.lawapp.android.data.ApiService
import com.lawapp.android.data.ApiServiceImpl
import com.lawapp.android.data.ChatRepository
import com.lawapp.android.data.ChatRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindApiService(
        apiServiceImpl: ApiServiceImpl
    ): ApiService

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository
}
