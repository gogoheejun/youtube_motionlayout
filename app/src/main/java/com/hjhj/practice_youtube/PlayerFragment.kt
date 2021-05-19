package com.hjhj.practice_youtube

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.hjhj.practice_youtube.adapter.VideoAdapter
import com.hjhj.practice_youtube.databinding.FragmentPlayerBinding
import com.hjhj.practice_youtube.dto.VideoDto
import com.hjhj.practice_youtube.service.VideoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs

class PlayerFragment:Fragment(R.layout.fragment_player) {

    private var binding:FragmentPlayerBinding? = null
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var player:SimpleExoPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        //playerfragment를 스와이프하면 바텀네비게이션도 생겼다없어졌다하잖아. 바텀네비게이션에 독자적인 핸들러를 주는대신 얘랑 연결해서 같이 움직이도록 하는거임
        initMotionLayoutEvent(fragmentPlayerBinding)//null예외안하려고 걍 논널 확정인 fragmentPlayerBinding썼음
        initRecyclerView(fragmentPlayerBinding)
        initPlayer(fragmentPlayerBinding)
        initContrloButton(fragmentPlayerBinding)

        getVideoList()
    }

    private fun initMotionLayoutEvent(fragmentPlayerBinding:FragmentPlayerBinding){
        fragmentPlayerBinding.playerMotionLayout.setTransitionListener(object:MotionLayout.TransitionListener{
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                binding?.let{
                    (activity as MainActivity).also{
                            mainActivity ->  mainActivity.findViewById<MotionLayout>(R.id.mainMoitonLayout).progress= abs(progress)
                    }
                }
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

        })
    }

    private fun initRecyclerView(fragmentPlayerBinding:FragmentPlayerBinding){

        videoAdapter = VideoAdapter(callback = {url,title ->
            play(url,title)
        })

        fragmentPlayerBinding.fragmentRecyclerView.apply{
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initPlayer(fragmentPlayerBinding: FragmentPlayerBinding){
        context?.let{
            player = SimpleExoPlayer.Builder(it).build()
        }
        fragmentPlayerBinding.playerView.player = player
        binding?.let{
            player?.addListener(object : Player.EventListener{
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)

                    if(isPlaying){
                        it.bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_pause_24)
                    }else{
                        it.bottomPlayerControlButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                    }
                }
            })
        }
    }

    private fun initContrloButton(fragmentPlayerBinding: FragmentPlayerBinding){
        fragmentPlayerBinding.bottomPlayerControlButton.setOnClickListener {
            //엘비스기호: 널이면 뒤의걸 반환해라. 여기선 리턴이니까 함수끝내라
            val player = this.player?:return@setOnClickListener
            if(player.isPlaying){
                player.pause()
            }else{
                player.play()
            }
        }
    }

    private fun getVideoList(){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(VideoService::class.java).also{
            it.listVideos()
                .enqueue(object: Callback<VideoDto> {
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

    fun play(url:String, title:String){

        context?.let{
            //url -> data소스로 변환->미디어소스로 변환->player에 넣기
            val dataSourceFactory = DefaultDataSourceFactory(it)
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(url)))

//            player?.setMediaSource(mediaSource)
//            player?.prepare()
//            player?.play()
            player?.apply{
                setMediaSource(mediaSource)
                prepare()
                play()
            }
        }

        binding?.let{
            it.playerMotionLayout.transitionToEnd()
            it.bottomTitleTextView.text = title
        }
    }

    override fun onStop() {
        super.onStop()

        //exoplayer가 자동으로 백그라운드재생시킴..이거 의도한거 아니니까 조정
        player?.pause()
    }

    //잊지말고 해주기
    override fun onDestroy() {
        super.onDestroy()

        binding = null

        player?.release()

    }
}