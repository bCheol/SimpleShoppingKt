package com.example.simpleshoppingkt

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RetrofitAPI {

    @GET("/v1/search/shop.json")
    fun getData(
        @Query("query") query : String,
        @Query("display") display : Int,
        @Query("sort") sort : String,
        @Header("X-Naver-Client-Id") id : String,
        @Header("X-Naver-Client-Secret") pwd : String
    ) : Call<GetData>
}