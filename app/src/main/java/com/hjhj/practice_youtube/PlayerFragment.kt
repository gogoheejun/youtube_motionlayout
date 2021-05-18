package com.hjhj.practice_youtube

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import com.hjhj.practice_youtube.databinding.FragmentPlayerBinding
import kotlin.math.abs

class PlayerFragment:Fragment(R.layout.fragment_player) {

    private var binding:FragmentPlayerBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentPlayerBinding.bind(view)
        binding = fragmentPlayerBinding

        //playerfragment를 스와이프하면 바텀네비게이션도 생겼다없어졌다하잖아. 바텀네비게이션에 독자적인 핸들러를 주는대신 얘랑 연결해서 같이 움직이도록 하는거임
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

    //잊지말고 해주기
    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}