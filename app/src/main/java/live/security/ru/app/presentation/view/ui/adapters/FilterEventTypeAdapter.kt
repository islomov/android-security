package ru.security.live.presentation.view.ui.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_checkable.view.*
import ru.security.live.R
import ru.security.live.domain.entity.FilterEventTypeItem
/**
 * @author sardor
 */
class FilterEventTypeAdapter(private val list: ArrayList<FilterEventTypeItem>,
                             private val onClick: (Int) -> Unit) : RecyclerView.Adapter<FilterEventTypeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_checkable, parent, false))

    override fun getItemCount() = list.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, onClick)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: FilterEventTypeItem?, onClick: (Int) -> Unit) {
            if (item != null) {
                itemView.tvTitle.text = item.name
                itemView.ivCheck.setImageResource(
                        if (item.isChecked) R.drawable.icon_checkbox_act_1
                        else R.drawable.icon_checkbox
                )
                itemView.setOnClickListener { _ ->
                    onClick(adapterPosition)
                }
                Log.d("appLogTag", "${item.name} ${item.isChecked}")
            }
        }
    }
}