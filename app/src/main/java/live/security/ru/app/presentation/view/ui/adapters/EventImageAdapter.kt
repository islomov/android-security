package ru.security.live.presentation.view.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_image.view.*
import ru.security.live.R
/**
 * @author sardor
 */
class EventImageAdapter(val links: List<String>,
                        val onClick: (String) -> Unit) : RecyclerView.Adapter<EventImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false))

    override fun getItemCount() = links.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(link = links[position], onClick = onClick)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(link: String, onClick: (String) -> Unit) {
            Picasso.get().load(link).fit().into(itemView.image)
            itemView.setOnClickListener { onClick(link) }
        }
    }
}