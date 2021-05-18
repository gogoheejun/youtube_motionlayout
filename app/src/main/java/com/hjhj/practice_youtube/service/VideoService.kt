package com.hjhj.practice_youtube.service

import com.hjhj.practice_youtube.dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {

    @GET("v3/9854532e-b140-4a80-a39d-d2b4ebd7e7b9")
    fun listVideos():Call<VideoDto>
}