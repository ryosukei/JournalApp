package jp.techacademy.ryosuke.aono.journalapp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.util.CollectionUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_first.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class MyJournalFragment : Fragment() {
    private val journalListAdapter by lazy {JournalListAdapter(requireContext())}
    private val handler = Handler(Looper.getMainLooper())
    private var snapshotListener: ListenerRegistration? = null
    private var fragmentCallback : FragmentCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is FragmentCallback){
            fragmentCallback = context
        }
    }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ここから初期化処理を行う
        // RecyclerViewの初期化
        journalListAdapter.apply{
            // Itemをクリックしたとき
            onClickItem = {
                fragmentCallback?.onClickItem(it)
            }
        }
        recyclerView.apply {
            adapter = journalListAdapter
            layoutManager = LinearLayoutManager(requireContext()) // 一列ずつ表示
        }
        swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
        updateData()
        super.onViewCreated(view, savedInstanceState)
    }
    private fun updateData(){
        // 一つ前のリスナーを消す
        snapshotListener?.remove()
        var journals = CollectionUtils.listOf<Journal>()
        // user情報を取得
        val user = FirebaseAuth.getInstance().currentUser
        snapshotListener = FirebaseFirestore.getInstance()
            .collection("journals")
            .whereEqualTo("public",false)
            .whereEqualTo("uid",user!!.uid)
            .addSnapshotListener{ querySnapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if(error != null){
                    return@addSnapshotListener
                }
                val results = querySnapshot?.toObjects(FirestoreJournal::class.java)
                results?.also {
                    journals = it.map { firestoreJournal:FirestoreJournal  ->
                        Log.d("tag",firestoreJournal.title)
                        Journal(firestoreJournal.title,firestoreJournal.feeling, firestoreJournal.content,firestoreJournal.public, firestoreJournal.name, firestoreJournal.uid,firestoreJournal.id, firestoreJournal.date)
                    }
                }
                Log.d("tag",journals.size.toString())
                handler.post {
                    updateRecyclerView(journals)
                }
            }

    }
    private fun updateRecyclerView(list: List<Journal>) {
        journalListAdapter.refresh(list)
        swipeRefreshLayout.isRefreshing = false // SwipeRefreshLayoutのくるくるを消す
    }
}