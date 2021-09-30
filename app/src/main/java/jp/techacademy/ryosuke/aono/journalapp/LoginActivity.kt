package jp.techacademy.ryosuke.aono.journalapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.preference.PreferenceManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCreateAccountListener: OnCompleteListener<AuthResult>
    private lateinit var mLoginListener: OnCompleteListener<AuthResult>

    // firestoreのinstanceを取得
    private val db = FirebaseFirestore.getInstance()

    // アカウント作成時にフラグを立て、ログイン処理後に名前をFirebaseに保存する
    private var mIsCreateAccount = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // FirebaseAuthのオブジェクトを取得する
        mAuth = FirebaseAuth.getInstance()

        // タイトルの設定
        title = getString(R.string.login_title)
        // アカウント作成処理のリスナー
        mCreateAccountListener = OnCompleteListener { task ->
            if (task.isSuccessful) {
                // 成功した場合
                // ログインを行う
                val email = email.text.toString()
                val password = password.text.toString()
                login(email, password)
            } else {

                // 失敗した場合
                // エラーを表示する
                val view = findViewById<View>(android.R.id.content)
                Snackbar.make(view, getString(R.string.signUp_failed), Snackbar.LENGTH_LONG).show()

                // プログレスバーを非表示にする
                loading.visibility = View.GONE
            }
        }
        // ログイン処理のリスナー
        mLoginListener = OnCompleteListener { task ->
            if(task.isSuccessful){
                val user = mAuth.currentUser
                val userRef = db.collection("users").document(user!!.uid)
                Log.d("tag",userRef.toString())
                if(mIsCreateAccount){
                    // アカウント作成の時は表示名をFirebaseに保存する
                    val name = username.text.toString()
                    val data = HashMap<String, String>()
                    data["name"] = name

                    userRef.set(data)
                    // 表示名をPreferenceに保存する
                    saveName(name)
                }else{
                    userRef.get().addOnSuccessListener { document ->
                        if(document != null){
                            val data = document.data
                            Log.d("tag",data.toString())
                            saveName(data!!["name"] as String)
                        }
                    }.addOnFailureListener { exception ->
                        Log.d("TAG", "get failed with ", exception)
                    }
                }
                // プログレスバーを非表示にする
                loading.visibility = View.GONE

                // Activityを閉じる
                finish()
            }else {
                // 失敗した場合
                // エラーを表示する
                val view = findViewById<View>(android.R.id.content)
                Snackbar.make(view, getString(R.string.login_failed), Snackbar.LENGTH_LONG).show()

                // プログレスバーを非表示にする
                loading.visibility = View.GONE
            }
        }
        // 新規登録のりすな
        signUp.setOnClickListener{v ->
            // キーボードが出てたら閉じる
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val email = email.text.toString()
            val password = password.text.toString()
            val name = username.text.toString()
            if (email.length != 0 && password.length >= 6 && name.length != 0) {
                // ログイン時に表示名を保存するようにフラグを立てる
                mIsCreateAccount = true

                createAccount(email, password)
            } else {
                // エラーを表示する
                Snackbar.make(v, getString(R.string.signUp_failed), Snackbar.LENGTH_LONG).show()
            }
        }
        // ログインのリスな
        login.setOnClickListener{v ->
            // キーボードが出てたら閉じる
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val email = email.text.toString()
            val password = password.text.toString()

            if (email.isNotEmpty() && password.length >= 6) {
                // フラグを落としておく
                mIsCreateAccount = false

                login(email, password)
            } else {
                // エラーを表示する
                Snackbar.make(v, getString(R.string.login_failed), Snackbar.LENGTH_LONG).show()
            }
        }
    }
    private fun createAccount(email: String, password: String){
        // プログレスバーを表示する
        loading.visibility = View.VISIBLE

        // アカウントを作成して、リスナーを発生させる
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(mCreateAccountListener)
    }
    private fun login(email: String, password: String) {
        // プログレスバーを表示する
        loading.visibility = View.VISIBLE

        // ログインする
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mLoginListener)
    }
    private fun saveName(name: String) {
        // Preferenceに保存する
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sp.edit()
        editor.putString("name", name)
        editor.commit()
    }
}