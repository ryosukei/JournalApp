package jp.techacademy.ryosuke.aono.journalapp

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.text.SimpleDateFormat

class JournalListAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // 取得したデータを解析し、Shop型オブジェクトとして生成したものを格納するリスト
    private val items = mutableListOf<Journal>()
    // Itemを押したときのメソッド
    var onClickItem: ((Journal) -> Unit)? = null
    // 表示リスト更新時に呼び出すメソッド
    fun refresh(list: List<Journal>) {
        items.apply {
            clear() // items を 空にする
            addAll(list) // itemsにlistを全て追加する
        }
        notifyDataSetChanged() // recyclerViewを再描画させる
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return JournalItemViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_journal,parent,false))
    }

    class JournalItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        // レイアウトファイルからidがrootViewのConstraintLayoutオブジェクトを取得し、代入
        val rootView : ConstraintLayout = view.findViewById(R.id.rootView)
        val titleView: TextView = view.findViewById(R.id.titleText)
        val feelingView: TextView = view.findViewById(R.id.feelingText)
        val contentView: TextView = view.findViewById(R.id.contentText)
        val nameView: TextView = view.findViewById(R.id.nameTextView)
        val dateView: TextView = view.findViewById(R.id.dateTextView)
    }

    override fun getItemCount(): Int {
        // itemsプロパティに格納されている要素数を返す
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is JournalItemViewHolder){
            updateJournalItemViewHolder(holder,position)
        }
    }
    private fun updateJournalItemViewHolder(holder: JournalItemViewHolder,position: Int){
        // 生成されたViewHolderの位置を指定し、オブジェクトを代入
        val data = items[position]
        holder.apply {
            rootView.apply {
                // それぞれの奴を当てはめる
                titleView.text = data.title
                feelingView.text = data.feeling
                contentView.text = data.content
                nameView.text = data.name
                val dateFormat = SimpleDateFormat("yyyy-MM-dd");
                dateView.text = dateFormat.format(data.date)
                // 偶数番目と奇数番目で背景色を変更させる
                setBackgroundColor(
                    ContextCompat.getColor(context,
                    if (position % 2 == 0) android.R.color.white else android.R.color.darker_gray))

                setOnClickListener{
                    onClickItem?.invoke(data)
                }
            }
        }
    }
}