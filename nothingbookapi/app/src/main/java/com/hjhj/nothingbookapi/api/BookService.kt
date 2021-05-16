package com.hjhj.nothingbookapi.api

import com.hjhj.nothingbookapi.model.BestSellerDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BookService {
    @GET("/api/search.api?output=json")
    fun getBestSellerBooks(
        @Query("key") apiKey:String
    ):Call<BestSellerDTO>
}