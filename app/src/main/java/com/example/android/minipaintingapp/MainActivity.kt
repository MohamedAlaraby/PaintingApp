package com.example.android.minipaintingapp
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myCanvasView=MyCanvasView(this)
        //request the full screen for the layout of myCanvasView
        myCanvasView.systemUiVisibility=SYSTEM_UI_FLAG_FULLSCREEN
        myCanvasView.contentDescription=getString(R.string.canvasContentDescription)
        setContentView(myCanvasView)

    }
}