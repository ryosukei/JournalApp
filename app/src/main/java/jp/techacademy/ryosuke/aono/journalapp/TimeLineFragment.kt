package jp.techacademy.ryosuke.aono.journalapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_first.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class TimeLineFragment : Fragment() {
    private val journalListAdapter by lazy {JournalListAdapter(requireContext())}
    private val handler = Handler(Looper.getMainLooper())
    private var snapshotListener: ListenerRegistration? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // ここから初期化処理を行う
        // RecyclerViewの初期化
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
        var journals = listOf<Journal>()

        snapshotListener = FirebaseFirestore.getInstance()
            .collection("journals")
            .whereEqualTo("public",true)
            .addSnapshotListener{querySnapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if(error != null){
                    return@addSnapshotListener
                }
                val results = querySnapshot?.toObjects(FirestoreJournal::class.java)
                results?.also {
                    journals = it.map { firestoreJournal:FirestoreJournal  ->
                        Journal(firestoreJournal.title,firestoreJournal.feeling, firestoreJournal.content,firestoreJournal.public, firestoreJournal.uid,firestoreJournal.id, firestoreJournal.date)
                    }
                }
            }
        handler.post {
            updateRecyclerView(journals)
        }
    }
    private fun updateRecyclerView(list: List<Journal>) {
        journalListAdapter.refresh(list)
        swipeRefreshLayout.isRefreshing = false // SwipeRefreshLayoutのくるくるを消す
    }
}