package es.uma.quiziosity.ui.leaderboard

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import es.uma.quiziosity.QuiziosityApp
import es.uma.quiziosity.R
import es.uma.quiziosity.data.entity.User
import es.uma.quiziosity.utils.TextDrawable

class MyItemRecyclerViewAdapter(
    private var values: List<User>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        holder.idView.text = item.username
        val avatarBitmap = TextDrawable.getAvatarBitmap(
            context = holder.itemView.context,
            text = item.username.first().toString(),
            sizeDp = 50 // Size in dp
        )
        holder.avatarView.setImageBitmap(avatarBitmap)
        holder.contentView.text = buildString {
            append(QuiziosityApp.getContext().getString(R.string.score))
            append(": ")
            append(item.score.toString())
        }
    }

    override fun getItemCount(): Int = values.size

    fun addItems(newItems: List<User>) {
        val startPosition = values.size
        values = values + newItems
        notifyItemRangeInserted(startPosition, newItems.size)
    }

    inner class ViewHolder(binding: View) :
        RecyclerView.ViewHolder(binding) {
        val idView: TextView = binding.findViewById(R.id.username)
        val contentView: TextView = binding.findViewById(R.id.score)
        val avatarView: ImageView = binding.findViewById(R.id.profile_image)

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}