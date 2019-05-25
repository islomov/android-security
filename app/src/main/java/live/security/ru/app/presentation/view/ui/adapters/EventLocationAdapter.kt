package ru.security.live.presentation.view.ui.adapters

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.security.live.R
import ru.security.live.domain.entity.EventLocationItem
/**
 * @author sardor
 */
class EventLocationAdapter(private val items: List<EventLocationItem>,
                           listener: OnEventLocationItemClickListener,
                           listener2: OnEventLocationItemClickListener) : RecyclerView.Adapter<EventLocationAdapter.ViewHolder>() {

    private val onClickListener: OnEventLocationItemClickListener = listener
    private val onCheckClickListener: OnEventLocationItemClickListener = listener2

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_choose_location, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val item = items[i]
        if (!item.hasChildren) {
            viewHolder.name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        viewHolder.name.text = item.name
        viewHolder.name.setOnClickListener { onClickListener.onClick(item) }
        viewHolder.check.setOnClickListener { onCheckClickListener.onClick(item) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView
        val check: ImageView
        val itemLayout: ConstraintLayout

        init {
            name = itemView.findViewById(R.id.tvName)
            check = itemView.findViewById(R.id.ivCheck)
            itemLayout = itemView.findViewById(R.id.itemLayout)
        }
    }

    interface OnEventLocationItemClickListener {
        fun onClick(item: EventLocationItem)
    }
}