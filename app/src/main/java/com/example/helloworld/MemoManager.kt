package com.example.helloworld

import java.io.File

//class MemoManager(private val directory: String = "./memos") {
class MemoManager(private val directory: String = "/storage/emulated/0/Android/data/com.example.helloworld/files/memos") {
    fun saveMemo(memo: Memo) {
        val dir = File(directory)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File("${directory}/memo_${memo.title}_${memo.timestamp}.txt")
        file.createNewFile()
        file.writeText(memo.data)
    }

    fun delMemo(memo: Memo) {
        File("${directory}/memo_${memo.title}_${memo.timestamp}.txt").delete()
    }

    fun loadMemo(title:String, timestamp: Long): Memo? {
        val file = File("${directory}/memo_${title}_${timestamp}.txt")
        return if (file.exists()) {
            Memo(title, file.readText(), timestamp)
        } else {
            null
        }
    }

    fun loadMemos(): List<Memo> {
        val memoFiles = File(directory).listFiles { dir, name -> name.startsWith("memo_") && name.endsWith(".txt") }
        return memoFiles?.mapNotNull { file ->
            val title = file.name.substring(5, file.name.length - 4 - System.currentTimeMillis().toString().length - 1)
            val timestamp = file.name.substring(5 + title.length + 1, file.name.length - 4).toLong()
            loadMemo(title, timestamp)
        } ?: emptyList()
    }
}


