package ru.security.live.presentation.view.ui.adapters

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ru.security.live.R
import ru.security.live.domain.entity.DeviceItem

/**
 * @author sardor
 */
class EventDeviceAdapter(private val items: List<DeviceItem>,
                         listener: OnEventDeviceItemClickListener) : RecyclerView.Adapter<EventDeviceAdapter.ViewHolder>() {

    private val onClickListener: OnEventDeviceItemClickListener = listener

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_choose_location, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val item = items[i]
        viewHolder.check.visibility = View.GONE
        viewHolder.name.setCompoundDrawables(null, null, null, null)
        viewHolder.name.text = item.name
        viewHolder.name.setOnClickListener { onClickListener.onClick(item) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvName)
        val check: ImageView = itemView.findViewById(R.id.ivCheck)
        val itemLayout: ConstraintLayout = itemView.findViewById(R.id.itemLayout)
    }

    interface OnEventDeviceItemClickListener {
        fun onClick(item: DeviceItem)
    }
}