package ru.security.live.presentation.view.ui.adapters

import android.content.Context
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.security.live.R
import ru.security.live.domain.entity.DesktopsItem
/**
 * @author sardor
 */
class DesktopsAdapter(val onClick: (DesktopsItem, Int) -> Unit) : ListAdapter<DesktopsItem, RecyclerView.ViewHolder>(diffCallback) {
    private lateinit var context: Context

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = getItem(position)

            holder.tvName.text = model._name

            holder.itemLayout.setOnClickListener { onClick(model, position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_desktops_list, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemLayout: View = view.findViewById(R.id.itemLayout)
        val tvName: TextView = view.findViewById(R.id.tvName)
    }

    object diffCallback : DiffUtil.ItemCallback<DesktopsItem>() {
        override fun areItemsTheSame(oldItem: DesktopsItem?, newItem: DesktopsItem?): Boolean {
            /*return oldItem?.name == newItem?.name*/
            return areContentsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: DesktopsItem?, newItem: DesktopsItem?): Boolean {
            return oldItem == newItem
        }
    }
}