package jp.techacademy.ryosuke.aono.journalapp

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_journal_send.*
import java.text.FieldPosition
import java.util.*

class JournalSendActivity : AppCompatActivity(), View.OnClickListener {
    // 日付を入力する
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal_send)

        // UIの準備
        title = getString(R.string.journal_send_title)
        // spinnerの準備
        val feelingSpinner = findViewById<Spinner>(R.id.feelingSpinner)
        val feelingAdapter = ArrayAdapter.createFromResource(this,R.array.feelingSpinnerItems,android.R.layout.simple_spinner_dropdown_item)
        feelingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        feelingSpinner.adapter = feelingAdapter

        val publicSpinner = findViewById<Spinner>(R.id.publicSpinner)
        val publicAdapter = ArrayAdapter.createFromResource(this,R.array.publicSpinnerItems,android.R.layout.simple_spinner_dropdown_item)
        publicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        publicSpinner.adapter = publicAdapter

        // 各spinnerが呼ばれたときの挙動
        feelingSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val text = parent?.selectedItem as String
            }
        }

        // 各spinnerが呼ばれたときの挙動
        publicSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, v: View?, p2: Int, p3: Long) {
                // キーボードが出てたら閉じる
                val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (v != null) {
                    im.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
                }
                val text = parent?.selectedItem as String
            }

        }

        // buttonの準備
        date_button.setOnClickListener(this)
        done_button.setOnClickListener(this)
    }
    override fun onClick(v: View) {
        if(v == date_button){
            val datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    mYear = year
                    mMonth = month
                    mDay = dayOfMonth
                    val dateString = mYear.toString() + "/" + String.format("%02d", mMonth + 1) + "/" + String.format("%02d", mDay)
                    date_button.text = dateString
                }, mYear, mMonth, mDay)
            datePickerDialog.show()
        }else if(v == done_button){
            // キーボードが出てたら閉じる
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)

            // 情報を取得
            val title = titleText.text.toString()
            val feeling = feelingSpinner.selectedItem
            val content = contentText.text.toString()
            val publicText = publicSpinner.selectedItem
            var public:Boolean = false;
            if(publicText.toString() == "公開"){
                public = true
            }
            val calendar = GregorianCalendar(mYear, mMonth, mDay)
            val date = calendar.time

            if (title.isEmpty()) {
                // タイトルが入力されていない時はエラーを表示するだけ
                Snackbar.make(v, getString(R.string.journal_title), Snackbar.LENGTH_LONG).show()
                return
            }

            if (content.isEmpty()) {
                // 質問が入力されていない時はエラーを表示するだけ
                Snackbar.make(v, getString(R.string.journal_content), Snackbar.LENGTH_LONG).show()
                return
            }

            // Preferenceから名前を取る
            val sp = PreferenceManager.getDefaultSharedPreferences(this)
            val name = sp.getString("name", "")

            // FirestoreQuestionのインスタンスを作成し、値を詰めていく
            var fireStoreJournal = FirestoreJournal()
            fireStoreJournal.uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            fireStoreJournal.title = title
            fireStoreJournal.feeling = feeling.toString()
            fireStoreJournal.content = content
            fireStoreJournal.public = public
            fireStoreJournal.name = name!!
            fireStoreJournal.date = date

            FirebaseFirestore.getInstance()
                .collection("journals")
                .document(fireStoreJournal.id)
                .set(fireStoreJournal)
                .addOnSuccessListener {
                    finish()
                }
                .addOnFailureListener {
                    it.printStackTrace()
                }
        }
    }
}