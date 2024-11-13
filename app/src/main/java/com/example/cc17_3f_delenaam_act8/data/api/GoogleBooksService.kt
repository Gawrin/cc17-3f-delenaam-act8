package com.example.cc17_3f_delenaam_act8.data.api

import com.example.cc17_3f_delenaam_act8.data.model.Book
import com.example.cc17_3f_delenaam_act8.data.model.BooksResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleBooksService {
    @GET("volumes")
    suspend fun getBooks(@Query("q") query: String): BooksResponse

    @GET("volumes/{volumeId}")
    suspend fun getBookDetails(@Path("volumeId") volumeId: String): Book
}