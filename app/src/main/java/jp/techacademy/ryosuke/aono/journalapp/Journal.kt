package jp.techacademy.ryosuke.aono.journalapp

import java.io.Serializable
import java.time.LocalDateTime

class Journal(val title: String, val body:String, val feeling:String, val public: Boolean, val uid:String,val journalId:String, val date: LocalDateTime): Serializable {

}