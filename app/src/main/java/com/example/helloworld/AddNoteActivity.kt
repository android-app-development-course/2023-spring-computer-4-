package com.example.helloworld

import android.annotation.SuppressLint
import android.content.Intent
import android.gesture.*
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddNoteActivity : AppCompatActivity(), GestureOverlayView.OnGesturePerformedListener,
    GestureOverlayView.OnGesturingListener {
    private lateinit var titleEditText: EditText
    private lateinit var dataEditText: EditText
    private lateinit var delmemo: Memo
    var mLibrary: GestureLibrary? = null
    var mDrawGestureView: GestureOverlayView? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        titleEditText = findViewById(R.id.title_edit_text)
        dataEditText = findViewById(R.id.data_edit_text)

        val title = intent.getStringExtra("title")
        val data = intent.getStringExtra("data")
        val time = intent.getStringExtra("time")
        titleEditText.setText(title)
        dataEditText.setText(data)
        if (time != null){
            delmemo = Memo(titleEditText.text.toString(), dataEditText.text.toString(), time.toLong())
        }

        mDrawGestureView = findViewById<GestureOverlayView>(R.id.gestures1)
        mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures)
        if (!mLibrary?.load()!!) {
            finish()
        }
        //设置手势可多笔画绘制，默认情况为单笔画绘制
        mDrawGestureView?.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE)
        //设置手势的颜色(蓝色)
        mDrawGestureView?.setGestureColor(resources.getColor(R.color.purple_200))
        //设置还没未能形成手势绘制是的颜色(红色)
        mDrawGestureView?.setUncertainGestureColor(Color.RED)
        //设置手势的粗细
        mDrawGestureView?.setGestureStrokeWidth(10f)
        /*手势绘制完成后淡出屏幕的时间间隔，即绘制完手指离开屏幕后相隔多长时间手势从屏幕上消失；
         * 可以理解为手势绘制完成手指离开屏幕后到调用onGesturePerformed的时间间隔
         * 默认值为420毫秒，这里设置为2秒
         */
        mDrawGestureView?.setFadeOffset(750)
        //绑定监听器
        mDrawGestureView?.addOnGesturePerformedListener(this)
        mDrawGestureView?.addOnGesturingListener(this)


        findViewById<Button>(R.id.save_button).setOnClickListener{
            val memo : Memo
            if (time != null){
                MemoManager().delMemo(delmemo)
                memo = Memo(titleEditText.text.toString(), dataEditText.text.toString(), time.toLong())
            }
            else{
                memo = Memo(titleEditText.text.toString(), dataEditText.text.toString())
            }
            MemoManager().saveMemo(memo)

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivityForResult(intent, 27)
            finish()
        }
//        findViewById<Button>(R.id.wri_button).setOnClickListener{
//            // TODO: 手写输入
//            finish()
//        }
        findViewById<Button>(R.id.can_button).setOnClickListener{
            finish()
        }
        findViewById<Button>(R.id.del_button).setOnClickListener{
            if (time != null){
                MemoManager().delMemo(delmemo)

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivityForResult(intent, 27)
            }
            finish()
        }
    }
    override fun onGesturingStarted(gestureOverlayView: GestureOverlayView) {
        println("关于手势开始---")
    }

    override fun onGesturingEnded(gestureOverlayView: GestureOverlayView) {
        println("关于手势结束---")
    }

    override fun onGesturePerformed(gestureOverlayView: GestureOverlayView, gesture: Gesture) {
        println("关于手势执行---$gesture")
        val predictions: ArrayList<*> = mLibrary!!.recognize(gesture)
        if (predictions.size > 0) {
            val prediction = predictions[0] as Prediction
            if (prediction.score > 1.0) {
//                Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show()
                dataEditText.append(prediction.name)
            }
        }
    }
}