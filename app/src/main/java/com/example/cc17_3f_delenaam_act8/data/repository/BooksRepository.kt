package com.example.cc17_3f_delenaam_act8.data.repository

import com.example.cc17_3f_delenaam_act8.data.api.RetrofitInstance
import com.example.cc17_3f_delenaam_act8.data.model.Book
import com.example.cc17_3f_delenaam_act8.utility.ensureHttpsImageUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class BooksRepository {
    private val service = RetrofitInstance.googleBooksService

    suspend fun searchBooks(query: String): List<Book> = withContext(Dispatchers.IO) {
        val response = service.getBooks(query, maxResults = 12)
        val bookIds = response.items?.map { it.id } ?: emptyList()

        bookIds.map { id ->
            async {
                try {
                    service.getBookDetails(id).ensureHttpsImageUrl()
                } catch (e: Exception) {
                    null
                }
            }
        }.mapNotNull { it.await() }
    }
}