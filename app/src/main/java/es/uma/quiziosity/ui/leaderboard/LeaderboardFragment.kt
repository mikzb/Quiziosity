package es.uma.quiziosity.ui.leaderboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import es.uma.quiziosity.QuiziosityApp
import es.uma.quiziosity.R
import es.uma.quiziosity.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class LeaderboardFragment : Fragment() {

    private var columnCount = 1
    private lateinit var adapter: MyItemRecyclerViewAdapter
    private var isLoading = false
    private var currentPage = 0
    private val pageSize = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_leaderboard_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyItemRecyclerViewAdapter(emptyList())
                this@LeaderboardFragment.adapter = adapter as MyItemRecyclerViewAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (!recyclerView.canScrollVertically(1) && !isLoading) {
                            loadMoreItems()
                        }
                    }
                })
            }
        }

        loadMoreItems()

        return view
    }

    private fun loadMoreItems() {
        isLoading = true
        lifecycleScope.launch {
            val users = QuiziosityApp.getUserRepository().getUsersByScorePaged(currentPage * pageSize, pageSize)
            adapter.addItems(users)
            currentPage++
            isLoading = false
        }
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            LeaderboardFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}