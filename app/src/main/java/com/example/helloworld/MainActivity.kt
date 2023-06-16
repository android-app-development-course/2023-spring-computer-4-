package com.example.helloworld

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Memo(
    val title: String,
    val data: String,
    val timestamp: Long = System.currentTimeMillis()
)

class MemoAdapter(var memos: List<Memo>, private val onItemClick: (Memo) -> Unit) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.memo_item, parent, false)
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memos[position]
        holder.bind(memo)
    }

    override fun getItemCount() = memos.size

    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.memo_title)
        private val dataTextView: TextView = itemView.findViewById(R.id.memo_data)

        @SuppressLint("SetTextI18n")
        fun bind(memo: Memo) {
            titleTextView.text = memo.title
            if (memo.data.length > 20){
                dataTextView.text = memo.data.substring(0, 20) + "..."
            }
            else{
                dataTextView.text = memo.data
            }

            itemView.setOnClickListener { onItemClick(memo) }
        }
    }
}

class MainActivity : AppCompatActivity(){
    private lateinit var memoList: RecyclerView

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化RecyclerView控件
        val adapter = MemoAdapter(MemoManager().loadMemos(), this::openMemo)
        memoList = findViewById(R.id.memo_list)
        memoList.layoutManager = LinearLayoutManager(this)
        memoList.adapter = adapter

        // 动效
        val animation = AnimationUtils.loadAnimation(this, R.anim.fd)
        val layoutAnimationController = LayoutAnimationController(animation)
        // 设置顺序
        layoutAnimationController.order = LayoutAnimationController.ORDER_NORMAL
        memoList.layoutAnimation = layoutAnimationController

        adapter.notifyDataSetChanged()

        findViewById<Button>(R.id.add_button).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                // 启动AddNoteActivity
                val intent = Intent(this, AddNoteActivity::class.java)
                startActivity(intent)
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
            }
        }
    }

    private fun openMemo(memo: Memo) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("title", memo.title)
        intent.putExtra("data", memo.data)
        intent.putExtra("time", memo.timestamp.toString())
        startActivity(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 27 && data != null) {
            // 更新适配器数据
            val adapter = memoList.adapter as MemoAdapter
            adapter.memos = MemoManager().loadMemos()
            adapter.notifyDataSetChanged()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1->{
                if (grantResults.isNotEmpty()&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    val intent = Intent(this, AddNoteActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "You denied the permission!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}