package com.hjhj.practice_youtube

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlin.math.log

class CustomMotionLayout(context: Context, attributeSet:AttributeSet?=null): MotionLayout(context,attributeSet) {
    private var motionTouchStarted = false
    private val mainContainerView by lazy{
        findViewById<View>(R.id.mainContainerLayout)
    }
    private val hitRect = Rect()

    init{
        setTransitionListener(object:TransitionListener{
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                motionTouchStarted = false
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
//        return super.onTouchEvent(event)

        //여기는 true로 바뀐 motionTouchStarted를 다시 false로 바꿔주기 위해 있는거임. 그래야 mainContainerView영역 이외의 영역을 누를땐 터치이벤트 발동안함
//        when(event.actionMasked){
//            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->{
//                motionTouchStarted = false
//                return super.onTouchEvent(event)
//            }
//        }

//        if(!motionTouchStarted){
//            mainContainerView.getHitRect(hitRect)
//            motionTouchStarted = hitRect.contains(event.x.toInt(), event.y.toInt())
//        }
//        -------------------------------------------------------
        //근데 걍 다음 두줄이면 장땡인데??ㅋㅋ
        mainContainerView.getHitRect(hitRect)
        motionTouchStarted = hitRect.contains(event.x.toInt(), event.y.toInt())
//        Log.d("touchTest", super.onTouchEvent(event).toString()+ "//"+motionTouchStarted.toString()
//                +"//터치위치:"+event.x.toString()+","+event.y.toString()
//                +"//rect위치:"+hitRect.toString())

        return super.onTouchEvent(event)&&motionTouchStarted
    }

    private val gestureListener by lazy{
        object:GestureDetector.SimpleOnGestureListener(){
            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                mainContainerView.getHitRect(hitRect)
                Log.d("test","gestureListener")
                return hitRect.contains(e1.x.toInt(),e1.y.toInt())
            }
        }
    }

    private val gestureDetector by lazy{
        Log.d("test","gestureDetector")
        GestureDetector(context,gestureListener)
    }

    //아래코드 없어도 잘 되는데?? 왶 ㅣㄹ요하지
    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        Log.d("test","onInterceptTouchEvent")
        return gestureDetector.onTouchEvent(event)
    }
}