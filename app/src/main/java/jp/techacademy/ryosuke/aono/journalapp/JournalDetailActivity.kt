package jp.techacademy.ryosuke.aono.journalapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_journal_detail.*

class JournalDetailActivity : AppCompatActivity() {
    private lateinit var mJournal: Journal
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_detail)

        val extras = intent.extras
        mJournal = extras!!.get("journal") as Journal

        title = "日記詳細"

        titleTextView.text = mJournal.title
        nameTextView.text = mJournal.name
        contentTextView.text = mJournal.content
    }
}