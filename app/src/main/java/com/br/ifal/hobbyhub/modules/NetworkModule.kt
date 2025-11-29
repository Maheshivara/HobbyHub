package com.br.ifal.hobbyhub.modules

import com.br.ifal.hobbyhub.network.DeezerApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private fun createRetrofit(baseUrl: String, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideDeezerApi(@Named("deezerRetrofit") retrofit: Retrofit): DeezerApi =
        retrofit.create(DeezerApi::class.java)

    @Provides
    @Singleton
    @Named("deezerRetrofit")
    fun provideDeezerRetrofit(client: OkHttpClient): Retrofit =
        createRetrofit("https://api.deezer.com/", client)
}
