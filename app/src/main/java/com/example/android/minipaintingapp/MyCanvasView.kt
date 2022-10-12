package com.example.android.minipaintingapp

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
 private const val STROKE_WIDTH = 12f // has to be float and it is pixels
class MyCanvasView(context:Context):View(context) {

    //these member variables for caching,what has been drawn before
    private lateinit var  extraCanvas:Canvas
    private lateinit var  extraBitmap:Bitmap

    private val backGround=ResourcesCompat.getColor(
      resources,R.color.colorBackground,null
    )
    private var path:Path=Path()
    private var motionTouchEventX=0f
    private var motionTouchEventY=0f
    //these two variables two to cache the latest x,y values
    private var currentX=0f
    private var currentY=0f
    //for holding the color to draw with
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)
    //interpolate(اقحام) a path between points for much better performance.
    //if the finger has moved less than a touch tolerance distance,don't draw
    //scaledTouchSlop return the distance in pixel
    private val touchTolerance=ViewConfiguration.get(context).scaledTouchSlop
    private lateinit var frame: Rect
    private val paint:Paint=Paint().apply {
        color=drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering(اهتياج او ارتجاف) affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND //how to join each segment to another , default: MITER
        strokeCap = Paint.Cap.ROUND // how is he beginning of the path and the ending look like ,default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }
    /*
    this method called whenever the size changed,and since the size in the beginning is 0,0
    and after the view created the size become bigger this method called,so we will
    create and setup our canvas view here.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        /*
          *  a new bitmap and canvas are created every time the function executes.
          *  You need a new bitmap, because the size has changed.
          *  However, this is a memory leak, leaving the old bitmaps around.
          *  To fix this,recycle extraBitmap before creating the next one.
          */
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
            /*
            create an instance of Bitmap with the new width and height,
            which are the screen size, and assign it to extraBitmap
            *The third argument is the bitmap color configuration.
            *ARGB_8888 stores each color in 4 bytes and is recommended.
             * */
        extraBitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        extraCanvas= Canvas(extraBitmap)
        extraCanvas.drawColor(backGround)
        // Calculate a rectangular frame around the picture.
        val inset = 40//starting point,width is from 40,40 to the end of our view
        frame = Rect(inset, inset, width - inset, height - inset)

     }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    /*
        Note: The 2D coordinate system used for drawing on a Canvas is in pixels,
         and the origin (0,0) is at the top left corner of the Canvas.
        */
        canvas?.drawBitmap(extraBitmap,0f,0f,paint)
        canvas?.drawRect(frame,paint)
    }
    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX,motionTouchEventY)
        //update the variables for caching
        currentX=motionTouchEventX
        currentY=motionTouchEventY
    }
    private fun touchMove() {
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            //using quad to instead of line create smoothing drawn line without any corners.
            path.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2,
                (motionTouchEventY +currentY) / 2)
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // Draw the path in the extra bitmap to cache it.
            extraCanvas.drawPath(path, paint)
        }
        //to recall draw()
        invalidate()
    }
    private fun touchUp() {
        // Reset the path so it doesn't get drawn again.
        path.reset()
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when(event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }





}