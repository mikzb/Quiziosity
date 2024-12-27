package es.uma.quiziosity.ui.leaderboard

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import es.uma.quiziosity.data.entity.User

import es.uma.quiziosity.databinding.FragmentLeaderboardBinding


class MyItemRecyclerViewAdapter(
    private val values: List<User>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentLeaderboardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.username
        item.score.toString().also { holder.contentView.text = it }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentLeaderboardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}