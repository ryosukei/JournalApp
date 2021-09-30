package jp.techacademy.ryosuke.aono.journalapp

import java.io.Serializable
import java.time.LocalDateTime
import java.util.*

class Journal(val title: String, val feeling:String, val content:String, val public: Boolean, val uid:String,val journalId:String, val date: Date): Serializable {

}