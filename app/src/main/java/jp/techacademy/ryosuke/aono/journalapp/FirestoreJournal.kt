package jp.techacademy.ryosuke.aono.journalapp

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.util.*

class FirestoreJournal {
    var id = UUID.randomUUID().toString()
    var title:String = ""
    var content:String = ""
    var feeling:String = ""
    var uid:String = ""
    var name:String = ""
    var public: Boolean = false
    @RequiresApi(Build.VERSION_CODES.O)
    var date:Date = Date();
}