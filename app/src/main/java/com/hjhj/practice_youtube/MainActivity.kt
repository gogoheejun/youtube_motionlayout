package com.hjhj.practice_youtube

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hjhj.practice_youtube.adapter.VideoAdapter
import com.hjhj.practice_youtube.dto.VideoDto
import com.hjhj.practice_youtube.service.VideoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var videoAdapter:VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer,PlayerFragment())
            .commit()

        videoAdapter = VideoAdapter(callback = {url,title ->
            //프래그먼트에서 선언한 play함수를 가져오기 위해서 다음처럼씀. fragment 다가져와! 걔들중에 PlayerFragment있으면 let!
            supportFragmentManager.fragments.find{it is PlayerFragment}?.let{
                (it as PlayerFragment).play(url,title)
            }
        })

        findViewById<RecyclerView>(R.id.mainRecyclerView).apply{
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }

        getVideoList()
    }

    private fun getVideoList(){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also{
            it.listVideos()
                .enqueue(object:Callback<VideoDto>{
                    override fun onResponse(call: Call<VideoDto>, response: Response<VideoDto>) {
                        if(response.isSuccessful.not()){
                            Log.d("MainActivity","response failed")
                            return
                        }
                        response.body()?.let{
                            videoDto->
                            Log.d("mainActivity", videoDto.toString())
                            videoAdapter.submitList(videoDto.videos)
                        }
                    }
                    override fun onFailure(call: Call<VideoDto>, t: Throwable) {
                    }

                })
        }
    }
}