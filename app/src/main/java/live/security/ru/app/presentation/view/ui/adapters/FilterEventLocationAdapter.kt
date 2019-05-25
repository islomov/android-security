package ru.security.live.presentation.view.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_expandable_check.view.*
import ru.security.live.R
import ru.security.live.domain.entity.EventLocationItem
import ru.security.live.util.allChecked
import ru.security.live.util.someChecked
/**
 * @author sardor
 */
class FilterEventLocationAdapter(
        private val list: ArrayList<EventLocationItem>,
        private val onClick: (Int) -> Unit,
        private val onCheckClick: (Int) -> Unit
) : RecyclerView.Adapter<FilterEventLocationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_expandable_check, parent, false))

    override fun getItemCount() = list.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[holder.adapterPosition], onClick, onCheckClick)
    }

    fun update(index: Int, item: EventLocationItem) {
        list.set(index, item)
        notifyItemChanged(index)
    }

    fun get(index: Int): EventLocationItem {
        return list[index]
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: EventLocationItem, onClick: (Int) -> Unit, onCheckClick: (Int) -> Unit) {
            itemView.tvTitle.text = item.name
            itemView.ivArrow.visibility = if (item.hasChildren) View.VISIBLE else View.GONE

            val icon = when (item.status) {
                someChecked -> R.drawable.icon_checkbox_act_2
                allChecked -> R.drawable.icon_checkbox_act_1
                else -> R.drawable.icon_checkbox
            }
            itemView.ivCheck.setImageResource(icon)

            itemView.setOnClickListener { onClick(adapterPosition) }
            itemView.ivCheck.setOnClickListener { onCheckClick(adapterPosition) }
        }
    }

    class ParentHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: EventLocationItem, onClick: (Int) -> Unit) {

        }
    }
}