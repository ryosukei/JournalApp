package jp.techacademy.ryosuke.aono.journalapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val viewPagerAdapter by lazy { ViewPagerAdapter(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            // ログインしてなければログイン画面
            if(user == null){
                val intent = Intent(applicationContext,LoginActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(applicationContext,JournalSendActivity::class.java)
                startActivity(intent)
            }

        }

        // ViewPager2の初期化
        viewPager2.apply{
            adapter = viewPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL // スワイプの方法
            offscreenPageLimit = viewPagerAdapter.itemCount
        }

        // TabLayoutの初期化
        // TabLayoutとViewPager2を紐づける
        TabLayoutMediator(tabLayout,viewPager2) {tab,position ->
            tab.setText(viewPagerAdapter.titleIds[position])
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            val intent = Intent(applicationContext, SettingActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}