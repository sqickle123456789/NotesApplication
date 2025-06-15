package com.sqickle.spacenotes.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sqickle.spacenotes.data.source.remote.ApiService
import com.sqickle.spacenotes.data.source.remote.OAuthTokenProvider
import com.sqickle.spacenotes.data.source.remote.RemoteNoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val API_BASE_URL = "https://hive.mrdekk.ru/todo/"
    private const val AUTH_TOKEN = "4cd6c1b9-e5a5-4b6f-9556-71523b0c2794"

    @Provides
    @Singleton
    fun provideJsonSerializer(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(): Interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $AUTH_TOKEN")
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .build()
        chain.proceed(request)
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        authInterceptor: Interceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor(authInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        jsonSerializer: Json,
        httpClient: OkHttpClient,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .client(httpClient)
        .addConverterFactory(jsonSerializer.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideNotesApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideOAuthTokenProvider(): OAuthTokenProvider =
        YandexOAuthTokenProvider()

    @Provides
    @Singleton
    fun provideRemoteNoteDataSource(
        apiService: ApiService,
        oauthTokenProvider: OAuthTokenProvider,
    ): RemoteNoteDataSource =
        RemoteNoteDataSource(apiService, oauthTokenProvider)
}

@Singleton
class YandexOAuthTokenProvider : OAuthTokenProvider {
    override fun refreshToken(clientId: String): String {
        return "sqickle_oauth_token"
    }
}