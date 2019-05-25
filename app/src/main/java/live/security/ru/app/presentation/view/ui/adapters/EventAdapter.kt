package ru.security.live.presentation.view.ui.adapters

import android.content.Context
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.security.live.R
import ru.security.live.domain.entity.EventItem
/**
 * @author sardor
 */
class EventAdapter(val onClick: (EventItem) -> Unit) : ListAdapter<EventItem, RecyclerView.ViewHolder>(diffCallback) {
    private lateinit var context: Context

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = getItem(position)

            holder.tvName.text = model.name


            holder.tvStatus.text = model.status


            holder.tvTime.text = model.time

            when (model.importance) {
                "Critical" -> holder.ivIcon.setImageResource(R.drawable.icon_lamp_2)
                "High" -> holder.ivIcon.setImageResource(R.drawable.icon_lamp_1)
                else -> holder.ivIcon.setImageResource(R.drawable.icon_lamp_3)
            }

            holder.itemLayout.setOnClickListener { onClick(model) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_list, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemLayout: View = view.findViewById(R.id.itemLayout)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)

    }

    object diffCallback : DiffUtil.ItemCallback<EventItem>() {
        override fun areItemsTheSame(oldItem: EventItem?, newItem: EventItem?): Boolean {
            /*return oldItem?.name == newItem?.name || oldItem?.status == newItem?.status || oldItem?.time == newItem?.time || oldItem?.icon == newItem?.icon*/
            return areContentsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: EventItem?, newItem: EventItem?): Boolean {
            return oldItem == newItem
        }
    }
}