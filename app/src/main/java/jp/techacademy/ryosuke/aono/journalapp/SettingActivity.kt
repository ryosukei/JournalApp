package jp.techacademy.ryosuke.aono.journalapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_setting.*
import java.util.*

class SettingActivity : AppCompatActivity() {
    // firestoreのinstanceを取得
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        // Preferenceから表示名を取得してEditTextに反映させる
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val name = sp.getString("name", "")
        changeUsername.setText(name)
        // UIの初期設定
        title = "設定"

        change.setOnClickListener{v ->
            // キーボードが出ていたら閉じる
            val im = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser
            // firestoreのinstance
            val userRef = db.collection("users").document(user!!.uid)

            if (user == null) {
                // ログインしていない場合は何もしない
                Snackbar.make(v, "ログインしていません", Snackbar.LENGTH_LONG).show()
            } else {
                // 変更した表示名をFirebaseに保存する
                val name2 = changeUsername.text.toString()
                val data = HashMap<String, String>()
                data["name"] = name2
                userRef.set(data)

                // 変更した表示名をPreferenceに保存する
                val sp2 = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val editor = sp2.edit()
                editor.putString("name", name)
                editor.commit()

                Snackbar.make(v, getString(R.string.change_disp_name), Snackbar.LENGTH_LONG).show()
            }
        }
        logout.setOnClickListener { v ->
            FirebaseAuth.getInstance().signOut()
            changeUsername.setText("")
            Snackbar.make(v, getString(R.string.logout_complete_message), Snackbar.LENGTH_LONG).show()
        }
    }
}