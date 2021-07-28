package com.example.android.drawapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.MotionEvent
import android.view.View
import java.nio.file.Path

class DrawingView(context: Context,attr:AttributeSet):View(context,attr) {
    private var mDrawPath:CustomPath?=null
private var mCanvasBitmap:Bitmap?=null
    private var mPaint: Paint?=null
    private var mCanvasPaint:Paint?=null
    private var mCanva:Canvas?=null
    private  var mBrushSize:Float=0.toFloat()
    private  var color:Int=Color.BLACK
    private val mPaths=ArrayList<CustomPath>()
    private val undoPath=ArrayList<CustomPath>()

init{
    setUpDrawing()
}

    private fun setUpDrawing() {
        mPaint=Paint()
        mDrawPath=CustomPath(color,mBrushSize)
        mPaint!!.color=color
        mPaint!!.style=Paint.Style.STROKE
        mPaint!!.strokeJoin=Paint.Join.ROUND
        mPaint!!.strokeCap=Paint.Cap.ROUND
        mCanvasPaint= Paint(Paint.DITHER_FLAG)
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            mCanvasBitmap= Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
            mCanva=Canvas(mCanvasBitmap!!)
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)
        for(path in mPaths){
            mPaint!!.strokeWidth=path.BrushSize
            mPaint!!.color =path.color

            canvas.drawPath(path,mPaint!!)
        }
        if(! mDrawPath!!.isEmpty){
        mPaint!!.strokeWidth=mDrawPath!!.BrushSize
            mPaint!!.color =mDrawPath!!.color

        canvas.drawPath(mDrawPath!!,mPaint!!)
        }

    }
fun setColor(newColor:String){
    color=Color.parseColor(newColor)
    mPaint!!.color=color
}
fun undoPath(){
    if(mPaths.size>0){
        undoPath.add(mPaths.removeAt(mPaths.size-1))
        invalidate()
    }


}



    fun setUpBrushSize(newSize:Float){
        mBrushSize=TypedValue.applyDimension(COMPLEX_UNIT_DIP,newSize,resources.displayMetrics

        )
        mPaint!!.strokeWidth=mBrushSize
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var touchX =event?.x
        var touchy=event?.y
        when(event?.action){
           MotionEvent.ACTION_DOWN ->{
               mDrawPath!!.color=color
               mDrawPath!!.BrushSize=mBrushSize
               mDrawPath!!.reset()
               mDrawPath!!.moveTo(touchX!!,touchy!!)
           }
            MotionEvent.ACTION_MOVE->{
                mDrawPath!!.lineTo(touchX!!,touchy!!)
            }
            MotionEvent.ACTION_UP->{
                mDrawPath=CustomPath(color,mBrushSize)
                mPaths.add(mDrawPath!!)
            }
            else ->{
                return false
            }
        }

        invalidate()

        return true
    }

    internal inner  class CustomPath(var color:Int,var BrushSize:Float) :
       android.graphics.Path(){





    }


}